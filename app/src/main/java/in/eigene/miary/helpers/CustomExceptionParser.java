package in.eigene.miary.helpers;

import android.util.Log;

import com.google.android.gms.analytics.ExceptionParser;

/**
 * Enables sending full exception stacktrace.
 */
public class CustomExceptionParser implements ExceptionParser {

    @Override
    public String getDescription(final String threadName, Throwable throwable) {
        final StringBuilder descriptionBuilder = new StringBuilder();
        while (throwable != null) {
            descriptionBuilder.append(throwable.getClass().getCanonicalName());
            descriptionBuilder.append("(\"");
            descriptionBuilder.append(throwable.getMessage());
            descriptionBuilder.append("\")");
            for (final StackTraceElement element : throwable.getStackTrace()) {
                if (element.getClassName().startsWith("in.eigene.miary")) {
                    descriptionBuilder.append(" in ");
                    descriptionBuilder.append(element.toString());
                }
            }
            throwable = throwable.getCause();
            if (throwable != null) {
                descriptionBuilder.append(" from ");
            }
        }
        Log.e("Exception", descriptionBuilder.toString());
        return descriptionBuilder.toString();
    }
}
