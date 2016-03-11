package in.eigene.miary.backup.storages;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.eigene.miary.backup.BackupOutput;
import in.eigene.miary.backup.Storage;
import in.eigene.miary.exceptions.InternalRuntimeException;
import in.eigene.miary.helpers.Tracking;

/**
 * Dropbox backup storage.
 */
public class DropboxStorage extends Storage {

    private static final String LOG_TAG = DropboxStorage.class.getSimpleName();

    private final DropboxAPI<AndroidAuthSession> api;

    private File tempFile;

    public DropboxStorage(final DropboxAPI<AndroidAuthSession> api) {
        this.api = api;
    }

    @Override
    public boolean checkReady() {
        return api.getSession().isLinked();
    }

    @Override
    public OutputStream getOutputStream(final String name) throws IOException {
        assert tempFile == null;

        tempFile = File.createTempFile(name, null);
        tempFile.deleteOnExit();
        Log.i(LOG_TAG, "Temporary file: " + tempFile.getAbsolutePath());
        return new FileOutputStream(tempFile);
    }

    @Override
    public void finish(final Context context, boolean uiThread, final BackupOutput output) {
        if (uiThread) {
            return;
        }
        Tracking.finishDropboxBackup(tempFile.length());
        try {
            api.putFileOverwrite(output.getName(), new FileInputStream(tempFile), tempFile.length(), null);
        } catch (final DropboxException e) {
            InternalRuntimeException.throwForException("Putting file failed.", e);
        } catch (final FileNotFoundException e) {
            InternalRuntimeException.throwForException("Could not find temporary file.", e);
        }
    }

    @Override
    public String getOutputName(final String suffix) {
        return "Miary Backup" + suffix;
    }

    public class Input extends Storage.Input {

        private final String suffix;

        public Input(final String suffix) {
            this.suffix = suffix;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            assert tempFile == null;

            final String name = getOutputName(suffix);
            tempFile = File.createTempFile(name, null);
            tempFile.deleteOnExit();
            try {
                final OutputStream outputStream = new FileOutputStream(tempFile);
                api.getFile(name, null, outputStream, null);
                outputStream.close();
                return new FileInputStream(tempFile);
            } catch (final DropboxServerException e) {
                if (e.error == DropboxServerException._404_NOT_FOUND) {
                    return null;
                }
                InternalRuntimeException.throwForException("Server exception.", e);
                return null;
            } catch (final DropboxException e) {
                InternalRuntimeException.throwForException("Getting file failed.", e);
                return null;
            }
        }
    }
}
