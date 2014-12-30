package in.eigene.miary.helpers;

import android.util.*;

import java.util.*;

public class Debouncer {

    private static final String LOG_TAG = Debouncer.class.getSimpleName();

    private final String name;
    private final long interval;

    private long lastActionTime;

    public Debouncer(final String name, final long interval, final boolean allowInstantly) {
        this.name = name;
        this.interval = interval;
        if (!allowInstantly) {
            lastActionTime = getTime();
        }
    }

    public boolean isActionAllowed() {
        final long timePassed = getTime() - lastActionTime;
        final boolean allowed = timePassed > interval;
        Log.d(LOG_TAG, String.format("%s [allowed=%s, passed=%s]", name, allowed, timePassed));
        return allowed;
    }

    public void ping() {
        lastActionTime = getTime();
        Log.d(LOG_TAG, "ping " + name);
    }

    private long getTime() {
        return new Date().getTime();
    }
}
