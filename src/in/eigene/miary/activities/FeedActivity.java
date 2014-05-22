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
        selectDrawerItem(0); // TODO: read position from savedInstanceState.
        // TODO: show the drawer for the first time.
        ParseAnalytics.trackAppOpened(getIntent());
    }

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

    private void selectDrawerItem(final int position) {
        selectFragment(R.id.feed_content_frame, new FeedFragment());
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
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
                saveNote(note);
                startNoteActivity(note);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Saves note to either Parse Cloud or Local Datastore depending on current settings.
     */
    private void saveNote(final Note note) {
        final SaveCallback callback = new SaveCallback() {
            @Override
            public void done(final ParseException e) {
                if (e != null) {
                    throw new InternalRuntimeException("Could not pin note.", e);
                }
            }
        };

        if (ParseUser.getCurrentUser() != null) {
            note.saveEventually(callback);
        } else {
            note.pinInBackground(callback);
        }
    }

    /**
     * Start note view/edit activity.
     */
    private void startNoteActivity(final Note note) {
        Log.i(LOG_TAG, "Starting note activity: " + note);
        startActivity(new Intent()
                .setClass(FeedActivity.this, NoteActivity.class)
                .putExtra(NoteFragment.EXTRA_NOTE_UUID, note.getUuid()));
    }
}
