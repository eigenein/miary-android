package in.eigene.miary.helpers;

import android.content.Context;
import android.content.Intent;

public class ActivityHelper {

    /**
     * Starts activity.
     */
    public static void start(final Context context, final Class<?> activityClass) {
        context.startActivity(new Intent().setClass(context, activityClass));
    }
}
