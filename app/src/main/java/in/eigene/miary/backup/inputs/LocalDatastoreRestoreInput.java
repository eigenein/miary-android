package in.eigene.miary.backup.inputs;

import android.util.Log;

import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import in.eigene.miary.backup.RestoreInput;
import in.eigene.miary.exceptions.InternalRuntimeException;
import in.eigene.miary.helpers.Util;
import in.eigene.miary.persistence.Note;

/**
 * Fake Parse Local Datastore input. To be removed.
 */
public class LocalDatastoreRestoreInput extends RestoreInput {

    private static final String LOG_TAG = LocalDatastoreRestoreInput.class.getSimpleName();

    public static void getCount(final CountCallback callback) {
        Log.i(LOG_TAG, "Get count.");
        ParseQuery.getQuery("Note").fromLocalDatastore().countInBackground(callback);
    }

    private Iterator<ParseObject> iterator;

    public LocalDatastoreRestoreInput(final InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public int start() throws IOException {
        Log.i(LOG_TAG, "Running query.");
        final List<ParseObject> notes;
        try {
            notes = ParseQuery.getQuery("Note").fromLocalDatastore().find();
        } catch (final ParseException e) {
            throw new IOException("Query failed.", e);
        }
        Log.i(LOG_TAG, notes.size() + " notes found.");
        iterator = notes.iterator();
        return notes.size();
    }

    @Override
    public Note read() throws IOException {
        if (!iterator.hasNext()) {
            throw new InternalRuntimeException("Iterator is over.");
        }
        final ParseObject object = iterator.next();
        return Note.createEmpty()
                .setId(Math.abs(object.getLong("uuidLsb")))
                .setTitle(Util.coalesce(object.getString("title"), ""))
                .setText(Util.coalesce(object.getString("text"), ""))
                .setCreatedDate(object.getDate("creationDate"))
                .setCustomDate(object.getDate("customDate"))
                .setDraft(object.getBoolean("draft"))
                .setColor(object.getInt("color"))
                .setStarred(object.getBoolean("starred"))
                .setDeleted(false);
    }

    @Override
    public void finish() throws IOException {
        // Do nothing.
    }

    public static class Factory extends RestoreInput.Factory {

        @Override
        public RestoreInput createInput(final InputStream inputStream) {
            return new LocalDatastoreRestoreInput(inputStream);
        }
    }
}
