package in.eigene.miary.core;

import com.parse.*;

import java.io.*;
import java.util.*;

/**
 * Diary note.
 */
@ParseClassName("Note")
public class Note extends ParseObject implements Serializable {

    private static final String UUID_KEY = "uuid";
    private static final String CREATION_DATE_KEY = "cd";
    private static final String DRAFT_KEY = "d";

    public static void getByUuid(final String uuid, final FindCallback<Note> callback) {
        final ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.fromLocalDatastore();
        query.whereEqualTo(UUID_KEY, uuid);
        query.setLimit(1);
        query.findInBackground(callback);
    }

    public Note() {
        // Do nothing.
    }

    public String getUuid() {
        return getString(UUID_KEY);
    }

    public Note setUuid(final String uuid) {
        put(UUID_KEY, uuid);
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

    public Note setCurrentUser() {
        setACL(new ParseACL(ParseUser.getCurrentUser()));
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
