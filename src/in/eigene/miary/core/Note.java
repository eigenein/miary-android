package in.eigene.miary.core;

import com.parse.*;

import java.util.*;

/**
 * Diary note.
 */
@ParseClassName("Note")
public class Note extends ParseObject {

    private static final String CREATION_DATE_KEY = "cd";

    public Note() {
        // Do nothing.
    }

    public Date getCreationDate() {
        return getDate(CREATION_DATE_KEY);
    }

    public void setCreationDate(final Date date) {
        put(CREATION_DATE_KEY, date);
    }
}
