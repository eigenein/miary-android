package in.eigene.miary.core.persistence;

import android.provider.*;

public abstract class Entity implements BaseColumns {

    protected long id;

    public long getId() {
        return id;
    }
}
