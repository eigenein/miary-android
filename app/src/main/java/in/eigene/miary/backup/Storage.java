package in.eigene.miary.backup;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents backup storage.
 */
public abstract class Storage {

    /**
     * Checks if the storage is ready.
     */
    public abstract boolean checkReady(final Context context);

    /**
     * Gets new backup name.
     */
    public abstract String getOutputName(final String suffix);

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

    public abstract class Input {

        public boolean checkReady(final Context context) {
            return Storage.this.checkReady(context);
        }

        public abstract InputStream getInputStream() throws IOException;
    }
}
