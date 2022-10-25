
package com.brins.commom.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Size;
import android.util.StateSet;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.drawable.DrawableWrapper;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 图片获取工具类
 * 
 * @author heyangbin 创建时间：2012-9-3 上午11:19:34
 */
public class DrawableUtil {

    private static HashMap<String, WeakReference<Bitmap>> mBitmapCache = new HashMap<String, WeakReference<Bitmap>>(
            0);

    private static Object mObjects = new Object();

    @Deprecated
    public static Bitmap createBitmapFromPath(String pathName) {
        if (pathName == null) {
            return null;
        }
        recycle(pathName);
        if (mBitmapCache.containsKey(pathName)) {
            Bitmap bm = mBitmapCache.get(pathName).get();
            if (bm != null) {
                return bm;
            } else {
                try {
                    bm = KGBitmapUtil.decodeFileMayNull(pathName);
                } catch (Exception e) {
                }
                if (bm != null) {
                    mBitmapCache.put(pathName, new WeakReference<Bitmap>(bm));
                    return bm;
                }
            }

        } else {
            Bitmap bm = null;
            try {
                bm = KGBitmapUtil.decodeFileMayNull(pathName);
                if (bm != null) {
                    mBitmapCache.put(pathName, new WeakReference<Bitmap>(bm));
                    return bm;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Deprecated
    public static Bitmap createBitmapFromResId(Context context, int resId) {
        if (resId <= 0) {
            return null;
        }
        recycle("" + resId);
        if (mBitmapCache.containsKey("" + resId)) {
            Bitmap bm = mBitmapCache.get("" + resId).get();
            if (bm != null) {
                return bm;
            } else {
                // BitmapFactory.Options opts = new BitmapFactory.Options();
                // opts.inJustDecodeBounds = true;
                try {
                    bm = KGBitmapUtil.decodeResource(context.getApplicationContext(), context
                            .getApplicationContext().getResources(), resId);
                } catch (Exception e) {
                }
                if (bm != null) {
                    mBitmapCache.put("" + resId, new WeakReference<Bitmap>(bm));
                    return bm;
                }
            }

        } else {
            Bitmap bm;
            // BitmapFactory.Options opts = new BitmapFactory.Options();
            // opts.inJustDecodeBounds = true;
            try {
                bm = KGBitmapUtil.decodeResource(context.getApplicationContext(), context
                        .getApplicationContext().getResources(), resId);
                if (bm != null) {
                    mBitmapCache.put("" + resId, new WeakReference<Bitmap>(bm));
                    return bm;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 获取背景图
     *
     * @param pathName 自定义背景图路径
     * @return
     */
    @Deprecated
    public static Drawable createDrawableFromPath(String pathName) {
        if (pathName == null) {
            return null;
        }
        recycle(pathName);
        if (mBitmapCache.containsKey(pathName)) {
            Bitmap bm = mBitmapCache.get(pathName).get();
            if (bm != null) {
                return drawableFromBitmap(bm);
            } else {
                try {
                    bm = KGBitmapUtil.decodeFileMayNull(pathName);
                } catch (Exception e) {
                }
                if (bm != null) {
                    mBitmapCache.put(pathName, new WeakReference<Bitmap>(bm));
                    return drawableFromBitmap(bm);
                }
            }

        } else {
            Bitmap bm = null;
            try {
                bm = KGBitmapUtil.decodeFileMayNull(pathName);
                if (bm != null) {
                    mBitmapCache.put(pathName, new WeakReference<Bitmap>(bm));
                    return drawableFromBitmap(bm);
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Deprecated
    public static Drawable createDrawableFromResId(Context context, int resId) {
        if (resId <= 0) {
            return null;
        }
        recycle("" + resId);
        if (mBitmapCache.containsKey("" + resId)) {
            Bitmap bm = mBitmapCache.get("" + resId).get();
            if (bm != null) {
                return drawableFromBitmap(bm);
            } else {
                // BitmapFactory.Options opts = new BitmapFactory.Options();
                // opts.inJustDecodeBounds = true;
                try {
                    bm = KGBitmapUtil.decodeResource(context.getApplicationContext(), context
                            .getApplicationContext().getResources(), resId);
                } catch (Exception e) {
                }
                if (bm != null) {
                    mBitmapCache.put("" + resId, new WeakReference<Bitmap>(bm));
                    return drawableFromBitmap(bm);
                }
            }

        } else {
            Bitmap bm;
            // BitmapFactory.Options opts = new BitmapFactory.Options();
            // opts.inJustDecodeBounds = true;
            try {
                bm = KGBitmapUtil.decodeResource(context.getApplicationContext(), context
                        .getApplicationContext().getResources(), resId);
                if (bm != null) {
                    mBitmapCache.put("" + resId, new WeakReference<Bitmap>(bm));
                    return drawableFromBitmap(bm);
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static Drawable drawableFromBitmap(Bitmap bm) {
        final BitmapDrawable drawable = new BitmapDrawable(bm);
        return drawable;
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int did) {
        Drawable drawable = context.getResources().getDrawable(did);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public static Bitmap getBitmap(@DrawableRes int did) {
        return getBitmap(DRCommonApplication.getContext(), did);
    }

    public static Drawable getOvalStateDrawable(int defaultColor, int overColor) {
        GradientDrawable normal = new GradientDrawable();
        normal.setShape(GradientDrawable.OVAL);
        normal.setColor(defaultColor);
        GradientDrawable over = new GradientDrawable();
        over.setShape(GradientDrawable.OVAL);
        over.setColor(overColor);
        LayerDrawable pressed = new LayerDrawable(new Drawable[]{normal, over});
        return getStateDrawable(normal, pressed);
    }

    public static Drawable getStateDrawable(Drawable defaultDrawable, Drawable pressedDrawable) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{
                android.R.attr.state_pressed
        }, pressedDrawable);
        drawable.addState(new int[]{
                android.R.attr.state_selected
        }, pressedDrawable);
        drawable.addState(new int[]{
                android.R.attr.state_focused, android.R.attr.state_enabled
        }, pressedDrawable);
        drawable.addState(new int[]{
                android.R.attr.state_enabled
        }, defaultDrawable);
        drawable.addState(new int[]{
                android.R.attr.state_focused
        }, pressedDrawable);
        drawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return drawable;
    }

    /**
     * 把之前产生的bitmap回收掉，防止内存溢出
     *
     * @param remainKey 不回收的key名，为图片路径名
     */
    @Deprecated
    public static void recycle(String remainKey) {
        synchronized (mObjects) {
            for (Iterator<String> keys = mBitmapCache.keySet().iterator(); keys.hasNext(); ) {
                String key = keys.next();
                if (key.equals(remainKey)) {
                    continue;
                }
                WeakReference<Bitmap> item = mBitmapCache.get(key);
                if (item != null) {
                    Bitmap bitmap = item.get();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    keys.remove();
                }
            }
        }
    }

    /**
     * 生成指定样式的圆角矩形
     * color: 填充色 int
     * radius: 半径,单位px
     */
    public static Drawable createRectRoundDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    /**
     * 创建圆角矩形
     *
     * @param color
     * @param radius
     * @return
     */
    public static Drawable createRoundDrawable(int color, @Size(8) float[] radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(radius);
        return drawable;
    }

    public static Drawable createRoundDrawable(@ColorInt int solidColor, float radius, @ColorInt int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(solidColor);
        return drawable;
    }

    public static GradientDrawable makeGradientDrawable(int shape, int width, int height, int color, int strokeWidth, int strokeColor) {
        GradientDrawable normalBGDrawable = new GradientDrawable();
        normalBGDrawable.setShape(shape);
        normalBGDrawable.setSize(width, height);
        normalBGDrawable.setStroke(strokeWidth, strokeColor);
        normalBGDrawable.setColor(color);
        return normalBGDrawable;
    }

    public static GradientDrawable makeGradientDrawable(int color) {
        return makeGradientDrawable(GradientDrawable.RECTANGLE, -1, -1, color, 0, 0);
    }

    public static GradientDrawable makeGradientDrawable(int shape, int width, int height, int color) {
        return makeGradientDrawable(shape, width, height, color, 0, 0);
    }


    public static GradientDrawable makeGradientDrawable(GradientDrawable.Orientation orientation,
                                                        int startColor, int endColor, int leftTop,
                                                        int rightTop, int leftBottom, int rightBottom) {
        int[] colors = {startColor, endColor};
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, colors);
        gradientDrawable.setCornerRadii(new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom,
                rightBottom, leftBottom, leftBottom});
        return gradientDrawable;
    }

    public static void setBounds(Drawable drawable, float w, float h) {
        drawable.setBounds(0, 0, SystemUtils.dip2px(DRCommonApplication.getContext(), w),
                SystemUtils.dip2px(DRCommonApplication.getContext(), h));
    }

    @SuppressLint("Drawable留意mutate")
    public static Drawable setBoundsBySelf(Drawable drawable) {
        Drawable d = drawable.mutate();
        d.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return d;
    }

    public static Drawable getDrawableApplyFilter(Context context, @DrawableRes int id, @ColorInt
        int color) {
        return new DrawableWrapper(context.getResources().getDrawable(id)).setTint(color).getDrawable();
    }
}
