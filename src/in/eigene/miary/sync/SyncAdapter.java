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
        Log.i(LOG_TAG, "Merging changes.");
        mergeChanges(localChanges, remoteChanges);
        // Saving changes.
        try {
            pinRemoteChanges(remoteChanges);
            saveAndPinLocalChanges(localChanges);
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
            localChanges.fillUp(queryLocalChanges(lastSyncDate, currentSyncDate));
            remoteChanges.fillUp(queryRemoteChanges(lastSyncDate, currentSyncDate));
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
    private static ParseQuery<Note> getQueryPrefix() {
        return ParseQuery.getQuery(Note.class);
    }

    private static List<Note> queryLocalChanges(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Querying local changes.");
        final ParseQuery<Note> oldNotesQuery = getQueryPrefix().fromLocalDatastore()
                .whereDoesNotExist(Note.KEY_LOCAL_UPDATED_AT);
        final ParseQuery<Note> newNotesQuery = whereUpdateAtBetween(
                getQueryPrefix().fromLocalDatastore(),
                lastSyncTime,
                currentSyncTime,
                Note.KEY_LOCAL_UPDATED_AT
        );
        final ArrayList<ParseQuery<Note>> queries = new ArrayList<ParseQuery<Note>>();
        queries.add(oldNotesQuery);
        queries.add(newNotesQuery);
        return ParseQuery.or(queries).find();
    }

    private static List<Note> queryRemoteChanges(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Querying remote changes.");
        return whereUpdateAtBetween(
                getQueryPrefix(),
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
     * Merges local and remote changes. All sync magic comes here.
     */
    private static void mergeChanges(final NoteMap localChanges, final NoteMap remoteChanges) {
        final ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        // Remove outdated remote changes.
        for (final NoteMap.Entry<UUID, Note> localEntry : localChanges.entrySet()) {
            final Note localNote = localEntry.getValue();
            // Fix ACL.
            localNote.setACL(acl);
            // Fix old notes without localUpdatedAt field set.
            if (localNote.getLocalUpdatedAt() == null) {
                localNote.setLocalUpdatedAt(new Date(0L));
            }
            // Search matching remote change.
            final Note remoteNote = remoteChanges.get(localEntry.getKey());
            if (remoteNote != null) {
                if (remoteNote.getRemoteUpdatedAt().before(localNote.getLocalUpdatedAt())) {
                    // Remote change is outdated.
                    remoteChanges.remove(localEntry.getKey());
                }
            }
        }
        // Remove outdated local changes.
        for (final NoteMap.Entry<UUID, Note> remoteEntry : remoteChanges.entrySet()) {
            final Note remoteNote = remoteEntry.getValue();
            // Set remote update date as local update date.
            remoteNote.setLocalUpdatedAt(remoteNote.getRemoteUpdatedAt());
            // Search matching local change.
            final Note localNote = localChanges.get(remoteEntry.getKey());
            if (localNote != null) {
                if (localNote.getLocalUpdatedAt().before(remoteNote.getRemoteUpdatedAt())) {
                    // Local change is outdated.
                    localChanges.remove(remoteEntry.getKey());
                }
            }
        }
    }

    private void pinRemoteChanges(final NoteMap remoteChanges) throws ParseException {
        Log.i(LOG_TAG, "Saving remote changes locally.");
        Note.pinAll(new ArrayList<Note>(remoteChanges.values()));
    }

    private void saveAndPinLocalChanges(final NoteMap localChanges) throws ParseException {
        Log.i(LOG_TAG, "Saving local changes remotely.");

        final List<Note> localNotes = new ArrayList<Note>(localChanges.values());
        Note.saveAll(localNotes); // upload
        Note.pinAll(localNotes); // save possibly updated objectId and localUpdatedAt
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
