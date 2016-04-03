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
        sendEvent("About", "Click", null, uri);
    }

    public static void newNote() {
        YandexMetrica.reportEvent("New Note");
        sendEvent("Note", "New", null, null);
    }

    public static void enterFullscreen() {
        YandexMetrica.reportEvent("Enter Fullscreen");
        sendEvent("Fullscreen", "Enter", null, null);
    }

    public static void enterCorrectPin() {
        YandexMetrica.reportEvent("Enter Pin", makeAttribute("Status", "Correct"));
        sendEvent("Passcode", "Correct", null, null);
    }

    public static void enterIncorrectPin() {
        YandexMetrica.reportEvent("Enter Pin", makeAttribute("Status", "Incorrect"));
        sendEvent("Passcode", "Incorrect", null, null);
    }

    public static void finishDropboxBackup(final long length) {
        YandexMetrica.reportEvent("Finish Dropbox Backup", makeAttribute("Length", length));
        sendEvent("Backup", "Dropbox", length, null);
    }

    public static void finishExternalStorageBackup(final long length) {
        YandexMetrica.reportEvent("Finish External Storage Backup", makeAttribute("Length", length));
        sendEvent("Backup", "External", length, null);
    }

    public static void finishMigration(final long noteCount, final String result) {
        YandexMetrica.reportEvent("Finish Migration", makeAttributes(
                "Note Count", noteCount, "Result", result));
        sendEvent("Backup", "Migrate", noteCount, result);
    }

    public static void sortFeed(final String sortOrder) {
        YandexMetrica.reportEvent("Sort Feed", makeAttribute("Sort Order", sortOrder));
        sendEvent("View", "Set Sorting Order", null, sortOrder);
    }

    public static void setSingleColumn() {
        YandexMetrica.reportEvent("Set Layout", makeAttribute("Layout", "Single Column"));
        sendEvent("View", "Set Layout", null, "Single Column");
    }

    public static void setMultiColumn() {
        YandexMetrica.reportEvent("Set Layout", makeAttribute("Layout", "Multi-column"));
        sendEvent("View", "Set Layout", null, "Multi-column");
    }

    public static void setDraft(final boolean draft) {
        YandexMetrica.reportEvent("Set Draft", makeAttribute("Is Draft", draft));
        sendEvent("Note", "Set Draft", null, Boolean.toString(draft));
    }

    public static void setStarred(final boolean starred) {
        YandexMetrica.reportEvent("Set Starred", makeAttribute("Is Starred", starred));
        sendEvent("Note", "Set Starred", null, Boolean.toString(starred));
    }

    public static void setColor(final int color) {
        YandexMetrica.reportEvent("Set Color", makeAttribute("Color", String.format("#%06X", color)));
        sendEvent("Note", "Set Color", null, null);
    }

    public static void removeNote() {
        YandexMetrica.reportEvent("Remove Note");
        sendEvent("Note", "Remove", null, null);
    }

    public static void setCustomDate() {
        YandexMetrica.reportEvent("Set Custom Date");
        sendEvent("Note", "Set Custom Date", null, null);
    }

    public static void selectSection(final String sectionName) {
        YandexMetrica.reportEvent("Select Section", makeAttribute("Name", sectionName));
        sendEvent("Drawer", "Select Section", null, sectionName);
    }

    public static void setFontSize(final String fontSize) {
        YandexMetrica.reportEvent("Set Font Size", makeAttribute("Size", fontSize));
        sendEvent("Font Size", "Set", null, fontSize);
    }

    public static void setTheme(final String theme) {
        YandexMetrica.reportEvent("Set Theme", makeAttribute("Theme", theme));
        sendEvent("Theme", "Set", null, theme);
    }

    public static void enablePasscode(final int length) {
        YandexMetrica.reportEvent("Passcode", makeAttributes("Action", "Enable", "Length", length));
        sendEvent("Passcode", "Enable", null, null);
    }

    public static void disablePasscode() {
        YandexMetrica.reportEvent("Passcode", makeAttribute("Action", "Disable"));
        sendEvent("Passcode", "Disable", null, null);
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

    public static void shareNote() {
        YandexMetrica.reportEvent("Share Note");
        sendEvent("Note", "Share", null, null);
    }

    public static void newNoteViaSend() {
        YandexMetrica.reportEvent("New Note via Send");
        sendEvent("Note", "New (via Send)", null, null);
    }

    public static void cancelRemovedNote() {
        YandexMetrica.reportEvent("Cancel Removed Note");
        sendEvent("Note", "Cancel Removed", null, null);
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

    private static HashMap<String, Object> makeAttributes(
            final String key1, final Object value1,
            final String key2, final Object value2
    ) {
        final HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(key1, value1);
        attributes.put(key2, value2);
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
}
