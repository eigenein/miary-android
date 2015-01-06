package in.eigene.miary.core.classes;

import com.parse.*;
import in.eigene.miary.helpers.*;

import java.util.*;

/**
 * Represents a local diary note.
 */
@ParseClassName("Note")
public class LocalNote extends ParseObject implements Note {

    public static final String KEY_LOCAL_UPDATED_AT = "localUpdatedAt";
    public static final String KEY_UUID_LSB = "uuidLsb";
    public static final String KEY_UUID_MSB = "uuidMsb";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CREATION_DATE = "creationDate";
    public static final String KEY_CUSTOM_DATE = "customDate";
    public static final String KEY_DRAFT = "draft";
    public static final String KEY_COLOR = "color";
    public static final String KEY_STARRED = "starred";
    public static final String KEY_DELETED = "deleted";

    public static final int COLOR_WHITE = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_ORANGE = 2;
    public static final int COLOR_YELLOW = 3;
    public static final int COLOR_GRAY = 4;
    public static final int COLOR_GREEN = 5;
    public static final int COLOR_BLUE = 6;
    public static final int COLOR_PURPLE = 7;

    /**
     * Gets note from Local Datastore by UUID.
     */
    public static void getByUuid(final UUID uuid, final GetCallback<LocalNote> callback) {
        ParseQuery.getQuery(LocalNote.class)
                .fromLocalDatastore()
                .whereEqualTo(KEY_UUID_LSB, uuid.getLeastSignificantBits())
                .whereEqualTo(KEY_UUID_MSB, uuid.getMostSignificantBits())
                .getFirstInBackground(callback);
    }

    public static LocalNote createNew() {
        return new LocalNote()
                .setUuid(UUID.randomUUID())
                .setCreationDate(new Date())
                .setCustomDate(new Date())
                .setDraft(false)
                .setStarred(false)
                .setDeleted(false);
    }

    public Date getLocalUpdatedAt() {
        return getDate(KEY_LOCAL_UPDATED_AT);
    }

    public LocalNote setLocalUpdatedAt(final Date updatedAt) {
        put(KEY_LOCAL_UPDATED_AT, updatedAt);
        return this;
    }

    @Override
    public UUID getUuid() {
        return new UUID(getLong(KEY_UUID_MSB), getLong(KEY_UUID_LSB));
    }

    public LocalNote setUuid(final UUID uuid) {
        put(KEY_UUID_MSB, uuid.getMostSignificantBits());
        put(KEY_UUID_LSB, uuid.getLeastSignificantBits());
        return this;
    }

    public String getTitle() {
        return Util.coalesce(getString(KEY_TITLE), "");
    }

    public LocalNote setTitle(final String title) {
        put(KEY_TITLE, title);
        return this;
    }

    public String getText() {
        return Util.coalesce(getString(KEY_TEXT), "");
    }

    public LocalNote setText(final String text) {
        put(KEY_TEXT, text);
        return this;
    }

    public Date getCreationDate() {
        return getDate(KEY_CREATION_DATE);
    }

    public LocalNote setCreationDate(final Date date) {
        put(KEY_CREATION_DATE, date);
        return this;
    }

    public Date getCustomDate() {
        return Util.coalesce(getDate(KEY_CUSTOM_DATE), getCreationDate());
    }

    public LocalNote setCustomDate(final Date date) {
        put(KEY_CUSTOM_DATE, date);
        return this;
    }

    public boolean isDraft() {
        return getBoolean(KEY_DRAFT);
    }

    public LocalNote setDraft(final boolean isDraft) {
        put(KEY_DRAFT, isDraft);
        return this;
    }

    public int getColor() {
        return getInt(KEY_COLOR);
    }

    public LocalNote setColor(final int color) {
        put(KEY_COLOR, color);
        return this;
    }

    public boolean isStarred() {
        return getBoolean(KEY_STARRED);
    }

    public LocalNote setStarred(final boolean isStarred) {
        put(KEY_STARRED, isStarred);
        return this;
    }

    public boolean isDeleted() {
        return getBoolean(KEY_DELETED);
    }

    public LocalNote setDeleted(final boolean isDeleted) {
        put(KEY_DELETED, isDeleted);
        return this;
    }
}
