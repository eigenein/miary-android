package in.eigene.miary.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import in.eigene.miary.helpers.PreferenceHelper;
import in.eigene.miary.services.NotificationIntentService;

public class ReminderManager {

    private static final String LOG_TAG = ReminderManager.class.getSimpleName();

    public static void setTime(final Context context, final int hour, final int minute) {
        PreferenceHelper.edit(context)
                .putInt(PreferenceHelper.KEY_REMINDER_HOUR, hour)
                .putInt(PreferenceHelper.KEY_REMINDER_MINUTE, minute)
                .apply();
    }

    public static int getReminderHour(final Context context) {
        return PreferenceHelper.get(context).getInt(PreferenceHelper.KEY_REMINDER_HOUR, 12);
    }

    public static int getReminderMinute(final Context context) {
        return PreferenceHelper.get(context).getInt(PreferenceHelper.KEY_REMINDER_MINUTE, 0);
    }

    public static Calendar getReminderTime(final Context context) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getReminderHour(context));
        calendar.set(Calendar.MINUTE, getReminderMinute(context));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static Set<String> getReminderDays(final Context context) {
        return PreferenceHelper.get(context).getStringSet(PreferenceHelper.KEY_REMINDER_DAYS, new HashSet<String>());
    }

    public static boolean isReminderDay(final Context context, final Calendar calendar) {
        final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return getReminderDays(context).contains(String.valueOf(currentDayOfWeek));
    }

    public static boolean isReminderEnabled(final Context context) {
        return PreferenceHelper.get(context)
                .getStringSet(PreferenceHelper.KEY_REMINDER_DAYS, new HashSet<String>())
                .size() != 0;
    }

    public static void scheduleReminder(final Context context) {
        final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = getNextReminderDate(context);
        alarmManager.setRepeating(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                getAlarmPendingIntent(context));
        Log.i(LOG_TAG, "Reminder scheduled to " + calendar.getTime());
    }

    public static void cancelReminder(final Context context) {
        final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getAlarmPendingIntent(context));
        Log.i(LOG_TAG, "Reminder cancelled");
    }

    private static Calendar getNextReminderDate(final Context context) {
        final Calendar calendar = getReminderTime(context);
        if (calendar.before(Calendar.getInstance())) {
            // Prevent extra notification on device boot.
            calendar.add(Calendar.DATE, 1);
        }
        return calendar;
    }

    private static PendingIntent getAlarmPendingIntent(final Context context) {
        final Intent intent = new Intent(context, NotificationIntentService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
