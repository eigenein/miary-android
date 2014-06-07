package in.eigene.miary.activities;

import android.app.*;
import android.os.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.*;

public class NoteActivity extends BaseActivity implements NoteFragment.ChangedListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        final Fragment fragment = new NoteFragment(this);
        fragment.setArguments(getIntent().getExtras()); // pass note UUID
        selectFragment(R.id.content_frame, fragment);
    }

    @Override
    public void onNoteRemoved() {
        finish();
    }
}
