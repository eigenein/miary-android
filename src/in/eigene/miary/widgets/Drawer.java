package in.eigene.miary.widgets;

import android.accounts.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.activities.*;
import in.eigene.miary.adapters.*;
import in.eigene.miary.core.caches.*;
import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.*;

import java.io.*;

public class Drawer extends DrawerListener {

    private final Activity activity;
    private final Listener listener;

    private static final String KEY_DRAWER_SHOWN = "drawer_shown";

    private final DrawerLayout layout;
    private final ActionBarDrawerToggle toggle;
    private final View view;

    private final DrawerCounter noteCounter;
    private final DrawerCounter starredCounter;
    private final DrawerCounter draftCounter;

    private final TextView accountTypeView;
    private final TextView accountNameView;

    public Drawer(final Activity activity, final Toolbar toolbar, final Listener listener) {
        this.activity = activity;
        this.listener = listener;

        view = activity.findViewById(R.id.drawer);
        layout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        layout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        toggle = new DrawerToggle(activity, layout, toolbar, R.string.drawer_open, R.string.drawer_close, this);
        layout.setDrawerListener(toggle);

        accountTypeView = (TextView)view.findViewById(R.id.drawer_account_type);
        accountNameView = (TextView)view.findViewById(R.id.drawer_account_name);
        view.findViewById(R.id.drawer_account).setOnClickListener(new AccountClickListener());

        noteCounter = new DrawerCounter(
                view,
                R.id.drawer_item_diary,
                R.drawable.ic_inbox_grey600_24dp,
                R.string.drawer_item_diary,
                new FeedModeChangedClickListener(FeedAdapter.Mode.DIARY),
                CounterCache.NOTE_COUNTER
        );
        starredCounter = new DrawerCounter(
                view,
                R.id.drawer_item_starred,
                R.drawable.ic_star_grey600_24dp,
                R.string.drawer_item_starred,
                new FeedModeChangedClickListener(FeedAdapter.Mode.STARRED),
                CounterCache.STARRED_COUNTER
        );
        draftCounter = new DrawerCounter(
                view,
                R.id.drawer_item_drafts,
                R.drawable.ic_drafts_grey600_24dp,
                R.string.drawer_item_drafts,
                new FeedModeChangedClickListener(FeedAdapter.Mode.DRAFTS),
                CounterCache.DRAFT_COUNTER
        );
        new DrawerItem(view, R.id.drawer_item_settings, R.drawable.ic_settings_grey600_24dp, R.string.settings,
                new StartActivityClickListener(activity, SettingsActivity.class));
        new DrawerItem(view, R.id.drawer_item_feedback, R.drawable.ic_help_grey600_24dp, R.string.activity_feedback,
                new StartActivityClickListener(activity, FeedbackActivity.class));
        new DrawerItem(view, R.id.drawer_item_about, R.drawable.ic_info_grey600_24dp, R.string.activity_about,
                new StartActivityClickListener(activity, AboutActivity.class));

        refresh();
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        refresh();
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
                    layout.openDrawer(Gravity.LEFT);
                    preferences.edit().putBoolean(KEY_DRAWER_SHOWN, true).commit();
                }
            }, 1000);
        }
    }

    /**
     * Refreshes drawer.
     */
    private void refresh() {
        refreshAccount();
        noteCounter.refresh();
        starredCounter.refresh();
        draftCounter.refresh();
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
     * Closes drawer.
     */
    private void close() {
        layout.closeDrawer(view);
    }

    /**
     * Listens for feed mode changes.
     */
    public interface Listener {

        public void onFeedModeChanged(final FeedAdapter.Mode mode);
    }

    /**
     * Posts runnable when clicked.
     */
    private class RunnableClickListener implements View.OnClickListener {

        private final Runnable runnable;

        public RunnableClickListener(final Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void onClick(final View view) {
            new Handler().postDelayed(runnable, 500L);
            close();
        }
    }

    /**
     * Changes feed mode on clicked.
     */
    private class FeedModeChangedClickListener extends RunnableClickListener {

        public FeedModeChangedClickListener(final FeedAdapter.Mode feedMode) {
            super(new Runnable() {
                @Override
                public void run() {
                    listener.onFeedModeChanged(feedMode);
                }
            });
        }
    }

    /**
     * Starts activity when clicked.
     */
    private class StartActivityClickListener extends RunnableClickListener {

        public StartActivityClickListener(final Context context, final Class<?> activityClass) {
            super(new Runnable() {
                @Override
                public void run() {
                    ActivityHelper.start(context, activityClass);
                }
            });
        }
    }

    /**
     * Starts authentication process when clicked.
     */
    private class AccountClickListener extends RunnableClickListener {

        public AccountClickListener() {
            super(new Runnable() {
                @Override
                public void run() {
                    // TODO: check if already logged in and open account activity.
                    AccountManager.get(activity).addAccount(
                            "miary.eigene.in", null, null, null, activity, new AccountManagerCallback(), null);
                }
            });
        }
    }

    /**
     * Processes authentication result.
     */
    private class AccountManagerCallback implements android.accounts.AccountManagerCallback<Bundle> {

        @Override
        public void run(final AccountManagerFuture<Bundle> future) {
            try {
                future.getResult();
            } catch (final android.accounts.OperationCanceledException e) {
                // Do nothing.
            } catch (final IOException e) {
                InternalRuntimeException.throwForException("Failed to add account.", e);
            } catch (final AuthenticatorException e) {
                InternalRuntimeException.throwForException("Failed to add account.", e);
            }
        }
    }
}
