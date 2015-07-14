package in.eigene.miary.helpers;

import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;

/**
 * Enables sending full exception stacktrace.
 */
public class CustomExceptionParser implements ExceptionParser {

    @Override
    public String getDescription(final String threadName, final Throwable throwable) {
        final StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append(threadName);
        descriptionBuilder.append(" :: ");
        descriptionBuilder.append(throwable.getMessage());
        for (final StackTraceElement element : throwable.getStackTrace()) {
            if (element.getClassName().startsWith("in.eigene.miary")) {
                descriptionBuilder.append(" :: ");
                descriptionBuilder.append(element.toString());
            }
        }
        Log.i("Exception", descriptionBuilder.toString());
        return descriptionBuilder.toString();
    }
}
