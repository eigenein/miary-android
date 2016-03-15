package in.eigene.miary.widgets;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.eigene.miary.R;
import in.eigene.miary.activities.AboutActivity;
import in.eigene.miary.activities.BaseActivity;
import in.eigene.miary.activities.FeedActivity;
import in.eigene.miary.activities.FeedbackActivity;
import in.eigene.miary.activities.SettingsActivity;
import in.eigene.miary.helpers.ActivityHelper;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.persistence.Note;

/**
 * Drawer initialization. Moved out to not clog the activity code.
 */
public class Drawer implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";
    private static final long HANDLER_DELAY = 500;

    public final DrawerLayout drawerLayout;
    public final ActionBarDrawerToggle toggle;
    public final NavigationView navigationView;

    private final FeedActivity activity;
    private final TextView textViewCounter;

    private final List<Pair<TextView, Note.Section>> counters = new ArrayList<>();

    public Drawer(final FeedActivity activity, final Toolbar toolbar) {
        this.activity = activity;

        navigationView = (NavigationView)activity.findViewById(R.id.navigation_view);

        // Menu action items.
        final Menu menu = navigationView.getMenu();
        counters.add(new Pair<>(
                (TextView)MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_drawer_diary)),
                Note.Section.DIARY
        ));
        counters.add(new Pair<>(
                (TextView)MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_drawer_starred)),
                Note.Section.STARRED
        ));
        counters.add(new Pair<>(
                (TextView)MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_drawer_drafts)),
                Note.Section.DRAFTS
        ));

        // Header view.
        final View headerView = LayoutInflater.from(activity).inflate(R.layout.drawer_header, navigationView, false);
        navigationView.addHeaderView(headerView);

        // Header content.
        textViewCounter = (TextView)headerView.findViewById(R.id.drawer_header_counter);

        // Layout.
        drawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Toggle.
        toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Updates counter text views.
             */
            @Override
            public void onDrawerOpened(final View view) {
                final int totalCount = getCounter(Note.Contract.DELETED + " = 0");
                textViewCounter.setText(activity.getResources().getQuantityString(
                        R.plurals.drawer_header_counter, totalCount, totalCount));
                for (final Pair<TextView, Note.Section> counter : counters) {
                    final int count = getCounter(counter.second.getSelection());
                    counter.first.setText(count != 0 ? String.format("%d", count) : null);
                }
            }
        };
        drawerLayout.addDrawerListener(toggle);

        // Listener.
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {

            case R.id.menu_item_drawer_diary:
                new Handler().postDelayed(
                        new SelectSectionRunnable(Note.Section.DIARY), HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_starred:
                new Handler().postDelayed(
                        new SelectSectionRunnable(Note.Section.STARRED), HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_drafts:
                new Handler().postDelayed(
                        new SelectSectionRunnable(Note.Section.DRAFTS), HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_settings:
                new Handler().postDelayed(
                        new StartActivityRunnable(SettingsActivity.class), HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_feedback:
                new Handler().postDelayed(
                        new StartActivityRunnable(FeedbackActivity.class), HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_about:
                new Handler().postDelayed(
                        new StartActivityRunnable(AboutActivity.class), HANDLER_DELAY);
                return true;

            default:
                return false;
        }
    }

    /**
     * Shows drawer if it was not shown since application installed.
     */
    public void showForFirstTime() {
        final SharedPreferences preferences = PreferenceHelper.get(navigationView.getContext());
        if (!preferences.getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(GravityCompat.START);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).apply();
                }
            }, 1000);
        }
    }

    private int getCounter(final String selection) {
        final Cursor cursor = activity.getContentResolver().query(
                Note.Contract.CONTENT_URI, new String[]{"COUNT(*)"}, selection, null, null);
        assert cursor != null;

        cursor.moveToFirst();
        final int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private class SelectSectionRunnable implements Runnable {

        private final Note.Section section;

        public SelectSectionRunnable(final Note.Section section) {
            this.section = section;
        }

        @Override
        public void run() {
            activity.getFeedFragment().setSection(section);
            activity.setTitle(section.getTitleResourceId());
            Tracking.selectSection(section.toString());
        }
    }

    private class StartActivityRunnable implements Runnable {

        private final Class<? extends BaseActivity> activityClass;

        public StartActivityRunnable(final Class<? extends BaseActivity> activityClass) {
            this.activityClass = activityClass;
        }

        @Override
        public void run() {
            ActivityHelper.start(activity, activityClass);
        }
    }
}
