package in.eigene.miary.core.backup;

import android.content.*;

import java.io.*;

/**
 * Represents backup storage.
 */
public abstract class BackupStorage {

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
     */
    public abstract void finish(final Context context, final String name, final String mimeType);
}
