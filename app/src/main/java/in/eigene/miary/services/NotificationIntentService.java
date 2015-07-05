package in.eigene.miary.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import in.eigene.miary.R;
import in.eigene.miary.managers.ReminderManager;
import in.eigene.miary.receivers.CreateNewNoteReceiver;

public class NotificationIntentService extends IntentService {

    private static final String LOG_TAG = NotificationIntentService.class.getSimpleName();

    private static final int REMIND_NOTIFICATION_ID = 1;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_stat)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_reminder_content_text));

        if (!ReminderManager.isReminderDay(this, Calendar.getInstance())) {
            Log.i(LOG_TAG, "Not a reminder day. Skip notification creation");
            return;
        }

        final Intent notificationIntent = new android.content.Intent(this, CreateNewNoteReceiver.class);
        final PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);

        builder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(REMIND_NOTIFICATION_ID, builder.build());

        Log.i(LOG_TAG, "Reminder notification created");
    }
}
