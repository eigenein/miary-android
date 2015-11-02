package in.eigene.miary.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import in.eigene.miary.R;

/**
 * Used to get note background, foreground, footer and hint colors.
 */
public class NoteColorHelper {

    public final int primaryColor;
    public final int foregroundColor;
    public final int secondaryColor;

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
