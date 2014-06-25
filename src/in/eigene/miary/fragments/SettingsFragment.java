package in.eigene.miary.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.format.DateFormat;
import android.widget.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;
import in.eigene.miary.core.export.*;

import java.text.*;
import java.util.*;

public class SettingsFragment extends PreferenceFragment {

    private static final String[] SHORT_WEEKDAYS = new DateFormatSymbols().getShortWeekdays();

    private String[] allWeekdays;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference(R.string.prefkey_export).setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            ExportAsyncTask.start(getActivity());
                        } else {
                            Toast.makeText(getActivity(), R.string.toast_storage_unready, Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
        });

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

    /**
     * Find preference by key resource ID.
     */
    private Preference findPreference(final int keyResourceId) {
        return findPreference(getString(keyResourceId));
    }
}
