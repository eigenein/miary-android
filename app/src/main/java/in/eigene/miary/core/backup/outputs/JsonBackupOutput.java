package in.eigene.miary.core.backup.outputs;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import in.eigene.miary.core.backup.BackupOutput;
import in.eigene.miary.core.backup.Storage;
import in.eigene.miary.core.persistence.Note;
import in.eigene.miary.helpers.Util;

public class JsonBackupOutput extends BackupOutput {

    /**
     * JSON schema version to be able to restore a backup.
     */
    private static final int SCHEMA_VERSION = 2;

    private final JsonWriter writer;

    protected JsonBackupOutput(final String name, final OutputStream outputStream) {
        super(name, outputStream);
        writer = new JsonWriter(streamWriter);
        writer.setIndent("  ");
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public void start(final int count) throws IOException {
        writer.beginObject();
        writer.name("schemaVersion").value(SCHEMA_VERSION);
        writer.name("count").value(count);
        writer.name("notes").beginArray();
    }

    @Override
    public void write(final Note note) throws IOException {
        writer.beginObject();
        writer.name("id").value(note.getId());
        writer.name("uuid").value(new UUID(note.getId(), note.getId()).toString()); // backwards compatibility
        writer.name("title").value(note.getTitle());
        writer.name("text").value(note.getText());
        writer.name("color").value(note.getColor());
        writer.name("isStarred").value(note.isStarred());
        writer.name("isDraft").value(note.isDraft());
        writer.name("customDate").value(Util.format(note.getCustomDate()));
        writer.name("creationDate").value(Util.format(note.getCreatedDate()));
        writer.name("isDeleted").value(note.isDeleted());
        writer.endObject();
    }

    @Override
    public void finish() throws IOException {
        writer.endArray();
        writer.endObject();
        writer.close();
    }

    public static class Factory extends BackupOutput.Factory {

        @Override
        public BackupOutput createOutput(final Storage storage) throws IOException {
            final String name = storage.getOutputName(".json");
            return new JsonBackupOutput(name, storage.getOutputStream(name));
        }
    }
}
