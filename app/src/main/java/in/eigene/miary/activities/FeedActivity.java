package in.eigene.miary.activities;

import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.fragments.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.widgets.*;

public class FeedActivity extends BaseActivity implements Drawer.Listener {

    private static final String LOG_TAG = FeedActivity.class.getName();

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSecureFlag();
        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();
        getFeedFragment().fixFeedViewPadding(getSupportActionBar().getThemedContext());
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
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer.getToggle().syncState();
    }

    @Override
    protected void initializeToolbar() {
        super.initializeToolbar();
        getToolbar().setBackgroundResource(R.color.toolbar_background_feed);
    }

    @Override
    public void onFeedModeChanged(final FeedAdapter.Mode feedMode) {
        final FeedFragment fragment = getFeedFragment();
        fragment.getFeedAdapter().setMode(feedMode);
        fragment.refresh();
    }

    private void initializeFloatingActionButton() {
        findViewById(R.id.fab_button).setOnClickListener(new NewNoteClickListener(
                getFeedFragment().getFeedAdapter()));
    }

    /**
     * Gets feed fragment.
     */
    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }
}
