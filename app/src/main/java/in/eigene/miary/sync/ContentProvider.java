package in.eigene.miary.sync;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.text.*;
import android.util.*;

import in.eigene.miary.core.persistence.*;

public class ContentProvider extends android.content.ContentProvider {

    public static final String AUTHORITY = "in.eigene.miary.provider";

    private static final String LOG_TAG = ContentProvider.class.getSimpleName();

    private static final int NOTE_ID = 1;
    private static final int NOTES_ID = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private DatabaseHelper helper;

    static {
        MATCHER.addURI(AUTHORITY, Note.Contract.TABLE, NOTES_ID);
        MATCHER.addURI(AUTHORITY, String.format("%s/#", Note.Contract.TABLE), NOTE_ID);
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
        Log.i(LOG_TAG, String.format("query %s %s", uri, selection));
        final SQLiteDatabase database = helper.getReadableDatabase();
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Note.Contract.TABLE);
        switch (MATCHER.match(uri)) {
            case NOTE_ID:
                queryBuilder.appendWhere(Note.Contract._ID + " = " + uri.getLastPathSegment());
                break;
            case NOTES_ID:
                // Do nothing.
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        final Cursor cursor = queryBuilder.query(
                database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(final Uri uri) {
        switch (MATCHER.match(uri)) {
            case NOTE_ID:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + Note.Contract.CONTENT_SUBTYPE;
            case NOTES_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + Note.Contract.CONTENT_SUBTYPE;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        Log.i(LOG_TAG, "insert " + uri);
        if (MATCHER.match(uri) != NOTES_ID) {
            throw new IllegalArgumentException(uri.toString());
        }
        final SQLiteDatabase database = helper.getWritableDatabase();
        final long id = database.insert(Note.Contract.TABLE, null, values);
        final Uri noteUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        return noteUri;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        Log.i(LOG_TAG, "delete " + uri);
        final SQLiteDatabase database = helper.getWritableDatabase();
        final int deleteCount;
        switch (MATCHER.match(uri)) {
            case NOTE_ID:
                deleteCount = database.delete(Note.Contract.TABLE, getWhereClause(uri, selection), selectionArgs);
                break;
            case NOTES_ID:
                deleteCount = database.delete(Note.Contract.TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(
            final Uri uri,
            final ContentValues values,
            final String selection,
            final String[] selectionArgs
    ) {
        Log.i(LOG_TAG, "update " + uri);
        final SQLiteDatabase database = helper.getWritableDatabase();
        final int updateCount;
        switch (MATCHER.match(uri)) {
            case NOTE_ID:
                updateCount = database.update(Note.Contract.TABLE, values, getWhereClause(uri, selection), selectionArgs);
                break;
            case NOTES_ID:
                updateCount = database.update(Note.Contract.TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    private static String getWhereClause(final Uri uri, final String selection) {
        final StringBuilder where = new StringBuilder(String.format("%s = %s",
                Note.Contract._ID, uri.getLastPathSegment()));
        if (!TextUtils.isEmpty(selection)) {
            where.append(" AND ").append(selection);
        }
        return where.toString();
    }
}
