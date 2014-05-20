package in.eigene.miary.helpers;

import android.graphics.*;
import android.text.*;
import android.text.style.*;

public class TypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;

    public TypefaceSpan(final Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(final TextPaint textPaint) {
        textPaint.setTypeface(typeface);
        textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(final TextPaint textPaint) {
        textPaint.setTypeface(typeface);
        textPaint.setFlags(textPaint.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}
