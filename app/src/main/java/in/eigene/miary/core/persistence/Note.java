package in.eigene.miary.core.persistence;

import android.content.*;
import android.database.*;
import android.net.*;
import android.provider.*;

import java.util.*;

import in.eigene.miary.sync.ContentProvider;

/**
 * Represents a local diary note.
 */
public class Note extends Entity {

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

    public static Note getById(final long id, final ContentResolver contentResolver) {
        final Cursor cursor = contentResolver.query(Uri.parse(String.format(
                "%s/%d", Contract.CONTENT_URI, id)), PROJECTION, null, null, null);
        return getByCursor(cursor);
    }

    private static Note getByCursor(final Cursor cursor) {
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

    @Override
    protected long insert(final ContentResolver contentResolver) {
        // TODO.
        return 0;
    }

    @Override
    protected void update(final ContentResolver contentResolver) {
        // TODO.
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
}
