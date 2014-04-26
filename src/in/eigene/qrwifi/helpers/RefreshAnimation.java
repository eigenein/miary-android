package in.eigene.qrwifi.helpers;

import android.annotation.*;
import android.os.*;
import android.view.*;
import in.eigene.qrwifi.*;

public class RefreshAnimation {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void start(final MenuItem item) {
        if (AndroidVersion.isHoneycomb()) {
            item.setActionView(R.layout.ab_indeterminate_progress);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void stop(final MenuItem item) {
        if (AndroidVersion.isHoneycomb()) {
            item.setActionView(null);
        }
    }
}
