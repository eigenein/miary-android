package in.eigene.miary.activities;

import android.annotation.*;
import android.app.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.helpers.*;

import java.util.*;

public abstract class BaseActivity extends Activity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private static long TIMEOUT = 5 * 60 * 1000;

    private static long lastActivityTime = 0;

    public static void refreshLastActivityTime() {
        lastActivityTime = new Date().getTime();
    }

    public static void resetLastActivityTime() {
        lastActivityTime = 0;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActionBar11();
        initializeActionBar14();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Passcode protection.
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
    @Override
    protected void onPause() {
        super.onPause();
        refreshLastActivityTime();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
