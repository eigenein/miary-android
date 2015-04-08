package in.eigene.miary.core.backup.outputs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import in.eigene.miary.core.backup.BackupOutput;
import in.eigene.miary.core.backup.Storage;
import in.eigene.miary.core.persistence.Note;

/**
 * Backups into a plain text file.
 */
public class PlainTextBackupOutput extends BackupOutput {

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
    public void start(final int count) {
        // Do nothing.
    }

    @Override
    public void write(final Note note) {
        if (note.isDeleted()) {
            return;
        }
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

    public static class Factory extends BackupOutput.Factory {

        @Override
        public BackupOutput createOutput(final Storage storage) throws IOException {
            final String name = storage.getOutputName(".txt");
            return new PlainTextBackupOutput(name, storage.getOutputStream(name));
        }
    }
}
