package in.eigene.miary;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.yandex.metrica.YandexMetrica;

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

        YandexMetrica.activate(getApplicationContext(), "fde53c21-e58b-4aaa-8d1a-831d985a879f");
        YandexMetrica.enableActivityAutoTracking(this);
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
