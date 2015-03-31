package in.eigene.miary.helpers;

import android.view.*;
import com.parse.*;
import in.eigene.miary.core.persistence.Note;

/**
 * Handles New Note click on either FAB or empty feed.
 */
public class NewNoteClickListener implements View.OnClickListener {

    @Override
    public void onClick(final View view) {
        final Note note = Note.getEmpty();
        note.save(view.getContext().getContentResolver());
        ParseAnalytics.trackEventInBackground("createNew");
    }
}
