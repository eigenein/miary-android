package in.eigene.miary.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;

import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.core.persistence.Note;
import in.eigene.miary.fragments.base.*;
import in.eigene.miary.fragments.dialogs.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.helpers.TextWatcher;

import java.util.*;

public class NoteFragment extends BaseFragment {

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    private static final String EXTRA_NOTE_ID = "id";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    private final Debouncer saveDebouncer = new Debouncer("saveNote", 3000L, false);

    private ChangedListener changedListener;
    private LeaveFullscreenListener leaveFullscreenListener;

    private LinearLayout editLayout;
    private EditText editTextTitle;
    private EditText editTextText;

    private Note note;

    private boolean substitutionEnabled = true;

    public static NoteFragment create(final long noteId, final boolean fullscreen) {
        final Bundle arguments = new Bundle();
        arguments.putLong(EXTRA_NOTE_ID, noteId);
        arguments.putSerializable(EXTRA_FULLSCREEN, fullscreen);
        final NoteFragment fragment = new NoteFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        changedListener = (ChangedListener)activity;
        leaveFullscreenListener = (LeaveFullscreenListener)activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.note_fragment, menu);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note, container, false);
        final View leaveFullscreenView = view.findViewById(R.id.note_button_leave_fullscreen);
        editLayout = (LinearLayout)view.findViewById(R.id.note_edit_layout);

        if (getArguments().getBoolean(EXTRA_FULLSCREEN, false)) {
            editLayout.setPadding(0, 0, 0, 0);
            leaveFullscreenView.setVisibility(View.VISIBLE);
        }

        leaveFullscreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                leaveFullscreenListener.onLeaveFullscreen();
            }
        });

        createTitleView(view);
        createTextView(view);

        return view;
    }

    private void createTextView(final View view) {
        editTextText = (EditText)view.findViewById(R.id.note_edit_text);
        editTextText.setTypeface(TypefaceCache.get(getActivity(), TypefaceCache.ROBOTO_SLAB_REGULAR));
        editTextText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                // Automatic substitution.
                final String currentText = editTextText.getText().toString();
                final String replacedText;
                if (substitutionEnabled) {
                    replacedText = Substitutions.replace(currentText);
                    if (!currentText.equals(replacedText)) {
                        Log.d(LOG_TAG, "Setting replaced text.");
                        final int selectionStart = editTextText.getSelectionStart();
                        editTextText.setText(replacedText);
                        editTextText.setSelection(Math.max(0, selectionStart + replacedText.length() - currentText.length()));
                    }
                } else {
                    replacedText = currentText;
                }
                // Update field.
                note.setText(replacedText);
                saveNote(true);
            }
        });
    }

    private void createTitleView(final View view) {
        editTextTitle = (EditText)view.findViewById(R.id.note_edit_title);
        editTextTitle.setTypeface(TypefaceCache.get(getActivity(), TypefaceCache.ROBOTO_SLAB_BOLD));
        editTextTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                note.setTitle(s.toString());
                saveNote(true);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        refresh();
        substitutionEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                getString(R.string.prefkey_substitution_enabled), true);
    }

    @Override
    public void onStop() {
        super.onStop();
        saveNote(false);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        if (note == null) {
            return;
        }

        menu.findItem(R.id.menu_item_note_draft).setVisible(!note.isDraft());
        menu.findItem(R.id.menu_item_note_not_draft).setVisible(note.isDraft());
        menu.findItem(R.id.menu_item_note_not_starred).setVisible(!note.isStarred());
        menu.findItem(R.id.menu_item_note_starred).setVisible(note.isStarred());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_note_draft:
                note.setDraft(true);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_note_drafted, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                return true;

            case R.id.menu_item_note_not_draft:
                note.setDraft(false);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_note_undrafted, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                return true;

            case R.id.menu_item_note_not_starred:
                note.setStarred(true);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_starred, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                return true;

            case R.id.menu_item_note_starred:
                note.setStarred(false);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_unstarred, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                return true;

            case R.id.menu_item_note_color:
                new ColorPickerDialogFragment()
                        .setActiveColor(note.getColor())
                        .setListener(new ColorPickerDialogFragment.Listener() {
                            @Override
                            public void colorChosen(final int color) {
                                note.setColor(color);
                                saveNote(false);
                                updateLayoutColor();
                                ParseHelper.trackEvent("setColor", "color", Integer.toString(color));
                            }
                        })
                        .show(getFragmentManager());
                return true;

            case R.id.menu_item_note_remove:
                new RemoveNoteDialogFragment()
                        .setListener(new RemoveNoteDialogFragment.Listener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                note.setDeleted(true);
                                saveNote(false);
                                Toast.makeText(getActivity(), R.string.note_removed, Toast.LENGTH_SHORT).show();
                                changedListener.onNoteRemoved();
                            }
                        })
                        .show(getFragmentManager());
                return true;

            case R.id.menu_item_note_custom_date:
                new CustomDateDialogFragment()
                        .setListener(new CustomDateDialogFragment.Listener() {
                            @Override
                            public void onPositiveButtonClicked(final Date date) {
                                note.setCustomDate(date);
                                saveNote(false);
                                ParseAnalytics.trackEventInBackground("setCustomDate");
                            }
                        })
                        .setCreationDate(note.getCreatedDate())
                        .setCustomDate(note.getCustomDate())
                        .show(getFragmentManager());
                return true;

            case R.id.menu_item_note_share:
                startActivity(getShareIntent());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates view with the note.
     */
    private void refresh() {
        final long noteId = getArguments().getLong(EXTRA_NOTE_ID);
        Log.i(LOG_TAG, "Update view: " + noteId);
        note = Note.getById(noteId, getActivity().getContentResolver());
        Log.i(LOG_TAG, "Note: " + note);
        editTextTitle.setText(note.getTitle());
        editTextText.setText(note.getText());
        updateLayoutColor();
        invalidateOptionsMenu();
    }

    /**
     * Saves the note. This method debounces frequent save calls.
     */
    private void saveNote(final boolean debounce) {
        note.setUpdatedDate(new Date());
        // Debounce.
        if (debounce && !saveDebouncer.isActionAllowed()) {
            return;
        }
        // Save.
        Log.i(LOG_TAG, "Save note.");
        note.save(getActivity().getContentResolver());
        // Update debouncer.
        saveDebouncer.ping();
    }

    /**
     * Updates layout according to the note color.
     */
    private void updateLayoutColor() {
        final NoteColorHelper color = NoteColorHelper.fromIndex(getActivity(), note.getColor());

        editLayout.setBackgroundColor(color.primaryColor);
        editTextTitle.setTextColor(color.foregroundColor);
        editTextTitle.setHintTextColor(color.secondaryColor);
        editTextText.setTextColor(color.foregroundColor);
        editTextText.setHintTextColor(color.secondaryColor);
    }

    /**
     * Gets share intent for share action provider.
     */
    private Intent getShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, note.getText().trim());
        if (!Util.isNullOrEmpty(note.getTitle())) {
            intent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle().trim());
        }
        return intent;
    }

    public interface ChangedListener {

        public void onNoteRemoved();
    }

    public interface LeaveFullscreenListener {

        public void onLeaveFullscreen();
    }
}
