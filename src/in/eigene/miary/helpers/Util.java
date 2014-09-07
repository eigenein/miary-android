package in.eigene.miary.helpers;

public class Util {

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T as(Object object, Class<T> class_) {
        if (class_.isInstance(object)) {
            return (T)object;
        }
        return null;
    }
}
