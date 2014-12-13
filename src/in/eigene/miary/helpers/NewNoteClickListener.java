package in.eigene.miary.helpers;

import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

/**
 * Handles New Note click on either FAB or empty feed.
 */
public class NewNoteClickListener implements View.OnClickListener {

    private static final String LOG_TAG = NewNoteClickListener.class.getSimpleName();

    private final FeedAdapter adapter;

    public NewNoteClickListener(final FeedAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onClick(final View view) {
        final Note note = Note.createNew();
        switch (adapter.getMode()) {
            case STARRED:
                note.setStarred(true);
                break;
            case DRAFTS:
                note.setDraft(true);
                break;
        }
        note.pinInBackground(new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                InternalRuntimeException.throwForException("Could not pin a new note.", e);
                Log.i(LOG_TAG, "Pinned new note: " + note);
                NoteActivity.start(view.getContext(), note, false);
            }
        });
    }
}
