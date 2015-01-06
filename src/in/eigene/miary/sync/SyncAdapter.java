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
        final Date lastSyncDate = lastSyncDateString != null ? new Date(Long.parseLong(lastSyncDateString)) : null;
        Log.i(LOG_TAG, "Getting notes since " + lastSyncDate);
        // Obtain changes.
        final NoteMap<LocalNote> newLocalNotes = new NoteMap<LocalNote>();
        final NoteMap<RemoteNote> newRemoteNotes = new NoteMap<RemoteNote>();
        if (!queryNotes(newLocalNotes, newRemoteNotes, lastSyncDate, new Date())) {
            syncResult.stats.numIoExceptions += 1;
            ParseHelper.trackEvent("syncFailed", "reason", "queryNotes");
            return;
        }
        // Merge local and remote notes.
        Log.i(LOG_TAG, "Merging notes.");
        final List<LocalNote> oldLocalNotes = new ArrayList<LocalNote>();
        final List<RemoteNote> oldRemoteNotes = new ArrayList<RemoteNote>();
        mergeNotes(newLocalNotes, oldLocalNotes, newRemoteNotes, oldRemoteNotes);
        // Save merged notes.
        try {
            saveLocalNotes(newLocalNotes);
            deleteRemoteNotes(oldRemoteNotes);
            pinRemoteNotes(newRemoteNotes);
            unpinLocalNotes(oldLocalNotes);
        } catch (final ParseException e) {
            Log.e(LOG_TAG, "Failed to save notes.", e);
            syncResult.stats.numIoExceptions += 1;
            ParseHelper.trackEvent("syncFailed", "reason", "saveNotes");
            return;
        }
        syncResult.stats.numUpdates = newLocalNotes.size() + newRemoteNotes.size();
        // Update last sync time.
        // accountManager.setUserData(account, KEY_LAST_SYNC_TIME, Long.toString(currentSyncTime));
        Log.i(LOG_TAG, "Finished.");
        ParseAnalytics.trackEventInBackground("syncSuccess");
    }

    /**
     * Gets local and remote notes.
     */
    private static boolean queryNotes(
            final NoteMap<LocalNote> localNotes,
            final NoteMap<RemoteNote> remoteChanges,
            final Date lastSyncDate,
            final Date currentSyncDate
    ) {
        try {
            localNotes.fillUp(queryLocalNotes(lastSyncDate, currentSyncDate));
            remoteChanges.fillUp(queryRemoteNotes(lastSyncDate, currentSyncDate));
        } catch (final ParseException e) {
            Log.e(LOG_TAG, "Failed to obtain changes.", e);
            return false;
        }
        Log.i(LOG_TAG, localNotes.size() + " local notes");
        Log.i(LOG_TAG, remoteChanges.size() + " remote notes");
        return true;
    }

    private static List<LocalNote> queryLocalNotes(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Querying local notes.");
        final ParseQuery<LocalNote> oldNotesQuery = ParseQuery.getQuery(LocalNote.class)
                .whereDoesNotExist(LocalNote.KEY_LOCAL_UPDATED_AT);
        final ParseQuery<LocalNote> newNotesQuery = whereUpdateAtBetween(
                ParseQuery.getQuery(LocalNote.class),
                lastSyncTime,
                currentSyncTime,
                LocalNote.KEY_LOCAL_UPDATED_AT
        );
        final ArrayList<ParseQuery<LocalNote>> queries = new ArrayList<ParseQuery<LocalNote>>();
        queries.add(oldNotesQuery);
        queries.add(newNotesQuery);
        return ParseQuery.or(queries).fromLocalDatastore().find();
    }

    private static List<RemoteNote> queryRemoteNotes(final Date lastSyncTime, final Date currentSyncTime) throws ParseException {
        Log.i(LOG_TAG, "Querying remote notes.");
        return whereUpdateAtBetween(
                ParseQuery.getQuery(RemoteNote.class),
                lastSyncTime,
                currentSyncTime,
                RemoteNote.KEY_UPDATED_AT
        ).find();
    }

    /**
     * Adds conditions on updatedAt field.
     */
    private static <TNote extends ParseObject> ParseQuery<TNote> whereUpdateAtBetween(
            final ParseQuery<TNote> query,
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
     * Merges local and remote notes. All sync magic comes here.
     */
    private static void mergeNotes(
            final NoteMap<LocalNote> localNotes,
            final List<LocalNote> oldLocalNotes,
            final NoteMap<RemoteNote> remoteNotes,
            final List<RemoteNote> oldRemoteNotes
    ) {
        // Remove outdated remote notes.
        for (final NoteMap.Entry<UUID, LocalNote> localEntry : localNotes.entrySet()) {
            final LocalNote localNote = localEntry.getValue();
            // Fix old notes without localUpdatedAt field set.
            if (localNote.getLocalUpdatedAt() == null) {
                localNote.setLocalUpdatedAt(new Date(0L));
            }
            // Search matching remote note.
            final RemoteNote remoteNote = remoteNotes.get(localEntry.getKey());
            if (remoteNote != null) {
                if (remoteNote.getUpdatedAt().before(localNote.getLocalUpdatedAt())) {
                    // Remote note is outdated.
                    remoteNotes.remove(localEntry.getKey());
                    oldRemoteNotes.add(remoteNote);
                }
            }
        }
        // Remove outdated local notes.
        for (final NoteMap.Entry<UUID, RemoteNote> remoteEntry : remoteNotes.entrySet()) {
            final RemoteNote remoteNote = remoteEntry.getValue();
            // Search matching local note.
            final LocalNote localNote = localNotes.get(remoteEntry.getKey());
            if (localNote != null) {
                if (!localNote.getLocalUpdatedAt().after(remoteNote.getUpdatedAt())) {
                    // Local note is not newer than the remote one.
                    localNotes.remove(remoteEntry.getKey());
                    oldLocalNotes.add(localNote);
                }
            }
        }
    }

    private void pinRemoteNotes(final NoteMap<RemoteNote> remoteNotes) throws ParseException {
        Log.i(LOG_TAG, "Pinning remote notes locally: " + remoteNotes.size());
        LocalNote.pinAll(Util.map(remoteNotes.values(), new Function<RemoteNote, ParseObject>() {
            @Override
            public ParseObject apply(final RemoteNote remoteNote) {
                return remoteNote.toLocalNote();
            }
        }));
    }

    private void saveLocalNotes(final NoteMap<LocalNote> localNotes) throws ParseException {
        Log.i(LOG_TAG, "Saving local notes remotely: " + localNotes.size());
        final ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        LocalNote.saveAll(Util.map(localNotes.values(), new Function<LocalNote, ParseObject>() {
            @Override
            public ParseObject apply(final LocalNote localNote) {
                return RemoteNote.fromLocalNote(localNote, acl);
            }
        }));
    }

    private void unpinLocalNotes(final List<LocalNote> localNotes) throws ParseException {
        Log.i(LOG_TAG, "Unpinning old local notes: " + localNotes.size());
        LocalNote.unpinAll(localNotes);
    }

    private void deleteRemoteNotes(final List<RemoteNote> remoteNotes) throws ParseException {
        Log.i(LOG_TAG, "Deleting old remote notes: " + remoteNotes.size());
        RemoteNote.deleteAll(remoteNotes);
    }

    /**
     * Maps UUID to note.
     */
    private static class NoteMap<TNote extends Note> extends HashMap<UUID, TNote> {

        public void fillUp(final List<TNote> notes) {
            for (final TNote note : notes) {
                put(note.getUuid(), note);
            }
        }
    }
}
