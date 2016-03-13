package in.eigene.miary.fragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Set;

import in.eigene.miary.R;
import in.eigene.miary.backup.inputs.JsonRestoreInput;
import in.eigene.miary.backup.outputs.JsonBackupOutput;
import in.eigene.miary.backup.outputs.PlainTextBackupOutput;
import in.eigene.miary.backup.storages.DropboxStorage;
import in.eigene.miary.backup.storages.ExternalStorage;
import in.eigene.miary.backup.tasks.BackupAsyncTask;
import in.eigene.miary.backup.tasks.RestoreAsyncTask;
import in.eigene.miary.fragments.dialogs.PinDialogFragment;
import in.eigene.miary.fragments.dialogs.TimePickerDialogFragment;
import in.eigene.miary.helpers.AccountHelper;
import in.eigene.miary.helpers.DropboxHelper;
import in.eigene.miary.helpers.Tracking;
import in.eigene.miary.helpers.lang.Consumer;
import in.eigene.miary.managers.PinManager;
import in.eigene.miary.managers.ReminderManager;

public class SettingsFragment extends PreferenceFragment {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    private static final int RESULT_CODE_RESTORE_JSON = 1;
    private static final String[] SHORT_WEEKDAYS = new DateFormatSymbols().getShortWeekdays();

    private boolean isDropboxAuthenticationInProgress;

    private Preference linkDropboxPreference;
    private Preference backupDropboxPreference;
    private Preference restoreDropboxPreference;
    private Preference exportPlainTextPreference;
    private Preference backupStoragePreference;
    private Preference restoreStoragePreference;

    /**
     * Caches weekdays from resources.
     */
    private String[] allWeekdays;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference pinEnabledPreference = (CheckBoxPreference)findPreference(R.string.prefkey_pin_enabled);
        pinEnabledPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        if (!pinEnabledPreference.isChecked()) {
                            disablePin(pinEnabledPreference);
                        } else {
                            enablePin(pinEnabledPreference);
                        }
                        return true;
                    }
                }
        );

        final ListPreference pinTimeoutPreference = (ListPreference)findPreference(R.string.prefkey_pin_timeout);
        pinTimeoutPreference.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                        refreshListPreferenceSummary(pinTimeoutPreference, newValue);
                        Tracking.setPasscodeTimeout(newValue.toString());
                        return true;
                    }
                }
        );

        findPreference(R.string.prefkey_theme).setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                        Tracking.setTheme(newValue.toString());
                        // getActivity().recreate() doesn't work properly here. I don't know why.
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                        return true;
                    }
                }
        );

        final ListPreference fontSizePreference = (ListPreference)findPreference(R.string.prefkey_font_size);
        fontSizePreference.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                        refreshListPreferenceSummary(fontSizePreference, newValue);
                        Tracking.setFontSize(newValue.toString());
                        return true;
                    }
                }
        );

        findPreference(R.string.prefkey_reminder_days).setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, Object newDays) {
                        final Set days = (Set)newDays;

                        refreshReminderDaysPreference(days);

                        if (days.size() == 0) {
                            ReminderManager.cancelReminder(getActivity());
                        } else {
                            ReminderManager.scheduleReminder(getActivity());
                        }

                        return true;
                    }
                }
        );

        findPreference(R.string.prefkey_reminder_time).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new TimePickerDialogFragment()
                                .setTitle(R.string.dialog_reminder_time_title)
                                .setTime(ReminderManager.getReminderHour(getActivity()),
                                        ReminderManager.getReminderMinute(getActivity()))
                                .setListener(new TimePickerDialogFragment.Listener() {
                                    @Override
                                    public void onPositiveButtonClicked(int hour, int minute) {
                                        ReminderManager.setTime(getActivity(), hour, minute);
                                        refreshReminderTimePreference();
                                        if (ReminderManager.isReminderEnabled(getActivity())) {
                                            ReminderManager.scheduleReminder(getActivity());
                                        }
                                    }
                                })
                                .show(getFragmentManager());

                        return true;
                    }
                }
        );

        allWeekdays = getActivity().getResources().getStringArray(R.array.weekdays_values);

        refreshReminderDaysPreference();
        refreshReminderTimePreference();
        refreshListPreferenceSummary(R.string.prefkey_pin_timeout);
        refreshListPreferenceSummary(R.string.prefkey_theme);
        refreshListPreferenceSummary(R.string.prefkey_font_size);

        setupBackupSettings();
    }

    @Override
    public void onResume() {
        super.onResume();

        final DropboxAPI<AndroidAuthSession> dropboxApi = DropboxHelper.createApi(getActivity());
        final AndroidAuthSession dropboxSession = dropboxApi.getSession();
        final AccountManager accountManager = AccountManager.get(getActivity());
        final Account account = AccountHelper.getAccount(accountManager);

        // Finish Dropbox authentication process if any.
        if (isDropboxAuthenticationInProgress) {
            isDropboxAuthenticationInProgress = false;
            finishDropboxAuthentication(dropboxApi, accountManager, account);
        }

        // Refresh Dropbox options.
        backupDropboxPreference.setEnabled(dropboxSession.isLinked());
        restoreDropboxPreference.setEnabled(dropboxSession.isLinked());
        final String dropboxEmail = accountManager.getUserData(account, AccountHelper.KEY_DROPBOX_EMAIL);
        if (dropboxEmail != null) {
            linkDropboxPreference.setSummary(getString(R.string.preference_summary_current_account, dropboxEmail));
        }

        // Refresh external storage options.
        final boolean isStorageAvailable = hasWritePermission() && isMediaMounted();
        exportPlainTextPreference.setEnabled(isStorageAvailable);
        backupStoragePreference.setEnabled(isStorageAvailable);
        restoreStoragePreference.setEnabled(isStorageAvailable);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (data == null) {
            // Cancelled.
            return;
        }
        switch (requestCode) {
            case RESULT_CODE_RESTORE_JSON:
                final Uri uri = data.getData();
                Log.i(LOG_TAG, "Activity result URI: " + uri);
                Log.i(LOG_TAG, "Path: " + uri.getPath());
                new RestoreAsyncTask(getActivity(), new ExternalStorage().new Input(getActivity(), uri), new JsonRestoreInput.Factory()).execute();
                break;
        }
    }

    /**
     * Passcode protection enable handler.
     */
    private void enablePin(final CheckBoxPreference checkBoxPreference) {
        new PinDialogFragment()
                .setTitle(R.string.dialog_new_pin_title)
                .setListener(new PinDialogFragment.Listener() {
                    @Override
                    public void onPositiveButtonClicked(final String pin) {
                        if (pin.length() == 4) {
                            PinManager.set(getActivity(), pin);
                            Toast.makeText(getActivity(), R.string.pin_enabled, Toast.LENGTH_SHORT).show();
                            Tracking.enablePasscode();
                        } else {
                            Toast.makeText(getActivity(), R.string.pin_too_short, Toast.LENGTH_SHORT).show();
                            checkBoxPreference.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        checkBoxPreference.setChecked(false);
                    }
                })
                .show(getFragmentManager());
    }

    /**
     * Passcode protection disable handler.
     */
    private void disablePin(final CheckBoxPreference checkBox) {
        new PinDialogFragment()
                .setTitle(R.string.dialog_current_pin_title)
                .setListener(new PinDialogFragment.Listener() {

                    @Override
                    public void onPositiveButtonClicked(final String pin) {
                        if (PinManager.check(getActivity(), pin)) {
                            Toast.makeText(getActivity(), R.string.pin_disabled, Toast.LENGTH_SHORT).show();
                            Tracking.disablePasscode();
                        } else {
                            Toast.makeText(getActivity(), R.string.pin_incorrect, Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        checkBox.setChecked(true);
                    }
                })
                .show(getFragmentManager());
    }

    private void refreshReminderDaysPreference() {
        refreshReminderDaysPreference(ReminderManager.getReminderDays(getActivity()));
    }

    private void refreshReminderDaysPreference(final Set<String> reminderDays) {
        final Preference preference = findPreference(R.string.prefkey_reminder_days);
        if (!reminderDays.isEmpty()) {
            final ArrayList<String> dayNames = new ArrayList<>();
            for (final String weekday : allWeekdays) {
                if (reminderDays.contains(weekday)) {
                    dayNames.add(SHORT_WEEKDAYS[Integer.valueOf(weekday)]);
                }
            }
            preference.setSummary(TextUtils.join(", ", dayNames));
        } else {
            preference.setSummary(R.string.preference_summary_reminder_never);
        }
    }

    private void refreshReminderTimePreference() {
        findPreference(R.string.prefkey_reminder_time).setSummary(DateFormat.getTimeFormat(getActivity()).format(
                ReminderManager.getReminderTime(getActivity()).getTime()));
    }

    private void refreshListPreferenceSummary(final int preferenceKeyResourceId) {
        final ListPreference preference = (ListPreference)findPreference(preferenceKeyResourceId);
        preference.setSummary(preference.getEntry());
    }

    private void refreshListPreferenceSummary(final ListPreference preference, final Object newValue) {
        final int newValueIndex = preference.findIndexOfValue(newValue.toString());
        preference.setSummary(preference.getEntries()[newValueIndex]);
    }

    private void setupBackupSettings() {
        // Plain text backup.
        exportPlainTextPreference = findPreference(R.string.prefkey_export_plain_text);
        exportPlainTextPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new BackupAsyncTask(getActivity(), new ExternalStorage(), new PlainTextBackupOutput.Factory()).execute();
                        return true;
                    }
                }
        );
        // JSON backup.
        backupStoragePreference = findPreference(R.string.prefkey_backup_storage);
        backupStoragePreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new BackupAsyncTask(getActivity(), new ExternalStorage(), new JsonBackupOutput.Factory()).execute();
                        return true;
                    }
                }
        );
        // Link Dropbox account.
        linkDropboxPreference = findPreference(R.string.prefkey_link_dropbox);
        linkDropboxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                isDropboxAuthenticationInProgress = true;
                DropboxHelper.createApi(getActivity()).getSession().startOAuth2Authentication(getActivity());
                return true;
            }
        });
        // Dropbox backup.
        backupDropboxPreference = findPreference(R.string.prefkey_backup_dropbox);
        backupDropboxPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new BackupAsyncTask(
                                getActivity(),
                                new DropboxStorage(DropboxHelper.createApi(getActivity())),
                                new JsonBackupOutput.Factory()).execute();
                        return true;
                    }
                }
        );
        // Dropbox restore.
        restoreDropboxPreference = findPreference(R.string.prefkey_restore_dropbox);
        restoreDropboxPreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new RestoreAsyncTask(
                                getActivity(),
                                new DropboxStorage(DropboxHelper.createApi(getActivity())).new Input(".json"),
                                new JsonRestoreInput.Factory()).execute();
                        return true;
                    }
                }
        );
        // JSON restore.
        restoreStoragePreference = findPreference(R.string.prefkey_restore_storage);
        restoreStoragePreference.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        try {
                            startActivityForResult(intent, RESULT_CODE_RESTORE_JSON);
                        } catch (final ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), R.string.toast_no_file_manager_app, Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
        );
        // Request permission if needed.
        if (!hasWritePermission() && isMediaMounted()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void finishDropboxAuthentication(
            final DropboxAPI<AndroidAuthSession> api,
            final AccountManager accountManager,
            final Account account
    ) {
        final AndroidAuthSession session = api.getSession();
        // Finish authentication.
        if (!session.authenticationSuccessful()) {
            return;
        }
        try {
            session.finishAuthentication();
        } catch (final IllegalStateException e) {
            Tracking.error("Dropbox authentication failed.", e);
            return;
        }
        // Save account info.
        accountManager.setUserData(account, AccountHelper.KEY_DROPBOX_ACCESS_TOKEN, session.getOAuth2AccessToken());
        DropboxHelper.getAccountInfo(getActivity(), api, new Consumer<DropboxAPI.Account>() {
            @Override
            public void accept(final DropboxAPI.Account accountInfo) {
                accountManager.setUserData(account, AccountHelper.KEY_DROPBOX_EMAIL, accountInfo.email);
                linkDropboxPreference.setSummary(getString(
                        R.string.preference_summary_current_account,
                        accountInfo.email));
            }
        });
        Tracking.linkDropbox();
    }

    private boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isMediaMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Find preference by key resource ID.
     */
    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }
}
