package in.eigene.miary.core.persistence;

import android.database.*;
import android.database.sqlite.*;

import java.security.*;

public abstract class BaseRepository<T> {

    /**
     * Used to generate random identifiers.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    public void createTable(final SQLiteDatabase database) {
        database.execSQL(getCreateTableSQL());
    }

    protected static long newId() {
        return RANDOM.nextLong();
    }

    public abstract T create();

    protected abstract T read(final Cursor cursor);

    protected abstract String getCreateTableSQL();
}
