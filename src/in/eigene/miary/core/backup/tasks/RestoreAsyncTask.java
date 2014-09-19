package in.eigene.miary.core.backup.tasks;

import android.content.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

/**
 * Used to restore a backup.
 */
public class RestoreAsyncTask extends BaseAsyncTask {

    private final RestoreInputFactory inputFactory;

    public RestoreAsyncTask(
            final Context context,
            final Storage storage,
            final RestoreInputFactory inputFactory) {
        super(context, storage);
        this.inputFactory = inputFactory;
    }

    @Override
    protected Result doInBackground(final Void... params) {
        if (!storage.checkReady()) {
            return Result.STORAGE_NOT_READY;
        }
        try {
            final Result result = Result.OK; // TODO.
            return result;
        } catch (final Exception e) {
            InternalRuntimeException.throwForException("Restore failed.", e);
            return Result.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(final Result result) {
        super.onPostExecute(result);

        if (result == Result.OK) {
            Toast.makeText(
                    context,
                    String.format(context.getString(in.eigene.miary.R.string.toast_restore_finished), noteCount),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return R.string.backup_message_restoring;
    }
}
