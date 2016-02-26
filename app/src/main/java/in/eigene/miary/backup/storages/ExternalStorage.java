package in.eigene.miary.backup.storages;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.eigene.miary.backup.BackupOutput;
import in.eigene.miary.backup.Storage;
import in.eigene.miary.exceptions.InternalRuntimeException;
import in.eigene.miary.helpers.Tracking;

/**
 * External backup storage (memory card).
 */
public class ExternalStorage extends Storage {

    @SuppressLint("SimpleDateFormat")
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static final File DOWNLOADS = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    @Override
    public boolean checkReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Override
    public OutputStream getOutputStream(final String name) {
        final File file = new File(DOWNLOADS, name);
        if (!DOWNLOADS.mkdirs() && !DOWNLOADS.isDirectory()) {
            throw new InternalRuntimeException("Could not create Downloads directory.");
        }
        try {
            return new FileOutputStream(file);
        } catch (final FileNotFoundException e) {
            InternalRuntimeException.throwForException("Cound not create output stream.", e);
            return null;
        }
    }

    @Override
    public void finish(final Context context, boolean uiThread, final BackupOutput output) {
        if (uiThread) {
            addCompletedBackup(context, output.getName(), output.getMimeType());
        }
    }

    @Override
    public String getOutputName(final String suffix) {
        return "Miary Backup " + DATE_FORMAT.format(new Date()) + suffix;
    }

    /**
     * Adds a completed backup into Downloads App.
     */
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
        Tracking.sendEvent(Tracking.Category.BACKUP, Tracking.Action.EXTERNAL, file.length());
    }

    public class Input extends Storage.Input {

        private final Context context;

        private final Uri uri;

        public Input(final Context context, final Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return context.getContentResolver().openInputStream(uri);
        }
    }
}
