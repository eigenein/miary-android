package in.eigene.miary.core.backup.storages;

import android.os.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

/**
 * External backup storage (memory card).
 */
public class ExternalBackupStorage extends BackupStorage {

    @Override
    public boolean checkReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Override
    public OutputStream getOutputStream(final String name) {
        final File downloadsPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        final File file = new File(downloadsPath, name);
        try {
            return new FileOutputStream(file);
        } catch (final FileNotFoundException e) {
            InternalRuntimeException.throwForException("Cound not create output stream.", e);
            return null;
        }
    }
}
