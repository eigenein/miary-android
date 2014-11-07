package in.eigene.miary.activities;

import android.accounts.*;
import android.accounts.OperationCanceledException;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import in.eigene.miary.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.fragments.*;
import in.eigene.miary.widgets.*;

import java.io.*;

public class FeedActivity extends BaseActivity implements Drawer.Listener {

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feed);
        initializeToolbar();
        getToolbar().setBackgroundResource(R.color.toolbar_background_feed);

        // TODO: findViewById(R.id.account_layout).setOnClickListener(new FeedActivity.AccountClickListener());

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        drawer = new Drawer(this, getToolbar(), this);
        drawer.showForFirstTime();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        drawer.getToggle().onConfigurationChanged(newConfiguration);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return drawer.getToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawer.getToggle().syncState();
    }

    @Override
    public void onFeedModeChanged(boolean starredOnly, boolean drafts) {
        getFeedFragment().setDrafts(drafts).setStarredOnly(starredOnly).refresh(false);
    }

    /**
     * Gets feed fragment.
     */
    private FeedFragment getFeedFragment() {
        return (FeedFragment)getFragmentManager().findFragmentById(R.id.fragment_feed);
    }

    private void onAccountChanged(final String name) {
        // TODO: update profile info.
    }

    private class AccountClickListener implements View.OnClickListener, AccountManagerCallback<Bundle> {

        @Override
        public void onClick(final View view) {
            // TODO: select account, create new or sign out.
            AccountManager.get(FeedActivity.this)
                    .addAccount("miary.eigene.in", null, null, null, FeedActivity.this, this, null);
        }

        @Override
        public void run(final AccountManagerFuture<Bundle> future) {
            try {
                onAccountChanged(future.getResult().getString(AccountManager.KEY_ACCOUNT_NAME));
            } catch (final OperationCanceledException e) {
                // Do nothing.
            } catch (final IOException e) {
                InternalRuntimeException.throwForException("Could not add account.", e);
            } catch (final AuthenticatorException e) {
                InternalRuntimeException.throwForException("Could not add account.", e);
            }
        }
    }
}
