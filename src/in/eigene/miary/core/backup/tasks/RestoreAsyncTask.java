package in.eigene.miary.core.backup.tasks;

import android.content.*;
import android.util.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.*;
import in.eigene.miary.exceptions.*;

import java.io.*;
import java.util.*;

/**
 * Used to restore a backup.
 */
public class RestoreAsyncTask extends BaseAsyncTask {

    private static final String LOG_TAG = RestoreAsyncTask.class.getSimpleName();

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
                    String.format(context.getString(in.eigene.miary.R.string.toast_restore_finished), noteCount),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected int getProgressMessageResourceId() {
        return R.string.backup_message_restoring;
    }

    private Result restore() throws IOException, ParseException {
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
        for (int i = 0; i < noteCount; i++) {
            final Note note = input.read();
            Log.i(LOG_TAG, "Read note: " + note.getUuid());
            // Find note to update.
            final List<Note> existingNotes = ParseQuery.getQuery(Note.class).fromLocalDatastore()
                    .whereEqualTo(Note.KEY_UUID_LSB, note.getUuid().getLeastSignificantBits())
                    .whereEqualTo(Note.KEY_UUID_MSB, note.getUuid().getMostSignificantBits())
                    .setLimit(1)
                    .find();
            if (existingNotes.size() != 0) {
                Log.i(LOG_TAG, "Update existing one.");
                final Note existingNote = existingNotes.get(0);
                existingNote.update(note);
                existingNote.pin();
            } else {
                Log.i(LOG_TAG, "Pin new one.");
                note.pin();
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
