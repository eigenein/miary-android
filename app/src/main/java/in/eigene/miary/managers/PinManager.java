package in.eigene.miary.managers;

import android.content.Context;
import android.preference.PreferenceManager;

public class PinManager {

    private static final String KEY_PIN = "pin";

    public static void set(final Context context, final String pin) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(PinManager.KEY_PIN, pin)
                .commit();
    }

    public static boolean check(final Context context, final String pin) {
        return pin.equals(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(KEY_PIN, "0000"));
    }
}
