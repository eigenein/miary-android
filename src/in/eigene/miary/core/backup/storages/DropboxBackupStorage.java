package in.eigene.miary.core.backup.storages;

import android.content.*;
import android.util.*;
import com.dropbox.client2.*;
import com.dropbox.client2.android.*;
import com.dropbox.client2.exception.*;
import com.dropbox.client2.session.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;

/**
 * Google Drive backup storage.
 */
public class DropboxBackupStorage extends BackupStorage {

    private static final String LOG_TAG = DropboxBackupStorage.class.getSimpleName();

    private final DropboxAPI<AndroidAuthSession> api;

    private File tempFile;

    public DropboxBackupStorage() {
        final AppKeyPair appKeys = new AppKeyPair("cvklgjd9ykfi561", "2sxel7scug156mz");
        final AndroidAuthSession session = new AndroidAuthSession(appKeys);
        api = new DropboxAPI<AndroidAuthSession>(session);
    }

    public void authenticate(final Context context) {
        api.getSession().startOAuth2Authentication(context);
    }

    public void finishAuthentication() {
        if (api.getSession().authenticationSuccessful()) {
            try {
                api.getSession().finishAuthentication();
            } catch (final IllegalStateException e) {
                InternalRuntimeException.throwForException("Authentication failure.", e);
            }
        }
    }

    @Override
    public boolean includeDate() {
        return false;
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
    public void finish(final Context context, boolean uiThread, final String name, final String mimeType) {
        if (uiThread) {
            return;
        }
        ParseHelper.trackEvent("backupToDropbox", "length", Long.toString(tempFile.length()));
        try {
            api.putFileOverwrite(name, new FileInputStream(tempFile), tempFile.length(), null);
        } catch (final DropboxException e) {
            InternalRuntimeException.throwForException("Putting file failed.", e);
        } catch (final FileNotFoundException e) {
            InternalRuntimeException.throwForException("Could not find temporary file.", e);
        }
    }
}
