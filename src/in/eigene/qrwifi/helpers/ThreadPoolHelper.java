package in.eigene.qrwifi.helpers;

import android.util.*;

import java.util.concurrent.*;

public class ThreadPoolHelper {

    private static final String LOG_TAG = ThreadPoolHelper.class.getSimpleName();

    public static void awaitTermination(final ThreadPoolExecutor executor) {
        try {
            while (executor.awaitTermination(1, TimeUnit.SECONDS));
        } catch (final InterruptedException e) {
            Log.w(LOG_TAG, e);
        }
    }
}
