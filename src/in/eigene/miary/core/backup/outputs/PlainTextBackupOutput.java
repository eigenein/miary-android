package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

/**
 * Backups into a plain text file.
 */
public class PlainTextBackupOutput extends BackupOutput {

    private PrintWriter writer;

    public PlainTextBackupOutput(final OutputStream outputStream) {
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
        } catch (final UnsupportedEncodingException e) {
            InternalRuntimeException.throwForException("Unsupported encoding.", e);
        }
    }

    @Override
    public void start() {
        // Do nothing.
    }

    @Override
    public void write(final Note note) {
        writer.println(note.getCustomDate()
                + (note.isStarred() ? " [starred]": "")
                + (note.isDraft() ? " [draft]" : ""));
        if (!note.getTitle().isEmpty()) {
            writer.println(note.getTitle());
            writer.println(note.getTitle().replaceAll(".", "-"));
        }
        if (!note.getText().isEmpty()) {
            writer.println(note.getText());
        }
        writer.println();
    }

    @Override
    public void finish() {
        // Do nothing.
    }
}
