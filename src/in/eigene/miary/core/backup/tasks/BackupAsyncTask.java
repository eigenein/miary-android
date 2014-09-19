package in.eigene.miary.core.backup.tasks;

import android.content.*;
import android.widget.*;
import com.parse.ParseException;
import com.parse.*;
import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Used to backup notes.
 */
public class BackupAsyncTask extends BaseBackupAsyncTask {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private final BackupStorage storage;
    private final BackupOutputFactory outputFactory;

    private String backupName;

    public BackupAsyncTask(
            final Context context,
            final BackupStorage storage,
            final BackupOutputFactory outputFactory) {
        super(context);
        this.storage = storage;
        this.outputFactory = outputFactory;
    }

    @Override
    protected BackupResult doInBackground(final Void... params) {
        if (!storage.checkReady()) {
            return BackupResult.STORAGE_NOT_READY;
        }
        backupName = getBackupName(storage.includeDate());
        try {
            final OutputStream outputStream = storage.getOutputStream(backupName);
            final BackupResult result = backup(outputFactory.createOutput(outputStream));
            publishProgress(BackupProgress.FINISHING);
            storage.finish(context, false, backupName, outputFactory.getMimeType());
            return result;
        } catch (final Exception e) {
            InternalRuntimeException.throwForException("Backup failed.", e);
            return BackupResult.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(final BackupResult result) {
        super.onPostExecute(result);

        if (result == BackupResult.OK) {
            Toast.makeText(
                    context,
                    String.format(context.getString(in.eigene.miary.R.string.toast_backup_finished), noteCount, backupName),
                    Toast.LENGTH_LONG
            ).show();
            storage.finish(context, true, backupName, outputFactory.getMimeType());
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return in.eigene.miary.R.string.backup_message_creating;
    }

    private BackupResult backup(final BackupOutput output) throws ParseException, IOException {
        noteCount = ParseQuery.getQuery(Note.class).fromLocalDatastore().count();
        if (noteCount == 0) {
            return BackupResult.NOTHING_TO_BACKUP;
        }
        // Query notes.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class).fromLocalDatastore();
        query.orderByAscending(Note.KEY_CUSTOM_DATE);
        query.setLimit(noteCount);
        final List<Note> notes = query.find();
        // Write notes.
        final BackupProgress progress = new BackupProgress(BackupProgress.State.PROGRESS, 0);
        publishProgress(progress);
        output.start();
        for (final Note note : notes) {
            if (isCancelled()) {
                break;
            }
            output.write(note);
            progress.incrementProgress();
            publishProgress(progress);
        }
        output.finish();
        return BackupResult.OK;
    }

    /**
     * Gets or generates a backup name.
     */
    private String getBackupName(boolean includeDate) {
        final StringBuilder nameBuilder = new StringBuilder("Miary Backup");
        if (includeDate) {
            nameBuilder.append(" ");
            nameBuilder.append(DATE_FORMAT.format(new Date()));
        }
        nameBuilder.append(".");
        nameBuilder.append(outputFactory.getExtension());
        return nameBuilder.toString();
    }
}
