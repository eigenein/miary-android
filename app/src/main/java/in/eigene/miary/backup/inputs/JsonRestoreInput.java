package in.eigene.miary.backup.inputs;

import android.util.JsonReader;
import android.util.Log;

import in.eigene.miary.backup.RestoreInput;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.helpers.Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class JsonRestoreInput extends RestoreInput {

    private static final String LOG_TAG = JsonRestoreInput.class.getSimpleName();

    private final JsonReader reader;

    private int schemaVersion;

    private int count;

    public JsonRestoreInput(final InputStream inputStream) {
        super(inputStream);
        this.reader = new JsonReader(streamReader);
        this.reader.setLenient(true);
    }

    @Override
    public int start() throws IOException {
        Log.d(LOG_TAG, "Starting.");
        reader.beginObject();
        while (reader.hasNext()) {
            final String name = reader.nextName();
            Log.d(LOG_TAG, "Read property name: " + name);
            if (name.equals("schemaVersion")) {
                schemaVersion = reader.nextInt();
                Log.i(LOG_TAG, "Schema version: " + schemaVersion);
            } else if (name.equals("count")) {
                count = reader.nextInt();
                Log.i(LOG_TAG, "Count: " + count);
            } else if (name.equals("notes")) {
                Log.d(LOG_TAG, "Begin array.");
                reader.beginArray();
                break;
            } else {
                reader.skipValue();
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
                note.setColor(reader.nextInt());
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
