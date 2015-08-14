package in.eigene.miary.managers;

import android.content.Context;

import in.eigene.miary.helpers.PreferenceHelper;

public class PinManager {

    private static final String DEFAULT_PIN = "0000";

    public static void set(final Context context, final String pin) {
        PreferenceHelper.edit(context).putString(PreferenceHelper.KEY_PIN, pin).apply();
    }

    public static boolean check(final Context context, final String pin) {
        return pin.equals(PreferenceHelper.get(context).getString(PreferenceHelper.KEY_PIN, DEFAULT_PIN));
    }
}
