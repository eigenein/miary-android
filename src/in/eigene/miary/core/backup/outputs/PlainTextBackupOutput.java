package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.*;

import java.io.*;

/**
 * Backups into a plain text file.
 */
public class PlainTextBackupOutput extends TextBackupOutput {

    private PrintWriter writer;

    public PlainTextBackupOutput(final String name, final OutputStream outputStream) {
        super(name, outputStream);
        this.writer = new PrintWriter(streamWriter, true);
    }

    @Override
    public String getMimeType() {
        return "text/plain";
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
        writer.close();
    }
}
