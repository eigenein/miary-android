package in.eigene.miary.helpers;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.eigene.miary.exceptions.InternalRuntimeException;

public class Util {

    public static final Date EPOCH = new Date(0);

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * Returns first non-null argument.
     */
    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

    /**
     * .NET "as" operator equivalent.
     */
    public static <T> T as(Object object, Class<T> class_) {
        if (class_.isInstance(object)) {
            return (T)object;
        }
        return null;
    }

    /**
     * Formats date according to ISO 8601 format.
     */
    public static String format(final Date date) {
        return ISO8601.format(date);
    }

    /**
     * Parses ISO 8601 formatted date.
     */
    public static Date parse(final String string) {
        try {
            return ISO8601.parse(string);
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Could not parse date: " + string, e);
            return null;
        }
    }
}
