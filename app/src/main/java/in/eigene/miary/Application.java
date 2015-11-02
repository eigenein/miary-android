package in.eigene.miary;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import in.eigene.miary.helpers.CustomExceptionParser;
import in.eigene.miary.helpers.ParseHelper;

public class Application extends android.app.Application {

    private static Tracker tracker;

    public static Tracker getTracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ParseHelper.initialize(this);

        tracker = GoogleAnalytics.getInstance(this).newTracker("UA-65034198-1");
        tracker.enableAutoActivityTracking(true);
        tracker.enableAdvertisingIdCollection(true);
        enableExceptionReporting();
    }

    private void enableExceptionReporting() {
        final ExceptionReporter exceptionReporter = new ExceptionReporter(
                tracker,
                Thread.getDefaultUncaughtExceptionHandler(),
                getApplicationContext());
        exceptionReporter.setExceptionParser(new CustomExceptionParser());
        Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);
    }
}
