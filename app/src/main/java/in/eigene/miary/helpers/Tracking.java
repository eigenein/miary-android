package in.eigene.miary.helpers;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;

import in.eigene.miary.Application;

/**
 * Analytics helper.
 */
public class Tracking {

    private static final String LOG_TAG = Tracking.class.getSimpleName();

    public static void clickAboutLink(final String uri) {
        YandexMetrica.reportEvent("Click About Link", makeAttribute("URI", uri));
        sendEvent("About", Action.CLICK, null, uri);
    }

    public static void newNote() {
        YandexMetrica.reportEvent("New Note");
        sendEvent("Note", Action.NEW, null, null);
    }

    public static void enterFullscreen() {
        YandexMetrica.reportEvent("Enter Fullscreen");
        sendEvent("Fullscreen", Action.ENTER, null, null);
    }

    public static void enterCorrectPin() {
        YandexMetrica.reportEvent("Enter Correct Pin");
        sendEvent("Passcode", Action.CORRECT, null, null);
    }

    public static void enterIncorrectPin() {
        YandexMetrica.reportEvent("Enter Incorrect Pin");
        sendEvent("Passcode", Action.INCORRECT, null, null);
    }

    public static void finishDropboxBackup(final long length) {
        YandexMetrica.reportEvent("Finish Dropbox Backup", makeAttribute("Length", length));
        sendEvent("Backup", Tracking.Action.DROPBOX, length, null);
    }

    public static void finishExternalStorageBackup(final long length) {
        YandexMetrica.reportEvent("Finish External Storage Backup", makeAttribute("Length", length));
        sendEvent("Backup", Tracking.Action.EXTERNAL, length, null);
    }

    public static void finishMigration(final long noteCount, final String result) {
        final HashMap<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Note Count", noteCount);
        eventAttributes.put("Result", result);
        YandexMetrica.reportEvent("Finish Migration", eventAttributes);

        sendEvent("Backup", Action.MIGRATE, noteCount, result);
    }

    public static void sortFeed(final String sortOrder) {
        YandexMetrica.reportEvent("Sort Feed", makeAttribute("Sort Order", sortOrder));
        sendEvent("View", Action.SET_SORTING_ORDER, null, sortOrder);
    }

    public static void setSingleColumn() {
        YandexMetrica.reportEvent("Set Single Column");
        sendEvent("View", Action.SET_LAYOUT, null, "Single Column");
    }

    public static void setMultiColumn() {
        YandexMetrica.reportEvent("Set Multi-Column");
        sendEvent("View", Action.SET_LAYOUT, null, "Multi-column");
    }

    public static void setDraft(final boolean draft) {
        YandexMetrica.reportEvent("Set Draft", makeAttribute("Is Draft", draft));
        sendEvent("Note", Action.SET_DRAFT, null, Boolean.toString(draft));
    }

    public static void setStarred(final boolean starred) {
        YandexMetrica.reportEvent("Set Starred", makeAttribute("Is Starred", starred));
        sendEvent("Note", Action.SET_STARRED, null, Boolean.toString(starred));
    }

    public static void setColor(final int color) {
        YandexMetrica.reportEvent("Set Color", makeAttribute("Color", String.format("#%06X", color)));
        sendEvent("Note", Action.SET_COLOR, null, null);
    }

    public static void removeNote() {
        YandexMetrica.reportEvent("Remove Note");
        sendEvent("Note", Action.REMOVE, null, null);
    }

    public static void setCustomDate() {
        YandexMetrica.reportEvent("Set Custom Date");
        sendEvent("Note", Action.SET_CUSTOM_DATE, null, null);
    }

    public static void selectSection(final String sectionName) {
        YandexMetrica.reportEvent("Select Section", makeAttribute("Name", sectionName));
        sendEvent("Drawer", Tracking.Action.CHANGE_SECTION, null, sectionName);
    }

    public static void setFontSize(final String fontSize) {
        YandexMetrica.reportEvent("Set Font Size", makeAttribute("Size", fontSize));
        sendEvent("Font Size", Action.SET, null, fontSize);
    }

    public static void setTheme(final String theme) {
        YandexMetrica.reportEvent("Set Theme", makeAttribute("Theme", theme));
        sendEvent("Theme", Action.SET, null, theme);
    }

    public static void enablePasscode(final int length) {
        YandexMetrica.reportEvent("Enable Passcode", makeAttribute("Length", length));
        sendEvent("Passcode", Action.ENABLE, null, null);
    }

    public static void disablePasscode() {
        YandexMetrica.reportEvent("Disable Passcode");
        sendEvent("Passcode", Action.DISABLE, null, null);
    }

    public static void linkDropbox() {
        YandexMetrica.reportEvent("Link Dropbox");
        sendEvent("Dropbox", "Link", null, null);
    }

    public static void setPasscodeTimeout(final String timeout) {
        YandexMetrica.reportEvent("Set Passcode Timeout", makeAttribute("Timeout", timeout));
        sendEvent("Passcode", "Set Timeout", null, timeout);
    }

    public static void rateApp() {
        YandexMetrica.reportEvent("Rate App");
        sendEvent("About", "Rate", null, null);
    }

    public static void error(final String message, final Throwable error) {
        Log.e(LOG_TAG, message, error);
        YandexMetrica.reportError(message, error);
        if (Application.getTracker() != null) {
            Application.getTracker().send(new HitBuilders.ExceptionBuilder().setDescription(message).build());
        }
    }

    private static HashMap<String, Object> makeAttribute(final String key, final Object value) {
        final HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(key, value);
        return attributes;
    }

    private static void sendEvent(
            final String category,
            final String action,
            final Long value,
            final String label) {

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

    private static class Action {

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
