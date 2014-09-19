package in.eigene.miary.core.backup.tasks;

import android.content.*;
import in.eigene.miary.*;
import in.eigene.miary.core.backup.*;

/**
 * Used to restore a backup.
 */
public class RestoreAsyncTask extends BaseBackupAsyncTask {

    public RestoreAsyncTask(final Context context) {
        super(context);
    }

    @Override
    protected BackupResult doInBackground(final Void... params) {
        // TODO.
        return null;
    }

    @Override
    protected void onPostExecute(final BackupResult result) {
        super.onPostExecute(result);

        if (result == BackupResult.OK) {
            // TODO.
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return R.string.backup_message_restoring;
    }
}
