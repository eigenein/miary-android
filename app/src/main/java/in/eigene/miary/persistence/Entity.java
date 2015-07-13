package in.eigene.miary.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface Entity extends BaseColumns {

    Uri insert(final ContentResolver contentResolver);
    int update(final ContentResolver contentResolver);
}
