package in.eigene.miary.core.persistence;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.security.SecureRandom;
import java.util.Date;

/**
 * Represents a local diary note.
 */
public class Note implements BaseColumns {

    /**
     * Used to generate random identifiers.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    private long syncId;
    private String title;
    private String text;
    private int color;
    private Date createdDate;
    private Date updatedDate;
    private Date customDate;
    private boolean draft;
    private boolean starred;
    private boolean deleted;

    /**
     * Initializes a new note.
     */
    public static Note createNew() {
        final Note note = new Note();
        note.syncId = RANDOM.nextLong();
        note.title = "";
        note.text = "";
        note.createdDate = new Date();
        note.updatedDate = note.createdDate;
        note.customDate = note.createdDate;
        return note;
    }

    /**
     * Creates note from the database cursor.
     */
    private static Note createFrom(final Cursor cursor) {
        final Note note = new Note();
        note.syncId = cursor.getLong(cursor.getColumnIndexOrThrow(Columns.SYNC_ID));
        note.title = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TITLE));
        note.text = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TEXT));
        note.color = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.COLOR));
        note.createdDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.CREATED_TIME)));
        note.updatedDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.UPDATED_TIME)));
        note.customDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Columns.CUSTOM_TIME)));
        note.draft = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.DRAFT)) != 0;
        note.starred = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.STARRED)) != 0;
        note.deleted = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.DELETED)) != 0;
        return note;
    }

    public long getSyncId() {
        return syncId;
    }

    public Note setSyncId(final long syncId) {
        this.syncId = syncId;
        // Do not set updated date.
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(final String title) {
        this.title = title;
        this.updatedDate = new Date();
        return this;
    }

    public String getText() {
        return text;
    }

    public Note setText(final String text) {
        this.text = text;
        this.updatedDate = new Date();
        return this;
    }

    public int getColor() {
        return color;
    }

    public Note setColor(final int color) {
        this.color = color;
        this.updatedDate = new Date();
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Note setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
        // Do not set updated date.
        return this;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Date getCustomDate() {
        return customDate;
    }

    public Note setCustomDate(final Date customDate) {
        this.customDate = customDate;
        this.updatedDate = new Date();
        return this;
    }

    public boolean isDraft() {
        return draft;
    }

    public Note setDraft(final boolean draft) {
        this.draft = draft;
        this.updatedDate = new Date();
        return this;
    }

    public boolean isStarred() {
        return starred;
    }

    public Note setStarred(final boolean starred) {
        this.starred = starred;
        this.updatedDate = new Date();
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Note setDeleted(final boolean deleted) {
        this.deleted = deleted;
        this.updatedDate = new Date();
        return this;
    }

    /**
     * Note background color.
     */
    public static class Color {

        public static final int WHITE = 0;
        public static final int RED = 1;
        public static final int ORANGE = 2;
        public static final int YELLOW = 3;
        public static final int GRAY = 4;
        public static final int GREEN = 5;
        public static final int BLUE = 6;
        public static final int PURPLE = 7;

        private Color() {
            // Do nothing.
        }
    }

    public static class Columns {

        public static final String SYNC_ID = "sync_id";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String COLOR = "color";
        public static final String CREATED_TIME = "created_time";
        public static final String UPDATED_TIME = "updated_time";
        public static final String CUSTOM_TIME = "custom_time";
        public static final String DRAFT = "draft";
        public static final String STARRED = "starred";
        public static final String DELETED = "deleted";
    }
}
