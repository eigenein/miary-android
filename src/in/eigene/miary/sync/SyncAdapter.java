package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.parse.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.lang.*;

import java.util.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    public static final String ACCOUNT_TYPE = "miary.eigene.in";
    public static final String AUTHORITY = "in.eigene.miary.provider";
    public static final String SYNC_FINISHED_EVENT_NAME = "in.eigene.miary.events.SYNC_FINISHED";

    private static final String KEY_LAST_SYNC_TIME = "lastSyncTime";

    private static final Function<Note, Object> noteToMapFunction = new Function<Note, Object>() {
        @Override
        public Object apply(final Note note) {
            return note.toSyncMap();
        }
    };

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
        // Obtain changed notes.
        Log.i(LOG_TAG, "Querying notes since " + lastSyncDate);
        final List<Note> notes;
        try {
            notes = queryNotes(lastSyncDate, currentSyncDate);
        } catch (final ParseException e) {
            handleIoException("Could not query updated notes.", "queryNotes", syncResult, e);
            return;
        }
        // Send them to server.
        Log.i(LOG_TAG, "Calling sync function.");
        final HashMap<String, Object> syncParams = new HashMap<String, Object>();
        syncParams.put("lastSyncTime", (lastSyncDate != null) ? lastSyncDate.getTime() : 0);
        syncParams.put("currentSyncTime", currentSyncDate.getTime());
        syncParams.put("notes", Util.map(notes, noteToMapFunction));
        try {
            final Object response = ParseCloud.callFunction("sync", syncParams);
        } catch (final ParseException e) {
            handleIoException("Could not call sync function.", "syncFunction", syncResult, e);
            return;
        }
        // TODO: response.
        // Update last sync time.
        // accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncDate.getTime()));
        Log.i(LOG_TAG, "Finished.");
        ParseAnalytics.trackEventInBackground("syncSuccess");
    }

    /**
     * Gets updated local notes.
     */
    private static List<Note> queryNotes(
            final Date lastSyncDate,
            final Date currentSyncDate
    ) throws ParseException {
        final ParseQuery<Note> oldNotesQuery = ParseQuery.getQuery(Note.class)
                .whereDoesNotExist(Note.KEY_LOCAL_UPDATED_AT);
        final ParseQuery<Note> newNotesQuery = ParseQuery.getQuery(Note.class)
                .whereLessThanOrEqualTo(Note.KEY_LOCAL_UPDATED_AT, currentSyncDate);
        if (lastSyncDate != null) {
            newNotesQuery.whereGreaterThanOrEqualTo(Note.KEY_LOCAL_UPDATED_AT, lastSyncDate);
        }
        final ArrayList<ParseQuery<Note>> queries = new ArrayList<ParseQuery<Note>>();
        queries.add(oldNotesQuery);
        queries.add(newNotesQuery);
        return ParseQuery.or(queries).fromLocalDatastore().find();
    }

    private static void handleIoException(final String message, final String reason, final SyncResult syncResult, final Throwable e) {
        Log.e(LOG_TAG, message, e);
        syncResult.stats.numIoExceptions += 1;
        ParseHelper.trackEvent("syncFailed", "reason", reason);
    }
}
