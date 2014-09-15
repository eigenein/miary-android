package in.eigene.miary.core.backup.storages;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;

/**
 * External backup storage (memory card).
 */
public class ExternalBackupStorage extends BackupStorage {

    private static final File DOWNLOADS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    @Override
    public boolean checkReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Override
    public OutputStream getOutputStream(final String name) {
        final File file = new File(DOWNLOADS, name);
        try {
            return new FileOutputStream(file);
        } catch (final FileNotFoundException e) {
            InternalRuntimeException.throwForException("Cound not create output stream.", e);
            return null;
        }
    }

    @Override
    public void finish(final Context context, final String name, final String mimeType) {
        if (AndroidVersion.isHoneycombMr1()) {
            addCompletedBackup(context, name, mimeType);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void addCompletedBackup(final Context context, final String name, String mimeType) {
        final File file = new File(DOWNLOADS, name);
        if (!file.exists()) {
            return;
        }
        final DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.addCompletedDownload(
                name,
                "Miary Backup",
                true,
                mimeType,
                file.getAbsolutePath(),
                file.length(),
                true);
    }
}
