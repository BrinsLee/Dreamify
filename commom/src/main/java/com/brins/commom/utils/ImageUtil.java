
package com.brins.commom.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.glide.GlideAsRoundRectTransform;
import com.brins.commom.glide.GlideRoundTransform;
import com.brins.commom.preference.CommonSettingPrefs;
import com.brins.commom.profile.SkinColorLib;
import com.brins.commom.skin.SkinSetting;
import com.brins.commom.utils.log.DrLog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 图片工具
 *
 * @author Zhanghanguo
 */
public final class ImageUtil {
    private static final String TAG = "ImageUtil";

    /**
     * 缩放图片
     *
     * @param bitmap 源图片
     * @param w      新图片宽
     * @param h      新图片高
     * @return 新图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = KGBitmapUtil.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        // bitmap.recycle();
        return newbmp;
    }

    /**
     * 缩放图片
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    // public static Bitmap zoomBitmap(Drawable drawable, int w, int h) {
    // Bitmap bitmap = drawableToBitmap(drawable);
    // return zoomBitmap(bitmap, w, h);
    // }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable {@link Drawable}
     * @return {@link Bitmap}
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = KGBitmapUtil.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                        : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将Drawable转化为Bitmap,加上水印
     *
     * @param drawable {@link Drawable}
     * @return {@link Bitmap}
     */
    public static Bitmap drawableToBitmapWithWatermark(Drawable drawable, int left, int top) {
        if (drawable == null) {
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = KGBitmapUtil.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                        : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        float scale = 0.75f;
        Paint mbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//防止锯齿
        String textString = "广告";
        mbPaint.setTextSize(SystemUtils.dip2px(DRCommonApplication.getContext(), 9));

        int baseX = SystemUtils.dip2px(DRCommonApplication.getContext(), left * scale);
        int baseY = SystemUtils.dip2px(DRCommonApplication.getContext(), top * scale);
        int backgroundRx = SystemUtils.dip2px(DRCommonApplication.getContext(), 2 * scale);
        int backgroundRy = SystemUtils.dip2px(DRCommonApplication.getContext(), 2 * scale);
        int backgroundWidth = SystemUtils.dip2px(DRCommonApplication.getContext(), 28 * scale);
        int backgroundHeight = SystemUtils.dip2px(DRCommonApplication.getContext(), 15 * scale);
        float textWidth = mbPaint.measureText(textString);
        float leftMargin = (backgroundWidth - textWidth) / 2;
        mbPaint.setColor(0x33000000);

        Paint.FontMetrics fontMetrics = mbPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float y = baseY + textHeight - fontMetrics.descent;

        Rect rect = new Rect(baseX, baseY, baseX + backgroundWidth, baseY + backgroundHeight);
        RectF mSkipRectF = new RectF(rect);
        canvas.drawRoundRect(mSkipRectF, backgroundRx, backgroundRy, mbPaint);

        mbPaint.setColor(Color.WHITE);
        canvas.drawText(textString, baseX + leftMargin, y, mbPaint);
        return bitmap;
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable {@link Drawable}
     * @return {@link Bitmap}
     */
    public static Bitmap drawableToBitmap(Drawable drawable, Config config) {
        if (drawable == null) {
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = KGBitmapUtil.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap colorToBitmap(@ColorInt int color, final int width, final int height) {
        return drawableToBitmap(new ColorDrawable(color) {
            @Override
            public int getIntrinsicWidth() {
                return width;
            }

            @Override
            public int getIntrinsicHeight() {
                return height;
            }
        });
    }

    /**
     * 将Bitmap转为Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        return (Drawable) bitmapDrawable;
    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap  源图片
     * @param roundPx 圆角半径
     * @return 新图片
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap output = KGBitmapUtil.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap 源图片
     * @param radii  圆角半径
     * @return 新图片
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float[] radii) {
        if (bitmap == null || radii == null) {
            return null;
        }
        Bitmap output = KGBitmapUtil.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final Path path = new Path();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        path.addRoundRect(rectF, radii, Path.Direction.CCW);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得圆形的方法
     *
     * @param bitmap  源图片
     * @param roundPx 圆角半径
     * @return 新图片
     */
    public static Bitmap getRoundedBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = KGBitmapUtil.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获得带边框圆形的方法
     *
     * @param bitmap     源图片
     * @param roundPx    圆角半径
     * @param boderPx    背景边框宽度
     * @param boderColor 背景边框颜色
     * @return 新图片
     */
    public static Bitmap getRoundedBitmapWithBorder(Bitmap bitmap, float roundPx, int boderPx,
                                                    int boderColor) {
        Bitmap tmpBitmap = getRoundedBitmap(bitmap, roundPx);
        if (tmpBitmap == null) {
            return null;
        }

        Bitmap output = KGBitmapUtil.createBitmap(tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint borderpaint = new Paint();
        final Rect borderrect = new Rect(0, 0, tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2);
        final RectF borderrectF = new RectF(borderrect);
        borderpaint.setAntiAlias(true);
        borderpaint.setColor(boderColor);
        // drawOval方法用于绘制一个包含borderrectF的圆
        canvas.drawOval(borderrectF, borderpaint);

        final Paint paint = new Paint();
        final Rect src = new Rect(0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight());
        final Rect dst = new Rect(boderPx, boderPx, tmpBitmap.getWidth() + boderPx,
                tmpBitmap.getHeight() + boderPx);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        // src参数是图片原来的大小，dst参数是 绘画该图片需显示多大
        canvas.drawBitmap(tmpBitmap, src, dst, paint);
        return output;
    }

    public static Bitmap getRoundedBitmapWithBorder2(Bitmap bitmap, float roundPx, int boderPx,
                                                    int boderColor) {
        Bitmap tmpBitmap = bitmap;
        if (tmpBitmap == null) {
            return null;
        }

        Bitmap output = KGBitmapUtil.createBitmap(tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint borderpaint = new Paint();
        final Rect borderrect = new Rect(0, 0, tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2);
        final RectF borderrectF = new RectF(borderrect);
        borderpaint.setAntiAlias(true);
        borderpaint.setColor(boderColor);
        // drawOval方法用于绘制一个包含borderrectF的圆
        canvas.drawRect(borderrectF, borderpaint);

        final Paint paint = new Paint();
        final Rect src = new Rect(0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight());
        final Rect dst = new Rect(boderPx, boderPx, tmpBitmap.getWidth() + boderPx,
                tmpBitmap.getHeight() + boderPx);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        // src参数是图片原来的大小，dst参数是 绘画该图片需显示多大
        canvas.drawBitmap(tmpBitmap, src, dst, paint);
        return output;
    }

    /**
     * 获取带边框的图片
     *
     * @param src 源图片
     * @param border 边框大小
     * @param borderColor 边框颜色
     * @return
     */
    // public static Bitmap getBorderBitmap(Bitmap src, int border, int
    // borderColor) {
    // if (src == null || border < 1) {
    // return src;
    // }
    // Bitmap outBitmap = Bitmap.createBitmap(src.getWidth() + border * 2,
    // src.getHeight()
    // + border * 2, Config.ARGB_8888);
    // Canvas canvas = new Canvas(outBitmap);
    // final Rect srcRect = new Rect(0, 0, src.getWidth(), src.getHeight());
    // final Rect dstRect = new Rect(border, border, outBitmap.getWidth() -
    // border,
    // outBitmap.getHeight() - border);
    // final Paint paint = new Paint();
    // paint.setColor(border);
    // canvas.drawBitmap(src, srcRect, dstRect, null);
    // return outBitmap;
    // }

    /**
     * 获取 首页bar的图片
     *
     * @param border 边框大小
     * @param borderColor 边框颜色
     * @param srcHeight 图片内容高度
     * @param width 图片宽
     * @param height 图片高
     * @param paddingTop 上边距
     * @return
     */
    // public static Bitmap getRandomAlbumBitmap(int border, int borderColor,
    // int srcHeight,
    // int width, int height, int paddingTop) {
    // Bitmap[] src = new Bitmap[3];
    // if (border < 1) {
    // return null;
    // }
    // File file = new DelFile(GlobalEnv.SINGER_FOLDER);
    // File[] files = file.listFiles();
    // if (files == null || files.length <= 0) {
    // return null;
    // }
    // TreeSet<Integer> set = getRandomIndexs(0, files.length - 1, src.length);
    // if (set.size() == 0) {
    // return null;
    // } else {
    // Iterator<Integer> iterator = set.iterator();
    // int i = -1;
    // while (iterator.hasNext()) {
    // i++;
    // src[i] = zoomBitmap(
    // BitmapFactory.decodeFile(files[iterator.next()].getAbsolutePath()),
    // srcHeight, srcHeight);
    // }
    // }
    // if (src[0] == null) {
    // return null;
    // }
    // if (src[1] == null) {
    // src[1] = src[0];
    // }
    // if (src[2] == null) {
    // src[2] = src[1];
    // }
    // int srcWidth = srcHeight;
    // int src23Width = (width - 4 * border - srcWidth) / 2;
    // Bitmap outBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    // Canvas canvas = new Canvas(outBitmap);
    // final Paint paint = new Paint();
    // paint.setColor(borderColor);
    // canvas.drawRect(new Rect(0, paddingTop, width, height), paint);
    // final Rect src1Rect = new Rect(0, 0, srcWidth, srcHeight);
    // final Rect dst1Rect = new Rect(border, paddingTop + border, border +
    // srcWidth, paddingTop
    // + border + srcHeight);
    // final Rect src2Rect = new Rect(srcWidth / 2, 0, srcWidth / 2 +
    // src23Width, srcHeight);
    // final Rect dst2Rect = new Rect(border * 2 + srcWidth, paddingTop +
    // border, border * 2
    // + srcWidth + src23Width, paddingTop + border + srcHeight);
    // final Rect src3Rect = new Rect(srcWidth / 2, 0, srcWidth / 2 +
    // src23Width, srcHeight);
    // final Rect dst3Rect = new Rect(border * 3 + srcWidth + src23Width,
    // paddingTop + border,
    // border * 3 + srcWidth + src23Width * 2, paddingTop + border + srcHeight);
    // canvas.drawBitmap(src[0], src1Rect, dst1Rect, null);
    // canvas.drawBitmap(src[1], src2Rect, dst2Rect, null);
    // canvas.drawBitmap(src[2], src3Rect, dst3Rect, null);
    // src[0].recycle();
    // src[1].recycle();
    // src[2].recycle();
    // return outBitmap;
    // }

    // public static Bitmap getAlbumBitmap(Bitmap[] src, int border, int
    // borderColor, int srcHeight,
    // int width, int height, int paddingTop) {
    // if (src == null || src.length <= 0 || src[0] == null) {
    // return null;
    // }
    // int length = src.length;
    // int srcWidth = srcHeight;
    // if (length == 1) {
    // int realWidth = 2 * border + srcWidth;
    // Bitmap outBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    // Canvas canvas = new Canvas(outBitmap);
    // final Paint paint = new Paint();
    // paint.setColor(borderColor);
    // canvas.drawRect(new Rect(0, paddingTop, realWidth, height), paint);
    // final Rect src1Rect = new Rect(0, 0, srcWidth, srcHeight);
    // final Rect dst1Rect = new Rect(border, paddingTop + border, border +
    // srcWidth,
    // paddingTop + border + srcHeight);
    // canvas.drawBitmap(zoomBitmap(src[0], srcWidth, srcHeight), src1Rect,
    // dst1Rect, null);
    // src[0].recycle();
    // return outBitmap;
    //
    // } else if (length == 2) {
    // int src23Width = (width - 4 * border - srcWidth) / 2;
    // int realWidth = 3 * border + srcWidth + src23Width;
    // Bitmap outBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    // Canvas canvas = new Canvas(outBitmap);
    // final Paint paint = new Paint();
    // paint.setColor(borderColor);
    // canvas.drawRect(new Rect(0, paddingTop, realWidth, height), paint);
    // final Rect src1Rect = new Rect(0, 0, srcWidth, srcHeight);
    // final Rect dst1Rect = new Rect(border, paddingTop + border, border +
    // srcWidth,
    // paddingTop + border + srcHeight);
    // final Rect src2Rect = new Rect(srcWidth / 2, 0, srcWidth / 2 +
    // src23Width, srcHeight);
    // final Rect dst2Rect = new Rect(border * 2 + srcWidth, paddingTop +
    // border, border * 2
    // + srcWidth + src23Width, paddingTop + border + srcHeight);
    // canvas.drawBitmap(zoomBitmap(src[0], srcWidth, srcHeight), src1Rect,
    // dst1Rect, null);
    // canvas.drawBitmap(zoomBitmap(src[1], srcWidth, srcHeight), src2Rect,
    // dst2Rect, null);
    // src[0].recycle();
    // src[1].recycle();
    // return outBitmap;
    // } else if (length == 3) {
    // int src23Width = (width - 4 * border - srcWidth) / 2;
    // Bitmap outBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    // Canvas canvas = new Canvas(outBitmap);
    // final Paint paint = new Paint();
    // paint.setColor(borderColor);
    // canvas.drawRect(new Rect(0, paddingTop, width, height), paint);
    // final Rect src1Rect = new Rect(0, 0, srcWidth, srcHeight);
    // final Rect dst1Rect = new Rect(border, paddingTop + border, border +
    // srcWidth,
    // paddingTop + border + srcHeight);
    // final Rect src2Rect = new Rect(srcWidth / 2, 0, srcWidth / 2 +
    // src23Width, srcHeight);
    // final Rect dst2Rect = new Rect(border * 2 + srcWidth, paddingTop +
    // border, border * 2
    // + srcWidth + src23Width, paddingTop + border + srcHeight);
    // final Rect src3Rect = new Rect(srcWidth / 2, 0, srcWidth / 2 +
    // src23Width, srcHeight);
    // final Rect dst3Rect = new Rect(border * 3 + srcWidth + src23Width,
    // paddingTop + border,
    // border * 3 + srcWidth + src23Width * 2, paddingTop + border + srcHeight);
    // canvas.drawBitmap(zoomBitmap(src[0], srcWidth, srcHeight), src1Rect,
    // dst1Rect, null);
    // canvas.drawBitmap(zoomBitmap(src[1], srcWidth, srcHeight), src2Rect,
    // dst2Rect, null);
    // canvas.drawBitmap(zoomBitmap(src[2], srcWidth, srcHeight), src3Rect,
    // dst3Rect, null);
    // src[0].recycle();
    // src[1].recycle();
    // src[2].recycle();
    // return outBitmap;
    // }
    // return null;
    //
    // }

    /**
     * 获得带倒影的图片方法
     *
     * @param bitmap 源图片
     * @return 带倒影图片
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = KGBitmapUtil.createBitmap(bitmap, 0, height / 2, width,
                height / 2, matrix, false);
        Bitmap bitmapWithReflection = KGBitmapUtil.createBitmap(width, (height + height / 2),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 保存bitmap,会自动创建文件
     *
     * @param bitmap   源图片
     * @param savePath 保存路径
     * @param format   图片格式
     */
    public static boolean saveBitmapWithCreateFile(Bitmap bitmap, String savePath,
                                                   CompressFormat format) {
        if (bitmap == null || TextUtils.isEmpty(savePath)) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            File file = new DelFile(savePath);
            if (!file.exists()) {
                String parentPath = file.getParent();
                File parent = new DelFile(parentPath);
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(format, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 图片压缩--判断图片尺寸是否小于指定尺寸,如果大于则进行压缩，否则不压缩
     *
     * @param path     图片路径
     * @param savePath 保存路径
     * @param format   格式
     * @return
     */
    public static String encodeImage(String path, String savePath, CompressFormat format) {
        try {
            // 压缩图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bgimage = BitmapFactory.decodeFile(path, options);
            int inSampleSize = 1;
            if (options.outHeight > options.outWidth) {
                inSampleSize = Math.round(options.outHeight / (float) 1024);
            } else {
                inSampleSize = Math.round(options.outWidth / (float) 1024);
            }
            inSampleSize = inSampleSize == 0 ? 1 : inSampleSize;
            if (options.outWidth / inSampleSize > 1024 || options.outHeight / inSampleSize > 1024) {
                inSampleSize = inSampleSize + 1;
            }
            if (inSampleSize > 1) {
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;
                bgimage = BitmapFactory.decodeFile(path, options);
                if (bgimage == null) {
                    return "";
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 得到输出流
                bgimage.compress(format, 100, baos);
                // 转输入流
                InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                bgimage.recycle();
                bgimage = null;
                File sendFilePath = new DelFile(savePath);
                ImageUtil.writeToFile(sendFilePath, isBm);
            } else {
                if (bgimage != null && !bgimage.isRecycled()) {
                    bgimage.recycle();
                    bgimage = null;
                }
            }
            return savePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    /**
     * 写入图片
     *
     * @param saveFile
     * @param in
     * @return
     */
    public static boolean writeToFile(File saveFile, InputStream in) {
        FileOutputStream fout = null;
        boolean success = true;
        try {
            fout = new FileOutputStream(saveFile);
            int len = -1;
            byte[] buff = new byte[4096];
            for (; (len = in.read(buff)) != -1; ) {
                fout.write(buff, 0, len);
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 读取文件，如果图片出错，返回null
     *
     * @param pathName
     * @return
     */
    public static Bitmap readDialogBgBitmap(String pathName) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(pathName)) {
            return null;
        } else {
            if (FileUtil.isExist(pathName)) {
                InputStream is = null;
                try {
                    try {
                        is = FileUtil.getFileInputStream(pathName);
                        bitmap = KGBitmapUtil.decodeStream(is);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                } catch (OutOfMemoryError e) {
                    System.gc();
                    try {
                        try {
                            is = FileUtil.getFileInputStream(pathName);
                            bitmap = KGBitmapUtil.decodeStream(is);
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (OutOfMemoryError e1) {
                    } catch (Exception e3) {
                    }
                } catch (Exception e2) {
                }
            }
        }
        return bitmap;
    }

    public static Bitmap readNetworkBitmap(final String imageUrl) {
        return readNetworkBitmap(imageUrl, LoadingTimeoutUtil.IGNORE_TIME);
    }

    public static Bitmap readNetworkBitmap(final String imageUrl, int waitTime) {
        if (imageUrl == null || imageUrl.length() == 0) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getImageStream(imageUrl, waitTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static InputStream getImageStream(String path, int waitTime) throws Exception {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        if (LoadingTimeoutUtil.IGNORE_TIME != waitTime) {
            conn.setReadTimeout(waitTime * 1000);
            if (DrLog.DEBUG) {
                DrLog.i("zzm-loading", "getImage  set readTimeout");
            }
        }
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    private final static float BOUND(float x, float mn, float mx) {

        return ((x) < (mn) ? (mn) : ((x) > (mx) ? (mx) : (x)));
    }

    // local function used in HLStoRGB
    private final static float Value(float n1, float n2, float hue) {

        if (hue > 360.0) {
            hue -= 360.0;
        } else if (hue < 0.0) {
            hue += 360.0;
        }
        if (hue < 60.0) {
            return (n1 + (n2 - n1) * hue / 60.0f);
        } else if (hue < 180.0) {
            return n2;
        } else if (hue < 240.0) {
            return (n1 + (n2 - n1) * (240.0f - hue) / 60.0f);
        } else {
            return n1;
        }
    }

    public static int matchColorByImage(Bitmap bm) {
        return matchColorByImage(bm, DefaultColor);
    }

    /**
     * 根据图片，自动算出匹配的颜色值
     *
     * @param bm 图片对象
     * @return 颜色值
     */
    public static int matchColorByImage(Bitmap bm, @ColorLib int colorLib) {
        if (bm == null) {
            return Color.WHITE;
        }
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] RGB;
        if (w > 100 && h > 100) {
            Bitmap temp = zoomBitmap(bm, w / 8, h / 8);
            w = temp.getWidth();
            h = temp.getHeight();
            RGB = new int[w * h];
            temp.getPixels(RGB, 0, w, 0, 0, w, h);
            temp.recycle();
        } else {
            RGB = new int[w * h];
            bm.getPixels(RGB, 0, w, 0, 0, w, h);
        }
        int color = matchColorByImage(RGB, colorLib);
        RGB = null;
        return color;
    }

    public static final int DefaultColor = 0;
    public static final int PlayerColor = 1;
    public static final int SkinColor = 2;
    public static final int MiniApp = 3;

    @IntDef({DefaultColor, PlayerColor, SkinColor, MiniApp})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ColorLib {
    }

//    public enum ColorLib {DefaultColor, PlayerColor, SkinColor}

    private final static int[] playerColorArr = {
            0xfff596aa,
            0xffdb4d6d,
            0xffeb7a77,
            0xffcb4042,
            0xfff17c67,
            0xffed784a,
            0xfffc9f4d,
            0xffebb471,
            0xfff9bf45,
            0xffbec23f,
            0xff90b44b,
            0xff86c166,
            0xff91b493,
            0xff5dac81,
            0xff5dafac,
            0xff6699a1,
            0xff5ab6c6,
            0xff58b2dc,
            0xff72a8ca,
            0xff2ea9df,
            0xff7b90d2,
            0xff8b81c3,
            0xff9b90c2,
            0xffb28fce,
            0xffb67db6,
            0xffc3568e,
            0xffe03c8a,
            0xffa0a19e,
            0xff91989f,
            0xffa5857e,
    };

    private final static int[] playerBgColor = {
            0xff331f23,
            0xff33121a,
            0xff331b1a,
//            0xff331011,
            0xff526990,
            0xff331a16,
            0xff331a10,
            0xff332010,
            0xff332718,
            0xff33270e,
            0xff323310,
            0xff293315,
            0xff23331b,
            0xff29332a,
            0xff1c3326,
            0xff1b3332,
            0xff203033,
            0xff172f33,
            0xff142933,
            0xff1d2a33,
            0xff0b2733,
            0xff1e2333,
            0xff242233,
            0xff292633,
            0xff2c2333,
            0xff332333,
            0xff331625,
            0xff330e20,
            0xff333332,
            0xff2e3133,
            0xff332927,
    };

    public final static int sDEFAULT_BG_COLOR = 0x00000000;

    public static int getBgColor(int color) {
        for (int i = 0, size = playerColorArr.length; i < size; i++) {
            if (color == playerColorArr[i]) {
                if (DrLog.DEBUG) {
                    DrLog.d("zlx_dev8",
                            "bg color index: " + i + " playerColorArr=" + playerColorArr[i] +
                                    " playerBgColor=" + playerBgColor[i]);
                }
                return playerBgColor[i];
            }
        }
        return sDEFAULT_BG_COLOR;
    }

    private final static int[] colorArr = {
            0xFFFFB6C1, // LightPink 浅粉红
            0xFFFFC0CB, // Pink 粉红
            0xFFDC143C, // Crimson 深红/猩红
            0xFFFFF0F5, // LavenderBlush 淡紫红
            0xFFDB7093, // PaleVioletRed 弱紫罗兰红
            0xFFFF69B4, // HotPink 热情的粉红
            0xFFFF1493,// DeepPink 深粉红
            0xFFC71585,// MediumVioletRed 中紫罗兰红
            0xFFDA70D6,// Orchid 暗紫色/兰花紫
            0xFFDDA0DD,// Plum 洋李色/李子紫
            0xFFEE82EE,// Violet 紫罗兰
            0xFFFF00FF, // Magenta 洋红/玫瑰红
            0xFFFF00FF, // Fuchsia 紫红/灯笼海棠
            0xFF8B008B, // DarkMagenta 深洋红
            0xFF800080, // Purple 紫色
            0xFFBA55D3, // MediumOrchid 中兰花紫
            0xFF9400D3, // DarkViolet 暗紫罗兰
            0xFF9932CC,// DarkOrchid 暗兰花紫
            0xFF4B0082, // Indigo 靛青/紫兰色
            0xFF8A2BE2, // BlueViolet 蓝紫罗兰
            0xFF9370DB, // MediumPurple 中紫色
            0xFF7B68EE, // MediumSlateBlue 中暗蓝色/中板岩蓝
            0xFF6A5ACD, // SlateBlue 石蓝色/板岩蓝
            0xFF483D8B, // DarkSlateBlue 暗灰蓝色/暗板岩蓝
            0xFF0000FF, // 纯蓝
            0xFF0000CD, // MediumBlue 中蓝色
            0xFF191970, // MidnightBlue 午夜蓝
            0xFF00008B, // DarkBlue 暗蓝色
            0xFF000080, // Navy 海军蓝
            0xFF4169E1, // RoyalBlue 皇家蓝/宝蓝
            0xFF6495ED, // CornflowerBlue 矢车菊蓝
            0xFF778899, // LightSlateGray 亮蓝灰/亮石板灰
            0xFF708090, // SlateGray 灰石色/石板灰
            0xFF1E90FF, // DodgerBlue 闪兰色/道奇蓝
            0xFFF0F8FF, // AliceBlue 爱丽丝蓝
            0xFF4682B4, // SteelBlue 钢蓝/铁青
            0xFF87CEFA, // LightSkyBlue 亮天蓝色
            0xFF87CEEB, // SkyBlue 天蓝色
            0xFF00BFFF, // DeepSkyBlue 深天蓝
            0xFFADD8E6, // LightBlue 亮蓝
            0xFFB0E0E6, // PowderBlue 粉蓝色/火药青
            0xFF5F9EA0, // CadetBlue 军兰色/军服蓝
            0xFFAFEEEE, // PaleTurquoise 弱绿宝石
            0xFF00FFFF, // Cyan 青色
            0xFF00FFFF, // Aqua 浅绿色/水色
            0xFF00CED1, // DarkTurquoise 暗绿宝石
            0xFF2F4F4F, // DarkSlateGray 暗瓦灰色/暗石板灰
            0xFF008B8B, // DarkCyan 暗青色
            0xFF008080, // Teal 水鸭色
            0xFF48D1CC,// MediumTurquoise 中绿宝石
            0xFF20B2AA,// LightSeaGreen 浅海洋绿
            0xFF40E0D0, // Turquoise 绿宝石
            0xFF7FFFD4, // Aquamarine 宝石碧绿
            0xFF66CDAA,// MediumAquamarine 中宝石碧绿
            0xFF00FA9A,// MediumSpringGreen 中春绿色
            0xFFF5FFFA,// MintCream 薄荷奶油
            0xFF00FF7F, // SpringGreen 春绿色
            0xFF3CB371,// MediumSeaGreen 中海洋绿
            0xFF2E8B57,// SeaGreen 海洋绿
            0xFFF0FFF0,// Honeydew 蜜色/蜜瓜色
            0xFF90EE90,// LightGreen 淡绿色
            0xFF98FB98,// PaleGreen 弱绿色
            0xFF8FBC8F, // DarkSeaGreen 暗海洋绿
            0xFF32CD32, // LimeGreen 闪光深绿
            0xFF00FF00, // Lime 闪光绿
            0xFF228B22, // ForestGreen 森林绿
            0xFF008000, // Green 纯绿
            0xFF006400, // DarkGreen 暗绿色
            0xFF7FFF00, // Chartreuse 黄绿色/查特酒绿
            0xFF7CFC00, // LawnGreen 草绿色/草坪绿
            0xFFADFF2F,// GreenYellow 绿黄色
            0xFF556B2F,// DarkOliveGreen 暗橄榄绿
            0xFF9ACD32,// YellowGreen 黄绿色
            0xFF6B8E23, // OliveDrab 橄榄褐色
            0xFFF5F5DC, // Beige 米色/灰棕色
            0xFFFAFAD2, // LightGoldenrodYellow 亮菊黄
            0xFFFFFFF0, // Ivory 象牙色
            0xFFFFFFE0,// LightYellow 浅黄色
            0xFFFFFF00,// Yellow 纯黄
            0xFF808000, // Olive 橄榄
            0xFFBDB76B, // DarkKhaki 暗黄褐色/深卡叽布
            0xFFEEE8AA, // PaleGoldenrod 灰菊黄/苍麒麟色
            0xFFF0E68C, // Khaki 黄褐色/卡叽布
            0xFFFFD700, // Gold 金色
            0xFFFFF8DC, // Cornsilk 玉米丝色
            0xFFDAA520,// Goldenrod 金菊黄
            0xFFB8860B, // DarkGoldenrod 暗金菊黄
            0xFFFFE4B5,// Moccasin 鹿皮色/鹿皮靴
            0xFFFFA500, // Orange 橙色
            0xFFFFEFD5, // PapayaWhip 番木色/番木瓜
            0xFFD2B48C, // Tan 茶色
            0xFFDEB887, // BurlyWood 硬木色
            0xFFFFE4C4,// Bisque 陶坯黄
            0xFFFF8C00,// DarkOrange 深橙色
            0xFFFAF0E6,// Linen 亚麻布
            0xFFCD853F, // Peru 秘鲁色
            0xFFFFDAB9, // PeachPuff 桃肉色
            0xFFF4A460, // SandyBrown 沙棕色
            0xFFD2691E, // Chocolate 巧克力色
            0xFF8B4513, // SaddleBrown 重褐色/马鞍棕色
            0xFFFFF5EE, // Seashell 海贝壳
            0xFFA0522D, // Sienna 黄土赭色
            0xFFFFA07A, // LightSalmon 浅鲑鱼肉色
            0xFFFF7F50, // Coral 珊瑚
            0xFFFF4500, // OrangeRed 橙红色
            0xFFE9967A,// DarkSalmon 深鲜肉/鲑鱼色
            0xFFFF6347, // Tomato 番茄红
            0xFFFFE4E1, // MistyRose 浅玫瑰色/薄雾玫瑰
            0xFFFA8072, // Salmon 鲜肉/鲑鱼色
            0xFFFFFAFA, // Snow 雪白色
            0xFFF08080, // LightCoral 淡珊瑚色
            0xFFBC8F8F, // RosyBrown 玫瑰棕色
            0xFFCD5C5C, // IndianRed 印度红
            0xFFFF0000, // Red 纯红
            0xFFA52A2A, // Brown 棕色
            0xFFB22222, // FireBrick 火砖色/耐火砖
            0xFF8B0000, // DarkRed 深红色
            0xFF800000, // Maroon 栗色
            0xFFC0C0C0, // Silver 银灰色
            0xFF696969, // DimGray 暗淡灰
            0xFF000000
            // Black 纯黑
    };

    public static int matchNearColor(int color, @ColorLib int colorLib) {
        // 默认值是原值
        int nearColor = color;

        int[] rgb = new int[3];
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);

        // 差距值
        double curDis = 255 * 3;
        int[] rgbItem = new int[3];

        int[] colors;
        switch (colorLib) {
            case PlayerColor:
                colors = playerColorArr;
                break;

            case SkinColor:
                colors = SkinColorLib.colors;
                break;

            case MiniApp:
                colors = SkinColorLib.miniappColors;
                break;

            case DefaultColor:
            default:
                colors = playerColorArr;
                break;
        }

        for (int i = 0; i < colors.length; i++) {
            rgbItem[0] = Color.red(colors[i]);
            rgbItem[1] = Color.green(colors[i]);
            rgbItem[2] = Color.blue(colors[i]);
            int abs1 = rgb[0] - rgbItem[0];
            int abs2 = rgb[1] - rgbItem[1];
            int abs3 = rgb[2] - rgbItem[2];
            double dis = StrictMath
                    .sqrt((Math.pow(abs1, 2) + Math.pow(abs2, 2) + Math.pow(abs3, 2)) / 3);
            if (dis < curDis) {
                curDis = dis;
                nearColor = colors[i];
            }
        }

        return nearColor;
    }

    public static int matchSkinColorIndexByImage(Bitmap bm) {

        if (bm == null) {
            return Color.WHITE;
        }
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] RGB;
        if (w > 100 && h > 100) {
            Bitmap temp = zoomBitmap(bm, w / 8, h / 8);
            w = temp.getWidth();
            h = temp.getHeight();
            RGB = new int[w * h];
            temp.getPixels(RGB, 0, w, 0, 0, w, h);
            temp.recycle();
        } else {
            RGB = new int[w * h];
            bm.getPixels(RGB, 0, w, 0, 0, w, h);
        }
        int index = matchSkinColorIndexByImage(RGB);
        RGB = null;
        return index;
    }

    private static int matchSkinColorIndexByImage(int[] RGB) {

        int[] rgb = new int[3];

        int ignore = 0;
        int MaxPosNumber = 10000;// 最多取一万个点求平均值，
        ignore = RGB.length / MaxPosNumber;
        if (ignore == 0) {
            ignore = 1;
        }
        int totalr = 0;
        int totalg = 0;
        int totalb = 0;
        int PosNumber = 0;
        int len = RGB.length - 1;
        for (int i = len; i >= 0; i -= ignore) {

            rgb[0] = (RGB[i] & 0x00ff0000) >> 16;
            rgb[1] = (RGB[i] & 0x0000ff00) >> 8;
            rgb[2] = (RGB[i] & 0x000000ff);

            // //一半的几率，剔除黑白色系颜色。start
            // if(PosNumber%2 == 0 && rgb[0] == rgb[1] && rgb[1] == rgb[2]){
            // PosNumber++;
            // continue;
            // }
            // end
            totalr += rgb[0];
            totalg += rgb[1];
            totalb += rgb[2];

            PosNumber++;
        }

        if (PosNumber > 0) {
            rgb[0] = totalr / PosNumber;
            rgb[1] = totalg / PosNumber;
            rgb[2] = totalb / PosNumber;
        }

        int returnColor = Color.rgb(rgb[0], rgb[1], rgb[2]);
        // 从给定的颜色数组里匹配一个最接近的颜色值
        int index = matchNearSkinColorIndex(returnColor);
        // //增加亮度值
        // return ChangeColorLightByPercent(returnColor, 20);

        return index;
    }

    public static int matchNearSkinColorIndex(int color) {
        int index = -1;

        Resources res = DRCommonApplication.getContext().getResources();

        int[] rgb = new int[3];
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);

        // 差距值
        double curDis = 255 * 3;
        int[] rgbItem = new int[3];
        for (int i = 0; i < SkinSetting.sThemeColor.length; i++) {

            int temColor = res.getColor(SkinSetting.sThemeColor[i]);

            rgbItem[0] = Color.red(temColor);
            rgbItem[1] = Color.green(temColor);
            rgbItem[2] = Color.blue(temColor);
            int abs1 = rgb[0] - rgbItem[0];
            int abs2 = rgb[1] - rgbItem[1];
            int abs3 = rgb[2] - rgbItem[2];
            double dis = StrictMath
                    .sqrt((Math.pow(abs1, 2) + Math.pow(abs2, 2) + Math.pow(abs3, 2)) / 3);
            if (dis < curDis) {
                curDis = dis;
                index = i;
            }
        }

        return index;
    }

    /**
     * 根据图片，自动算出匹配的颜色值r,g,b
     *
     * @param RGB 图片的像素数组,int里的每4个字节代表rgba中的一个值
     * @return 颜色值
     */
    private static int matchColorByImage(int[] RGB, @ColorLib int colorLib) {

        int[] rgb = new int[3];

        int ignore = 0;
        int MaxPosNumber = 10000;// 最多取一万个点求平均值，
        ignore = RGB.length / MaxPosNumber;
        if (ignore == 0) {
            ignore = 1;
        }
        int totalr = 0;
        int totalg = 0;
        int totalb = 0;
        int PosNumber = 0;
        int len = RGB.length - 1;
        for (int i = len; i >= 0; i -= ignore) {

            rgb[0] = (RGB[i] & 0x00ff0000) >> 16;
            rgb[1] = (RGB[i] & 0x0000ff00) >> 8;
            rgb[2] = (RGB[i] & 0x000000ff);

            // //一半的几率，剔除黑白色系颜色。start
            // if(PosNumber%2 == 0 && rgb[0] == rgb[1] && rgb[1] == rgb[2]){
            // PosNumber++;
            // continue;
            // }
            // end
            totalr += rgb[0];
            totalg += rgb[1];
            totalb += rgb[2];

            PosNumber++;
        }

        if (PosNumber > 0) {
            rgb[0] = totalr / PosNumber;
            rgb[1] = totalg / PosNumber;
            rgb[2] = totalb / PosNumber;
        }

        int returnColor = Color.rgb(rgb[0], rgb[1], rgb[2]);
        // 从给定的颜色数组里匹配一个最接近的颜色值
        returnColor = matchNearColor(returnColor, colorLib);
        // //增加亮度值
        // return ChangeColorLightByPercent(returnColor, 20);

        return returnColor;
    }

    /**
     * 这个是对一个颜色算出灰阶
     *
     * @param r
     * @param g
     * @param b
     * @return 灰度
     */
    private static int RGB2Gray(int r, int g, int b) {
        int gray = 0;
        gray = (((b) * 117 + (g) * 601 + (r) * 306) >> 10);

        return gray;
    }

    /**
     * 判断是否使用亮色的文字
     *
     * @param
     * @return true, 使用亮色文字;flase,使用暗色文字.
     */
    public static boolean IsUseLightFont(int color) {

        boolean useLightFont = false;
        int gray = RGB2Gray(Color.red(color), Color.green(color), Color.blue(color));

        if (gray <= 150) {
            useLightFont = true;
        }

        return useLightFont;
    }

    /**
     * 计算出新图的亮度偏移值
     *
     * @return
     */
    private static float CalPowl(float TargetL) {
        double powl = 0;
        if (TargetL - 50.0f > 0) {
            powl = Math.pow(TargetL - 50.0f, 0.88f);
        } else {
            powl = -Math.pow(50.0f - TargetL, 0.88f);
        }
        return (float) powl;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param Source 目标颜色值 rgb,rgb各占一个int空间
     * @return
     */
    private static int[] CalMergeColor(int[] Source, float Powl, float SelectH, float SelectS,
                                       float SelectL) {
        int[] DColor = new int[3];

        float hls[] = new float[3];
        hls = RGBtoHLS(Source[0], Source[1], Source[2]);
        // float h = hls[0];
        float l = hls[1];
        float s = hls[2];

        float DL, DS, DH;
        if (l >= 100.0f || l <= 0.0f) {
            DColor = Source;
            return DColor;
        }

        DL = l;
        DS = s;
        DH = SelectH;

        DColor = HLStoRGB(DH, DL, DS);

        return DColor;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param Source 颜色值
     * @param L      要改变的亮度值增量 （0.0f - 1。0f）
     * @return 改变后的颜色值
     */
    public static int ChangeColorLight(int Source, float L) {
        int[] rgb = new int[3];
        rgb[0] = Color.red(Source);
        rgb[1] = Color.green(Source);
        rgb[2] = Color.blue(Source);
        int alpha = Color.alpha(Source);
        float hls[] = new float[3];

        hls = RGBtoHLS(rgb[0], rgb[1], rgb[2]);
        float h = hls[0];
        float l = hls[1];
        float s = hls[2];
        l += L;
        if (l > 1.0f) {
            l = 1.0f;
        } else if (l < 0.0f) {
            l = 0.0f;
        }

        rgb = HLStoRGB(h, l, s);
        int DColor = Color.argb(alpha, rgb[0], rgb[1], rgb[2]);
        return DColor;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param Source 颜色值
     * @param L   0~100 百分比
     * @return 改变后的颜色值
     */
    public static int ChangeColorLightByPercent(int Source, int L) {
        int[] rgb = new int[3];
        rgb[0] = Color.red(Source);
        rgb[1] = Color.green(Source);
        rgb[2] = Color.blue(Source);
        int alpha = Color.alpha(Source);
        float hls[] = new float[3];

        hls = RGBtoHLS(rgb[0], rgb[1], rgb[2]);
        float h = hls[0];
        float l = hls[1];
        float s = hls[2];
        l += l * L / 100f;
        if (l > 1.0f) {
            l = 1.0f;
        } else if (l < 0.0f) {
            l = 0.0f;
        }

        rgb = HLStoRGB(h, l, s);
        int DColor = Color.argb(alpha, rgb[0], rgb[1], rgb[2]);
        return DColor;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param bitmap    图片
     * @param targetColor 目标颜色值
     * @return
     */
    //
    public static Bitmap changeImageColorWithAlpha(Bitmap bitmap, int targetColor, float alpha) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        int[] oldPx = new int[width * height];//用来存储旧的色素点的数组
        int[] newPx = new int[width * height];//用来存储新的像素点的数组
        int color;//用来存储原来颜色值
        int r, g, b, a;//存储颜色的四个分量：红，绿，蓝，透明度

        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = y * width + x;
                color = oldPx[i];

                a = (int) (Color.alpha(color) * alpha);
                r = Color.red(targetColor);
                g = Color.green(targetColor);
                b = Color.blue(targetColor);

                newPx[i] = Color.argb(a, r, g, b);
            }
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param RGB 图片的像素数组,int里的每4个字节代表rgba中的一个值
     * @param targetrgb 目标颜色值 rgb,rgb各占一个int空间
     * @return
     */
    //
    private static int[] changeImageColor(int[] RGB, int[] targetrgb) {

        float h = 0.0f;
        float l = 0.0f;
        float s = 0.0f;
        float hls[] = new float[3];
        hls = RGBtoHLS(targetrgb[0], targetrgb[1], targetrgb[2]);
        h = hls[0];
        l = hls[1];
        s = hls[2];
        float powl = CalPowl(l);
        int[] rgb = new int[3];
        int oldRgbSrc = 999;
        int oldRgbDes = 999;
        int len = RGB.length - 1;
        for (int i = len; i >= 0; i--) {
            if (oldRgbSrc == RGB[i] && oldRgbDes != 999) {
                RGB[i] = oldRgbDes;
            } else {
                rgb[0] = (RGB[i] & 0x00ff0000) >> 16;
                rgb[1] = (RGB[i] & 0x0000ff00) >> 8;
                rgb[2] = (RGB[i] & 0x000000ff);

                oldRgbSrc = RGB[i];

                rgb = CalMergeColor(rgb, powl, h, l, s);

                RGB[i] = (RGB[i] & 0xff000000) | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8)
                        | (rgb[2] & 0xff);
                oldRgbDes = RGB[i];
            }
        }
        return RGB;
    }

    /**
     * @param h
     * @param l
     * @param s
     * @return
     */
    private static int[] HLStoRGB(float h, float l, float s) {

        int[] rgb = new int[3];
        float m1, m2;
        float R, G, B;
        if (l <= 0.5) {
            m2 = l * (1.0f + s);
        } else {
            m2 = l + s - l * s;
        }
        m1 = 2.0f * l - m2;
        R = Value(m1, m2, h + 120.0f);
        G = Value(m1, m2, h);
        B = Value(m1, m2, h - 120.0f);
        int iR = (int) (R * 255.0);
        int iG = (int) (G * 255.0);
        int iB = (int) (B * 255.0);
        rgb[0] = (int) BOUND(iR, 0, 255);
        rgb[1] = (int) BOUND(iG, 0, 255);
        rgb[2] = (int) BOUND(iB, 0, 255);
        return rgb;
    }

    /**
     * @param r
     * @param g
     * @param b
     * @return
     */
    private static float[] RGBtoHLS(int r, int g, int b) {

        float[] hls = new float[3];
        float mx, mn, delta;
        float R, G, B;
        R = (float) (r / 255.0);
        G = (float) (g / 255.0);
        B = (float) (b / 255.0);
        mx = Math.max(R, Math.max(G, B));
        mn = Math.min(R, Math.min(G, B));
        hls[1] = (mx + mn) / 2.0f;
        if (mx == mn) {
            hls[2] = 0.0f;
            hls[0] = 0.0f; // undefined!
        } else {
            delta = mx - mn;
            if (hls[1] < 0.5) {
                hls[2] = delta / (mx + mn);
            } else {
                hls[2] = delta / (2.0f - mx - mn);
            }
            if (R == mx) {
                hls[0] = (G - B) / delta;
            } else if (G == mx) {
                hls[0] = 2.0f + (B - R) / delta;
            } else if (B == mx) {
                hls[0] = 4.0f + (R - G) / delta;
            }
            hls[0] *= 60.0;
            if (hls[0] < 0.0) {
                hls[0] += 360.0;
            } else if (hls[0] > 360.0) {
                hls[0] -= 360.0;
            }
        }
        return hls;
    }

    /**
     * 裁剪图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap newBitmap = KGBitmapUtil.createBitmap(bitmap, 0, 0, width, height, null, true);
        return newBitmap;
    }

    /**
     * 裁剪图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap cropArcBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap bgBitmap = KGBitmapUtil.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bgBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, bitmapRect, bitmapRect, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        RectF rectF = new RectF(width, 0, bitmap.getWidth(), height);
        canvas.drawArc(rectF,0,360,false,paint);
        return bgBitmap;
    }

    /**
     * 从右边开始截取百分百为p的图片,并缩放
     *
     * @param bitmap
     * @param p
     * @param sx
     * @param sy
     * @return
     */
    public static Bitmap cropBitmapFromRight(Bitmap bitmap, float p, float sx, float sy) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        int sourceW = bitmap.getWidth();
        int sourceH = bitmap.getHeight();

        int targetW = (int) (sourceW * p);

        int x = sourceW - targetW;

        Matrix m = null;
        if (sourceW * p < 2 || targetW * p < 2) {
            m = null;
        } else {
            m = new Matrix();
            m.postScale(sx, sy);
        }

        Bitmap newBitmap = KGBitmapUtil.createBitmap2(bitmap, x, 0, targetW, sourceH, m, true);
        return newBitmap;
    }

    /**
     * 从底部开始截取百分百为p的图片,并缩放
     *
     * @param bitmap
     * @param p
     * @param sx
     * @param sy
     * @return
     */
    public static Bitmap cropBitmapFromBottom(Bitmap bitmap, float p, float sx, float sy) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        int sourceW = bitmap.getWidth();
        int sourceH = bitmap.getHeight();

        int targetH = (int) (sourceH * p);

        int y = sourceH - targetH;

        Matrix m = null;
        if (sourceW * p < 2 || targetH * p < 2) {
            m = null;
        } else {
            m = new Matrix();
            m.postScale(sx, sy);
        }

        Bitmap newBitmap = KGBitmapUtil.createBitmap2(bitmap, 0, y, sourceW, targetH, m, true);
        return newBitmap;
    }

    public static Bitmap cropCenterBitmap(Bitmap bitmap, int cropWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int cropMaxWidth = width < height ? width : height;

        if (cropWidth > cropMaxWidth) {
            cropWidth = cropMaxWidth;
        }
        int cropX = (width - cropWidth) / 2;
        int cropY = (height - cropWidth) / 2;

        Bitmap newBitmap =
                KGBitmapUtil.createBitmap(bitmap, cropX, cropY, cropWidth, cropWidth, null, true);
        return newBitmap;
    }

    /**
     * 图片平铺
     */
    public static Bitmap bitmapCreateRepeater(int width, Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    /**
     * 按照bitmap的宽度先把原图等比缩放，然后裁剪处一个边长为宽度的矩形
     *
     * @param bitmap
     * @param w
     * @return
     */
    public static Bitmap zoomAndCropSquareBitmap(Bitmap bitmap, int w) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap newbmpTmp = KGBitmapUtil.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        if (newbmpTmp == null || newbmpTmp.isRecycled()) {
            return null;
        }
        Bitmap newBitmap = KGBitmapUtil.createBitmap(newbmpTmp, 0, 0, w, w, null, true);
        return newBitmap;
    }

    public static Bitmap centerCropImage(Bitmap bitmap, int outWidth, int outHeight) {
        return centerCropImage(bitmap, Config.RGB_565, outWidth, outHeight);
    }

    public static Bitmap centerCropImage(Bitmap bitmap, Config config, int outWidth,
                                         int outHeight) {
        if (bitmap == null || outWidth <= 0 || outHeight <= 0) {
            return bitmap;
        }

        Bitmap croppedImage = Bitmap.createBitmap(outWidth, outHeight, config);
        Canvas canvas = new Canvas(croppedImage);

        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = new Rect(0, 0, outWidth, outHeight);

        float dstRate = (float) (dstRect.width() / dstRect.height());
        float srcRate = (float) (srcRect.width() / srcRect.height());

        int deltaW;
        int deltaH;
        if (dstRate > srcRate) {
            deltaW = 0;
            deltaH = (int) ((srcRect.height() - (float) srcRect.width() / dstRate) / 2);
        } else {
            deltaW = (int) ((srcRect.width() - dstRate * (float) srcRect.height()) / 2);
            deltaH = 0;
        }

        srcRect.inset(deltaW, deltaH);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);

        return croppedImage;
    }

    public static Bitmap circleHead(Bitmap source, int width, int height, int borderSize,
                                    int circleColor) {
        Bitmap ret = null;
        if (source != null) {
            Bitmap zoomBitmap = ImageUtil.zoomBitmap(source, width, width);
            Bitmap roundedCornerBitmap = ImageUtil.getRoundedBitmapWithBorder(zoomBitmap, 5.0f,
                    borderSize, circleColor);
            ret = KGBitmapUtil.createBitmap(roundedCornerBitmap, 0, 0, width + borderSize * 2,
                    height + borderSize * 2);
            zoomBitmap.recycle();
            roundedCornerBitmap.recycle();
        }
        return ret;
    }

    public static Bitmap radiusHead(Bitmap source, int width, int height, int borderSize,
                                    int circleColor) {
        Bitmap ret = null;
        if (source != null) {
            Bitmap zoomBitmap = ImageUtil.zoomBitmap(source, width, width);
            Bitmap roundedCornerBitmap = ImageUtil.getRoundedBitmapWithBorder2(zoomBitmap, 5.0f,
                    borderSize, circleColor);
            ret = KGBitmapUtil.createBitmap(roundedCornerBitmap, 0, 0, width + borderSize * 2,
                    height + borderSize * 2);
            zoomBitmap.recycle();
            roundedCornerBitmap.recycle();
        }
        return ret;
    }

    /**
     * 生成圆形半透明边框用户头像
     *
     * @param bitmap
     * @return
     */
    public static Bitmap decodeUserRoundBitmap(Bitmap bitmap) {
        Bitmap ret = null;
        int width = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        int height = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        Bitmap zoomBitmap = ImageUtil.zoomBitmap(bitmap, width, width);
        Bitmap roundedCornerBitmap = ImageUtil.getRoundedBitmapWithBorder(zoomBitmap, 5.0f, 5,
                0x66ffffff);
        ret = KGBitmapUtil.createBitmap(roundedCornerBitmap, 0, 0, width + 10, height + 10);
        zoomBitmap.recycle();
        roundedCornerBitmap.recycle();
        return ret;
    }

    /**
     * 从已经下载号的缓存头像decode出来
     *
     * @return - bitmap
     */
    public static Bitmap decodeUserHead(Bitmap bitmap) {
        Bitmap ret = null;
        int width = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        int height = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        Bitmap zoomBitmap = ImageUtil.zoomBitmap(bitmap, width, width);
        Bitmap roundedCornerBitmap = ImageUtil.getRoundedBitmapWithBorder(zoomBitmap, 5.0f, 5,
                0x66ffffff);
        ret = KGBitmapUtil.createBitmap(roundedCornerBitmap, 0, 0, width + 10, height + 10);
        bitmap.recycle();
        zoomBitmap.recycle();
        roundedCornerBitmap.recycle();
        return ret;
    }

    public static Bitmap decodeUserHead(Bitmap bitmap, boolean isRecyleSrcBitmap) {
        Bitmap ret = null;
        int width = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        int height = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.navigation_header_icon_size);
        Bitmap zoomBitmap = ImageUtil.zoomBitmap(bitmap, width, width);
        Bitmap roundedCornerBitmap = ImageUtil.getRoundedBitmapWithBorder(zoomBitmap, 5.0f, 5,
                0x66ffffff);
        ret = KGBitmapUtil.createBitmap(roundedCornerBitmap, 0, 0, width + 10, height + 10);
        if (isRecyleSrcBitmap) {
            bitmap.recycle();
        }
        zoomBitmap.recycle();
        roundedCornerBitmap.recycle();
        return ret;
    }

    /**
     * 把bitmap保存到SD卡上
     *
     * @param bitmap   源图片
     * @param savePath 保存路径
     * @param format   图片格式
     */
    public static boolean writeBitmapToFile(Bitmap bitmap, String savePath, CompressFormat format) {
        if (bitmap == null || TextUtils.isEmpty(savePath)) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savePath);
            bitmap.compress(format, 100, fos);
        } catch (FileNotFoundException e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Bitmap decodeResizedFile(String pathName, int width, int height) {
        Bitmap bitmap = null;
        if (DrLog.DEBUG) DrLog.d("jamesNotify", "decodeResizedFile path="+pathName);
        try {
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;
            if (width == 0) {
                width = actualWidth;
            }
            if (height == 0) {
                height = actualHeight;
            }

            decodeOptions.inJustDecodeBounds = false;

            int desiredWidth = getResizedDimension(width, height, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(width, height, actualHeight, actualWidth);
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeFile(pathName, decodeOptions);
            if (tempBitmap != null
                    &&
                    (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)
                    && desiredWidth > 0 && desiredHeight > 0) {
                // desiredWidth和desiredHeight必须确保大于0
                // ,不然会报java.lang.IllegalArgumentException: width and height
                // must be > 0
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap decodeResizedResource(Resources res, int id, int width, int height) {
        Bitmap bitmap = null;

        try {
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, id, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;
            if (width == 0) {
                width = actualWidth;
            }
            if (height == 0) {
                height = actualHeight;
            }

            decodeOptions.inJustDecodeBounds = false;

            int desiredWidth = getResizedDimension(width, height, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(width, height, actualHeight, actualWidth);
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                    desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeResource(res, id, decodeOptions);
            if (tempBitmap != null
                    && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                          int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary /
                    (double) actualSecondary; // parasoft-suppress BD.PB.ZERO-1
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth,
                                  int desiredHeight) {
        float n = 1.0f;
        if (desiredWidth == 0 || desiredHeight == 0) {
            return (int) n;
        }

        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degree) {
        if (bitmap == null) {
            return null;
        }

        Matrix m = new Matrix();
        m.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return bitmap;
    }

    /**
     * @param parent
     * @param rotation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void rotateBtnToggleMenu(View parent, float rotation) {
        ImageView imageView = (ImageView) parent.findViewById(R.id.btn_toggle_menu_v);
        if (imageView == null) {
            if (parent instanceof ImageView) {
                imageView = (ImageView) parent;
            }
        }

        if (imageView == null) {
            if (DrLog.DEBUG) {
                DrLog.e("BLUE-btnToggleRotate", "got null imageview in rotateBtnToggleMen");
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= 11) {
            imageView.setRotation(rotation);
        } else {
            if (rotation == 0) {
                imageView.setImageResource(R.drawable.kg_item_ic_btn_toggle_menu);
            } else if (rotation == 180) {
                imageView.setImageResource(R.drawable.fm_list_item_more_up);
            } else {
                // we give up doing the rotatin now.
            }
        }
    }

    /**
     * 获得一个高度模糊的背景图。
     * <br> pad用于歌单列表页的头部背景
     *
     * @param context
     * @param bmp     请注意回收
     * @param width   根据宽度调整图片
     * @return 新生成的bitmap
     */
    public static Bitmap getBlurBg(Context context, Bitmap bmp, int width, int height,
                                   int radious) {
        Bitmap zoomBmp = getBitmapZoomByWidth(bmp, width);
        Bitmap corpBmp = ImageUtil.cropBitmap(zoomBmp, width, height);
        try {
            Bitmap fastblur = Blur.fastblur(context, corpBmp, radious);
            zoomBmp.recycle();
            corpBmp.recycle();
            return fastblur;
        } catch (Throwable e) {
            zoomBmp.recycle();
            return corpBmp;
        }
    }

    public static Bitmap getBitmapZoomByWidth(Bitmap bmp, int width) {
        float scale = 1.0f * width / bmp.getWidth();
        int height = (int) (scale * bmp.getHeight());
        Bitmap zoomBmp = ImageUtil.zoomBitmap(bmp, width, height);
        return zoomBmp;
    }

    public static Bitmap fastBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at="" quasimondo.com="">
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at="" kayenko.com="">
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
        if (sentBitmap == null || sentBitmap.isRecycled()) {
            return null;
        }

        if (sentBitmap.getWidth() <= 0 || sentBitmap.getHeight() <= 0) {
            return null;
        }

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (bitmap == null) {
            return null;
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    //将图片缩放成屏幕宽高的比例的小图
    public static Bitmap zoomtoScreenSize(Context mContext, Bitmap mBackGroundBitmap) {
        Bitmap result = mBackGroundBitmap;
        int[] scrSize = SystemUtils.getScreenSize(mContext);
        int width = mBackGroundBitmap.getWidth();
        int height = mBackGroundBitmap.getHeight();
        if (DrLog.DEBUG) {
            DrLog.e("imageUtil", "width:" + width + "height:" + height + "scrSize[0]:" +
                    scrSize[0] + "scrSize[1]:" + scrSize[1]);
        }
        float p2 = height / ((float) scrSize[1]);
        float p1 = width / ((float) scrSize[0]);
        int scaleWidth;
        int scaleHeight;
        try {
            int startX = 0;
            int startY = 0;
            //图片宽高比例和屏幕宽高比例比较
            if (p1 > p2) {
                scaleHeight = height;
                scaleWidth = scaleHeight * scrSize[0] / scrSize[1];
                startX = (width - scaleWidth) / 2;
                startY = 0;
            } else if (p1 < p2) {
                scaleWidth = width;
                scaleHeight = scaleWidth * scrSize[1] / scrSize[0];
                startX = 0;
                startY = (height - scaleHeight) / 2;
            } else {
                //图片宽高比例和屏幕比例一致
                if (DrLog.DEBUG) {
                    DrLog.e("ImageUtil",
                            "result" + result.getWidth() + "result" + result.getHeight());
                }
                return result;
            }
            if (scaleWidth != 0 && scaleHeight != 0) {
                result = Bitmap.createBitmap(mBackGroundBitmap, startX, startY, scaleWidth,
                        scaleHeight, null, false);
                if (DrLog.DEBUG) {
                    DrLog.e("ImageUtil",
                            "result" + result.getWidth() + "result" + result.getHeight());
                }
            }
        } catch (Exception e) {
            if (DrLog.DEBUG) {
                DrLog.e("ImageUtil", "Exception:" + e.toString());
            }
        }
        return result;
    }

    /**
     * 创建圆角图片
     *
     * @param source
     * @param round
     * @param borderColor
     * @param borderWidth
     * @param rx
     * @param ry
     * @return
     */
    public static Bitmap createTransformedBitmap(Bitmap source, boolean round, int borderColor,
                                                 int borderWidth, float rx, float ry) {
        final int w = source.getWidth();
        final int h = source.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        if (bitmap == null) {
            return null;
        }
        Canvas canvas = new Canvas(bitmap);

        if (borderColor != Color.TRANSPARENT && borderWidth > 0) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(borderColor);
            if (round) {
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
                canvas.drawRoundRect(rectF, rx, ry, paint);
            }
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        BitmapShader shader = new BitmapShader(source, TileMode.CLAMP, TileMode.CLAMP);
        paint.setShader(shader);
        if (round) {
            final float radius = (w > h ? h / 2.0f : w / 2.0f) - borderWidth;
            canvas.drawCircle(w / 2, h / 2, radius, paint);
        } else {
            RectF rectF = new RectF(borderWidth, borderWidth, w - borderWidth, h - borderWidth);
            canvas.drawRoundRect(rectF, rx, ry, paint);
        }

        return bitmap;
    }

    /**
     * 将图片的四角圆化
     *
     * @param bitmap      原图
     * @param roundPixels 圆滑率
     */
    public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

    /**
     * 获取圆形图片
     */
    public static Bitmap createCircleImage(Bitmap source) {
        try {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            }
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Canvas canvas = new Canvas(result);
            paint.setShader(new BitmapShader(squared, TileMode.CLAMP, TileMode.CLAMP));
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        } catch (Throwable e) {
            DrLog.printException(e);
            return null;
        }
    }

    /**
     * 生成带边框圆形图片的方法
     *
     * @param bitmap
     * @param boderPx
     * @param boderColor
     * @return
     */
    public static Bitmap makeRoundedBitmapWithBorder(Bitmap bitmap, int boderPx, int boderColor) {
        Bitmap tmpBitmap = makeRoundedBitmap(bitmap);
        if (tmpBitmap == null) {
            return null;
        }

        Bitmap output = createBitmap(tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint borderpaint = new Paint();
        final Rect borderrect = new Rect(0, 0,
                tmpBitmap.getWidth() + boderPx * 2,
                tmpBitmap.getHeight() + boderPx * 2);
        final RectF borderrectF = new RectF(borderrect);
        borderpaint.setAntiAlias(true);
        borderpaint.setColor(boderColor);
        // drawOval方法用于绘制一个包含borderrectF的圆
        canvas.drawOval(borderrectF, borderpaint);

        final Paint paint = new Paint();
        final Rect src = new Rect(0, 0,
                tmpBitmap.getWidth(),
                tmpBitmap.getHeight());
        final Rect dst = new Rect(boderPx, boderPx,
                tmpBitmap.getWidth() + boderPx,
                tmpBitmap.getHeight() + boderPx);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        // src参数是图片原来的大小，dst参数是 绘画该图片需显示多大
        canvas.drawBitmap(tmpBitmap, src, dst, paint);
        return output;
    }

    /**
     * 生成圆形图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap makeRoundedBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output =
                createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.GRAY);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 生成圆角图片
     *
     * @param bitmap
     * @param radiusPx 圆角半径
     * @return
     */
    public static Bitmap makeRoundedCornerBitmap(Bitmap bitmap, int radiusPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output =
                createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.GRAY);
        canvas.drawRoundRect(rectF, radiusPx, radiusPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable {@link Drawable}
     * @return {@link Bitmap}
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int opacity = drawable.getOpacity();
        Config bmCfg = (opacity == PixelFormat.OPAQUE) ? Config.RGB_565
                : Config.ARGB_8888;
        Bitmap bitmap = createBitmap(width, height, bmCfg);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap createBitmap(int width, int height, Config config) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            System.gc();
            try {
                bitmap = Bitmap.createBitmap(width, height, config);
            } catch (OutOfMemoryError e1) {
            }
        } catch (Exception e2) {
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    /**
     * 将Bitmap转为Drawable
     *
     * @param bitmap
     * @return
     */

    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height,
                                       Matrix m) {
        Bitmap bitmap = null;
        if (source != null && !source.isRecycled()) {
            try {
                bitmap = Bitmap.createBitmap(source, x, y, width, height, m, true);
            } catch (OutOfMemoryError err) {
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(source, x, y, width, height, m, true);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        return bitmap;
    }

    /**
     * 释放view中使用的bitmap资源
     *
     * @param view
     */
    @SuppressWarnings("deprecation")
    public static void recycleBitmap(View view) {
        if (view != null) {
            // recycle background
            Drawable backgroundDrawable = view.getBackground();
            if (backgroundDrawable != null && backgroundDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
                view.setBackgroundDrawable(null);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            // recycle bitmap drawable
            Drawable viewDrawable = null;
            if (view instanceof ImageView) {
                viewDrawable = ((ImageView) view).getDrawable();
                if (viewDrawable != null && viewDrawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) viewDrawable).getBitmap();
                    ((ImageView) view).setImageDrawable(null);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }
        }
    }

    /**
     * 从资源文件中加载不超过指定宽高的bitmap
     *
     * @param context
     * @param rid
     * @param reqWidth  单位：像素
     * @param reqHeight 单位：像素
     * @return
     */
    public static Bitmap loadBitmap(Context context, int rid, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        if (context != null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            if (reqWidth > 0 && reqHeight > 0) {
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(context.getResources(), rid, opts);
                opts.inSampleSize = calcInSampleSize(opts, reqWidth, reqHeight);
                opts.inJustDecodeBounds = false;
            }
            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), rid, opts);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 从文件中加载不超过指定宽高的bitmap
     *
     * @param picFile
     * @param reqWidth  单位：像素
     * @param reqHeight 单位：像素
     * @return
     */
    public static Bitmap loadBitmap(File picFile, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        if (picFile != null && picFile.exists()) {
            String path = picFile.getPath();
            BitmapFactory.Options opts = new BitmapFactory.Options();
            if (reqWidth > 0 && reqHeight > 0) {
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, opts);
                opts.inSampleSize = calcInSampleSize(opts, reqWidth, reqHeight);
                opts.inJustDecodeBounds = false;
            }
            try {
                bitmap = BitmapFactory.decodeFile(path, opts);
            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 计算图片缩放比例
     *
     * @param opts
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calcInSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {
        final int width = opts.outWidth;
        final int height = opts.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int wRate = width / reqWidth;
            int hRate = height / reqHeight;
            int bigRate = (wRate > hRate) ? wRate : hRate;
            inSampleSize = (int) Math.pow(2, Math.round(bigRate) - 1);
        }
        return inSampleSize;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String savePath, CompressFormat format,
                                           int quality) {
        if (bitmap == null || TextUtils.isEmpty(savePath)) {
            return false;
        }
        if (quality > 100) {
            quality = 100;
        } else if (quality < 50) {
            quality = 50;
        }

        FileOutputStream fos = null;
        try {
            File file = new DelFile(savePath);
            if (!file.exists()) {
                String parentPath = file.getParent();
                File parent = new DelFile(parentPath);
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(format, quality, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 保存bitmap
     *
     * @param bitmap   源图片
     * @param savePath 保存路径
     * @param format   图片格式
     */
    public static boolean saveBitmap(Bitmap bitmap, String savePath, CompressFormat format) {
        return saveBitmapToFile(bitmap, savePath, format, 80);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 80, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Bitmap readBitmap(final String filePath) {
        if (TextUtils.isEmpty(filePath) || !FileUtil.isExist(filePath)) {
            return null;
        } else {
            return KGBitmapUtil.decodeFile(filePath);
        }
    }

    public static Bitmap readBitmap(final String filePath, boolean isNullEnable) {
        if (TextUtils.isEmpty(filePath) || !FileUtil.isExist(filePath)) {
            return null;
        } else {
            return KGBitmapUtil.decodeFile(filePath, isNullEnable);
        }
    }

    public static Drawable getRoundCornerBtnBgOnlyStrokeSkin(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(5);
        drawable.setColor(Color.TRANSPARENT);
        drawable.setStroke(2, SkinSetting.getSkinColor(context));
        return drawable;
    }

    public static void loadRoundNetworkImage(ImageView image, Context context, String imageUrl,
                                             int resId) {
        if (image == null) {
            return;
        }

        String tag = (String) image.getTag();
        if (!TextUtils.isEmpty(tag) && TextUtils.equals(tag, imageUrl)) {
            return;
        }

        image.setTag(imageUrl);
        Glide.with(context).load(UrlUtil.makeKtvImageUrl(imageUrl))
                .transform(new GlideRoundTransform(context)).placeholder(resId).into(image);
    }

    public static void loadCornerNetworkImage(int roundPx, ImageView image, Context context,
                                              String imageUrl, int resId) {
        if (image == null) {
            return;
        }

        String tag = (String) image.getTag();
        if (!TextUtils.isEmpty(tag) && TextUtils.equals(tag, imageUrl)) {
            return;
        }
        image.setTag(imageUrl);
        Glide.with(context).load(UrlUtil.makeKtvImageUrl(imageUrl)).placeholder(resId)
                .transform(new GlideAsRoundRectTransform(context, roundPx, roundPx)).into(image);
    }

    // 黑白效果
    public static Bitmap changeToGray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayBitmap;
    }

    public static Bitmap changeColor(Bitmap bitmap, float[] src) {
        if (bitmap == null) {
            return null;
        }
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 设置抗锯齿
        ColorMatrix colorMatrix = new ColorMatrix(src);
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayBitmap;
    }

    public static Bitmap changeToGray(Context context, int resId) {
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        return changeToGray(bitmap);
    }

    /**
     * 生成带圆角的图片
     *
     * @param bitmap 原图片
     * @param pixels 圆角弧度
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static Observable<BitmapEntity> loadBitmap(final String url, final int w,
                                                                final int h) {
        return Observable.create(new Observable.OnSubscribe<BitmapEntity>() {
            @Override
            public void call(final Subscriber<? super BitmapEntity> subscriber) {
                Glide.with(DRCommonApplication.getContext()).load(url).asBitmap()
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<Bitmap> target,
                                                       boolean isFirstResource) {
                                subscriber.onError(new Throwable("load bitmap failed."));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model,
                                                           Target<Bitmap> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                Bitmap bitmap = resource;
                                if (bitmap != null) {
                                    subscriber.onNext(new BitmapEntity(bitmap, url));
                                }
                                subscriber.onCompleted();
                                return false;
                            }
                        }).into(w, h);
            }
        });
    }

    public static Observable<BitmapEntity> loadBitmap(final String url) {
        return Observable.create(new Observable.OnSubscribe<BitmapEntity>() {
            @Override
            public void call(final Subscriber<? super BitmapEntity> subscriber) {
                Glide.with(DRCommonApplication.getContext()).load(url).asBitmap()
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<Bitmap> target,
                                                       boolean isFirstResource) {
                                subscriber.onError(new Throwable("load bitmap failed."));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model,
                                                           Target<Bitmap> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                Bitmap bitmap = resource;
                                if (bitmap != null) {
                                    subscriber.onNext(new BitmapEntity(bitmap, url));
                                }
                                subscriber.onCompleted();
                                return false;
                            }
                        });
            }
        });


    }

    /**
     * 加载镜像处理的图片
     *
     * @param url
     * @param w
     * @param h
     * @return
     */
    public static Observable<BitmapEntity> loadMirrorBitmap(final String url, final int w,
                                                                      final int h) {
        return Observable.create(new Observable.OnSubscribe<BitmapEntity>() {
            @Override
            public void call(final Subscriber<? super BitmapEntity> subscriber) {
                Glide.with(DRCommonApplication.getContext()).load(url).asBitmap()
                        .listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model,
                                                       Target<Bitmap> target,
                                                       boolean isFirstResource) {
                                subscriber.onError(new Throwable("load bitmap failed."));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model,
                                                           Target<Bitmap> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                Bitmap bitmap = resource;
                                if (bitmap != null) {
                                    Observable
                                            .just(bitmap)
                                            .observeOn(Schedulers.computation())
                                            .filter(new Func1<Bitmap, Boolean>() {
                                                @Override
                                                public Boolean call(Bitmap bitmap) {
                                                    return bitmap != null;
                                                }
                                            })
                                            .map(new Func1<Bitmap, Bitmap>() {
                                                @Override
                                                public Bitmap call(Bitmap bitmap) {
                                                    return getMirrorBitmap(bitmap, false);
                                                }
                                            })
                                            .subscribe(new Action1<Bitmap>() {
                                                @Override
                                                public void call(Bitmap bitmap) {
                                                    subscriber
                                                            .onNext(new BitmapEntity(bitmap, url));
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    subscriber.onError(
                                                            new Throwable("load bitmap failed."));
                                                    DrLog.e(TAG, "load mirror bitmap failed. " +
                                                            Log.getStackTraceString(throwable));
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    subscriber.onCompleted();
                                                }
                                            });
                                } else {
                                    subscriber.onCompleted();
                                }
                                return false;
                            }
                        }).into(w, h);
            }
        });
    }

    /**
     * 获取镜像图片
     *
     * @param src
     * @return
     */
    public static Bitmap getMirrorBitmap(Bitmap src, boolean recycle) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newb;
        synchronized (getDecodeLock()) {
            newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
            Matrix m = new Matrix();
            //m.postScale(1, -1);   //镜像垂直翻转
            m.postScale(-1, 1);   //镜像水平翻转
            //m.postRotate(-90);  //旋转-90度
            Bitmap new2 = Bitmap.createBitmap(src, 0, 0, w, h, m, true);
            Canvas cv = new Canvas(newb);
            cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()),
                    new Rect(0, 0, w, h), null);
            if (recycle) {
                src.recycle();
            }
        }
        return newb;
    }

    public static ByteArrayOutputStream compress(Bitmap bitmap, int maxSize, int quality,
                                                 CompressFormat format) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Bitmap mBitmap = bitmap;
        synchronized (getDecodeLock()) {
            mBitmap.compress(format, COMPRESS_QUALITY_100, byteBuffer);
            while (byteBuffer.size() >= maxSize) {
                mBitmap = decode(byteBuffer.toByteArray());
                byteBuffer.reset();
                mBitmap.compress(format, quality, byteBuffer);
            }
        }
        bitmap.recycle();
        //saveToFile(byteBuffer, FxConstant.IMAGE_USER_IMG_FOLDER + new Date().toString() + "_compressed.jpg");
        return byteBuffer;
    }

    /**
     * 将bitmap压缩到指定大小
     *
     * @param bitmap  原始bitmap
     * @param maxSize 压缩后大小
     * @param quality 每次压缩的质量值
     * @return
     */
    public static ByteArrayOutputStream compress(Bitmap bitmap, int maxSize, int quality) {
        return compress(bitmap, maxSize, quality, CompressFormat.JPEG);
    }

    public static final int COMPRESS_QUALITY_100 = 100;

    public static final int COMPRESS_QUALITY_90 = 90;

    public static final int MAX_SIZE_2M = 2 * 1024 * 1024;

    public static final int MAX_WIDTH_OR_HEIGHT_2048_PX = 2048;

    /**
     * 根据option解析bitmap
     *
     * @param options
     * @param inputStream
     * @return
     */
    public static Bitmap decode(BitmapFactory.Options options, InputStream inputStream) {
        Bitmap bitmap;
        int w = options.outWidth;
        int h = options.outHeight;
        synchronized (getDecodeLock()) {
            Bitmap tmp;
            if (options == null) {
                tmp = BitmapFactory.decodeStream(inputStream);
            } else {
                tmp = BitmapFactory.decodeStream(inputStream, null, options);
            }
            bitmap = scale(tmp, w, h);
        }
        return bitmap;
    }

    public static int[] getImageWHFromUri(Context context, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream inputStream = getInputStreamFromUri(context, uri);
        if (inputStream != null) {
            BitmapFactory.decodeStream(inputStream, null, options);
        }
        CloseUtils.closeIO(inputStream);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * bitmap缩放
     *
     * @param bitmap
     * @param targetW
     * @param targetH
     * @return
     */
    public static Bitmap scale(Bitmap bitmap, int targetW, int targetH) {
        DrLog.d(TAG, "Before scale. bitmap w:" + bitmap.getWidth() + " h:" + bitmap.getHeight());
        Bitmap b = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
        DrLog.d(TAG, "After scale.bitmap w:" + b.getWidth() + " h:" + b.getHeight());
        return b;
    }

    /**
     * 根据指定分辨率大小decode option
     *
     * @param inputStream
     * @param maxWidthOrHeight 高宽最大值
     * @return
     */
    public static BitmapFactory.Options getDecodeOpt(InputStream inputStream,
                                                     int maxWidthOrHeight) {
        BitmapFactory.Options options = null;
        synchronized (getDecodeLock()) {
            try {
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                int actualWidth = options.outWidth;
                int actualHeight = options.outHeight;
                int maxWidth = (actualWidth > actualHeight) ? actualWidth : actualHeight;
                if (maxWidth > maxWidthOrHeight) {
                    //高宽任意一个大于目标值就需要处理。
                    if (actualWidth > actualHeight) {
                        //宽大于高
                        options.outWidth = maxWidthOrHeight;
                        options.outHeight = (int) ((float) actualHeight * (float) options.outWidth /
                                (float) actualWidth);
                    } else {
                        options.outHeight = maxWidthOrHeight;
                        options.outWidth = (int) ((float) actualWidth * (float) options.outHeight /
                                (float) actualHeight);
                    }
                    DrLog.d("ImageUtil",
                            "bitmap's width/height is too large [w:" + actualWidth + ",h:" +
                                    actualHeight + "]." +
                                    "target w:" + options.outWidth + ",h:" + options.outHeight);
                }
                options.inSampleSize = getBestInSample(actualWidth, actualHeight, options.outWidth,
                        options.outHeight);
                DrLog.d(TAG, "BitmapFactory.Options.inSampleSize is " + options.inSampleSize);
                options.inJustDecodeBounds = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                inputStream.close();
                return options;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    public static Bitmap decode(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * 从uri中获取流
     *
     * @return
     */
    public static InputStream getInputStreamFromUri(Context mContext, Uri uri) {
        try {
            return mContext.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Object sDecodeLock = new Object();

    /**
     * 获取解析bitmap统一的lock，防止同一时间同时解析多个图片
     *
     * @return ImageRequest.sDecodeLock
     */
    private static Object getDecodeLock() {
        return sDecodeLock;
//		DrLog.e(TAG, "requesting for ImageRequest.sDecodeLock object failed.");
//		return null;
    }

    public static class BitmapEntity {
        public Bitmap mBitmap;
        public String url;

        public BitmapEntity(Bitmap mBitmap, String url) {
            this.mBitmap = mBitmap;
            this.url = url;
        }
    }

    /**
     * 改动自Volley ImageRequest获取最佳Options.inSampleSize的方法。
     *
     * @param actualWidth
     * @param actualHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private static int getBestInSample(int actualWidth,
                                       int actualHeight,
                                       int desiredWidth,
                                       int desiredHeight) {
//		try {
//			Method mMethod = ImageRequest.class.getDeclaredMethod("findBestSampleSize", int.class, int.class, int.class, int.class);
//			mMethod.setAccessible(true);
//			return (int) mMethod.invoke(ImageRequest.class, actualWidth, actualHeight, desiredWidth, desiredHeight);
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		return 0;
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
		/*double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while (n <= ratio) {
			n *= 2;
		}

		return (int) n;*/
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge =
                    (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (OutOfMemoryError e) {
                return null;
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength,
                        edgeLength);
                scaledBitmap.recycle();
            } catch (OutOfMemoryError e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        } else if (widthOrg > edgeLength || heightOrg > edgeLength) {//缩放处理
            int width = widthOrg;
            int height = heightOrg;
            // 设置想要的大小
            int newWidth = edgeLength;
            int newHeight = edgeLength;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            try {
                result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                        true);
            } catch (OutOfMemoryError e) {
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    /**
     * 根据图片，自动算出匹配的颜色值
     *
     * @param bm 图片对象
     * @return 颜色值
     */
    public static int getImageMainColor(Bitmap bm) {
        if (bm == null) {
            return Color.WHITE;
        }
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] RGB;
        if (w > 100 && h > 100) {
            Bitmap temp = zoomBitmap(bm, w / 8, h / 8);
            w = temp.getWidth();
            h = temp.getHeight();
            RGB = new int[w * h];
            temp.getPixels(RGB, 0, w, 0, 0, w, h);
            temp.recycle();
        } else {
            RGB = new int[w * h];
            bm.getPixels(RGB, 0, w, 0, 0, w, h);
        }

        int[] rgb = new int[3];

        int ignore = 0;
        int MaxPosNumber = 10000;// 最多取一万个点求平均值，
        ignore = RGB.length / MaxPosNumber;
        if (ignore == 0) {
            ignore = 1;
        }
        int totalr = 0;
        int totalg = 0;
        int totalb = 0;
        int PosNumber = 0;
        int len = RGB.length - 1;
        for (int i = len; i >= 0; i -= ignore) {

            rgb[0] = (RGB[i] & 0x00ff0000) >> 16;
            rgb[1] = (RGB[i] & 0x0000ff00) >> 8;
            rgb[2] = (RGB[i] & 0x000000ff);

            //剔除白色系颜色
            if (rgb[0] == rgb[1] && rgb[1] == rgb[2] && rgb[0] > 100 ||
                    (rgb[0] > 200 && rgb[1] > 200 && rgb[2] > 200)) {
                continue;
            }

            totalr += rgb[0];
            totalg += rgb[1];
            totalb += rgb[2];

            PosNumber++;
        }

        if (PosNumber > 0) {
            rgb[0] = totalr / PosNumber;
            rgb[1] = totalg / PosNumber;
            rgb[2] = totalb / PosNumber;
        }

        int returnColor = Color.rgb(rgb[0], rgb[1], rgb[2]);
        RGB = null;
        return returnColor;
    }


    /**
     * 以CenterCrop方式resize图片,并以math_parent的方式填充
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */

    public static Bitmap fillBitmapCenterCrop(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return null;
        }
        int bitmapWidth = src.getWidth();
        int bitmapHeight = src.getHeight();

        if ((bitmapWidth < destWidth || bitmapHeight < destHeight) ||
                (bitmapWidth > destWidth && bitmapHeight > destHeight)) {
            float ratio = Math.max(destWidth * 1.0F / bitmapWidth,
                    destHeight * 1.0F / bitmapHeight);
            if (DrLog.isDebug()) {
                DrLog.d(TAG, "ratio: " + ratio + "\n" +
                        "destWidth: " + destWidth + "\n" +
                        "destHeight: " + destHeight + "\n" +
                        "bitmapWidth: " + bitmapWidth + "\n" +
                        "bitmapHeight: " + bitmapHeight);
            }
            Bitmap scaleBitmap = scaleBitmap(src, ratio);
            return resizeBitmapByCenterCrop(scaleBitmap, destWidth, destHeight);
        }
        return resizeBitmapByCenterCrop(src, destWidth, destHeight);
    }

    /**
     * 以CenterCrop方式resize图片
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */
    public static Bitmap resizeBitmapByCenterCrop(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return null;
        }
        int bitmapWidth = src.getWidth();
        int bitmapHeight = src.getHeight();
        int clipWidth = bitmapWidth;
        int clipHeight = bitmapHeight;
        int startX = 0;
        int startY = 0;

        float temp = (destHeight * 1.0f / destWidth) - (bitmapHeight * 1.0f / bitmapWidth);

        if (temp > 0) {
            clipWidth = (bitmapHeight * destWidth) / destHeight;
            startX = Math.abs((bitmapWidth - clipWidth) / 2);
            if (clipHeight > destHeight) {
                startY = (clipHeight - destHeight) / 2;
            }
            if (clipWidth > destWidth) {
                startX += (clipWidth - destWidth) / 2;
            }

        } else {
            clipHeight = (destHeight * bitmapWidth) / destWidth;
            startY = (bitmapHeight - clipHeight) / 2;
            if (clipWidth > destWidth) {
                startX = (clipWidth - destWidth) / 2;
            }

            if (clipHeight > destHeight) {
                startY += (clipHeight - destHeight) / 2;
            }
        }

        if (DrLog.isDebug()) {
            DrLog.d(TAG,
                    "destHeight: " + destHeight + "\n" +
                            "destWidth: " + destWidth + "\n" +
                            "bitmapWidth: " + bitmapWidth + "\n" +
                            "bitmapHeight: " + bitmapHeight + "\n" +
                            "clipWidth: " + clipWidth + "\n" +
                            "clipHeight: " + clipHeight + "\n" +
                            "startX: " + startX + "\n" +
                            "startY: " + startY);
        }
        return Bitmap.createBitmap(src, startX, startY, clipWidth, clipHeight);
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }


    /**
     *
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */

    public static float getScaleRatio(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return 1.0F;
        }
        int bitmapWidth = src.getWidth();
        int bitmapHeight = src.getHeight();

        if ((bitmapWidth < destWidth || bitmapHeight < destHeight) ||
                (bitmapWidth > destWidth && bitmapHeight > destHeight)) {
            float ratio = Math.max(destWidth * 1.0F / bitmapWidth,
                    destHeight * 1.0F / bitmapHeight);
            if (DrLog.isDebug()) {
                DrLog.d(TAG, "ratio: " + ratio + "\n" +
                        "destWidth: " + destWidth + "\n" +
                        "destHeight: " + destHeight + "\n" +
                        "bitmapWidth: " + bitmapWidth + "\n" +
                        "bitmapHeight: " + bitmapHeight);
            }
            return ratio;
        }
        return 1.0F;
    }
}
