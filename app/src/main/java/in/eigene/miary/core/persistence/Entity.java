package in.eigene.miary.core.persistence;

import android.content.*;
import android.provider.*;

public abstract class Entity implements BaseColumns {

    protected Long id;

    public long getId() {
        return id;
    }

    public void save(final ContentResolver contentResolver) {
        if (id != null) {
            update(contentResolver);
        } else {
            id = insert(contentResolver);
        }
    }

    protected abstract long insert(final ContentResolver contentResolver);
    protected abstract void update(final ContentResolver contentResolver);
}
