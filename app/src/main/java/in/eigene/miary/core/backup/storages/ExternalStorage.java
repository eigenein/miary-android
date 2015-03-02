package in.eigene.miary.core.backup.storages;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * External backup storage (memory card).
 */
public class ExternalStorage extends Storage {

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
        if (AndroidVersion.isHoneycombMr1() && uiThread) {
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
