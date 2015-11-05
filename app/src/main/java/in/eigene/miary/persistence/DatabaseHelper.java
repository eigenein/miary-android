package in.eigene.miary.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import in.eigene.miary.helpers.lang.Consumer;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static final String NAME = "notes.db";
    private static final int VERSION = 2;

    private static final SparseArray<Consumer<SQLiteDatabase>> UPGRADES = new SparseArray<>();

    static {
        UPGRADES.append(2, new Consumer<SQLiteDatabase>() {
            /**
             * Replaces legacy color codes with actual color values.
             */
            @Override
            public void accept(final SQLiteDatabase db) {
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 0; -- white",
                        Note.Contract.TABLE, 0xFFFFFFFF));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 1; -- red",
                        Note.Contract.TABLE, 0xFFEF5350));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 2; -- orange",
                        Note.Contract.TABLE, 0xFFFFA726));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 3; -- yellow",
                        Note.Contract.TABLE, 0xFFFFEB3B));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 4; -- gray",
                        Note.Contract.TABLE, 0xFFF5F5F5));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 5; -- green",
                        Note.Contract.TABLE, 0xFF8BC34A));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 6; -- blue",
                        Note.Contract.TABLE, 0xFF90CAF9));
                db.execSQL(String.format("UPDATE %s SET color = %s WHERE color = 7; -- purple",
                        Note.Contract.TABLE, 0xFFCE93D8));
            }
        });
    }

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
        Log.i(LOG_TAG, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        for (int version = oldVersion + 1; version <= newVersion; version += 1) {
            Log.i(LOG_TAG, "Applying upgrade to version " + version);
            UPGRADES.get(version).accept(db);
        }
        Log.i(LOG_TAG, "Upgrades finished.");
    }
}
