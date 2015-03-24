package in.eigene.miary.core.persistence;

import android.content.*;
import android.database.sqlite.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "notes.db";
    private static final int VERSION = 1;

    public DatabaseHelper(final Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Note.Repository.INSTANCE.createTable(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO.
    }
}
