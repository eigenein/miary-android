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
import in.eigene.miary.helpers.TextWatcher;

import java.util.*;

public class NoteFragment extends Fragment implements ChooseColorFragment.DialogListener {

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    public static final String EXTRA_NOTE_UUID = "note_uuid";

    private static final HashMap<Integer, Integer> COLOR_TO_RESOURCE_ID =
            new HashMap<Integer, Integer>();

    private LinearLayout editLayout;
    private EditText editTextTitle;
    private EditText editTextText;

    private Note note;

    static {
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_WHITE, android.R.color.white);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_BLUE, R.color.blue_light);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_VIOLET, R.color.violet_light);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_GREEN, R.color.green_light);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_ORANGE, R.color.orange_light);
        COLOR_TO_RESOURCE_ID.put(Note.COLOR_RED, R.color.red_light);
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
                    note.saveEverywhere();
                }
            }
        });

        editTextText = (EditText)view.findViewById(R.id.note_edit_text);
        editTextText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                if (note != null) {
                    note.setText(s.toString());
                    note.saveEverywhere();
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_note_color:
                new ChooseColorFragment(this).show(getFragmentManager(), "ChooseColorFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void colorChosen(final int color) {
        note.setColor(color).saveEverywhere();
        updateLayoutColor();
    }

    private void updateView(final UUID noteUuid) {
        Note.getByUuid(noteUuid, new GetCallback<Note>() {
            @Override
            public void done(final Note note, final ParseException e) {
                if (e != null) {
                    throw new InternalRuntimeException("Failed to find note.", e);
                }
                Log.i(LOG_TAG, "Note: " + note);
                NoteFragment.this.note = note;
                editTextTitle.setText(note.getTitle());
                editTextText.setText(note.getText());
                updateLayoutColor();
            }
        });
    }

    private void updateLayoutColor() {
        editLayout.setBackgroundResource(COLOR_TO_RESOURCE_ID.get(note.getColor()));
    }
}