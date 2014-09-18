package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class PlainTextBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final OutputStream outputStream) {
        return new PlainTextBackupOutput(outputStream);
    }

    @Override
    public String getExtension() {
        return "txt";
    }

    @Override
    public String getMimeType() {
        return "text/plain";
    }
}
