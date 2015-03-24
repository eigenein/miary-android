package in.eigene.miary.core.persistence;

import android.database.*;
import android.provider.*;

import java.util.*;

/**
 * Represents a local diary note.
 */
public class Note extends Entity {

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
    }

    public static class Repository extends BaseRepository<Note> {

        public static final Repository INSTANCE = new Repository();

        private static final String TABLE_NAME = "notes";
        private static final String CREATE_SQL = String.format("" +
                        "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s INTEGER NOT NULL," +
                        "%s TEXT NOT NULL, %s TEXT NOT NULL," +
                        "%s INTEGER NOT NULL," +
                        "%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL," +
                        "%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s INTEGER NOT NULL);" +
                        "CREATE INDEX ix_%s_%s ON %s (%s);" +
                        "CREATE INDEX ix_%s_%s ON %s (%s);" +
                        "CREATE INDEX ix_%s_%s ON %s (%s);" +
                        "CREATE INDEX ix_%s_%s ON %s (%s);" +
                        "CREATE INDEX ix_%s_%s ON %s (%s);",
                TABLE_NAME,
                _ID,
                Columns.SYNC_ID,
                Columns.TITLE, Columns.TEXT,
                Columns.COLOR,
                Columns.CREATED_TIME, Columns.UPDATED_TIME, Columns.CUSTOM_TIME,
                Columns.DRAFT, Columns.STARRED, Columns.DELETED,
                TABLE_NAME, Columns.DRAFT, TABLE_NAME, Columns.DRAFT,
                TABLE_NAME, Columns.STARRED, TABLE_NAME, Columns.STARRED,
                TABLE_NAME, Columns.DELETED, TABLE_NAME, Columns.DELETED,
                TABLE_NAME, Columns.SYNC_ID, TABLE_NAME, Columns.SYNC_ID,
                TABLE_NAME, Columns.CUSTOM_TIME, TABLE_NAME, Columns.CUSTOM_TIME
        );

        @Override
        public Note create() {
            final Note note = new Note();
            note.syncId = newId();
            note.title = "";
            note.text = "";
            note.createdDate = new Date();
            note.updatedDate = note.createdDate;
            note.customDate = note.createdDate;
            return note;
        }

        @Override
        protected Note read(final Cursor cursor) {
            final Note note = new Note();
            note.id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
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

        @Override
        protected String getCreateTableSQL() {
            return CREATE_SQL;
        }

        private static class Columns {

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
}
