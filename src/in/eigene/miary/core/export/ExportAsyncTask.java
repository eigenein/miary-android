package in.eigene.miary.core.export;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.parse.*;
import com.parse.ParseException;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

public class ExportAsyncTask extends AsyncTask<Void, ExportProgress, Integer> {

    private static final String LOG_TAG = ExportAsyncTask.class.getSimpleName();

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private static final Exporter[] EXPORTERS = new Exporter[] {
        new PlainTextExporter()
    };

    private final Context context;
    private final File file;

    private ProgressDialog progressDialog;

    public static void start(final Context context) {
        // Make file path.
        final String date = DATE_FORMAT.format(new Date());
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        final File archive = new File(path, String.format("Miary Export %s.zip", date));
        // Start export task.
        new ExportAsyncTask(context, archive).execute();
    }

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

        final ExportProgress progress = new ExportProgress(noteCount * EXPORTERS.length, 0, null);

        try {
            final ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(file));
            for (final Exporter exporter : EXPORTERS) {
                Log.i(LOG_TAG, "Exporting with " + exporter.getClass().getSimpleName());
                progress.setMessage(String.format(
                        context.getString(R.string.export_message_progress),
                        context.getString(exporter.getWriterTitle())));
                publishProgress(progress);
                stream.putNextEntry(new ZipEntry("notes." + exporter.getExtension()));
                export(noteCount, exporter, stream, new Action<Integer>() {
                    @Override
                    public void done(final Integer value) {
                        progress.incrementValue(value);
                        publishProgress(progress);
                    }
                });
                stream.flush();
            }
            stream.close();
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
            final Exporter writer,
            final OutputStream stream,
            final Action<Integer> incrementProgress) {
        // Query notes.
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class).fromLocalDatastore();
        query.orderByAscending(Note.KEY_CUSTOM_DATE);
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
