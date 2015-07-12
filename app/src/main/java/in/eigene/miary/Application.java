package in.eigene.miary;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import in.eigene.miary.helpers.ParseHelper;

public class Application extends android.app.Application {

    private static Tracker tracker;

    public static Tracker getTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        ParseHelper.initialize(this);

        tracker = GoogleAnalytics.getInstance(this).newTracker("UA-65034198-1");
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
    }
}
