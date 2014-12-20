package in.eigene.miary.core.backup.tasks;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.backup.*;

/**
 * Base task for backup and restore.
 */
public abstract class BaseAsyncTask extends AsyncTask<Void, Progress, Result> {

    protected final Context context;

    protected int noteCount;

    private ProgressDialog progressDialog;

    protected BaseAsyncTask(final Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
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
    protected void onProgressUpdate(final Progress... values) {
        final Progress progress = values[0];

        switch (progress.getState()) {
            case PROGRESS:
                progressDialog.setMessage(context.getString(getProgressMessageResourceId()));
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
    protected void onPostExecute(final Result result) {
        progressDialog.hide();

        switch (result) {
            case STORAGE_NOT_READY:
                Toast.makeText(context, R.string.toast_storage_unready, Toast.LENGTH_LONG).show();
                break;
            case NOTHING_TO_BACKUP:
                Toast.makeText(context, R.string.toast_backup_nothing, Toast.LENGTH_LONG).show();
                break;
            case NOT_FOUND:
                Toast.makeText(context, R.string.toast_restore_not_found, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, R.string.toast_cancelled, Toast.LENGTH_SHORT).show();
    }

    /**
     * Allows progress dialog to refresh its state at the end of task.
     * https://github.com/eigenein/miary-android/issues/112
     */
    protected void sleepCheat() throws InterruptedException {
        Thread.sleep(100L);
    }

    /**
     * Gets progress dialog message resource ID.
     */
    protected abstract int getProgressMessageResourceId();
}