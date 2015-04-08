package in.eigene.miary.core.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface Entity extends BaseColumns {

    public Uri insert(final ContentResolver contentResolver);
    public int update(final ContentResolver contentResolver);
}
