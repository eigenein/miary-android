package in.eigene.miary.helpers;

import com.google.android.gms.analytics.HitBuilders;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;

import in.eigene.miary.Application;

/**
 * Google Analytics helper.
 */
public class Tracking {

    public static void sendEvent(final String category, final String action) {
        sendEvent(category, action, null);
    }

    public static void sendEvent(final String category, final String action, final String label) {
        sendEvent(category, action, null, label);
    }

    public static void sendEvent(final String category, final String action, final long value) {
        sendEvent(category, action, value, null);
    }

    public static void sendEvent(
            final String category,
            final String action,
            final Long value,
            final String label) {
        // Yandex.Metrica
        final HashMap<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Category", category);
        eventAttributes.put("Action", action);
        if (value != null) {
            eventAttributes.put("Value", value);
        }
        if (label != null) {
            eventAttributes.put("Label", label);
        }
        // Let's first try to pass the event "as is" and then decide how to make this better.
        YandexMetrica.reportEvent("Google Analytics", eventAttributes);

        // Google Analytics.
        if (Application.getTracker() == null) {
            return;
        }
        final HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action);
        if (value != null) {
            builder.setValue(value);
        }
        if (label != null) {
            builder.setLabel(label);
        }
        Application.getTracker().send(builder.build());
    }

    public static class Category {

        public static final String NOTE = "Note";
        public static final String FULLSCREEN = "Fullscreen";
        public static final String PASSCODE = "Passcode";
        public static final String BACKUP = "Backup";
        public static final String VIEW = "View";
        public static final String DRAWER = "Drawer";
        public static final String ABOUT = "About";
        public static final String FONT_SIZE = "Font Size";
        public static final String THEME = "Theme";
    }

    public static class Action {

        public static final String NEW = "New";
        public static final String ENTER = "Enter";
        public static final String CORRECT = "Correct";
        public static final String INCORRECT = "Incorrect";
        public static final String SET_CUSTOM_DATE = "Set Custom Date";
        public static final String ENABLE = "Enable";
        public static final String DISABLE = "Disable";
        public static final String SET_COLOR = "Set Color";
        public static final String DROPBOX = "Dropbox";
        public static final String EXTERNAL = "External";
        public static final String MIGRATE = "Migrate";
        public static final String SET_SORTING_ORDER = "Set Sorting Order";
        public static final String SET_LAYOUT = "Set Layout";
        public static final String SET_DRAFT = "Set Draft";
        public static final String SET_STARRED = "Set Starred";
        public static final String REMOVE = "Remove";
        public static final String CHANGE_SECTION = "Change Section";
        public static final String CLICK = "Click";
        public static final String SET = "Set";
    }
}
