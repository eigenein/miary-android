package in.eigene.miary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.parse.ParseException;
import com.parse.SaveCallback;
import in.eigene.miary.activities.NoteActivity;
import in.eigene.miary.core.Note;
import in.eigene.miary.exceptions.InternalRuntimeException;

public class CreateNewNoteReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = CreateNewNoteReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Note note = Note.getNewNote();
        note.pinInBackground(new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                InternalRuntimeException.throwForException("Could not pin a new note.", e);
                Log.i(LOG_TAG, "Pinned new note: " + note);
                NoteActivity.start(context, note, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
            }
        });

    }
}
