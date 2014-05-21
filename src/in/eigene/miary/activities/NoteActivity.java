package in.eigene.miary.activities;

import android.os.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.*;

public class NoteActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_activity);
        selectFragment(R.id.note_content_frame, new NoteFragment());
    }
}
