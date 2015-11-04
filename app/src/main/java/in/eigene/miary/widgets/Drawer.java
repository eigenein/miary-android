package in.eigene.miary.widgets;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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

    public final DrawerLayout layout;
    public final ActionBarDrawerToggle toggle;
    public final NavigationView view;

    private final FeedActivity activity;

    public Drawer(final FeedActivity activity, final Toolbar toolbar) {
        this.activity = activity;
        // Initialize drawer itself.
        view = (NavigationView)activity.findViewById(R.id.navigation_view);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new ActionBarDrawerToggle(activity, layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(final View view) {
                // TODO: refresh counters.
            }
        };
        layout.setDrawerListener(toggle);
        view.setNavigationItemSelectedListener(this);
    }

    /**
     * Shows drawer if it was not shown since application installed.
     */
    public void showForFirstTime() {
        final SharedPreferences preferences = PreferenceHelper.get(view.getContext());
        if (!preferences.getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.openDrawer(GravityCompat.START);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).apply();
                }
            }, 1000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        layout.closeDrawers();

        switch (item.getItemId()) {

            case R.id.menu_item_drawer_diary:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Note.Section section = Note.Section.DIARY;
                        activity.getFeedFragment().setSection(section);
                        activity.setTitle(section.getTitleResourceId());
                        Tracking.sendEvent(Tracking.Category.DRAWER, Tracking.Action.CHANGE_SECTION, section.toString());
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
                        Tracking.sendEvent(Tracking.Category.DRAWER, Tracking.Action.CHANGE_SECTION, section.toString());
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
                        Tracking.sendEvent(Tracking.Category.DRAWER, Tracking.Action.CHANGE_SECTION, section.toString());
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
}
