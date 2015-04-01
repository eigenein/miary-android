package in.eigene.miary.helpers;

import android.net.*;
import android.view.*;
import com.parse.*;

import in.eigene.miary.activities.*;
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
