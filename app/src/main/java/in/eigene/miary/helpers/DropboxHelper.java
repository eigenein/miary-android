package in.eigene.miary.helpers;

import android.accounts.AccountManager;
import android.content.Context;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxHelper {

    public static DropboxAPI<AndroidAuthSession> createApi(final Context context) {
        final AppKeyPair appKeys = new AppKeyPair("cvklgjd9ykfi561", "2sxel7scug156mz");
        final AccountManager accountManager = AccountManager.get(context);
        final String accessToken = accountManager.getUserData(
                AccountHelper.getAccount(accountManager), AccountHelper.KEY_DROPBOX_ACCESS_TOKEN);
        return new DropboxAPI<>(new AndroidAuthSession(appKeys, accessToken));
    }
}
