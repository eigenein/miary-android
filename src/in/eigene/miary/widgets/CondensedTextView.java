package in.eigene.miary.widgets;

import android.content.*;
import android.util.*;
import android.widget.*;
import in.eigene.miary.helpers.*;

public class CondensedTextView extends TextView {

    public CondensedTextView(final Context context) {
        super(context);
        setTypeface(TypefaceCache.get(context, TypefaceCache.REGULAR));
    }

    public CondensedTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setTypeface(TypefaceCache.get(context, TypefaceCache.REGULAR));
    }

    public CondensedTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(TypefaceCache.get(context, TypefaceCache.REGULAR));
    }
}
