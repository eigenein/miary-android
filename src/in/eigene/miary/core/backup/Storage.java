package in.eigene.miary.core.backup;

import android.content.*;

import java.io.*;

/**
 * Represents backup storage.
 */
public abstract class Storage {

    /**
     * Checks if the storage is ready.
     */
    public abstract boolean checkReady();

    /**
     * Gets new output stream.
     */
    public abstract OutputStream getOutputStream(final String name) throws IOException;

    /**
     * Performs final actions on the backup.
     * Invoked first on non-UI thread and then on UI thread.
     */
    public abstract void finish(
            final Context context,
            boolean uiThread,
            final BackupOutput output);

    /**
     * Gets new backup name.
     */
    public abstract String getOutputName(final String suffix);
}
