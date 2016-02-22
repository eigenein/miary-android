package in.eigene.miary.helpers;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class TypefaceCache {

    public static final String ROBOTO_SLAB_REGULAR = "fonts/RobotoSlab-Regular.ttf";
    public static final String ROBOTO_CONDENSED_BOLD = "fonts/RobotoCondensed-Bold.ttf";

    private static final HashMap<String, Typeface> CACHE = new HashMap<>();

    public static Typeface get(final Context context, final String path) {
        Typeface typeface = CACHE.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            CACHE.put(path, typeface);
        }
        return typeface;
    }
}
