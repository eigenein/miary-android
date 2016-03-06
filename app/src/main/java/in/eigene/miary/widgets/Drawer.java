package in.eigene.miary.widgets;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import in.eigene.miary.R;
import in.eigene.miary.activities.AboutActivity;
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


    public Drawer(final FeedActivity activity, final Toolbar toolbar) {
        this.activity = activity;

        navigationView = (NavigationView)activity.findViewById(R.id.navigation_view);
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
            @Override
            public void onDrawerOpened(final View view) {
                final Cursor cursor = activity.getContentResolver().query(
                        Note.Contract.CONTENT_URI, new String[]{"COUNT(*)"}, Note.Contract.DELETED + " = 0", null, null);
                assert cursor != null;
                cursor.moveToFirst();
                textViewCounter.setText(activity.getResources().getQuantityString(
                        R.plurals.drawer_header_counter, cursor.getInt(0), cursor.getInt(0)));
                cursor.close();
            }
        };
        drawerLayout.setDrawerListener(toggle);
        // Listener.
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {

            case R.id.menu_item_drawer_diary:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Note.Section section = Note.Section.DIARY;
                        activity.getFeedFragment().setSection(section);
                        activity.setTitle(section.getTitleResourceId());
                        Tracking.selectSection(section.toString());
                    }
                }, HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_starred:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Note.Section section = Note.Section.STARRED;
                        activity.getFeedFragment().setSection(section);
                        activity.setTitle(section.getTitleResourceId());
                        Tracking.selectSection(section.toString());
                    }
                }, HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_drafts:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Note.Section section = Note.Section.DRAFTS;
                        activity.getFeedFragment().setSection(section);
                        activity.setTitle(section.getTitleResourceId());
                        Tracking.selectSection(section.toString());
                    }
                }, HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_settings:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, SettingsActivity.class);
                    }
                }, HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_feedback:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, FeedbackActivity.class);
                    }
                }, HANDLER_DELAY);
                return true;

            case R.id.menu_item_drawer_about:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ActivityHelper.start(activity, AboutActivity.class);
                    }
                }, HANDLER_DELAY);
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
}
