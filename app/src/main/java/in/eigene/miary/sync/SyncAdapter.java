package in.eigene.miary.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.util.Date;

import in.eigene.miary.helpers.ParseHelper;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    public static final String ACCOUNT_TYPE = "miary.eigene.in";
    public static final String AUTHORITY = "in.eigene.miary.provider";
    public static final String SYNC_FINISHED_EVENT_NAME = "in.eigene.miary.SYNC_FINISHED";

    private static final String KEY_LAST_SYNC_TIME = "lastSyncTime";

   private final AccountManager accountManager;

    public SyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
        accountManager = AccountManager.get(context);
    }

    public SyncAdapter(final Context context, final boolean autoInitialize, final boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        accountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult) {
        try {
            onPerformSync(account, syncResult);
        } finally {
            getContext().sendBroadcast(new Intent(SYNC_FINISHED_EVENT_NAME));
        }
    }

    private void onPerformSync(final Account account, final SyncResult syncResult) {
        Log.i(LOG_TAG, "Starting.");
        if (ParseUser.getCurrentUser() == null) {
            Log.w(LOG_TAG, "No current user.");
            return;
        }
        // Initialize last sync date.
        final String lastSyncDateString = accountManager.getUserData(account, KEY_LAST_SYNC_TIME);
        final Date lastSyncDate = (lastSyncDateString != null) ? new Date(Long.parseLong(lastSyncDateString)) : null;
        final Date currentSyncDate = new Date();
        // TODO.
        // Update last sync time.
        accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncDate.getTime()));
        Log.i(LOG_TAG, "Finished.");
        ParseAnalytics.trackEventInBackground("syncSuccess");
    }

    private static void handleIoException(final String message, final String reason, final SyncResult syncResult, final Throwable e) {
        Log.e(LOG_TAG, message, e);
        syncResult.stats.numIoExceptions += 1;
        ParseHelper.trackEvent("syncFailed", "reason", reason);
    }
}
