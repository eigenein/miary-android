package in.eigene.miary.backup.tasks;


import android.content.Context;
import android.preference.PreferenceManager;

import in.eigene.miary.R;
import in.eigene.miary.activities.FeedActivity;
import in.eigene.miary.backup.Result;
import in.eigene.miary.backup.inputs.LocalDatastoreRestoreInput;
import in.eigene.miary.backup.storages.LocalDatastoreStorage;
import in.eigene.miary.helpers.Tracking;

/**
 * Migrates notes from Parse Local Datastore. To be removed.
 */
public class MigrateAsyncTask extends RestoreAsyncTask {

    public MigrateAsyncTask(final Context context) {
        super(
                context,
                new LocalDatastoreStorage().new Input(),
                new LocalDatastoreRestoreInput.Factory()
        );
    }

    @Override
    protected int getProgressMessageResourceId() {
        return R.string.backup_message_migrating;
    }

    @Override
    protected int getFinishedMessageResourceId() {
        return R.string.toast_migrate_finished;
    }

    @Override
    protected void onPostExecute(final Result result) {
        super.onPostExecute(result);

        if (result == Result.OK) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit().putBoolean(FeedActivity.KEY_NOTES_MIGRATED, true).apply();
        }
        Tracking.sendEvent(Tracking.Category.BACKUP, Tracking.Action.MIGRATE, noteCount, result.toString());
    }
}