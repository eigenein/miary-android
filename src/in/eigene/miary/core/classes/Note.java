package in.eigene.miary.core.classes;

import com.parse.*;
import in.eigene.miary.helpers.*;

import java.util.*;

/**
 * Represents a local diary note.
 */
@ParseClassName("Note")
public class Note extends ParseObject {

    public static final String KEY_LOCAL_UPDATED_AT = "localUpdatedAt";
    public static final String KEY_UUID = "uuid";
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
    public static void getByUuid(final UUID uuid, final GetCallback<Note> callback) {
        ParseQuery.getQuery(Note.class)
                .fromLocalDatastore()
                .whereEqualTo(KEY_UUID_LSB, uuid.getLeastSignificantBits())
                .whereEqualTo(KEY_UUID_MSB, uuid.getMostSignificantBits())
                .getFirstInBackground(callback);
    }

    public static Note createNew() {
        final Date currentDate = new Date();
        return new Note()
                .setUuid(UUID.randomUUID())
                .setCreationDate(currentDate)
                .setCustomDate(currentDate)
                .setLocalUpdatedAt(currentDate)
                .setDraft(false)
                .setStarred(false)
                .setDeleted(false);
    }

    public Date getLocalUpdatedAt() {
        return getDate(KEY_LOCAL_UPDATED_AT);
    }

    public Note setLocalUpdatedAt(final Date updatedAt) {
        put(KEY_LOCAL_UPDATED_AT, updatedAt);
        return this;
    }

    public UUID getUuid() {
        return new UUID(getLong(KEY_UUID_MSB), getLong(KEY_UUID_LSB));
    }

    public Note setUuid(final UUID uuid) {
        put(KEY_UUID_MSB, uuid.getMostSignificantBits());
        put(KEY_UUID_LSB, uuid.getLeastSignificantBits());
        return this;
    }

    public String getTitle() {
        return Util.coalesce(getString(KEY_TITLE), "");
    }

    public Note setTitle(final String title) {
        put(KEY_TITLE, title);
        return this;
    }

    public String getText() {
        return Util.coalesce(getString(KEY_TEXT), "");
    }

    public Note setText(final String text) {
        put(KEY_TEXT, text);
        return this;
    }

    public Date getCreationDate() {
        return getDate(KEY_CREATION_DATE);
    }

    public Note setCreationDate(final Date date) {
        put(KEY_CREATION_DATE, date);
        return this;
    }

    public Date getCustomDate() {
        return Util.coalesce(getDate(KEY_CUSTOM_DATE), getCreationDate());
    }

    public Note setCustomDate(final Date date) {
        put(KEY_CUSTOM_DATE, date);
        return this;
    }

    public boolean isDraft() {
        return getBoolean(KEY_DRAFT);
    }

    public Note setDraft(final boolean isDraft) {
        put(KEY_DRAFT, isDraft);
        return this;
    }

    public int getColor() {
        return getInt(KEY_COLOR);
    }

    public Note setColor(final int color) {
        put(KEY_COLOR, color);
        return this;
    }

    public boolean isStarred() {
        return getBoolean(KEY_STARRED);
    }

    public Note setStarred(final boolean isStarred) {
        put(KEY_STARRED, isStarred);
        return this;
    }

    public boolean isDeleted() {
        return getBoolean(KEY_DELETED);
    }

    public Note setDeleted(final boolean isDeleted) {
        put(KEY_DELETED, isDeleted);
        return this;
    }

    /**
     * Gets map to be sent via sync framework.
     */
    public HashMap<String, Object> toSyncMap() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_UUID, getUuid().toString());
        map.put(KEY_LOCAL_UPDATED_AT, Util.coalesce(getLocalUpdatedAt(), Util.EPOCH).getTime());
        map.put(KEY_TITLE, getTitle());
        map.put(KEY_TEXT, getText());
        map.put(KEY_CREATION_DATE, getCreationDate().getTime());
        map.put(KEY_CUSTOM_DATE, getCustomDate().getTime());
        map.put(KEY_DRAFT, isDraft());
        map.put(KEY_STARRED, isStarred());
        map.put(KEY_DELETED, isDeleted());
        map.put(KEY_COLOR, getColor());
        return map;
    }
}
