package in.eigene.miary.core;

import com.parse.*;
import in.eigene.miary.helpers.*;

import java.io.*;
import java.util.*;

/**
 * Diary note.
 */
@ParseClassName("Note")
public class Note extends ParseObject implements Serializable {

    public static final String UUID_LSB_KEY = "ul";
    public static final String UUID_MSB_KEY = "um";
    public static final String HEADER_KEY = "h";
    public static final String TEXT_KEY = "t";
    public static final String CREATION_DATE_KEY = "cd";
    public static final String DRAFT_KEY = "d";

    /**
     * Gets note from Local Datastore by UUID.
     */
    public static void getByUuid(final UUID uuid, final GetCallback<Note> callback) {
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        query.whereEqualTo(UUID_LSB_KEY, uuid.getLeastSignificantBits());
        query.whereEqualTo(UUID_MSB_KEY, uuid.getMostSignificantBits());
        query.getFirstInBackground(callback);
    }

    public Note() {
        // Do nothing.
    }

    public UUID getUuid() {
        return new UUID(getLong(UUID_MSB_KEY), getLong(UUID_LSB_KEY));
    }

    public Note setUuid(final UUID uuid) {
        put(UUID_MSB_KEY, uuid.getMostSignificantBits());
        put(UUID_LSB_KEY, uuid.getLeastSignificantBits());
        return this;
    }

    public String getHeader() {
        return Util.coalesce(getString(HEADER_KEY), "");
    }

    public Note setHeader(final String header) {
        put(HEADER_KEY, header);
        return this;
    }

    public String getText() {
        return Util.coalesce(getString(TEXT_KEY), "");
    }

    public Note setText(final String text) {
        put(TEXT_KEY, text);
        return this;
    }

    public Date getCreationDate() {
        return getDate(CREATION_DATE_KEY);
    }

    public Note setCreationDate(final Date date) {
        put(CREATION_DATE_KEY, date);
        return this;
    }

    public boolean isDraft() {
        return getBoolean(DRAFT_KEY);
    }

    public Note setDraft(boolean isDraft) {
        put(DRAFT_KEY, isDraft);
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
