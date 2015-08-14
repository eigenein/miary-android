package in.eigene.miary.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper around {@see SharedPreferences}.
 */
public class PreferenceHelper {

    /**
     * #179. Specifies whether notes from previous app versions where migrated.
     */
    public static final String KEY_NOTES_MIGRATED = "notes_migrated";

    public static final String KEY_SORT_ORDER = "feed_sort_order";
    public static final String KEY_MULTI_COLUMN = "feed_multi_column";

    public static final String KEY_PIN = "pin";

    public static final String KEY_REMINDER_HOUR = "reminder_hour";
    public static final String KEY_REMINDER_MINUTE = "reminder_minute";
    public static final String KEY_REMINDER_DAYS = "reminder_days";

    /**
     * Gets {@see SharedPreferences}.
     */
    public static SharedPreferences get(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Gets preferences editor.
     */
    public static SharedPreferences.Editor edit(final Context context) {
        return get(context).edit();
    }

    /**
     * Clears preferences.
     */
    public static void clear(final Context context) {
        edit(context).clear().apply();
    }
}
