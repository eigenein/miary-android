package in.eigene.miary.helpers;

public class Util {

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }
}