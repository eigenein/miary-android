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

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] drawerTitles;
    private ListView drawerList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_activity);

        initializeDrawer();
        showDrawerForFirstTime();
        selectDrawerItem(0); // TODO: read position from savedInstanceState.

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
                        .setCreationDate(new Date())
                        .setDraft(true);
                note.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        InternalRuntimeException.throwForException("Could not pin a new note.", e);
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
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /**
     * Initializes drawer.
     */
    private void initializeDrawer() {
        // Initialize drawer list.
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_item, R.id.drawer_item_title, drawerTitles));
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView adapterView, final View view, final int position, final long id) {
                selectDrawerItem(position);
            }
        });
        // Initialize drawer layout.
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
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

    private void selectDrawerItem(final int position) {
        selectFragment(R.id.feed_content_frame, new FeedFragment());
        drawerLayout.closeDrawer(drawerList);
    }
}
