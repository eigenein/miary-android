package in.eigene.miary.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseAnalytics;

import in.eigene.miary.R;
import in.eigene.miary.adapters.DrawerAdapter;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.fragments.FeedFragment;
import in.eigene.miary.widgets.Drawer;

/**
 * Displays diary.
 */
public class FeedActivity extends BaseActivity {

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSecureFlag();
        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();
        final FeedFragment feedFragment = getFeedFragment();
        feedFragment.fixTopPadding(getSupportActionBar().getThemedContext());
        // Initialize preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Initialize navigation drawer.
        drawer = new Drawer(this, getToolbar(), new DrawerAdapter(this, feedFragment));
        drawer.showForFirstTime();
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

    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }
}
