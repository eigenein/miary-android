package in.eigene.miary.widgets;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.caches.*;
import in.eigene.miary.helpers.*;

public class Drawer extends DrawerListener {

    private final Activity activity;
    private final Listener listener;

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private final DrawerLayout layout;
    private final ActionBarDrawerToggle toggle;
    private final View view;

    private final DrawerCounter noteCounter;
    private final DrawerCounter starredCounter;
    private final DrawerCounter draftCounter;

    public Drawer(final Activity activity, final Toolbar toolbar, final Listener listener) {
        this.activity = activity;
        this.listener = listener;

        view = activity.findViewById(R.id.drawer);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new DrawerToggle(activity, layout, toolbar, R.string.drawer_open, R.string.drawer_close, this);
        layout.setDrawerListener(toggle);

        noteCounter = new DrawerCounter(
                view,
                R.id.drawer_item_diary,
                R.drawable.ic_inbox_grey600_24dp,
                R.string.drawer_item_diary,
                new FeedModeChangedClickListener(FeedAdapter.Mode.DIARY),
                CounterCache.NOTE_COUNTER
        );
        starredCounter = new DrawerCounter(
                view,
                R.id.drawer_item_starred,
                R.drawable.ic_star_grey600_24dp,
                R.string.drawer_item_starred,
                new FeedModeChangedClickListener(FeedAdapter.Mode.STARRED),
                CounterCache.STARRED_COUNTER
        );
        draftCounter = new DrawerCounter(
                view,
                R.id.drawer_item_drafts,
                R.drawable.ic_drafts_grey600_24dp,
                R.string.drawer_item_drafts,
                new FeedModeChangedClickListener(FeedAdapter.Mode.DRAFTS),
                CounterCache.DRAFT_COUNTER
        );
        new DrawerItem(view, R.id.drawer_item_settings, R.drawable.ic_settings_grey600_24dp, R.string.settings,
                new RunnableClickListener(new Runnable() {
                    @Override
                    public void run() {
                        SettingsActivity.start(activity);
                    }
                }));
        new DrawerItem(view, R.id.drawer_item_feedback, R.drawable.ic_help_grey600_24dp, R.string.activity_feedback,
                new RunnableClickListener(new Runnable() {
                    @Override
                    public void run() {
                        FeedbackActivity.start(activity);
                    }
                }));

        refreshCounters();
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        refreshCounters();
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
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
     * Refreshes starred and drafts counter values and their visibility.
     */
    private void refreshCounters() {
        noteCounter.refresh();
        starredCounter.refresh();
        draftCounter.refresh();
    }

    /**
     * Closes drawer.
     */
    private void close() {
        layout.closeDrawer(view);
    }

    /**
     * Listens for feed mode changes.
     */
    public interface Listener {

        public void onFeedModeChanged(final FeedAdapter.Mode mode);
    }

    /**
     * Listen for clicks on a drawer item.
     */
    private class RunnableClickListener implements View.OnClickListener {

        private final Runnable runnable;

        public RunnableClickListener(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void onClick(final View view) {
            new Handler().postDelayed(runnable, 500L);
            close();
        }
    }

    /**
     * Listens for clicks on feed mode change.
     */
    private class FeedModeChangedClickListener extends RunnableClickListener {

        public FeedModeChangedClickListener(final FeedAdapter.Mode feedMode) {
            super(new Runnable() {
                @Override
                public void run() {
                    listener.onFeedModeChanged(feedMode);
                }
            });
        }
    }
}
