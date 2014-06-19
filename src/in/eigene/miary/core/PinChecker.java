package in.eigene.miary.core;

import android.content.*;

public class PinChecker {

    public static final String PREFS_NAME = "pin";

    public static final String KEY_PIN = "pin";

    public static boolean check(final Context context, final String pin) {
        return pin.equals(context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_PIN, "0000"));
    }
}
