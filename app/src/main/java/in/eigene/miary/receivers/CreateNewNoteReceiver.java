package in.eigene.miary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import in.eigene.miary.activities.NoteActivity;
import in.eigene.miary.persistence.Note;

/**
 * Used when creating a note from notification.
 */
public class CreateNewNoteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Note note = Note.createEmpty();
        final Uri noteUri = note.insert(context.getContentResolver());
        NoteActivity.start(context, noteUri, false, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
    }
}
