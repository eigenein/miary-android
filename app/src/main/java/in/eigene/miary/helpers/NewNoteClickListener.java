package in.eigene.miary.helpers;

import android.net.Uri;
import android.view.View;

import com.parse.ParseAnalytics;

import in.eigene.miary.activities.NoteActivity;
import in.eigene.miary.core.persistence.Note;

/**
 * Handles New Note click on either FAB or empty feed.
 */
public class NewNoteClickListener implements View.OnClickListener {

    @Override
    public void onClick(final View view) {
        final Uri noteUri = Note.getEmpty().insert(view.getContext().getContentResolver());
        NoteActivity.start(view.getContext(), noteUri, false);
        ParseAnalytics.trackEventInBackground("createNew");
    }
}
