package in.eigene.miary.persistence;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.SparseIntArray;

import java.security.SecureRandom;
import java.util.Date;

import in.eigene.miary.Application;
import in.eigene.miary.R;
import in.eigene.miary.sync.ContentProvider;

/**
 * Represents a local diary note.
 */
public class Note implements Entity {

    public static final String[] PROJECTION = {
            Contract._ID,
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

    /**
     * Maps legacy color codes into normal color values.
     */
    public static final SparseIntArray LEGACY_COLORS = new SparseIntArray();

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Unique note identifier. For backwards compatibility it is equal to UUID LSB.
     */
    private Long id;
    private String title;
    private String text;
    private int color;
    private Date createdDate;
    private Date updatedDate;
    private Date customDate;
    private boolean draft;
    private boolean starred;
    private boolean deleted;

    static {
        LEGACY_COLORS.put(0, 0xFFFFFFFF); // white
        LEGACY_COLORS.put(1, 0xFFEF5350); // red
        LEGACY_COLORS.put(2, 0xFFFFA726); // orange
        LEGACY_COLORS.put(3, 0xFFFFEB3B); // yellow
        LEGACY_COLORS.put(4, 0xFFF5F5F5); // gray
        LEGACY_COLORS.put(5, 0xFF8BC34A); // green
        LEGACY_COLORS.put(6, 0xFF90CAF9); // blue
        LEGACY_COLORS.put(7, 0xFFCE93D8); // purple
    }

    /**
     * Initializes a new note instance.
     */
    public static Note createEmpty() {
        final Note note = new Note();
        note.id = Math.abs(RANDOM.nextLong());
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
        note.title = cursor.getString(1);
        note.text = cursor.getString(2);
        note.color = cursor.getInt(3);
        note.createdDate = new Date(cursor.getLong(4));
        note.updatedDate = new Date(cursor.getLong(5));
        note.customDate = new Date(cursor.getLong(6));
        note.draft = cursor.getInt(7) != 0;
        note.starred = cursor.getInt(8) != 0;
        note.deleted = cursor.getInt(9) != 0;
        return note;
    }

    private Note() {
        // Do nothing.
    }

    public long getId() {
        return id;
    }

    public Note setId(final long id) {
        this.id = id;
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
     * Adds note to the specified section.
     */
    public Note addToSection(final Section section) {
        switch (section) {
            case DIARY:
                this.starred = this.draft = false;
                break;
            case STARRED:
                this.starred = true;
                break;
            case DRAFTS:
                this.draft = true;
                break;
        }
        return this;
    }

    /**
     * Inserts the note into the database.
     */
    @Override
    public Uri insert(final ContentResolver contentResolver) {
        return contentResolver.insert(Contract.CONTENT_URI, getContentValues());
    }

    /**
     * Updates the note in the database.
     */
    @Override
    public int update(final ContentResolver contentResolver) {
        return contentResolver.update(ContentUris.withAppendedId(Contract.CONTENT_URI, id), getContentValues(), null, null);
    }

    private ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(Contract._ID, id);
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

    public static class Contract implements BaseColumns {

        public static final String TABLE = "notes";
        public static final String CONTENT_SUBTYPE = "/vnd.in.eigene.miary.note";
        public static final Uri CONTENT_URI = Uri.parse(String.format(
                "content://%s/%s", ContentProvider.AUTHORITY, TABLE));

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

    public enum Section {
        DIARY {
            @Override
            public String getSelection() {
                return "deleted = 0 AND draft = 0";
            }

            @Override
            public int getTitleResourceId() {
                return R.string.drawer_item_diary;
            }

            @Override
            public int getImageResourceId() {
                return R.drawable.ic_inbox_grey300_216dp;
            }
        },
        STARRED {
            @Override
            public String getSelection() {
                return "deleted = 0 AND starred = 1";
            }

            @Override
            public int getTitleResourceId() {
                return R.string.drawer_item_starred;
            }

            @Override
            public int getImageResourceId() {
                return R.drawable.ic_star_grey300_216dp;
            }
        },
        DRAFTS {
            @Override
            public String getSelection() {
                return "deleted = 0 AND draft = 1";
            }

            @Override
            public int getTitleResourceId() {
                return R.string.drawer_item_drafts;
            }

            @Override
            public int getImageResourceId() {
                return R.drawable.ic_drafts_grey300_216dp;
            }
        };

        public abstract String getSelection();
        public abstract int getTitleResourceId();
        public abstract int getImageResourceId();
    }

    public enum SortOrder {
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

        private SortOrder opposite;

        public abstract String getSortOrder();

        public SortOrder getOpposite() {
            return opposite;
        }
    }
}
