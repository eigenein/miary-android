package in.eigene.miary.helpers;

import in.eigene.miary.exceptions.*;

import java.text.*;
import java.util.*;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

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
}
