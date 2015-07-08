package in.eigene.miary.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseAnalytics;

import in.eigene.miary.R;
import in.eigene.miary.fragments.FeedFragment;
import in.eigene.miary.helpers.MigrationHelper;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.widgets.Drawer;

/**
 * Displays diary.
 */
public class FeedActivity extends BaseActivity {

    /**
     * #179. Specifies whether notes from previous app versions where migrated.
     */
    public static final String KEY_NOTES_MIGRATED = "notes_migrated";

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSecureFlag();

        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();

        // Initialize feed fragment.
        final FeedFragment feedFragment = getFeedFragment();
        feedFragment.fixTopPadding(getSupportActionBar().getThemedContext());
        setTitle(feedFragment.getSection().getTitleResourceId());

        // Initialize preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Initialize navigation drawer.
        drawer = new Drawer(this, getToolbar());
        drawer.showForFirstTime();

        // #179: migrate notes from previous app versions. To be removed.
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_NOTES_MIGRATED, false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        MigrationHelper.migrate(FeedActivity.this);
                    } else {
                        Log.w(LOG_TAG, "Finishing. Could not migrate now.");
                    }
                }
            }, 1000L);
        } else {
            Log.i(LOG_TAG, "Already migrated.");
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        drawer.getToggle().onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return drawer.getToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer.getToggle().syncState();
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getToolbar().setBackgroundResource(R.color.toolbar_background_feed);
    }

    private void initializeFloatingActionButton() {
        findViewById(R.id.fab_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Uri noteUri = Note.createEmpty()
                        .addToSection(getFeedFragment().getSection())
                        .insert(view.getContext().getContentResolver());
                NoteActivity.start(view.getContext(), noteUri, false);
                ParseAnalytics.trackEventInBackground("createNew");
            }
        });
    }
}
