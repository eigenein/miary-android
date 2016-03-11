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
        // TODO.
    }

    private static void handleIoException(final String message, final String reason, final SyncResult syncResult, final Throwable e) {
        Log.e(LOG_TAG, message, e);
        syncResult.stats.numIoExceptions += 1;
    }
}
