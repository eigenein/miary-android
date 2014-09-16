package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class JsonBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final OutputStream outputStream) {
        return new JsonBackupOutput(outputStream);
    }

    @Override
    protected String getExtension() {
        return "json";
    }

    @Override
    protected String getMimeType() {
        return "application/json";
    }
}
