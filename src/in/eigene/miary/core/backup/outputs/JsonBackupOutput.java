package in.eigene.miary.core.backup.outputs;

import android.util.*;
import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.helpers.*;

import java.io.*;

public class JsonBackupOutput extends BackupOutput {

    /**
     * JSON schema version to be able to restore a backup.
     */
    private static final int SCHEMA_VERSION = 1;

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
        writer.name("uuid").value(note.getUuid().toString());
        writer.name("title").value(note.getTitle());
        writer.name("text").value(note.getText());
        writer.name("color").value(note.getColor());
        writer.name("isStarred").value(note.isStarred());
        writer.name("isDraft").value(note.isDraft());
        writer.name("customDate").value(Util.format(note.getCustomDate()));
        writer.name("creationDate").value(Util.format(note.getCreationDate()));
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
