package in.eigene.miary.activities;

import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.caches.*;
import in.eigene.miary.fragments.*;
import in.eigene.miary.helpers.*;
import in.eigene.miary.widgets.*;

public class FeedActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View drawer;

    private TextView textViewStarredCounter;
    private TextView textViewDraftCounter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        initializeDrawer();
        showDrawerForFirstTime();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        drawerToggle.onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(final View view, final float v) {
        // Do nothing.
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        CounterCache.invalidate(new Action<Object>() {
            @Override
            public void done(final Object o) {
                refreshDrawerCounters();
            }
        });
    }

    @Override
    public void onDrawerClosed(final View view) {
        // Do nothing.
    }

    @Override
    public void onDrawerStateChanged(final int i) {
        // Do nothing.
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /**
     * Initializes drawer.
     */
    private void initializeDrawer() {
        initializeDrawerLayout();
        initializeDrawerItems();
    }

    private void initializeDrawerLayout() {
        drawer = findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerToggle = new DrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close, this);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void initializeDrawerItems() {
        initializeDrawerItem(
                R.id.drawer_item_feed,
                R.drawable.ic_drawer_feed,
                R.string.drawer_item_feed,
                false,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        getFeedFragment().setDrafts(false).setStarredOnly(false).refresh(false);
                        drawerLayout.closeDrawer(drawer);
                    }
                });
        // Starred.
        textViewStarredCounter = (TextView)initializeDrawerItem(
                R.id.drawer_item_starred,
                R.drawable.ic_drawer_starred,
                R.string.drawer_item_starred,
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        getFeedFragment().setDrafts(false).setStarredOnly(true).refresh(false);
                        drawerLayout.closeDrawer(drawer);
                    }
                }).findViewById(R.id.drawer_item_counter);
        textViewStarredCounter.setVisibility(View.GONE);
        // Drafts.
        textViewDraftCounter = (TextView)initializeDrawerItem(
                R.id.drawer_item_drafts,
                R.drawable.ic_drawer_drafts,
                R.string.drawer_item_drafts,
                true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        getFeedFragment().setDrafts(true).setStarredOnly(false).refresh(false);
                        drawerLayout.closeDrawer(drawer);
                    }
                }).findViewById(R.id.drawer_item_counter);
        textViewDraftCounter.setVisibility(View.GONE);
    }

    /**
     * Initializes a drawer item.
     */
    private View initializeDrawerItem(
            final int viewId,
            final int iconResourceId,
            final int titleResourceId,
            final boolean counterVisible,
            final View.OnClickListener listener) {
        final View view = drawer.findViewById(viewId);
        view.findViewById(R.id.drawer_item_counter).setVisibility(counterVisible ? View.VISIBLE : View.GONE);
        ((ImageView)view.findViewById(R.id.drawer_item_icon)).setImageResource(iconResourceId);
        ((TextView)view.findViewById(R.id.drawer_item_title)).setText(titleResourceId);
        view.setOnClickListener(listener);
        return view;
    }

    /**
     * Shows drawer if it was not shown since application installed.
     */
    private void showDrawerForFirstTime() {
        if (!getPreferences(0).getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    getPreferences(0).edit().putBoolean(KEY_DRAWER_SHOWN, true).commit();
                }
            }, 1000);
        }
    }

    /**
     * Refreshes starred and drafts counter values and their visibility.
     */
    private void refreshDrawerCounters() {
        textViewStarredCounter.setText(Integer.toString(CounterCache.getStarredCount()));
        textViewStarredCounter.setVisibility(CounterCache.getStarredCount() != 0 ? View.VISIBLE : View.GONE);
        textViewDraftCounter.setText(Integer.toString(CounterCache.getDraftCount()));
        textViewDraftCounter.setVisibility(CounterCache.getDraftCount() != 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Gets feed fragment.
     */
    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }
}
