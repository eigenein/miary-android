package in.eigene.miary.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import in.eigene.miary.R;
import in.eigene.miary.core.ReminderManager;
import in.eigene.miary.receivers.CreateNewNoteReceiver;

import java.util.Calendar;

public class NotificationService extends Service {

    private static final String LOG_TAG = NotificationService.class.getSimpleName();

    private static final int REMIND_NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_stat)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_reminder_content_text));


        if (!ReminderManager.isReminderDay(this, Calendar.getInstance())) {
            Log.i(LOG_TAG, "Not a reminder day. Skip notification creation");
            return START_STICKY;
        }

        Intent notificationIntent = new Intent(this, CreateNewNoteReceiver.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(REMIND_NOTIFICATION_ID, mBuilder.build());

        Log.i(LOG_TAG, "Reminder notification created");

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
