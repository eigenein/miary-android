package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class PlainTextBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final OutputStream outputStream) {
        return new PlainTextBackupOutput(outputStream);
    }

    @Override
    protected String getExtension() {
        return "txt";
    }

    @Override
    protected String getMimeType() {
        return "text/plain";
    }
}
