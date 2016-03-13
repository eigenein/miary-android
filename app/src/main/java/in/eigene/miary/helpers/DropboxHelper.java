package in.eigene.miary.helpers;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import in.eigene.miary.helpers.lang.Consumer;

public class DropboxHelper {

    public static DropboxAPI<AndroidAuthSession> createApi(final Context context) {
        final AppKeyPair appKeys = new AppKeyPair("cvklgjd9ykfi561", "2sxel7scug156mz");
        final AccountManager accountManager = AccountManager.get(context);
        final String accessToken = accountManager.getUserData(
                AccountHelper.getAccount(accountManager), AccountHelper.KEY_DROPBOX_ACCESS_TOKEN);
        return new DropboxAPI<>(new AndroidAuthSession(appKeys, accessToken));
    }

    public static void getAccountInfo(
            final Activity activity,
            final DropboxAPI<AndroidAuthSession> api,
            final Consumer<DropboxAPI.Account> accountConsumer
    ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final DropboxAPI.Account accountInfo = api.accountInfo();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            accountConsumer.accept(accountInfo);
                        }
                    });
                } catch (final DropboxException e) {
                    // Just ignore the error.
                    Tracking.error("Failed to get Dropbox account info.", e);
                }
            }
        });
    }
}
