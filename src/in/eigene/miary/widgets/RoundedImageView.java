package in.eigene.miary.widgets;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.widget.*;

/**
 * http://stackoverflow.com/a/16208548/359730
 */
public class RoundedImageView extends ImageView {

    private Bitmap cachedBitmap;

    public RoundedImageView(final Context context) {
        super(context);
    }

    public RoundedImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageResource(final int resourceId) {
        super.setImageResource(resourceId);
        cachedBitmap = null;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (cachedBitmap == null) {
            cachedBitmap = getRoundedCornerBitmap();
        }
        if (cachedBitmap != null) {
            canvas.drawBitmap(cachedBitmap, 0, 0, null);
        }
    }

    private Bitmap getRoundedCornerBitmap() {
        final Drawable drawable = getDrawable();
        if ((drawable == null) || (getWidth() == 0) || (getHeight() == 0)) {
            return null;
        }
        final Bitmap sourceBitmap = ((BitmapDrawable)drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        return getRoundedCornerBitmap(sourceBitmap, getWidth() / 2.0f, getHeight() / 2.0f);
    }

    /**
     * http://stackoverflow.com/a/15032283/359730
     */
    private static Bitmap getRoundedCornerBitmap(final Bitmap source, final float rx, final float ry) {

        final Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        final RectF rect = new RectF(0.0f, 0.0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rect, rx, ry, paint);

        return output;
    }
}
