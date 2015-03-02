package in.eigene.miary.helpers;

import in.eigene.miary.exceptions.*;
import in.eigene.miary.helpers.lang.*;

import java.text.*;
import java.util.*;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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

    public static String format(final Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date parse(final String string) {
        try {
            return DATE_FORMAT.parse(string);
        } catch (final ParseException e) {
            InternalRuntimeException.throwForException("Could not parse date: " + string, e);
            return null;
        }
    }

    /**
     * Checks whether the object is null or string is empty.
     */
    public static boolean isNullOrEmpty(final String string) {
        return (string == null) || string.isEmpty();
    }

    /**
     * Apply function to every item of collection and return a list of the results.
     */
    public static <TValue, TResult> List<TResult> map(
            final Collection<TValue> from,
            Function<TValue, TResult> function) {
        final ArrayList<TResult> result = new ArrayList<TResult>();
        for (final TValue value : from) {
            result.add(function.apply(value));
        }
        return result;
    }
}