package in.eigene.miary.core.backup.outputs;

import in.eigene.miary.core.backup.*;

import java.io.*;

public class PlainTextBackupOutputFactory extends BackupOutputFactory {

    @Override
    public BackupOutput createOutput(final Storage storage) throws IOException {
        final String name = storage.getOutputName(".txt");
        return new PlainTextBackupOutput(name, storage.getOutputStream(name));
    }
}
