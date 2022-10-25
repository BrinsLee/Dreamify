package com.brins.commom.glide;

import android.content.Context;
import android.graphics.Bitmap;
import com.brins.commom.utils.FastBlurUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * 模糊图片转换器
 */
public class GlideBlurTransform2 extends BitmapTransformation {

    private int mBlurRadius = 0;
    private int mBlurAlpha = 0;
    private int zoomOut = 6;

    private boolean recyleBitmap = true;

    /**
     * @param context
     * @param radius
     * @param alpha   黑色蒙层透明度，0-255
     */
    public GlideBlurTransform2(Context context, int radius, int alpha) {
        super(context);
        mBlurRadius = radius;
        mBlurAlpha = alpha;
        recyleBitmap = true;
    }

    public GlideBlurTransform2 setRecyleBitmap(boolean recyle) {
        recyleBitmap = recyle;
        return this;
    }

    /**
     * 设置缩小倍数
     *
     * @param zoomOut
     * @return
     */
    public GlideBlurTransform2 setZoomOut(int zoomOut) {
        this.zoomOut = zoomOut;
        return this;
    }
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }
    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap bitmap = source;
        int width = Math.min(bitmap.getWidth() / zoomOut, 100);
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        source = FastBlurUtils.javaMakeBlur2(bitmap, width, height, mBlurRadius, mBlurAlpha);
//        if(recyleBitmap) {
//            bitmap.recycle();
//        }
        return source;
    }
    @Override
    public String getId() {
        return getClass().getName();
    }
}