package in.eigene.miary.core.backup;

import java.io.*;

/**
 * Represents backup storage.
 */
public abstract class BackupStorage {

    /**
     * Checks if the storage is ready.
     */
    public abstract boolean checkReady();

    public abstract OutputStream getOutputStream(final String name);
}
