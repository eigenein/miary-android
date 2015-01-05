package in.eigene.miary.sync;

import android.accounts.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.parse.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.helpers.*;

import java.util.*;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    public static final String ACCOUNT_TYPE = "miary.eigene.in";
    public static final String AUTHORITY = "in.eigene.miary.provider";

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
        if (ParseUser.getCurrentUser() == null) {
            Log.w(LOG_TAG, "No current user.");
            return;
        }
        // Initialize last sync date.
        final String lastSyncDateString = accountManager.getUserData(account, KEY_LAST_SYNC_TIME);
        final Date lastSyncDate = lastSyncDateString != null ? new Date(Long.parseLong(lastSyncDateString)) : null;
        Log.i(LOG_TAG, "Getting changes since " + lastSyncDate);
        // Obtain changes.
        final NoteMap localChanges = new NoteMap();
        final NoteMap remoteChanges = new NoteMap();
        if (!getChanges(localChanges, remoteChanges, lastSyncDate, new Date())) {
            syncResult.stats.numIoExceptions += 1;
            ParseHelper.trackEvent("syncFailed", "reason", "getChanges");
            return;
        }
        // Remove outdated changes.
        Log.i(LOG_TAG, "Filtering remote changes.");
        filterChanges(localChanges, Note.KEY_LOCAL_UPDATED_AT, remoteChanges, Note.KEY_REMOTE_UPDATED_AT);
        Log.i(LOG_TAG, "Filtering local changes.");
        filterChanges(remoteChanges, Note.KEY_REMOTE_UPDATED_AT, localChanges, Note.KEY_LOCAL_UPDATED_AT);
        // Saving changes.
        try {
            saveRemoteChanges(remoteChanges);
            saveLocalChanges(localChanges);
        } catch (final ParseException e) {
            Log.e(LOG_TAG, "Failed to save changes.", e);
            syncResult.stats.numIoExceptions += 1;
            ParseHelper.trackEvent("syncFailed", "reason", "saveChanges");
            return;
        }
        syncResult.stats.numUpdates = localChanges.size() + remoteChanges.size();
        // Update last sync time.
        // accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncTime));
        Log.i(LOG_TAG, "Finished.");
        ParseAnalytics.trackEventInBackground("syncSuccess");
    }

    /**
     * Gets local and remote changes.
     */
    private static boolean getChanges(
            final NoteMap localChanges,
            final NoteMap remoteChanges,
            final Date lastSyncDate,
            final Date currentSyncDate
    ) {
        try {
            localChanges.fillUp(getLocalChanges(lastSyncDate, currentSyncDate));
            remoteChanges.fillUp(getRemoteChanges(lastSyncDate, currentSyncDate));
        } catch (final ParseException e) {
            Log.e(LOG_TAG, "Failed to obtain changes.", e);
            return false;
        }
        Log.i(LOG_TAG, localChanges.size() + " local changes");
        Log.i(LOG_TAG, remoteChanges.size() + " remote changes");
        return true;
    }

    /**
     * Gets common changes query part.
     */
    private static ParseQuery<Note> getChangesQuery() {
        return ParseQuery.getQuery(Note.class);
    }

    private static List<Note> getLocalChanges(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Getting local changes.");
        return whereUpdateAtBetween(
                getChangesQuery().fromLocalDatastore(),
                lastSyncTime,
                currentSyncTime,
                Note.KEY_LOCAL_UPDATED_AT
        ).find();
    }

    private static List<Note> getRemoteChanges(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Getting remote changes.");
        return whereUpdateAtBetween(
                getChangesQuery(),
                lastSyncTime,
                currentSyncTime,
                Note.KEY_REMOTE_UPDATED_AT
        ).find();
    }

    /**
     * Adds conditions on updatedAt field.
     */
    private static ParseQuery<Note> whereUpdateAtBetween(
            final ParseQuery<Note> query,
            final Date lastSyncDate,
            final Date currentSyncDate,
            final String fieldName
    ) {
        if (lastSyncDate != null) {
            query.whereGreaterThanOrEqualTo(fieldName, lastSyncDate);
        }
        return query.whereLessThanOrEqualTo(fieldName, currentSyncDate);
    }

    /**
     * Filters outdated changes from the target.
     */
    private static void filterChanges(
            final NoteMap source,
            final String sourceFieldName,
            final NoteMap target,
            final String targetFieldName
    ) {
        for (final NoteMap.Entry<UUID, Note> entry : source.entrySet()) {
            final Note targetNote = target.get(entry.getKey());
            if (targetNote == null) {
                continue;
            }
            if (targetNote.getDate(targetFieldName).before(entry.getValue().getDate(sourceFieldName))) {
                Log.i(LOG_TAG, "Remove outdated change " + entry.getKey());
                target.remove(entry.getKey());
            }
        }
    }

    private void saveRemoteChanges(final NoteMap remoteChanges) throws ParseException {
        Log.i(LOG_TAG, "Saving remote changes.");
        final List<Note> remoteNotes = new ArrayList<Note>(remoteChanges.values());
        for (final Note note : remoteNotes) {
            note.setLocalUpdatedAt(note.getUpdatedAt());
        }
        Note.pinAll(remoteNotes);
    }

    private void saveLocalChanges(final NoteMap localChanges) throws ParseException {
        Log.i(LOG_TAG, "Saving local changes.");
        final ParseACL defaultACL = new ParseACL(ParseUser.getCurrentUser());
        final List<Note> localNotes = new ArrayList<Note>(localChanges.values());
        for (final Note note : localNotes) {
            note.setACL(defaultACL);
        }
        Note.saveAll(localNotes);
        Note.pinAll(localNotes); // save objectId
    }

    /**
     * Maps UUID to note.
     */
    private static class NoteMap extends HashMap<UUID, Note> {

        public void fillUp(final List<Note> notes) {
            for (final Note note : notes) {
                put(note.getUuid(), note);
            }
        }
    }
}
