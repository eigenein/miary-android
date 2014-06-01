package in.eigene.miary.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.TextWatcher;

import java.util.*;

public class NoteFragment extends Fragment {

    public interface ChangedListener {

        public void onNoteRemoved();
    }

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    public static final String EXTRA_NOTE_UUID = "note_uuid";

    private static final long DEBOUNCE_INTERVAL = 3000L;

    private final ChangedListener changedListener;

    private LinearLayout editLayout;
    private EditText editTextTitle;
    private EditText editTextText;

    private Note note;

    /**
     * Contains last note save date.
     */
    private long lastSaveDateTime = new Date().getTime();

    public NoteFragment(final ChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.note, menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.note_fragment, container, false);

        editLayout = (LinearLayout)view.findViewById(R.id.note_edit_layout);

        editTextTitle = (EditText)view.findViewById(R.id.note_edit_title);
        editTextTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                if (note != null) {
                    note.setTitle(s.toString());
                    saveNote(true);
                }
            }
        });

        editTextText = (EditText)view.findViewById(R.id.note_edit_text);
        editTextText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                if (note != null) {
                    note.setText(s.toString());
                    saveNote(true);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final UUID noteUuid = (UUID)getArguments().getSerializable(EXTRA_NOTE_UUID);
        Log.i(LOG_TAG, "Start: " + noteUuid);
        updateView(noteUuid);
    }

    @Override
    public void onStop() {
        super.onStop();

        saveNote(false);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_note_color:
                new ColorPickerDialogFragment(new ColorPickerDialogFragment.DialogListener() {
                    @Override
                    public void colorChosen(final int color) {
                        note.setColor(color);
                        saveNote(false);
                        updateLayoutColor();
                    }
                }).show(getFragmentManager(), "ChooseColorFragment");
                return true;

            case R.id.menu_item_note_remove:
                new RemoveNoteDialogFragment(new RemoveNoteDialogFragment.Listener() {
                    @Override
                    public void onPositiveButtonClicked() {
                        note.unpinInBackground(new DeleteCallback() {
                            @Override
                            public void done(final ParseException e) {
                                InternalRuntimeException.throwForException("Could not unpin note.", e);
                                note = null;
                                Toast.makeText(getActivity(), R.string.note_removed, Toast.LENGTH_SHORT);
                                changedListener.onNoteRemoved();
                                ParseAnalytics.trackEvent("remove_note");
                            }
                        });
                        note = null;
                    }
                }).show(getFragmentManager(), "RemoveNoteDialogFragment");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Saves the note. This method debounces frequent save calls.
     */
    private void saveNote(final boolean debounce) {
        // Check that note is not removed.
        if (note == null) {
            Log.i(LOG_TAG, "Not saving null note.");
            return;
        }
        // Debounce.
        final long currentDateTime = new Date().getTime();
        if (debounce) {
            if (currentDateTime - lastSaveDateTime < DEBOUNCE_INTERVAL) {
                Log.d(LOG_TAG, "Save debounce. " + (currentDateTime - lastSaveDateTime));
                return;
            }
        }
        // Save callback.
        final SaveCallback callback = new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                InternalRuntimeException.throwForException("Could not pin note.", e);
            }
        };
        // Save, log and track.
        Log.i(LOG_TAG, "Save note.");
        note.pinInBackground(callback);
        lastSaveDateTime = currentDateTime;
        ParseAnalytics.trackEvent("save_note");
    }

    /**
     * Updates view with the note.
     */
    private void updateView(final UUID noteUuid) {
        Note.getByUuid(noteUuid, new GetCallback<Note>() {
            @Override
            public void done(final Note note, final ParseException e) {
                InternalRuntimeException.throwForException("Failed to find note.", e);
                Log.i(LOG_TAG, "Note: " + note);
                NoteFragment.this.note = note;
                editTextTitle.setText(note.getTitle());
                editTextText.setText(note.getText());
                updateLayoutColor();
            }
        });
    }

    /**
     * Updates layout according to the note color.
     */
    private void updateLayoutColor() {
        final StyleHolder styleHolder = StyleHolders.get(note.getColor());

        editLayout.setBackgroundResource(styleHolder.noteBackgroundColorId);
        final int hintColor = getResources().getColor(styleHolder.hintColorId);
        editTextTitle.setHintTextColor(hintColor);
        editTextText.setHintTextColor(hintColor);
    }
}
