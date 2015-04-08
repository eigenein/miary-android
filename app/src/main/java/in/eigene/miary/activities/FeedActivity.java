package in.eigene.miary.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import in.eigene.miary.R;
import in.eigene.miary.core.persistence.Note;
import in.eigene.miary.fragments.FeedFragment;
import in.eigene.miary.helpers.NewNoteClickListener;
import in.eigene.miary.widgets.Drawer;

/**
 * Displays diary.
 */
public class FeedActivity extends BaseActivity implements Drawer.SectionChooseListener {

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSecureFlag();
        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();
        getFeedFragment().fixTopPadding(getSupportActionBar().getThemedContext());
        // Initialize preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Initialize navigation drawer.
        drawer = new Drawer(this, getToolbar(), this);
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
    public void onSectionChosen(final Note.Section section) {
        getFeedFragment().setSection(section);
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
        findViewById(R.id.fab_button).setOnClickListener(new NewNoteClickListener());
    }

    /**
     * Gets feed fragment.
     */
    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }
}
