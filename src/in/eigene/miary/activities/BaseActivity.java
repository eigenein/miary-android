package in.eigene.miary.activities;

import android.annotation.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.helpers.*;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActionBar11();
        initializeActionBar14();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super. onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replaces current fragment with the specified one.
     */
    protected void selectFragment(final int resource_id, final Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(resource_id, fragment).commit();
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
