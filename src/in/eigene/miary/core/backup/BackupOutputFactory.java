package in.eigene.miary.core.backup;

import java.io.*;

public abstract class BackupOutputFactory {

    public abstract BackupOutput createOutput(final OutputStream outputStream);

    /**
     * Gets format-specific backup name extension.
     */
    protected abstract String getExtension();
}
