package in.eigene.miary.activities;

import android.annotation.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.R;
import in.eigene.miary.fragments.*;
import in.eigene.miary.helpers.*;

public class MainActivity extends FragmentActivity {

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
        setContentView(R.layout.main_activity);
        initializeDrawer();
        initializeActionBar11();
        initializeActionBar14();
        selectDrawerItem(0); // TODO: read position from saved state.
        // TODO: show the drawer for the first time.
        Parse.initialize(this, "jpnD20rkM3xxna9OhRtun2IbzE7QjPEULtEmIRKC", "ChviiekJmgXCOcQuuzNnifiIHjQ3vHa2GqYW4yCC");
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
        selectFragment(new ContentFragment());
        drawerLayout.closeDrawer(drawerList);
    }

    /**
     * Replaces current fragment with the specified one.
     */
    private void selectFragment(final Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    /**
     * Initializes action bar features for Honeycomb and higher.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initializeActionBar11() {
        if (AndroidVersion.isHoneycomb()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes action bar features for Ice Cream Sandwich and higher.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initializeActionBar14() {
        if (AndroidVersion.isIceCreamSandwich()) {
            getActionBar().setHomeButtonEnabled(true);
        }
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

        return super.onOptionsItemSelected(item);
    }
}
