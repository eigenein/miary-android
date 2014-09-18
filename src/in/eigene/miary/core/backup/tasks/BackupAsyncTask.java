package in.eigene.miary.core.backup.tasks;

import android.app.*;
import android.content.*;
import android.widget.*;
import com.parse.ParseException;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Used to backup notes.
 */
public class BackupAsyncTask extends BaseBackupRestoreAsyncTask {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private final Context context;
    private final BackupStorage storage;
    private final BackupOutputFactory outputFactory;

    private ProgressDialog progressDialog;

    private String backupName;
    private int noteCount;

    public BackupAsyncTask(
            final Context context,
            final BackupStorage storage,
            final BackupOutputFactory outputFactory) {
        this.context = context;
        this.storage = storage;
        this.outputFactory = outputFactory;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.progress_title_export);
        progressDialog.setMessage(context.getString(R.string.backup_message_starting));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                cancel(true);
            }
        });
        progressDialog.show();
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
    protected void onProgressUpdate(final BackupProgress... values) {
        final BackupProgress progress = values[0];

        switch (progress.getState()) {
            case PROGRESS:
                progressDialog.setMessage(context.getString(R.string.backup_message_progress));
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(noteCount);
                progressDialog.setProgress(progress.getProgress());
                break;
            case FINISHING:
                progressDialog.setMessage(context.getString(R.string.backup_message_finishing));
                progressDialog.setIndeterminate(true);
                break;
        }
    }

    @Override
    protected void onPostExecute(final BackupResult result) {
        progressDialog.hide();
        switch (result) {
            case OK:
                Toast.makeText(
                        context,
                        String.format(context.getString(R.string.toast_backup_finished), noteCount, backupName),
                        Toast.LENGTH_LONG
                ).show();
                storage.finish(context, true, backupName, outputFactory.getMimeType());
                break;
            case STORAGE_NOT_READY:
                Toast.makeText(context, R.string.toast_storage_unready, Toast.LENGTH_LONG).show();
                break;
            case NOTHING_TO_BACKUP:
                Toast.makeText(context, R.string.toast_backup_nothing, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, R.string.toast_cancelled, Toast.LENGTH_SHORT).show();
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
