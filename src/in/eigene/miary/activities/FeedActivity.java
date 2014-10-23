package in.eigene.miary.activities;

import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.fragments.*;
import in.eigene.miary.widgets.*;

public class FeedActivity extends BaseActivity implements Drawer.Listener {

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        drawer = new Drawer(this, this);
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
    public void onFeedModeChanged(boolean starredOnly, boolean drafts) {
        getFeedFragment().setDrafts(drafts).setStarredOnly(starredOnly).refresh(false);
    }

    /**
     * Gets feed fragment.
     */
    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }
}
