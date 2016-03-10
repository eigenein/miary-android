package in.eigene.miary.helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Automatic substitutions helper.
 */
public class Substitutions {

    private static final Pattern PATTERN = Pattern.compile("(--| - |<<|>>|\\.\\.\\.)");
    private static final HashMap<String, String> TABLE = new HashMap<>();

    static {
        TABLE.put("--", "–");
        TABLE.put(" - ", " – ");
        TABLE.put("<<", "«");
        TABLE.put(">>", "»");
        TABLE.put("...", "…");
    }

    @NonNull
    public static String replaceAll(final String string) {
        final Matcher matcher = PATTERN.matcher(string);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, TABLE.get(matcher.group()));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
