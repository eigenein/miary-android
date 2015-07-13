package in.eigene.miary.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "notes.db";
    private static final int VERSION = 1;

    public DatabaseHelper(final Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + Note.Contract.TABLE + " (" +
                Note.Contract._ID + " INTEGER PRIMARY KEY," +
                Note.Contract.TITLE + " TEXT NOT NULL," +
                Note.Contract.TEXT + " TEXT NOT NULL," +
                Note.Contract.COLOR + " INTEGER NOT NULL," +
                Note.Contract.CREATED_TIME + " INTEGER NOT NULL," +
                Note.Contract.UPDATED_TIME + " INTEGER NOT NULL," +
                Note.Contract.CUSTOM_TIME + " INTEGER NOT NULL," +
                Note.Contract.DRAFT + " INTEGER NOT NULL," +
                Note.Contract.STARRED + " INTEGER NOT NULL," +
                Note.Contract.DELETED + " INTEGER NOT NULL);"
        );
        db.execSQL(String.format("CREATE INDEX ix_%s_%s_%s_%s ON %s (%s, %s, %s);",
                Note.Contract.TABLE, Note.Contract.STARRED, Note.Contract.DELETED, Note.Contract.CUSTOM_TIME,
                Note.Contract.TABLE, Note.Contract.STARRED, Note.Contract.DELETED, Note.Contract.CUSTOM_TIME));
        db.execSQL(String.format("CREATE INDEX ix_%s_%s_%s_%s ON %s (%s, %s, %s);",
                Note.Contract.TABLE, Note.Contract.DRAFT, Note.Contract.DELETED, Note.Contract.CUSTOM_TIME,
                Note.Contract.TABLE, Note.Contract.DRAFT, Note.Contract.DELETED, Note.Contract.CUSTOM_TIME));
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO.
    }
}
