package in.eigene.miary.core.backup.tasks;

import android.content.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.exceptions.*;

import java.io.*;
import java.util.*;

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
        } catch (final Exception e) {
            InternalRuntimeException.throwForException("Backup failed.", e);
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

    private Result backup() throws ParseException, IOException {
        noteCount = ParseQuery.getQuery(LocalNote.class).fromLocalDatastore().count();
        if (noteCount == 0) {
            return Result.NOTHING_TO_BACKUP;
        }
        // Query notes.
        final ParseQuery<LocalNote> query = ParseQuery.getQuery(LocalNote.class).fromLocalDatastore();
        query.orderByAscending(LocalNote.KEY_CUSTOM_DATE);
        query.setLimit(noteCount);
        final List<LocalNote> notes = query.find();
        // Write notes.
        publishProgress(new Progress(Progress.State.PROGRESS, 0));
        output.start(noteCount);
        int progress = 0;
        for (final LocalNote note : notes) {
            if (isCancelled()) {
                break;
            }
            output.write(note);
            progress += 1;
            publishProgress(new Progress(Progress.State.PROGRESS, progress));
        }
        output.finish();
        return Result.OK;
    }
}
