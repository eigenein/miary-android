package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;
import in.eigene.miary.core.classes.*;

import java.util.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT_TYPE = "miary.eigene.in";

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

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
            final SyncResult syncResult
    ) {
        // Initialize times.
        final long currentSyncTime = new Date().getTime();
        final long lastSyncTime = safeParseLong(accountManager.getUserData(account, KEY_LAST_SYNC_TIME));
        // Update last sync time.
        // accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncTime));
    }

    private static List<Note> getLocalChanges() {
        return null;
    }

    private static List<Note> getServerChanges() {
        return null;
    }

    private static long safeParseLong(final String string) {
        return string != null ? Long.parseLong(string) : 0L;
    }
}
