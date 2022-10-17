package com.brins.commom.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import com.brins.commom.utils.SystemUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * 圆角图片转换器
 */
public class GlideAsRoundRectTransform extends BitmapTransformation {

    private final boolean mRound;

    private final int mBorderColor;

    private final int mBorderWidth;

    private final float mRx;

    private final float mRy;

    private float[] mRadii;
    private boolean mUserRadii;

    public GlideAsRoundRectTransform(Context context) {
        super(context);
        mRound = false;
        mBorderColor = 0;
        mBorderWidth = 0;

        mRx = SystemUtils.dip2px(context,3);
        mRy = mRx;
    }

    public GlideAsRoundRectTransform(Context context, float[] radii) {
        super(context);
        mRound = false;
        mBorderColor = 0;
        mBorderWidth = 0;

        mRx = SystemUtils.dip2px(context, 3);
        mRy = mRx;

        mUserRadii = true;
        mRadii = radii;
    }

    public GlideAsRoundRectTransform(Context context, float rx, float ry) {
        super(context);
        mRound = false;
        mBorderColor = 0;
        mBorderWidth = 0;
        mRx = rx;
        mRy = ry;
    }

    public GlideAsRoundRectTransform(Context context, int borderColor,
                                     int borderWidth, float rx, float ry){
        super(context);
        mRound = false;
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
        mRx = rx;
        mRy = ry;
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        final int w = toTransform.getWidth();
        final int h = toTransform.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (bitmap == null)
            return null;
        Canvas canvas = new Canvas(bitmap);

        if (mBorderColor != Color.TRANSPARENT && mBorderWidth > 0) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(mBorderColor);
            if (mRound) {
                boolean isWidthMinor = w < h;
                float majorStart = Math.abs(w - h) / 2.0f;
                float majorEnd = Math.abs(w + h) / 2.0f;
                float left = isWidthMinor ? 0 : majorStart;
                float top = isWidthMinor ? majorStart : 0;
                float right = isWidthMinor ? w : majorEnd;
                float bottom = isWidthMinor ? majorEnd : h;
                RectF oval = new RectF(left, top, right, bottom);
                canvas.drawOval(oval, paint);
            } else {
                RectF rectF = new RectF(0, 0, w, h);
                canvas.drawRoundRect(rectF, mRx, mRy, paint);
            }
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        if (mRound) {
            final float radius = (w > h ? h / 2.0f : w / 2.0f) - mBorderWidth;
            canvas.drawCircle(w / 2, h / 2, radius, paint);
        } else {
            RectF rectF = new RectF(mBorderWidth, mBorderWidth, w - mBorderWidth, h - mBorderWidth);
            if (mUserRadii) {
                Path path = new Path();
                path.addRoundRect(rectF, mRadii, Path.Direction.CCW);
                canvas.drawPath(path, paint);
            } else {
                canvas.drawRoundRect(rectF, mRx, mRy, paint);
            }
        }

        return bitmap;
    }
    @Override
    public String getId() {
        return getClass().getName() + mRx + mRy + mUserRadii;
    }
}