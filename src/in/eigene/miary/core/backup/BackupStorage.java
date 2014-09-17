package in.eigene.miary.core.backup;

import android.content.*;

import java.io.*;

/**
 * Represents backup storage.
 */
public abstract class BackupStorage {

    /**
     * Gets whether a backup should not have a constant name.
     */
    public abstract boolean includeDate();

    /**
     * Checks if the storage is ready.
     */
    public abstract boolean checkReady();

    /**
     * Gets output stream for the specified backup name.
     */
    public abstract OutputStream getOutputStream(final String name);

    /**
     * Performs final actions on the backup.
     * Invoked first on non-UI thread and then on UI thread.
     */
    public abstract void finish(
            final Context context,
            boolean uiThread,
            final String name,
            final String mimeType);
}
