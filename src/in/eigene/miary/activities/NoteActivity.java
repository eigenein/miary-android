package in.eigene.miary.activities;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.core.classes.*;
import in.eigene.miary.fragments.*;

import java.util.*;

/**
 * Displays a single note.
 */
public class NoteActivity extends BaseActivity implements NoteFragment.ChangedListener {

    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    private static final String EXTRA_NOTE_UUID = "note_uuid";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    private boolean fullscreen;

    public static void start(final Context context, final Note note, final boolean fullscreen) {
        start(context, note, fullscreen, 0);
    }

    public static void start(
            final Context context,
            final Note note,
            final boolean fullscreen,
            final int additionalFlags) {
        Log.i(LOG_TAG, "Starting note activity: " + note);
        context.startActivity(new Intent()
                .setClass(context, NoteActivity.class)
                .addFlags(additionalFlags)
                .putExtra(EXTRA_NOTE_UUID, note.getUuid())
                .putExtra(EXTRA_FULLSCREEN, fullscreen));
    }

    public void restart(final boolean fullscreen) {
        finish();
        startActivity(getIntent().putExtra(EXTRA_FULLSCREEN, fullscreen));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // Fullscreen mode.
        fullscreen = getIntent().getBooleanExtra(EXTRA_FULLSCREEN, false);
        if (fullscreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        initializeToolbar();

        final NoteFragment noteFragment = (NoteFragment)getFragmentManager().findFragmentById(R.id.fragment_note);

        if (fullscreen) {
            getSupportActionBar().hide();
            noteFragment.disablePadding();
            noteFragment.setOnLeaveFullscreenListener(new NoteFragment.LeaveFullscreenListener() {
                @Override
                public void onLeave() {
                    restart(false);
                }
            });
        }

        final UUID noteUuid = (java.util.UUID)getIntent().getSerializableExtra(EXTRA_NOTE_UUID);
        noteFragment.setNoteUuid(noteUuid);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.note_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_note_fullscreen:
                restart(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNoteRemoved() {
        finish();
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getToolbar().setBackgroundResource(R.color.toolbar_background_note);
    }
}
