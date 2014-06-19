package in.eigene.miary.core;

import android.content.*;
import android.preference.*;

public class PinChecker {

    public static final String KEY_PIN = "pin";

    public static boolean check(final Context context, final String pin) {
        return pin.equals(PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(KEY_PIN, "0000"));
    }
}
