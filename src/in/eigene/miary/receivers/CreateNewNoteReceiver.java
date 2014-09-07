package in.eigene.miary.receivers;

import android.content.*;
import android.util.*;
import com.parse.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

public class CreateNewNoteReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = CreateNewNoteReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Note note = Note.createNew();
        note.pinInBackground(new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                InternalRuntimeException.throwForException("Could not pin a new note.", e);
                Log.i(LOG_TAG, "Pinned new note: " + note);
                NoteActivity.start(context, note, false, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
            }
        });

    }
}
