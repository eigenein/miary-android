package in.eigene.miary.activities;

import android.content.*;
import android.os.*;
import android.util.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.fragments.*;

public class NoteActivity extends BaseActivity implements NoteFragment.ChangedListener {

    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    private static final String EXTRA_NOTE_UUID = "note_uuid";

    public static void start(final Context context, final Note note) {
        Log.i(LOG_TAG, "Starting note activity: " + note);
        context.startActivity(new Intent()
                .setClass(context, NoteActivity.class)
                .putExtra(EXTRA_NOTE_UUID, note.getUuid()));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        selectFragment(R.id.content_frame, new NoteFragment()
                .setNoteUuid((java.util.UUID)getIntent().getSerializableExtra(EXTRA_NOTE_UUID)));
    }

    @Override
    public void onNoteRemoved() {
        finish();
    }
}
