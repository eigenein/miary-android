package in.eigene.miary.core.backup;

import java.io.*;

public abstract class BackupOutputFactory {

    public abstract BackupOutput createOutput(final Storage storage) throws IOException;
}
