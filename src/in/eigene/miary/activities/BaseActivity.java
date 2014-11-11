package in.eigene.miary.activities;

import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.*;

import java.util.*;

public abstract class BaseActivity extends ActionBarActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private static long TIMEOUT = 5 * 60 * 1000;

    private static long lastActivityTime = 0;

    private Toolbar toolbar;

    public static void refreshLastActivityTime() {
        lastActivityTime = new Date().getTime();
    }

    public static void resetLastActivityTime() {
        lastActivityTime = 0;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPassCodeProtection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshLastActivityTime();
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Initializes Material Toolbar.
     */
    protected void initializeToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void checkPassCodeProtection() {
        final long currentTime = new Date().getTime();
        if ((currentTime - lastActivityTime) > TIMEOUT) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    getString(R.string.prefkey_pin_enabled), false)) {
                Log.w(LOG_TAG, "Passcode required.");
                finish();
                PinActivity.start(this, getIntent());
            } else {
                Log.i(LOG_TAG, "Passcode protection is disabled.");
            }
        } else {
            Log.i(LOG_TAG, "Passcode is not required: " + (currentTime - lastActivityTime) + "ms.");
            refreshLastActivityTime();
        }
    }
}
