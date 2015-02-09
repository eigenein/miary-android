package in.eigene.miary.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import in.eigene.miary.core.managers.ReminderManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (ReminderManager.isReminderEnabled(context)) {
                ReminderManager.scheduleReminder(context);
            }
        }
    }
}