package in.eigene.miary.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import in.eigene.miary.R;
import in.eigene.miary.fragments.NoteFragment;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;

/**
 * Displays a single note.
 */
public class NoteActivity extends BaseActivity implements NoteFragment.Listener {

    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    private static final String EXTRA_NOTE_URI = "noteUri";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    public static void start(final Context context, final Uri noteUri, final boolean fullscreen) {
        start(context, noteUri, fullscreen, 0);
    }

    public static void start(
            final Context context,
            final Uri noteUri,
            final boolean fullscreen,
            final int additionalFlags) {
        Log.i(LOG_TAG, "Starting note activity: " + noteUri);
        context.startActivity(new Intent()
                .setClass(context, NoteActivity.class)
                .addFlags(additionalFlags)
                .putExtra(EXTRA_NOTE_URI, noteUri)
                .putExtra(EXTRA_FULLSCREEN, fullscreen));
    }

    public void restart(final boolean fullscreen) {
        finish();
        startActivity(getIntent().putExtra(EXTRA_FULLSCREEN, fullscreen));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final Intent intent = getIntent();

        // Fullscreen mode.
        final boolean fullscreen = intent.getBooleanExtra(EXTRA_FULLSCREEN, false);
        if (fullscreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        initializeToolbar();

        if (fullscreen) {
            getSupportActionBar().hide();
        }

        initializeNoteFragment(intent, fullscreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        setSecureFlag();
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
                Tracking.sendEvent(Tracking.Category.FULLSCREEN, Tracking.Action.ENTER);
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
    public void onLeaveFullscreen() {
        restart(false);
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initializeNoteFragment(final Intent intent, final boolean fullscreen) {
        final Uri noteUri;

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            final Note note = Note.createEmpty();
            if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                note.setText(intent.getStringExtra(Intent.EXTRA_TEXT).trim());
            }
            if (intent.getStringExtra(Intent.EXTRA_SUBJECT) != null) {
                note.setTitle(intent.getStringExtra(Intent.EXTRA_SUBJECT).trim());
            }
            noteUri = note.insert(getContentResolver());
        } else {
            noteUri = getIntent().getParcelableExtra(EXTRA_NOTE_URI);
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_note, NoteFragment.create(noteUri, fullscreen))
                .commit();
    }
}
