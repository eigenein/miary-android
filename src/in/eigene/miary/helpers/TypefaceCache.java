package in.eigene.miary.helpers;

import android.content.*;
import android.graphics.*;

import java.util.*;

public class TypefaceCache {

    public static final String ROBOTO_SLAB_REGULAR = "fonts/RobotoSlab/RobotoSlab-Regular.ttf";
    public static final String ROBOTO_SLAB_BOLD = "fonts/RobotoSlab/RobotoSlab-Bold.ttf";

    private static final HashMap<String, Typeface> CACHE = new HashMap<String, Typeface>();

    public static Typeface get(final Context context, final String path) {
        Typeface typeface = CACHE.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            CACHE.put(path, typeface);
        }
        return typeface;
    }
}