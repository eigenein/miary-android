package in.eigene.miary.core.classes;

import com.parse.*;

import java.util.*;

/**
 * Represents a remote diary note.
 */
@ParseClassName("RemoteNote")
public class RemoteNote extends ParseObject implements Note {

    public static final String KEY_UUID = "uuid";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CREATION_DATE = "creationDate";
    public static final String KEY_CUSTOM_DATE = "customDate";
    public static final String KEY_DRAFT = "draft";
    public static final String KEY_COLOR = "color";
    public static final String KEY_STARRED = "starred";
    public static final String KEY_DELETED = "deleted";

    public static RemoteNote fromLocalNote(final LocalNote localNote, final ParseACL acl) {
        final RemoteNote remoteNote = new RemoteNote();
        remoteNote.setACL(acl);
        remoteNote.put(KEY_UUID, localNote.getUuid().toString());
        remoteNote.put(KEY_TITLE, localNote.getTitle());
        remoteNote.put(KEY_TEXT, localNote.getText());
        remoteNote.put(KEY_CREATION_DATE, localNote.getCreationDate());
        remoteNote.put(KEY_CUSTOM_DATE, localNote.getCustomDate());
        remoteNote.put(KEY_DRAFT, localNote.isDraft());
        remoteNote.put(KEY_COLOR, localNote.getColor());
        remoteNote.put(KEY_STARRED, localNote.isStarred());
        remoteNote.put(KEY_DELETED, localNote.isDeleted());
        return remoteNote;
    }

    @Override
    public UUID getUuid() {
        return UUID.fromString(getString(KEY_UUID));
    }

    public LocalNote toLocalNote() {
        return new LocalNote()
                .setLocalUpdatedAt(getUpdatedAt())
                .setUuid(getUuid())
                .setTitle(getString(KEY_TITLE))
                .setText(getString(KEY_TEXT))
                .setCreationDate(getDate(KEY_CREATION_DATE))
                .setCustomDate(getDate(KEY_CUSTOM_DATE))
                .setDraft(getBoolean(KEY_DRAFT))
                .setColor(getInt(KEY_COLOR))
                .setStarred(getBoolean(KEY_STARRED))
                .setDeleted(getBoolean(KEY_DELETED));
    }
}
