package in.eigene.miary.helpers;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class ColorHelper {

    @ColorInt
    public static final int LIGHT_TEXT_HINT = 0x43000000;
    @ColorInt
    public static final int LIGHT_TEXT_PRIMARY = 0xDE000000;
    @ColorInt
    public static final int DARK_TEXT_HINT = 0x4DFFFFFF;
    @ColorInt
    public static final int DARK_TEXT_PRIMARY = Color.WHITE;

    public static int argb(final int alpha, final int color) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public static int getTextColor(final boolean isLight) {
        return isLight ? LIGHT_TEXT_PRIMARY : DARK_TEXT_PRIMARY;
    }

    public static int getHintColor(final boolean isLight) {
        return isLight ? LIGHT_TEXT_HINT : DARK_TEXT_HINT;
    }

    public static boolean isLight(final int color) {
        return saturation(color) > 127;
    }

    public static int saturation(final int color) {
        return (30 * Color.red(color) + 59 * Color.green(color) + 11 * Color.blue(color)) / 100;
    }
}
