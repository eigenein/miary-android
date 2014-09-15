package in.eigene.miary.core.backup;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.parse.ParseException;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

import java.text.*;
import java.util.*;

public class BackupAsyncTask extends AsyncTask<Void, Integer, BackupResult> {

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
        progressDialog.setMessage(context.getString(R.string.export_message_starting));
        progressDialog.setIndeterminate(false);
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
        backupName = getBackupName();
        try {
            return backup(outputFactory.createOutput(storage.getOutputStream(backupName)));
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Backup failed.", e);
            return BackupResult.FAILURE;
        }
    }

    @Override
    protected void onProgressUpdate(final Integer... values) {
        progressDialog.setMax(noteCount);
        progressDialog.setProgress(values[0]);
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

    private BackupResult backup(final BackupOutput output) throws ParseException {
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
        int writtenCount = 0;
        for (final Note note : notes) {
            output.write(note);
            writtenCount += 1;
            publishProgress(writtenCount);
        }
        output.finish();
        return BackupResult.OK;
    }

    private String getBackupName() {
        return String.format("Miary Export %s.%s", DATE_FORMAT.format(new Date()), outputFactory.getExtension());
    }
}
