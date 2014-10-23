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
import in.eigene.miary.helpers.lang.*;

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

        initializeCounterView(
                R.id.drawer_item_feed, R.string.drawer_item_feed, new OnClickListener(false, false));
        textViewStarredCounter = (TextView)initializeCounterView(
                R.id.drawer_item_starred, R.string.drawer_item_starred, new OnClickListener(true, false)
        ).findViewById(R.id.drawer_item_counter);
        textViewDraftCounter = (TextView)initializeCounterView(
                R.id.drawer_item_drafts, R.string.drawer_item_drafts, new OnClickListener(false, true)
        ).findViewById(R.id.drawer_item_counter);

        refreshCounters();
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
        CounterCache.invalidate(new Consumer<Object>() {
            @Override
            public void accept(final Object o) {
                refreshCounters();
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

    /**
     * Initializes a drawer item.
     */
    private View initializeCounterView(
            final int itemViewId,
            final int titleResourceId,
            final View.OnClickListener listener) {
        final View itemView = view.findViewById(itemViewId);
        ((TextView)itemView.findViewById(R.id.drawer_item_title)).setText(titleResourceId);
        itemView.setOnClickListener(listener);
        return itemView;
    }

    /**
     * Refreshes starred and drafts counter values and their visibility.
     */
    private void refreshCounters() {
        textViewStarredCounter.setText(Integer.toString(CounterCache.getStarredCount()));
        textViewStarredCounter.setVisibility(CounterCache.getStarredCount() != 0 ? View.VISIBLE : View.GONE);
        textViewDraftCounter.setText(Integer.toString(CounterCache.getDraftCount()));
        textViewDraftCounter.setVisibility(CounterCache.getDraftCount() != 0 ? View.VISIBLE : View.GONE);
    }

    public interface Listener {

        public void onFeedModeChanged(final boolean starredOnly, final boolean drafts);
    }

    private class OnClickListener implements View.OnClickListener {

        private final boolean starredOnly;
        private final boolean drafts;

        public OnClickListener(final boolean starredOnly, final boolean drafts) {
            this.starredOnly = starredOnly;
            this.drafts = drafts;
        }

        @Override
        public void onClick(final View view) {
            listener.onFeedModeChanged(starredOnly, drafts);
            layout.closeDrawer(Drawer.this.view);
        }
    }
}
