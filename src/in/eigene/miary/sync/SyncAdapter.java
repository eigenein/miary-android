package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.parse.*;
import in.eigene.miary.core.classes.*;

import java.util.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT_TYPE = "miary.eigene.in";
    public static final String AUTHORITY = "in.eigene.miary.provider";

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
        Log.i(LOG_TAG, "Starting.");
        // Initialize times.
        final long currentSyncTime = new Date().getTime();
        final long lastSyncTime = safeParseLong(accountManager.getUserData(account, KEY_LAST_SYNC_TIME));
        // Obtain changes.
        try {
            final NoteMap localChanges = NoteMap.from(getLocalChanges());
            final NoteMap remoteChanges = NoteMap.from(getRemoteChanges());
        } catch (final ParseException e) {
            Log.e(LOG_TAG, "Failed to obtain changes.", e);
            syncResult.stats.numIoExceptions += 1;
            return;
        }
        // Update last sync time.
        // accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncTime));
        Log.i(LOG_TAG, "Finished.");
    }

    private static List<Note> getLocalChanges() throws ParseException {
        Log.i(LOG_TAG, "Getting local changes.");
        return ParseQuery.getQuery(Note.class).fromLocalDatastore().find();
    }

    private static List<Note> getRemoteChanges() throws ParseException {
        Log.i(LOG_TAG, "Getting remote changes.");
        return ParseQuery.getQuery(Note.class).find();
    }

    private static long safeParseLong(final String string) {
        return string != null ? Long.parseLong(string) : 0L;
    }

    /**
     * Maps UUID to note.
     */
    private static class NoteMap extends HashMap<UUID, Note> {

        public static NoteMap from(final List<Note> notes) {
            final NoteMap noteMap = new NoteMap();
            for (final Note note : notes) {
                noteMap.put(note.getUuid(), note);
            }
            return noteMap;
        }

        private NoteMap() {
            // Do nothing.
        }
    }
}
