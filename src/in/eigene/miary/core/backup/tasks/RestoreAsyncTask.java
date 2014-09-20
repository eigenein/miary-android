package in.eigene.miary.core.backup.tasks;

import android.content.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

/**
 * Used to restore a backup.
 */
public class RestoreAsyncTask extends BaseAsyncTask {

    private final Storage.Input storageInput;
    private final RestoreInput.Factory inputFactory;

    public RestoreAsyncTask(
            final Context context,
            final Storage.Input storageInput,
            final RestoreInput.Factory inputFactory) {
        super(context);
        this.storageInput = storageInput;
        this.inputFactory = inputFactory;
    }

    @Override
    protected Result doInBackground(final Void... params) {
        if (!storageInput.checkReady()) {
            return Result.STORAGE_NOT_READY;
        }
        try {
            final Result result = restore();
            publishProgress(Progress.FINISHING);
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

    private Result restore() throws IOException {
        final InputStream inputStream = storageInput.getInputStream();
        if (inputStream == null) {
            return Result.NOT_FOUND;
        }
        final RestoreInput input = inputFactory.createInput(inputStream);
        // TODO.
        return Result.OK;
    }
}
