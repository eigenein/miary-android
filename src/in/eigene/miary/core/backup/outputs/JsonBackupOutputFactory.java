package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class JsonBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final Storage storage) throws IOException {
        final String name = storage.getOutputName(".json");
        return new JsonBackupOutput(name, storage.getOutputStream(name));
    }
}
