package in.eigene.miary.activities;

import android.os.*;
import android.support.v4.app.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.*;

public class NoteActivity extends BaseActivity implements NoteFragment.ChangedListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.note_activity);
        final Fragment fragment = new NoteFragment(this);
        fragment.setArguments(getIntent().getExtras()); // pass note UUID
        selectFragment(R.id.note_content_frame, fragment);
    }

    @Override
    public void onNoteRemoved() {
        finish();
    }
}
