package in.eigene.miary.activities;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.fragments.*;

import java.util.*;

public class FeedActivity extends BaseActivity {

    private static final String LOG_TAG = FeedActivity.class.getSimpleName();

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";
    private static final String KEY_DRAFTS = "drafts";
    private static final String KEY_STARRED = "starred";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View drawer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_activity);

        initializeDrawer();
        showDrawerForFirstTime();
        selectFragment(R.id.feed_content_frame, new FeedFragment(false, false));

        ParseAnalytics.trackAppOpened(getIntent());
    }

    /**
     * Start note view/edit activity.
     */
    public void startNoteActivity(final Note note) {
        Log.i(LOG_TAG, "Starting note activity: " + note);
        startActivity(new Intent()
                .setClass(FeedActivity.this, NoteActivity.class)
                .putExtra(NoteFragment.EXTRA_NOTE_UUID, note.getUuid()));
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        drawerToggle.onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_item_note_new:
                final Note note = new Note()
                        .setUuid(UUID.randomUUID())
                        .setCreationDate(new Date());
                note.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        InternalRuntimeException.throwForException("Could not pin a new note.", e);
                        Log.i(LOG_TAG, "Pinned new note: " + note);
                        startNoteActivity(note);
                    }
                });
                ParseAnalytics.trackEvent("new_note");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // TODO: save drafts and starred mode.
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
        // Initialize drawer layout.
        drawer = findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);

        // Initialize drawer items.
        // TODO: Add click listener and close drawer.
        // drawerLayout.closeDrawer(drawer);

        initializeDrawerItem(R.id.drawer_item_feed, R.drawable.ic_drawer_feed, R.string.drawer_item_feed);
        initializeDrawerItem(R.id.drawer_item_starred, R.drawable.ic_drawer_starred, R.string.drawer_item_starred);
        initializeDrawerItem(R.id.drawer_item_drafts, R.drawable.ic_drawer_drafts, R.string.drawer_item_drafts);
    }

    private void initializeDrawerItem(
            final int viewId,
            final int iconResourceId,
            final int titleResourceId) {
        final View view = drawer.findViewById(viewId);
        ((ImageView)view.findViewById(R.id.drawer_item_icon)).setImageResource(iconResourceId);
        ((TextView)view.findViewById(R.id.drawer_item_title)).setText(titleResourceId);
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
}
