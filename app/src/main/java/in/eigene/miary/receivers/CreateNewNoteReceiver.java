package in.eigene.miary.receivers;

import android.content.*;
import android.net.*;

import com.parse.*;

import in.eigene.miary.activities.*;
import in.eigene.miary.core.persistence.*;

public class CreateNewNoteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Note note = Note.getEmpty();
        final Uri noteUri = note.insert(context.getContentResolver());
        NoteActivity.start(context, noteUri, false, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
        ParseAnalytics.trackEventInBackground("createNewFromNotification");
    }
}
