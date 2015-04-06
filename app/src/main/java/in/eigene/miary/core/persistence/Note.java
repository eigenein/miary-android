package in.eigene.miary.core.persistence;

import android.content.*;
import android.database.*;
import android.net.*;
import android.provider.*;

import java.security.*;
import java.util.*;

import in.eigene.miary.sync.ContentProvider;

/**
 * Represents a local diary note.
 */
public class Note implements Entity {

    public static final String[] PROJECTION = {
            Contract._ID,
            Contract.SYNC_ID,
            Contract.TITLE,
            Contract.TEXT,
            Contract.COLOR,
            Contract.CREATED_TIME,
            Contract.UPDATED_TIME,
            Contract.CUSTOM_TIME,
            Contract.DRAFT,
            Contract.STARRED,
            Contract.DELETED,
    };

    private static final SecureRandom RANDOM = new SecureRandom();

    private Long id;
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
     * Initializes a new note instance.
     */
    public static Note getEmpty() {
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
     * Gets existing note by URI.
     */
    public static Note getByUri(final Uri uri, final ContentResolver contentResolver) {
        final Cursor cursor = contentResolver.query(uri, PROJECTION, null, null, null);
        cursor.moveToFirst();
        return getByCursor(cursor);
    }

    /**
     * Reads note instance from cursor.
     */
    public static Note getByCursor(final Cursor cursor) {
        final Note note = new Note();
        note.id = cursor.getLong(0);
        note.syncId = cursor.getLong(1);
        note.title = cursor.getString(2);
        note.text = cursor.getString(3);
        note.color = cursor.getInt(4);
        note.createdDate = new Date(cursor.getLong(5));
        note.updatedDate = new Date(cursor.getLong(6));
        note.customDate = new Date(cursor.getLong(7));
        note.draft = cursor.getInt(8) != 0;
        note.starred = cursor.getInt(9) != 0;
        note.deleted = cursor.getInt(10) != 0;
        return note;
    }

    private Note() {
        // Do nothing.
    }

    public Long getId() {
        return id;
    }

    public long getSyncId() {
        return syncId;
    }

    public Note setSyncId(final long syncId) {
        this.syncId = syncId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Note setText(final String text) {
        this.text = text;
        return this;
    }

    public int getColor() {
        return color;
    }

    public Note setColor(final int color) {
        this.color = color;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Note setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Note setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public Date getCustomDate() {
        return customDate;
    }

    public Note setCustomDate(final Date customDate) {
        this.customDate = customDate;
        return this;
    }

    public boolean isDraft() {
        return draft;
    }

    public Note setDraft(final boolean draft) {
        this.draft = draft;
        return this;
    }

    public boolean isStarred() {
        return starred;
    }

    public Note setStarred(final boolean starred) {
        this.starred = starred;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Note setDeleted(final boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    /**
     * Inserts the note into the database.
     */
    @Override
    public Uri insert(final ContentResolver contentResolver) {
        final Uri uri = contentResolver.insert(Contract.CONTENT_URI, getContentValues());
        this.id = ContentUris.parseId(uri);
        return uri;
    }

    /**
     * Updates the note in the database.
     */
    @Override
    public void update(final ContentResolver contentResolver) {
        assert id != null;
        contentResolver.update(ContentUris.withAppendedId(Contract.CONTENT_URI, id), getContentValues(), null, null);
    }

    private ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(Contract.SYNC_ID, syncId);
        values.put(Contract.TITLE, title);
        values.put(Contract.TEXT, text);
        values.put(Contract.COLOR, color);
        values.put(Contract.CREATED_TIME, createdDate.getTime());
        values.put(Contract.UPDATED_TIME, updatedDate.getTime());
        values.put(Contract.CUSTOM_TIME, customDate.getTime());
        values.put(Contract.DRAFT, draft);
        values.put(Contract.STARRED, starred);
        values.put(Contract.DELETED, deleted);
        return values;
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

    public static class Contract implements BaseColumns {

        public static final String TABLE = "notes";
        public static final String CONTENT_SUBTYPE = "/vnd.in.eigene.miary.note";
        public static final Uri CONTENT_URI = Uri.parse(String.format(
                "content://%s/%s", ContentProvider.AUTHORITY, TABLE));

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

    public static enum Section {
        DIARY {
            @Override
            public String getSelection() {
                return "deleted = 0 AND draft = 0";
            }
        },
        STARRED {
            @Override
            public String getSelection() {
                return "deleted = 0 AND starred = 1";
            }
        },
        DRAFTS {
            @Override
            public String getSelection() {
                return "deleted = 0 AND draft = 1";
            }
        };

        public abstract String getSelection();
    }

    public static enum SortOrder {
        OLDEST_FIRST {
            @Override
            public String getSortOrder() {
                return "custom_time ASC";
            }
        },
        NEWEST_FIRST {
            @Override
            public String getSortOrder() {
                return "custom_time DESC";
            }
        };

        static {
            OLDEST_FIRST.opposite = NEWEST_FIRST;
            NEWEST_FIRST.opposite = OLDEST_FIRST;
        }

        public abstract String getSortOrder();

        private SortOrder opposite;

        public SortOrder getOpposite() {
            return opposite;
        }
    }
}
