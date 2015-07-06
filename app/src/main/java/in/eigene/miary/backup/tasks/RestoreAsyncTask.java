package in.eigene.miary.backup.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import in.eigene.miary.R;
import in.eigene.miary.backup.Progress;
import in.eigene.miary.backup.RestoreInput;
import in.eigene.miary.backup.Result;
import in.eigene.miary.backup.Storage;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.exceptions.InternalRuntimeException;

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
            sleepCheat();
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
                    String.format(context.getString(getFinishedMessageResourceId()), noteCount),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return R.string.backup_message_restoring;
    }

    protected int getFinishedMessageResourceId() {
        return R.string.toast_restore_finished;
    }

    private Result restore() throws IOException {
        final InputStream inputStream = storageInput.getInputStream();
        if (inputStream == null) {
            return Result.NOT_FOUND;
        }
        // Create input and read note count.
        final RestoreInput input = inputFactory.createInput(inputStream);
        noteCount = input.start();
        // Publish initial progress.
        final Progress progress = new Progress(Progress.State.PROGRESS, 0);
        publishProgress(progress);
        // Restore notes.
        final Date restoreDate = new Date();
        final ContentResolver contentResolver = context.getContentResolver();
        for (int i = 0; i < noteCount; i++) {
            final Note note = input.read().setUpdatedDate(restoreDate);
            if (note.update(contentResolver) == 0) {
                // The note is new.
                note.insert(contentResolver);
            }
            // Publish progress.
            progress.incrementProgress();
            publishProgress(progress);
        }
        // Finish.
        input.finish();
        return Result.OK;
    }
}
