package in.eigene.miary.core;

import com.parse.*;
import in.eigene.miary.helpers.*;

import java.util.*;

/**
 * Diary note.
 */
@ParseClassName("Note")
public class Note extends ParseObject {

    public static final String KEY_UUID_LSB = "uuidLsb";
    public static final String KEY_UUID_MSB = "uuidMsb";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CREATION_DATE = "creationDate";
    public static final String KEY_DRAFT = "draft";
    public static final String KEY_COLOR = "color";
    public static final String KEY_STARRED = "starred";

    public static final int COLOR_WHITE = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_ORANGE = 2;
    public static final int COLOR_YELLOW = 3;
    public static final int COLOR_GRAY = 4;
    public static final int COLOR_GREEN = 5;
    public static final int COLOR_BLUE = 6;
    public static final int COLOR_VIOLET = 7;

    private static final String LOG_TAG = Note.class.getSimpleName();

    /**
     * Gets note from Local Datastore by UUID.
     */
    public static void getByUuid(final UUID uuid, final GetCallback<Note> callback) {
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        query.whereEqualTo(KEY_UUID_LSB, uuid.getLeastSignificantBits());
        query.whereEqualTo(KEY_UUID_MSB, uuid.getMostSignificantBits());
        query.getFirstInBackground(callback);
    }

    public Note() {
        // Do nothing.
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

    @Override
    public String toString() {
        return String.format(
                "%s[id: %s, uuid: %s, creationDate: %s, draft: %s]",
                Note.class.getSimpleName(),
                getObjectId(),
                getUuid(),
                getCreationDate(),
                isDraft());
    }
}
