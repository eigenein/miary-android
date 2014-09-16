package in.eigene.miary.core.backup.outputs;

import android.util.*;
import in.eigene.miary.core.*;

import java.io.*;
import java.text.*;

public class JsonBackupOutput extends TextBackupOutput {

    /**
     * JSON schema version to be able to restore a backup.
     */
    private static final int SCHEMA_VERSION = 1;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final JsonWriter writer;

    protected JsonBackupOutput(final OutputStream outputStream) {
        super(outputStream);
        writer = new JsonWriter(streamWriter);
        writer.setIndent("  ");
    }

    @Override
    public void start() throws IOException {
        writer.beginObject();
        writer.name("schemaVersion").value(SCHEMA_VERSION);
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
        writer.name("customDate").value(DATE_FORMAT.format(note.getCustomDate()));
        writer.name("creationDate").value(DATE_FORMAT.format(note.getCreationDate()));
        writer.endObject();
    }

    @Override
    public void finish() throws IOException {
        writer.endArray();
        writer.endObject();
        writer.close();
    }
}
