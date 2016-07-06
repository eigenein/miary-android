package in.eigene.miary.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;

import in.eigene.miary.R;
import in.eigene.miary.fragments.FeedFragment;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.widgets.Drawer;

/**
 * Displays diary.
 */
public class FeedActivity extends BaseActivity {

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private Drawer drawer;
    private View fabView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();

        // Initialize feed fragment.
        final FeedFragment feedFragment = getFeedFragment();
        setTitle(feedFragment.getSection().getTitleResourceId());

        // Initialize preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialize navigation drawer.
        drawer = new Drawer(this, getToolbar());
        drawer.showForFirstTime();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSecureFlag();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // Restores notes.
        final View.OnClickListener removedActionListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ContentValues values = new ContentValues();
                values.put(Note.Contract.DELETED, false);
                final Uri noteUri = data.getParcelableExtra(NoteActivity.EXTRA_NOTE_URI);
                getContentResolver().update(noteUri, values, null, null);
                Tracking.cancelRemovedNote();
            }
        };

        switch (resultCode) {
            case NoteActivity.RESULT_REMOVED:
                Snackbar
                        .make(fabView, R.string.note_removed, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.cancel, removedActionListener)
                        .show();
                break;
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        drawer.toggle.onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return drawer.toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer.toggle.syncState();
    }

    private void initializeFloatingActionButton() {
        fabView = findViewById(R.id.fab_button);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Uri noteUri = Note.createEmpty()
                        .addToSection(getFeedFragment().getSection())
                        .insert(FeedActivity.this.getContentResolver());
                NoteActivity.startForResult(FeedActivity.this, noteUri, 0);
                Tracking.newNote();
            }
        });
    }
}
