package in.eigene.miary.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.Date;

import in.eigene.miary.R;
import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.helpers.Themes;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    private static long lastActivityTime = 0;

    private Toolbar toolbar;
    private String currentTheme;

    public static void refreshLastActivityTime() {
        lastActivityTime = new Date().getTime();
    }

    public static void resetLastActivityTime() {
        lastActivityTime = 0;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
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
        if (
                !isFinishing() &&
                (currentTheme != null) &&
                currentTheme.compareTo(PreferenceHelper.getCurrentThemeName(this)) != 0
        ) {
            Log.i(LOG_TAG, "Recreate activity due to theme change.");
            recreate();
        }
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
        final SharedPreferences preferences = PreferenceHelper.get(this);
        final long currentTime = new Date().getTime();
        final long timeout = Long.valueOf(preferences.getString(getString(R.string.prefkey_pin_timeout), "300000"));

        if ((currentTime - lastActivityTime) > timeout) {
            final boolean isPasscodeEnabled = preferences.getBoolean(getString(R.string.prefkey_pin_enabled), false);
            if (isPasscodeEnabled) {
                Log.w(LOG_TAG, "Passcode required.");
                finish();
                PinActivity.start(this, getIntent());
            } else {
                Log.i(LOG_TAG, "Passcode protection is disabled.");
            }
        } else {
            Log.i(LOG_TAG, "Passcode is not required.");
            refreshLastActivityTime();
        }
    }

    private void setTheme() {
        currentTheme = PreferenceHelper.getCurrentThemeName(this);
        setTheme(Themes.getThemeResourceId(currentTheme));
    }
}
