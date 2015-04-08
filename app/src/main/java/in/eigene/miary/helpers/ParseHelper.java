package in.eigene.miary.helpers;

import android.content.*;
import com.parse.*;

import in.eigene.miary.core.persistence.*;

import java.util.*;

public class ParseHelper {

    private static final String APPLICATION_ID = "jpnD20rkM3xxna9OhRtun2IbzE7QjPEULtEmIRKC";
    private static final String CLIENT_KEY = "ChviiekJmgXCOcQuuzNnifiIHjQ3vHa2GqYW4yCC";

    public static void initialize(final Context context) {
        ParseCrashReporting.enable(context);
        Parse.enableLocalDatastore(context);
        ParseObject.registerSubclass(Feedback.class);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }

    public static void trackEvent(final String name, final String dimensionKey, final String dimensionValue) {
        final HashMap<String, String> dimensions = new HashMap<>();
        dimensions.put(dimensionKey, dimensionValue);
        ParseAnalytics.trackEventInBackground(name, dimensions);
    }
}
