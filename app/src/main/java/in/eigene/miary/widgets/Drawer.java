package in.eigene.miary.widgets;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import in.eigene.miary.R;
import in.eigene.miary.adapters.DrawerAdapter;
import in.eigene.miary.exceptions.InternalRuntimeException;
import in.eigene.miary.helpers.ParseHelper;
import in.eigene.miary.persistence.Note;
import in.eigene.miary.sync.SyncAdapter;

public class Drawer {

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private final Activity activity;
    private final SectionChooseListener listener;

    private final DrawerLayout layout;
    private final ActionBarDrawerToggle toggle;
    private final View view;

    private final TextView accountTypeView;
    private final TextView accountNameView;

    public Drawer(final Activity activity, final Toolbar toolbar, final SectionChooseListener listener) {
        this.activity = activity;
        this.listener = listener;

        final DrawerAdapter adapter = new DrawerAdapter(activity);
        // Initialize drawer itself.
        view = activity.findViewById(R.id.drawer);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new ActionBarDrawerToggle(activity, layout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(final View view) {
                adapter.notifyDataSetChanged();
            }
        };
        layout.setDrawerListener(toggle);

        // Initialize account info.
        accountTypeView = (TextView)view.findViewById(R.id.drawer_account_type);
        accountNameView = (TextView)view.findViewById(R.id.drawer_account_name);
        view.findViewById(R.id.drawer_account).setOnClickListener(new AccountClickListener());

        // Initialize navigation list view.
        final ListView listView = (ListView)view.findViewById(R.id.drawer_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.onClick(view.getContext(), position);
                    }
                }, 500L);
                layout.closeDrawer(Drawer.this.view);
            }
        });
        listView.setAdapter(adapter);
    }

    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    /**
     * Shows drawer if it was not shown since application installed.
     */
    public void showForFirstTime() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (!preferences.getBoolean(KEY_DRAWER_SHOWN, false)) {
            // Open drawer for the first time.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.openDrawer(GravityCompat.START);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).apply();
                }
            }, 1000);
        }
    }

    /**
     * Refreshes account info.
     */
    private void refreshAccount() {
        final ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            accountTypeView.setText(R.string.account_basic);
            accountNameView.setText(user.getUsername());
            accountNameView.setVisibility(View.VISIBLE);
        } else {
            accountTypeView.setText(R.string.account_offline);
            accountNameView.setVisibility(View.GONE);
        }
    }

    /**
     * Chooses diary section.
     */
    public interface SectionChooseListener {

        void onSectionChosen(final Note.Section section);
    }

    /**
     * Starts authentication process when clicked.
     */
    private class AccountClickListener implements View.OnClickListener, Runnable {

        @Override
        public void onClick(final View view) {
            new Handler().postDelayed(this, 500L);
            layout.closeDrawer(Drawer.this.view);
        }

        @Override
        public void run() {
            final ParseUser user = ParseUser.getCurrentUser();
            if (user != null) {
                // TODO.
            } else {
                final AccountManager accountManager = AccountManager.get(activity);
                final Account[] accounts = accountManager.getAccountsByType(SyncAdapter.ACCOUNT_TYPE);
                if (accounts.length != 0) {
                    accountManager.getAuthToken(accounts[0], SyncAdapter.ACCOUNT_TYPE, null, false, new GetAuthTokenCallback(), null);
                } else {
                    accountManager.addAccount(SyncAdapter.ACCOUNT_TYPE, null, null, null, activity, new AddAccountCallback(), null);
                }
            }
        }
    }

    private class AddAccountCallback implements AccountManagerCallback<Bundle> {

        @Override
        public void run(final AccountManagerFuture<Bundle> future) {
            try {
                future.getResult();
                ParseHelper.linkInstallation();
            } catch (final android.accounts.OperationCanceledException e) {
                // Do nothing.
            } catch (final Exception e) {
                InternalRuntimeException.throwForException("Failed to add account.", e);
            }
        }
    }

    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        @Override
        public void run(final AccountManagerFuture<Bundle> future) {
            final String authToken;
            try {
                authToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
            } catch (final Exception e) {
                InternalRuntimeException.throwForException("Failed to get auth token.", e);
                return;
            }
            ParseUser.becomeInBackground(authToken, new LogInCallback() {
                @Override
                public void done(final ParseUser user, final ParseException e) {
                    Toast.makeText(
                            activity,
                            e == null ? R.string.account_auth_success : R.string.account_auth_retry,
                            Toast.LENGTH_LONG
                    ).show();
                    ParseHelper.linkInstallation();
                }
            });
        }
    }
}
