package in.eigene.miary.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.parse.ParseAnalytics;

import in.eigene.miary.R;
import in.eigene.miary.fragments.NoteFragment;

/**
 * Displays a single note.
 */
public class NoteActivity extends BaseActivity
        implements NoteFragment.ChangedListener, NoteFragment.LeaveFullscreenListener {

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
        // Fullscreen mode.
        final boolean fullscreen = getIntent().getBooleanExtra(EXTRA_FULLSCREEN, false);
        if (fullscreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        setSecureFlag();
        setContentView(R.layout.activity_note);
        initializeToolbar();

        if (fullscreen) {
            getSupportActionBar().hide();
        }

        final Uri noteUri = getIntent().getParcelableExtra(EXTRA_NOTE_URI);
        final NoteFragment noteFragment = NoteFragment.create(noteUri, fullscreen);
        getFragmentManager().beginTransaction().add(R.id.fragment_note, noteFragment).commit();
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
                ParseAnalytics.trackEventInBackground("fullscreen");
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

        getToolbar().setBackgroundResource(R.color.toolbar_background_note);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
