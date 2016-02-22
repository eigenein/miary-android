package in.eigene.miary.backup.inputs;

import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.util.SparseIntArray;

import in.eigene.miary.backup.RestoreInput;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.helpers.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class JsonRestoreInput extends RestoreInput {

    private static final String LOG_TAG = JsonRestoreInput.class.getSimpleName();

    /**
     * Used to restore old color codes.
     */
    private static final SparseIntArray LEGACY_COLORS = new SparseIntArray();

    private final JsonReader reader;

    private int schemaVersion;

    private int count;

    static {
        LEGACY_COLORS.put(0, Color.WHITE);
        LEGACY_COLORS.put(1, 0xFFEF5350);
        LEGACY_COLORS.put(2, 0xFFFFA726);
        LEGACY_COLORS.put(3, 0xFFFFEB3B);
        LEGACY_COLORS.put(4, 0xFFF5F5F5);
        LEGACY_COLORS.put(5, 0xFF8BC34A);
        LEGACY_COLORS.put(6, 0xFF90CAF9);
        LEGACY_COLORS.put(7, 0xFFCE93D8);
    }

    public JsonRestoreInput(final InputStream inputStream) {
        super(inputStream);
        this.reader = new JsonReader(streamReader);
        this.reader.setLenient(true);
    }

    @Override
    public int start() throws IOException {
        Log.d(LOG_TAG, "Starting.");
        reader.beginObject();
        label:
        while (reader.hasNext()) {
            final String name = reader.nextName();
            Log.d(LOG_TAG, "Read property name: " + name);
            switch (name) {
                case "schemaVersion":
                    schemaVersion = reader.nextInt();
                    Log.i(LOG_TAG, "Schema version: " + schemaVersion);
                    break;
                case "count":
                    count = reader.nextInt();
                    Log.i(LOG_TAG, "Count: " + count);
                    break;
                case "notes":
                    Log.d(LOG_TAG, "Begin array.");
                    reader.beginArray();
                    break label;
                default:
                    reader.skipValue();
                    break;
            }
        }
        return count;
    }

    @Override
    public Note read() throws IOException {
        final Note note = Note.createEmpty();
        Log.d(LOG_TAG, "Begin object.");
        reader.beginObject();
        while (reader.hasNext()) {
            final String name = reader.nextName();
            Log.d(LOG_TAG, "Read property name: " + name);
            if (name.equals("uuid")) {
                // Backwards compatibility.
                note.setId(Math.abs(UUID.fromString(reader.nextString()).getLeastSignificantBits()));
            } else if (name.equals("title")) {
                note.setTitle(reader.nextString());
            } else if (name.equals("text")) {
                note.setText(reader.nextString());
            } else if (name.equals("color")) {
                final int color = reader.nextInt();
                note.setColor(LEGACY_COLORS.get(color, color));
            } else if (name.equals("isStarred")) {
                note.setStarred(reader.nextBoolean());
            } else if (name.equals("isDraft")) {
                note.setDraft(reader.nextBoolean());
            } else if (name.equals("customDate")) {
                note.setCustomDate(Util.parse(reader.nextString()));
            } else if (name.equals("creationDate")) {
                note.setCreatedDate(Util.parse(reader.nextString()));
            } else if (name.equals("isDeleted")) {
                note.setDeleted(reader.nextBoolean());
            } else {
                reader.skipValue();
            }
        }
        Log.d(LOG_TAG, "End object.");
        reader.endObject();
        return note;
    }

    @Override
    public void finish() throws IOException {
        reader.endArray();
        reader.endObject();
        reader.close();
    }

    public static class Factory extends RestoreInput.Factory {

        @Override
        public RestoreInput createInput(final InputStream inputStream) {
            return new JsonRestoreInput(inputStream);
        }
    }
}
