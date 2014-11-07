package in.eigene.miary.fragments;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.format.DateFormat;
import android.util.*;
import android.widget.*;
import com.parse.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.core.backup.inputs.*;
import in.eigene.miary.core.backup.outputs.*;
import in.eigene.miary.core.backup.storages.*;
import in.eigene.miary.core.backup.tasks.*;
import in.eigene.miary.core.managers.*;
import in.eigene.miary.fragments.dialogs.*;

import java.text.*;
import java.util.*;

public class SettingsFragment extends PreferenceFragment {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    private static final int RESULT_CODE_RESTORE_JSON = 1;

    private static final String[] SHORT_WEEKDAYS = new DateFormatSymbols().getShortWeekdays();

    /**
     * Fragment state.
     */
    private State state = State.DEFAULT;
    /**
     * Fragment state object.
     */
    private Object stateTag;

    /**
     * Caches weekdays from resources.
     */
    private String[] allWeekdays;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(R.string.prefkey_substitution_table).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_replacement_table_title);
                        builder.setItems(Substitutions.REPRS, null);
                        builder.setCancelable(true);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    }
                }
        );

        findPreference(R.string.prefkey_pin_enabled).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final CheckBoxPreference checkBox = (CheckBoxPreference)preference;
                        if (!checkBox.isChecked()) {
                            disablePin(checkBox);
                        } else {
                            enablePin(checkBox);
                        }
                        return true;
                    }
                }
        );

        findPreference(R.string.prefkey_reminder_days).setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, Object newDays) {
                        final Set days = (Set) newDays;

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

        setupBackupSettings();
    }

    @Override
    public void onResume() {
        super.onResume();

        DropboxStorage storage;

        switch (state) {
            case DROPBOX_BACKUP_IN_PROGRESS:
                state = State.DEFAULT;
                storage = (DropboxStorage)stateTag;
                storage.finishAuthentication();
                new BackupAsyncTask(getActivity(), storage, new JsonBackupOutput.Factory()).execute();
                break;
            case DROPBOX_RESTORE_IN_PROGRESS:
                state = State.DEFAULT;
                storage = (DropboxStorage)stateTag;
                storage.finishAuthentication();
                new RestoreAsyncTask(getActivity(), storage.new Input(".json"), new JsonRestoreInput.Factory()).execute();
                break;
            case DEFAULT:
                // Do nothing.
                break;
        }
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
    private void enablePin(final CheckBoxPreference checkBox) {
        new PinDialogFragment()
                .setTitle(R.string.dialog_new_pin_title)
                .setListener(new PinDialogFragment.Listener() {
                    @Override
                    public void onPositiveButtonClicked(final String pin) {
                        if (pin.length() == 4) {
                            PinManager.set(getActivity(), pin);
                            Toast.makeText(getActivity(), R.string.pin_enabled, Toast.LENGTH_SHORT).show();
                            ParseAnalytics.trackEvent("protectionEnabled");
                        } else {
                            Toast.makeText(getActivity(), R.string.pin_too_short, Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        checkBox.setChecked(false);
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
                            ParseAnalytics.trackEvent("protectionDisabled");
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
            final ArrayList<String> dayNames = new ArrayList<String>();
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

    private void setupBackupSettings() {
        // Plain text backup.
        findPreference(R.string.prefkey_backup_plain_text).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new BackupAsyncTask(getActivity(), new ExternalStorage(), new PlainTextBackupOutput.Factory()).execute();
                        return true;
                    }
                }
        );
        // JSON backup.
        findPreference(R.string.prefkey_backup_json).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        new BackupAsyncTask(getActivity(), new ExternalStorage(), new JsonBackupOutput.Factory()).execute();
                        return true;
                    }
                }
        );
        // Dropbox backup.
        findPreference(R.string.prefkey_backup_dropbox).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        state = State.DROPBOX_BACKUP_IN_PROGRESS;
                        final DropboxStorage storage = new DropboxStorage();
                        stateTag = storage;
                        storage.authenticate(getActivity());
                        return true;
                    }
                }
        );
        // Dropbox restore.
        findPreference(R.string.prefkey_restore_dropbox).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        state = State.DROPBOX_RESTORE_IN_PROGRESS;
                        final DropboxStorage storage = new DropboxStorage();
                        stateTag = storage;
                        storage.authenticate(getActivity());
                        return true;
                    }
                }
        );
        // JSON restore.
        findPreference(R.string.prefkey_restore_json).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/json");
                        startActivityForResult(intent, RESULT_CODE_RESTORE_JSON);
                        return true;
                    }
                }
        );
    }

    /**
     * Find preference by key resource ID.
     */
    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }

    /**
     * Settings fragment state.
     */
    private enum State {
        DEFAULT,
        DROPBOX_BACKUP_IN_PROGRESS,
        DROPBOX_RESTORE_IN_PROGRESS,
    }
}
