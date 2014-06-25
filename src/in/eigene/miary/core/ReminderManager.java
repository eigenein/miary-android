package in.eigene.miary.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import in.eigene.miary.services.NotificationIntentService;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ReminderManager {

    private static final String LOG_TAG = ReminderManager.class.getSimpleName();

    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";
    private static final String KEY_REMINDER_DAYS = "reminder_days";

    public static void setTime(final Context context, final int hour, final int minute) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MINUTE, minute)
                .commit();
    }

    public static int getReminderHour(final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(KEY_REMINDER_HOUR, 0);
    }

    public static int getReminderMinute(final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(KEY_REMINDER_MINUTE, 0);
    }

    public static boolean isReminderDay(final Context context, final Calendar calendar) {
        final Set<String> days = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getStringSet(KEY_REMINDER_DAYS, new HashSet<String>());

        final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return days.contains(String.valueOf(currentDayOfWeek));
    }

    public static boolean isReminderEnabled(final Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getStringSet(KEY_REMINDER_DAYS, new HashSet<String>())
                .size() > 0;
    }

    public static void scheduleReminder(final Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = getNextReminderDate(context);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, getAlarmPendingIntent(context));

        Log.i(LOG_TAG, "Reminder scheduled to " + calendar.getTime());
    }

    public static void cancelReminder(final Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(getAlarmPendingIntent(context));

        Log.i(LOG_TAG, "Reminder cancelled");
    }

    private static Calendar getNextReminderDate(final Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, getReminderHour(context));
        calendar.set(Calendar.MINUTE, getReminderMinute(context));
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private static PendingIntent getAlarmPendingIntent(final Context context) {
        android.content.Intent intent = new android.content.Intent(context, NotificationIntentService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
