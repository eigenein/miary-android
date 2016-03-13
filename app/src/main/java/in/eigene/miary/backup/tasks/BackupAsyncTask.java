package in.eigene.miary.backup.tasks;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.io.IOException;

import in.eigene.miary.backup.BackupOutput;
import in.eigene.miary.backup.Progress;
import in.eigene.miary.backup.Result;
import in.eigene.miary.backup.Storage;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.exceptions.InternalRuntimeException;

/**
 * Used to backup notes.
 */
public class BackupAsyncTask extends BaseAsyncTask {

    private final Storage storage;
    private final BackupOutput.Factory outputFactory;

    private BackupOutput output;

    public BackupAsyncTask(
            final Context context,
            final Storage storage,
            final BackupOutput.Factory outputFactory) {
        super(context);
        this.storage = storage;
        this.outputFactory = outputFactory;
    }

    @Override
    protected Result doInBackground(final Void... params) {
        if (!storage.checkReady()) {
            return Result.STORAGE_NOT_READY;
        }
        try {
            output = outputFactory.createOutput(storage);
            final Result result = backup();
            sleepCheat();
            publishProgress(Progress.FINISHING);
            storage.finish(context, false, output);
            return result;
        } catch (final IOException e) {
            Tracking.error("Backup failed.", e);
            return Result.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(final Result result) {
        super.onPostExecute(result);

        if (result == Result.OK) {
            Toast.makeText(
                    context,
                    String.format(context.getString(in.eigene.miary.R.string.toast_backup_finished), noteCount, output.getName()),
                    Toast.LENGTH_LONG
            ).show();
            storage.finish(context, true, output);
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return in.eigene.miary.R.string.backup_message_creating;
    }

    private Result backup() throws IOException {
        // Query notes.
        final Cursor cursor = context.getContentResolver().query(
                Note.Contract.CONTENT_URI,
                Note.PROJECTION,
                null, null,
                Note.SortOrder.OLDEST_FIRST.getSortOrder());
        noteCount = cursor.getCount();
        if (noteCount == 0) {
            return Result.NOTHING_TO_BACKUP;
        }
        // Write notes.
        publishProgress(new Progress(Progress.State.PROGRESS, 0));
        output.start(noteCount);
        int progress = 0;
        while (cursor.moveToNext()) {
            if (isCancelled()) {
                break;
            }
            output.write(Note.getByCursor(cursor));
            progress += 1;
            publishProgress(new Progress(Progress.State.PROGRESS, progress));
        }
        output.finish();
        return Result.OK;
    }
}
