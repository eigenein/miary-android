package in.eigene.miary.core.persistence;

import android.content.*;
import android.net.*;
import android.provider.*;

public interface Entity extends BaseColumns {

    public Uri insert(final ContentResolver contentResolver);
    public int update(final ContentResolver contentResolver);
}
