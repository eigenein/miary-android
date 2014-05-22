package in.eigene.miary.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;

import java.util.*;

public class NoteFragment extends Fragment {

    public static final String EXTRA_NOTE_UUID = "note_uuid";

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    private Note note;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final UUID noteUuid = (UUID)getArguments().getSerializable(EXTRA_NOTE_UUID);
        Log.i(LOG_TAG, "On view creation: " + noteUuid);
        updateView(noteUuid);
        return inflater.inflate(R.layout.note_fragment, container, false);
    }

    private void updateView(final UUID noteUuid) {
        Note.getByUuid(noteUuid, new GetCallback<Note>() {
            @Override
            public void done(final Note note, final ParseException e) {
                if (e != null) {
                    throw new InternalRuntimeException("Failed to find note.", e);
                }
                Log.i(LOG_TAG, "Note: " + note);
            }
        });
    }
}
