package in.eigene.miary.core.backup;

import java.io.*;

public abstract class BackupOutputFactory {

    public abstract BackupOutput createOutput(final OutputStream outputStream);

    /**
     * Gets format-specific backup name extension.
     */
    public abstract String getExtension();

    /**
     * Gets format-specific MIME type.
     */
    public abstract String getMimeType();
}
