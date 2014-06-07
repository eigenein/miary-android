package in.eigene.miary.core.export;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ExportAsyncTask extends AsyncTask<Void, ExportProgress, Integer> {

    private static final String LOG_TAG = ExportAsyncTask.class.getSimpleName();

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
    protected Integer doInBackground(final Void... params) {
        int noteCount = 0;
        try {
            noteCount = ParseQuery.getQuery(Note.class).fromLocalDatastore().count();
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Could not get note count.", e);
        }
        Log.i(LOG_TAG, "Note count: " + noteCount);
        if (noteCount == 0) {
            return noteCount;
        }

        final int max = noteCount * WRITERS.length;
        final ExportProgress progress = new ExportProgress(max, 0, null);

        try {
            final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
            for (final ExportWriter writer : WRITERS) {
                Log.i(LOG_TAG, "Exporting with " + writer.getClass().getSimpleName());
                progress.setMessage(String.format(
                        context.getString(R.string.export_message_progress),
                        context.getString(writer.getWriterTitle())));
                publishProgress(progress);
                zip.putNextEntry(new ZipEntry("notes." + writer.getExtension()));
                export(noteCount, writer, zip, new Action<Integer>() {
                    @Override
                    public void done(final Integer value) {
                        progress.incrementValue(value);
                        publishProgress(progress);
                    }
                });
                zip.flush();
            }
            zip.close();
        } catch (final Exception e) {
            InternalRuntimeException.throwForException("Export failed.", e);
        }

        Log.i(LOG_TAG, "Finished.");
        return noteCount;
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
    protected void onPostExecute(final Integer result) {
        progressDialog.hide();
        if (result != 0) {
            Toast.makeText(
                    context,
                    String.format(context.getString(R.string.toast_export_finished), result, file.getAbsolutePath()),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            Toast.makeText(context, R.string.toast_export_nothing, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, R.string.toast_cancelled, Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog createDialog() {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.progress_title_export);
        dialog.setMessage(context.getString(R.string.export_message_starting));
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

    private void export(
            final int count,
            final ExportWriter writer,
            final OutputStream stream,
            final Action<Integer> incrementProgress) {
        // Query notes.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class).fromLocalDatastore();
        query.orderByAscending(Note.KEY_CREATION_DATE);
        query.setLimit(count);
        final List<Note> notes;
        try {
            notes = query.find();
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Could not find notes.", e);
            return;
        }
        // Write notes.
        for (final Note note : notes) {
            writer.putNote(stream, note);
            incrementProgress.done(1);
        }
    }
}
