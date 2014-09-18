package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class JsonBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final OutputStream outputStream) {
        return new JsonBackupOutput(outputStream);
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }
}
