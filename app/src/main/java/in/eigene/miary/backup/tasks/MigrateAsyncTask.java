package in.eigene.miary.backup.tasks;


import android.content.Context;

import in.eigene.miary.R;
import in.eigene.miary.backup.inputs.LocalDatastoreRestoreInput;
import in.eigene.miary.backup.storages.LocalDatastoreStorage;

/**
 * Migrates notes from Parse Local Datastore.
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
}
