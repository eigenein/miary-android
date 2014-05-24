package in.eigene.miary.helpers;

import in.eigene.miary.*;

/**
 * Defines styles for a note in both feed and note fragments.
 */
public enum StyleHolder {

    WHITE(
            R.drawable.feed_item_white,
            android.R.color.white,
            R.color.hint_foreground_light,
            R.color.darker_gray),
    RED(
            R.drawable.feed_item_red,
            R.color.red_light,
            R.color.red_dark),
    ORANGE(
            R.drawable.feed_item_orange,
            R.color.orange_light,
            R.color.orange_dark),
    YELLOW(
            R.drawable.feed_item_yellow,
            R.color.yellow_light,
            R.color.yellow_dark),
    GRAY(
            R.drawable.feed_item_gray,
            R.color.background_holo_light,
            R.color.hint_foreground_light),
    GREEN(
            R.drawable.feed_item_green,
            R.color.green_light,
            R.color.green_dark),
    BLUE(
            R.drawable.feed_item_blue,
            R.color.blue_light,
            R.color.blue_dark),
    VIOLET(
            R.drawable.feed_item_violet,
            R.color.violet_light,
            R.color.violet_dark);

    public final int feedItemDrawableId;
    public final int noteBackgroundColorId;
    public final int hintColorId;
    public final int feedItemFooterColorId;

    StyleHolder(
            final int feedItemDrawableId,
            final int noteBackgroundColorId,
            final int hintColorId) {
        this(feedItemDrawableId, noteBackgroundColorId, hintColorId, hintColorId);
    }

    StyleHolder(
            final int feedItemDrawableId,
            final int noteBackgroundColorId,
            final int hintColorId,
            final int feedItemFooterColorId) {
        this.feedItemDrawableId = feedItemDrawableId;
        this.noteBackgroundColorId = noteBackgroundColorId;
        this.hintColorId = hintColorId;
        this.feedItemFooterColorId = feedItemFooterColorId;
    }
}
