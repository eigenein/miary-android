package in.eigene.miary.core.backup.storages;

import android.content.*;
import in.eigene.miary.core.backup.*;

import java.io.*;

/**
 * Google Drive backup storage.
 */
public class DriveBackupStorage extends BackupStorage {

    @Override
    public boolean checkReady() {
        return false;
    }

    @Override
    public OutputStream getOutputStream(final String name) {
        // TODO.
        return null;
    }

    @Override
    public void finish(final Context context, final String name) {
        // Do nothing.
    }
}
