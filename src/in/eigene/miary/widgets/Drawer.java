package in.eigene.miary.widgets;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.caches.*;
import in.eigene.miary.helpers.*;

public class Drawer implements DrawerLayout.DrawerListener {

    private final Activity activity;
    private final Listener listener;

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private final DrawerLayout layout;
    private final ActionBarDrawerToggle toggle;
    private final View view;

    private TextView textViewStarredCounter;
    private TextView textViewDraftCounter;

    public Drawer(final Activity activity, final Listener listener) {
        this.activity = activity;
        this.listener = listener;

        view = activity.findViewById(R.id.drawer);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new DrawerToggle(activity, layout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close, this);
        layout.setDrawerListener(toggle);

        initializeDrawerItems();
        refreshDrawerCounters();
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
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

    /**
     * Shows drawer if it was not shown since application installed.
     */
    public void showForFirstTime() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (!preferences.getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    layout.openDrawer(Gravity.LEFT);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).commit();
                }
            }, 1000);
        }
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
                        listener.onFeedModeChanged(false, false);
                        layout.closeDrawer(view);
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
                        listener.onFeedModeChanged(true, false);
                        layout.closeDrawer(view);
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
                        listener.onFeedModeChanged(false, true);
                        layout.closeDrawer(view);
                    }
                }).findViewById(R.id.drawer_item_counter);
        textViewDraftCounter.setVisibility(View.GONE);
    }

    /**
     * Initializes a drawer item.
     */
    private View initializeDrawerItem(
            final int itemViewId,
            final int iconResourceId,
            final int titleResourceId,
            final boolean counterVisible,
            final View.OnClickListener listener) {
        final View itemView = view.findViewById(itemViewId);
        itemView.findViewById(R.id.drawer_item_counter).setVisibility(counterVisible ? View.VISIBLE : View.GONE);
        ((ImageView)itemView.findViewById(R.id.drawer_item_icon)).setImageResource(iconResourceId);
        ((TextView)itemView.findViewById(R.id.drawer_item_title)).setText(titleResourceId);
        itemView.setOnClickListener(listener);
        return itemView;
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

    public interface Listener {

        public void onFeedModeChanged(final boolean starredOnly, final boolean drafts);
    }
}
