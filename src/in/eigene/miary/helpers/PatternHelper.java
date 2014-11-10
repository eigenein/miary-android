package in.eigene.miary.helpers;

import java.util.*;
import java.util.regex.*;

public class PatternHelper {

    public static String[] findAll(final Pattern pattern, final String string) {
        final ArrayList<String> list = new ArrayList<String>();
        final Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        final String[] array = list.toArray(new String[list.size()]);
        return array;
    }
}
