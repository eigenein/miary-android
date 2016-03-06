package in.eigene.miary.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Date;

import in.eigene.miary.R;
import in.eigene.miary.fragments.base.BaseFragment;
import in.eigene.miary.fragments.dialogs.CustomDateDialogFragment;
import in.eigene.miary.fragments.dialogs.RemoveNoteDialogFragment;
import in.eigene.miary.helpers.ColorHelper;
import in.eigene.miary.helpers.Debouncer;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.Substitutions;
import in.eigene.miary.helpers.TextWatcher;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.helpers.TypefaceCache;
import in.eigene.miary.persistence.Note;

public class NoteFragment extends BaseFragment {

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    private static final String EXTRA_NOTE_URI = "uri";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    private final Debouncer saveDebouncer = new Debouncer("saveNote", 3000L, false);

    private Listener listener;

    private View editLayout;
    private EditText editTextTitle;
    private EditText editTextText;

    private Note note;

    public static NoteFragment create(final Uri noteUri, final boolean fullscreen) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_NOTE_URI, noteUri);
        arguments.putSerializable(EXTRA_FULLSCREEN, fullscreen);
        final NoteFragment fragment = new NoteFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        listener = (Listener)activity;
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
        editLayout = view.findViewById(R.id.note_edit_layout);

        if (getArguments().getBoolean(EXTRA_FULLSCREEN, false)) {
            leaveFullscreenView.setVisibility(View.VISIBLE);
        }

        leaveFullscreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                listener.onLeaveFullscreen();
            }
        });

        createTitleView(view);
        createTextView(view);

        return view;
    }

    private void createTextView(final View view) {
        editTextText = (EditText)view.findViewById(R.id.note_edit_text);
        editTextText.setTypeface(TypefaceCache.get(getActivity(), TypefaceCache.ROBOTO_SLAB_REGULAR));
        editTextText.setTextSize(Float.valueOf(PreferenceHelper.get(getActivity()).getString(
                getString(R.string.prefkey_font_size), "18")));
        editTextText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                // Automatic substitution.
                final String currentText = editTextText.getText().toString();
                final String replacedText;
                final boolean substitutionEnabled = PreferenceHelper.get(getActivity()).getBoolean(getString(
                        R.string.prefkey_substitution_enabled), true);
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
                if (note != null) {
                    note.setText(replacedText);
                    saveNote(true);
                }
            }
        });
    }

    private void createTitleView(final View view) {
        editTextTitle = (EditText)view.findViewById(R.id.note_edit_title);
        editTextTitle.setTypeface(TypefaceCache.get(getActivity(), TypefaceCache.ROBOTO_CONDENSED_BOLD));
        editTextTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                if (note != null) {
                    note.setTitle(s.toString());
                    saveNote(true);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
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
                Tracking.setDraft(true);
                return true;

            case R.id.menu_item_note_not_draft:
                note.setDraft(false);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_note_undrafted, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                Tracking.setDraft(false);
                return true;

            case R.id.menu_item_note_not_starred:
                note.setStarred(true);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_starred, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                Tracking.setStarred(false);
                return true;

            case R.id.menu_item_note_starred:
                note.setStarred(false);
                saveNote(false);
                Toast.makeText(getActivity(), R.string.toast_unstarred, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                Tracking.setStarred(true);
                return true;

            case R.id.menu_item_note_color:
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .lightnessSliderOnly()
                        .setTitle(getString(R.string.dialog_colorpicker_title))
                        .initialColor(note.getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .setPositiveButton(getString(android.R.string.ok), new ColorPickerClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int color, final Integer[] allColors) {
                                note.setColor(color);
                                saveNote(false);
                                updateLayoutColor();
                                Tracking.setColor(color);
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, final int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;

            case R.id.menu_item_note_remove:
                new RemoveNoteDialogFragment()
                        .setListener(new RemoveNoteDialogFragment.Listener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                note.setDeleted(true);
                                saveNote(false);
                                Toast.makeText(getActivity(), R.string.note_removed, Toast.LENGTH_SHORT).show();
                                listener.onNoteRemoved();
                                Tracking.removeNote();
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
                                Tracking.setCustomDate();
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

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Updates view with the note.
     */
    private void refresh() {
        final Uri noteUri = getArguments().getParcelable(EXTRA_NOTE_URI);
        Log.i(LOG_TAG, "Update view: " + noteUri);
        note = Note.getByUri(noteUri, getActivity().getContentResolver());
        Log.i(LOG_TAG, "Note: " + note);
        editTextTitle.setText(note.getTitle());
        editTextText.setText(note.getText());
        updateLayoutColor();
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
        note.setUpdatedDate(new Date());
        note.update(getActivity().getContentResolver());
        // Update debouncer.
        saveDebouncer.ping();
    }

    /**
     * Updates layout according to the note color.
     */
    private void updateLayoutColor() {
        final boolean isLight = ColorHelper.isLight(note.getColor());

        editLayout.setBackgroundColor(note.getColor());
        editTextTitle.setTextColor(ColorHelper.getTextColor(isLight));
        editTextTitle.setHintTextColor(ColorHelper.getHintColor(isLight));
        editTextText.setTextColor(ColorHelper.getTextColor(isLight));
        editTextText.setHintTextColor(ColorHelper.getHintColor(isLight));
    }

    /**
     * Gets share intent for share action provider.
     */
    private Intent getShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, note.getText().trim());
        if (!TextUtils.isEmpty(note.getTitle())) {
            intent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle().trim());
        }
        return intent;
    }

    public interface Listener {

        void onLeaveFullscreen();
        void onNoteRemoved();
    }
}
