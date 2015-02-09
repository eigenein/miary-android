package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(final Context context, final boolean autoInitialize, final boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(
            final Account account,
            final Bundle extras,
            final String authority,
            final ContentProviderClient provider,
            final SyncResult syncResult
    ) {
        // TODO.
    }
}
