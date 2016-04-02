package in.eigene.miary.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import in.eigene.miary.R;
import in.eigene.miary.fragments.NoteFragment;
import in.eigene.miary.persistence.Note;

/**
 * Displays a single note.
 */
public class NoteActivity extends BaseActivity implements NoteFragment.Listener {

    public static final int RESULT_REMOVED = 1;

    private static final String LOG_TAG = NoteActivity.class.getSimpleName();

    private static final String EXTRA_NOTE_URI = "noteUri";
    private static final String EXTRA_FULLSCREEN = "fullscreen";

    private Uri noteUri;
    private boolean isFullscreen;

    public static void startForResult(
            final Activity activity, final Uri noteUri, final int requestCode) {

        Log.i(LOG_TAG, "Starting note activity: " + noteUri);
        final Intent intent = new Intent()
                .setClass(activity, NoteActivity.class)
                .putExtra(EXTRA_NOTE_URI, noteUri)
                .putExtra(EXTRA_FULLSCREEN, false);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(
            final Context context,
            final Uri noteUri,
            final boolean fullscreen,
            final int additionalFlags) {

        Log.i(LOG_TAG, "Starting note activity: " + noteUri);
        final Intent intent = new Intent()
                .setClass(context, NoteActivity.class)
                .addFlags(additionalFlags)
                .putExtra(EXTRA_NOTE_URI, noteUri)
                .putExtra(EXTRA_FULLSCREEN, fullscreen);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        final Intent intent = getIntent();

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            final Note note = Note.createEmpty();
            if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                note.setText(intent.getStringExtra(Intent.EXTRA_TEXT).trim());
            }
            if (intent.getStringExtra(Intent.EXTRA_SUBJECT) != null) {
                note.setTitle(intent.getStringExtra(Intent.EXTRA_SUBJECT).trim());
            }
            noteUri = note.insert(getContentResolver());
            isFullscreen = false;
        } else if (savedInstanceState != null) {
            noteUri = savedInstanceState.getParcelable(EXTRA_NOTE_URI);
            isFullscreen = savedInstanceState.getBoolean(EXTRA_FULLSCREEN, false);
        } else {
            noteUri = intent.getParcelableExtra(EXTRA_NOTE_URI);
            isFullscreen = intent.getBooleanExtra(EXTRA_FULLSCREEN, false);
        }

        // Fullscreen mode.
        if (isFullscreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        initializeToolbar();

        if (isFullscreen) {
            getSupportActionBar().hide();
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_note, NoteFragment.create(noteUri, isFullscreen))
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSecureFlag();
    }

    @Override
    public void onNoteRemoved() {
        setResult(RESULT_REMOVED);
        finish();
    }

    @Override
    public void onEnterFullscreen() {
        isFullscreen = true;
        recreate();
    }

    @Override
    public void onLeaveFullscreen() {
        isFullscreen = false;
        recreate();
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putBoolean(EXTRA_FULLSCREEN, isFullscreen);
        savedInstanceState.putParcelable(EXTRA_NOTE_URI, noteUri);
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
