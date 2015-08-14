package in.eigene.miary.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.Date;

import in.eigene.miary.R;
import in.eigene.miary.helpers.PreferenceHelper;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private static final long TIMEOUT = 5 * 60 * 1000;

    private static long lastActivityTime = 0;

    private Toolbar toolbar;

    public static void refreshLastActivityTime() {
        lastActivityTime = new Date().getTime();
    }

    public static void resetLastActivityTime() {
        lastActivityTime = 0;
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
        checkPasscodeProtection();
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

    /**
     * Makes the activity secure: removes snapshot from recent apps.
     */
    protected void setSecureFlag() {
        if (PreferenceHelper.get(this).getBoolean(getString(R.string.prefkey_flag_secure_enabled), true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    /**
     * Checks if passcode is required and starts passcode activity if needed.
     */
    private void checkPasscodeProtection() {
        final long currentTime = new Date().getTime();
        if ((currentTime - lastActivityTime) > TIMEOUT) {
            if (isPasscodeEnabled()) {
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

    private boolean isPasscodeEnabled() {
        return PreferenceHelper.get(this).getBoolean(getString(R.string.prefkey_pin_enabled), false);
    }
}
