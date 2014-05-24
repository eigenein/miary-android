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

public class NoteFragment extends Fragment {

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    public static final String EXTRA_NOTE_UUID = "note_uuid";

    private EditText editTextTitle;
    private EditText editTextText;

    private Note note;

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

        editTextTitle = (EditText)view.findViewById(R.id.note_edit_title);
        editTextTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                note.setTitle(s.toString());
                note.saveEverywhere();
            }
        });

        editTextText = (EditText)view.findViewById(R.id.note_edit_text);
        editTextText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
                note.setText(s.toString());
                note.saveEverywhere();
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
                new ChooseColorFragment().show(getFragmentManager(), "ChooseColorFragment");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            }
        });
    }
}
