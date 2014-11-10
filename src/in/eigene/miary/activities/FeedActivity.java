package in.eigene.miary.activities;

import android.accounts.*;
import android.accounts.OperationCanceledException;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.fragments.*;
import in.eigene.miary.widgets.*;

import java.io.*;

public class FeedActivity extends BaseActivity implements Drawer.Listener {

    private static final String LOG_TAG = FeedActivity.class.getName();

    private Drawer drawer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize view.
        setContentView(R.layout.activity_feed);
        initializeToolbar();
        initializeFloatingActionButton();
        // TODO: findViewById(R.id.account_layout).setOnClickListener(new FeedActivity.AccountClickListener());
        // Initialize preferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Initialize navigation drawer.
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
    protected void initializeToolbar() {
        super.initializeToolbar();
        getToolbar().setBackgroundResource(R.color.toolbar_background_feed);
    }

    @Override
    public void onFeedModeChanged(final FeedAdapter.Mode feedMode) {
        getFeedFragment().getFeedAdapter().setMode(feedMode).refresh();
    }

    private void initializeFloatingActionButton() {
        findViewById(R.id.fab_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Note note = Note.createNew();
                note.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(final ParseException e) {
                        InternalRuntimeException.throwForException("Could not pin a new note.", e);
                        Log.i(LOG_TAG, "Pinned new note: " + note);
                        NoteActivity.start(FeedActivity.this, note, false);
                    }
                });
            }
        });
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
