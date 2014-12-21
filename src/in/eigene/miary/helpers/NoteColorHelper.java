package in.eigene.miary.helpers;

import android.content.*;
import android.graphics.*;
import android.util.*;
import in.eigene.miary.*;
import in.eigene.miary.core.*;

/**
 * Used to get note background, foreground, footer and hint colors.
 */
public class NoteColorHelper {

    private static final SparseIntArray COLOR_RESOURCES = new SparseIntArray();

    private static final int DARK_SECONDARY_COLOR = 0x43000000;
    private static final int LIGHT_SECONDARY_COLOR = 0xFFFFFFFF - DARK_SECONDARY_COLOR;

    public final int primaryColor;
    public final int foregroundColor;
    public final int secondaryColor;

    static {
        COLOR_RESOURCES.put(Note.COLOR_WHITE, R.color.note_white);
        COLOR_RESOURCES.put(Note.COLOR_RED, R.color.note_red);
        COLOR_RESOURCES.put(Note.COLOR_ORANGE, R.color.note_orange);
        COLOR_RESOURCES.put(Note.COLOR_YELLOW, R.color.note_yellow);
        COLOR_RESOURCES.put(Note.COLOR_GRAY, R.color.note_gray);
        COLOR_RESOURCES.put(Note.COLOR_GREEN, R.color.note_green);
        COLOR_RESOURCES.put(Note.COLOR_BLUE, R.color.note_blue);
        COLOR_RESOURCES.put(Note.COLOR_VIOLET, R.color.note_violet);
    }

    public static NoteColorHelper fromIndex(final Context context, final int index) {
        return new NoteColorHelper(context.getResources().getColor(COLOR_RESOURCES.get(index)));
    }

    private static int getSaturation(final int color) {
        return (30 * Color.red(color) + 59 * Color.green(color) + 11 * Color.blue(color)) / 100;
    }

    public NoteColorHelper(final int primaryColor) {
        this.primaryColor = primaryColor;

        if (getSaturation(primaryColor) >= 128) {
            // Light primary color.
            this.foregroundColor = Color.BLACK;
            this.secondaryColor = DARK_SECONDARY_COLOR;
        } else {
            // Dark primary color.
            this.foregroundColor = Color.WHITE;
            this.secondaryColor = LIGHT_SECONDARY_COLOR;
        }
    }
}
