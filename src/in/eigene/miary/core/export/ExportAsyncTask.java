package in.eigene.miary.core.export;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

import java.io.*;

public class ExportAsyncTask extends AsyncTask<Void, ExportProgress, Void> {

    private static final ExportWriter[] WRITERS = new ExportWriter[] {
        new TextExportWriter()
    };

    private final Context context;
    private final File file;

    private ProgressDialog progressDialog;

    public ExportAsyncTask(final Context context, final File file) {
        this.context = context;
        this.file = file;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = createDialog();
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(final Void... params) {
        // Determine note count.
        int noteCount = 0;
        try {
            noteCount = ParseQuery.getQuery(Note.class).count();
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Could not get note count.", e);
        }
        // Check note count.
        if (noteCount == 0) {
            return null;
        }
        // Publish initial progress.
        final int max = noteCount * WRITERS.length;
        publishProgress(new ExportProgress(max, 0, null));
        // Export.
        for (final ExportWriter writer : WRITERS) {
            // TODO: Export with the writer and publish progress.
        }
        // Well done.
        return null;
    }

    @Override
    protected void onProgressUpdate(final ExportProgress... values) {
        final ExportProgress progress = values[0];
        progressDialog.setMax(progress.getMax());
        progressDialog.setProgress(progress.getValue());
        if (progress.getMessage() != null) {
            progressDialog.setMessage(progress.getMessage());
        }
    }

    @Override
    protected void onPostExecute(final Void result) {
        progressDialog.hide();
        Toast.makeText(
                context,
                String.format(context.getString(R.string.toast_export_finished), file.getAbsolutePath()),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, R.string.toast_cancelled, Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog createDialog() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.progress_title_export);
        dialog.setMessage(context.getString(R.string.toast_export_starting));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                cancel(true);
            }
        });
        return dialog;
    }

    private void export(final ExportWriter writer) {
        // TODO: export to the writer.
    }
}
