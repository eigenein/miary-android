package in.eigene.miary.core.persistence;

import android.content.*;
import android.net.*;
import android.provider.*;

public abstract class Entity implements BaseColumns {

    public abstract Uri insert(final ContentResolver contentResolver);
    public abstract void update(final ContentResolver contentResolver);
}
