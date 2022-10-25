package com.brins.commom.drawable;

import android.annotation.SuppressLint;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * 使用此类来操作Drawable，不会出现IDE的lint警告
 * <p>
 * lint 警告只针对调用"setAlpha", "setColorFilter", "setBounds", "setTint", "setXfermode" 四个方法
 */
@SuppressLint("Drawable留意mutate")
public final class DrawableWrapper {

    private final Drawable mDrawable;

    public DrawableWrapper(Drawable mDrawable) {
        this.mDrawable = mDrawable.mutate();
    }

    public DrawableWrapper setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mDrawable.setAlpha(alpha);
        return this;
    }

    public DrawableWrapper setBounds(int left, int top, int right, int bottom) {
        mDrawable.setBounds(left, top, right, bottom);
        return this;
    }

    public DrawableWrapper setColorFilter(ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
        return this;
    }

    public DrawableWrapper setBounds(@NonNull Rect bounds) {
        return setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public DrawableWrapper setTint(@ColorInt int tintColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDrawable.setTint(tintColor);
        }
        return this;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }
}
