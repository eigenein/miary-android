package in.eigene.miary.helpers;

import android.content.Context;
import android.content.Intent;

import in.eigene.miary.activities.BaseActivity;

public class ActivityHelper {

    /**
     * Starts activity.
     */
    public static void start(final Context context, final Class<? extends BaseActivity> activityClass) {
        context.startActivity(new Intent().setClass(context, activityClass));
    }
}
