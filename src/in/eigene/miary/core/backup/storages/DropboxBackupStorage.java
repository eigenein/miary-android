package in.eigene.miary.core.backup.storages;

import android.content.*;
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

    private final DropboxAPI<AndroidAuthSession> api;

    /**
     * TODO: temporary file on disk because of possibly large size.
     */
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

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
    public OutputStream getOutputStream(final String name) {
        return stream;
    }

    @Override
    public void finish(final Context context, boolean uiThread, final String name, final String mimeType) {
        if (uiThread) {
            return;
        }
        ParseHelper.trackEvent("backupToDropbox", "length", Integer.toString(stream.size()));
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
        try {
            api.putFileOverwrite(name, inputStream, stream.size(), null);
        } catch (final DropboxException e) {
            InternalRuntimeException.throwForException("Putting file failed.", e);
        }
    }
}
