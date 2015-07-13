package in.eigene.miary.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.SparseIntArray;

import in.eigene.miary.R;
import in.eigene.miary.persistence.Note;

/**
 * Used to get note background, foreground, footer and hint colors.
 */
public class NoteColorHelper {

    private static final SparseIntArray COLOR_RESOURCES = new SparseIntArray();

    private static final int LIGHT_PRIMARY_COLOR = 0xDE000000;
    private static final int LIGHT_SECONDARY_COLOR = 0x43000000;

    public final int primaryColor;
    public final int foregroundColor;
    public final int secondaryColor;

    static {
        COLOR_RESOURCES.put(Note.Color.WHITE, R.color.white);
        COLOR_RESOURCES.put(Note.Color.RED, R.color.red_400);
        COLOR_RESOURCES.put(Note.Color.ORANGE, R.color.orange_400);
        COLOR_RESOURCES.put(Note.Color.YELLOW, R.color.yellow_500);
        COLOR_RESOURCES.put(Note.Color.GRAY, R.color.grey_100);
        COLOR_RESOURCES.put(Note.Color.GREEN, R.color.light_green_500);
        COLOR_RESOURCES.put(Note.Color.BLUE, R.color.blue_200);
        COLOR_RESOURCES.put(Note.Color.PURPLE, R.color.purple_200);
    }

    public static NoteColorHelper fromIndex(final Context context, final int index) {
        return fromPrimaryColor(context, context.getResources().getColor(COLOR_RESOURCES.get(index, R.color.white)));
    }

    public static NoteColorHelper fromPrimaryColor(final Context context, final int primaryColor) {
        final Resources resources = context.getResources();
        if (getSaturation(primaryColor) >= 128) {
            return new NoteColorHelper(
                    primaryColor,
                    resources.getColor(R.color.light_text_hint),
                    resources.getColor(R.color.light_text_primary));
        } else {
            return new NoteColorHelper(
                    primaryColor,
                    resources.getColor(R.color.dark_text_hint),
                    resources.getColor(R.color.dark_text_primary));
        }
    }

    private static int getSaturation(final int color) {
        return (30 * Color.red(color) + 59 * Color.green(color) + 11 * Color.blue(color)) / 100;
    }

    private NoteColorHelper(final int primaryColor, final int secondaryColor, final int foregroundColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.foregroundColor = foregroundColor;
    }
}
