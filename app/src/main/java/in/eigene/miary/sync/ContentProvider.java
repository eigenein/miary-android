package in.eigene.miary.sync;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;

import java.net.*;

import in.eigene.miary.core.persistence.*;

public class ContentProvider extends android.content.ContentProvider {

    private static final String AUTHORITY = "in.eigene.miary.provider";

    private static final int NOTE_ID = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private DatabaseHelper helper;

    static {
        MATCHER.addURI(AUTHORITY, "notes", NOTES_ID);
        MATCHER.addURI(AUTHORITY, "notes/#", NOTE_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(
            final Uri uri,
            final String[] projection,
            final String selection,
            final String[] selectionArgs,
            final String sortOrder
    ) {
        return null;
    }

    @Override
    public String getType(final Uri uri) {
        switch (MATCHER.match(uri)) {
            case NOTE_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + Note.Contract.CONTENT_SUBTYPE;
            case NOTES_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + Note.Contract.CONTENT_SUBTYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        if (MATCHER.match(uri) != NOTES_ID) {
            throw new IllegalArgumentException(uri.toString());
        }
        final SQLiteDatabase database = helper.getWritableDatabase();
        final long id = database.insert(Note.Contract.TABLE, null, values);
        final Uri noteUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(noteUri, null);
        return noteUri;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            final Uri uri,
            final ContentValues values,
            final String selection,
            final String[] selectionArgs
    ) {
        return 0;
    }
}
