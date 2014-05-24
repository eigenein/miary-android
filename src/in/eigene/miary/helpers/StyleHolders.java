package in.eigene.miary.helpers;

import in.eigene.miary.core.*;

import java.util.*;

/**
 * Maps note color into a style holder.
 */
public class StyleHolders {

    private static final HashMap<Integer, StyleHolder> MAPPING =
            new HashMap<Integer, StyleHolder>();

    static {
        MAPPING.put(Note.COLOR_WHITE, StyleHolder.WHITE);
        MAPPING.put(Note.COLOR_RED, StyleHolder.RED);
        MAPPING.put(Note.COLOR_ORANGE, StyleHolder.ORANGE);
        MAPPING.put(Note.COLOR_YELLOW, StyleHolder.YELLOW);
        MAPPING.put(Note.COLOR_GRAY, StyleHolder.GRAY);
        MAPPING.put(Note.COLOR_GREEN, StyleHolder.GREEN);
        MAPPING.put(Note.COLOR_BLUE, StyleHolder.BLUE);
        MAPPING.put(Note.COLOR_VIOLET, StyleHolder.VIOLET);
    }

    public static StyleHolder get(final int color) {
        return MAPPING.get(color);
    }
}
