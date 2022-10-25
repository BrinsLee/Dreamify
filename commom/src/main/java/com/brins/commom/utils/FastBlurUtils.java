package com.brins.commom.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;

/**
 * 图像模糊处理
 * Created by ricky on 2015/2/5.
 */
public class FastBlurUtils {
    private static final String KEY_CACHE_BASE = "FASTBLUR_LICX";
    /**
     * JNI数组处理模糊过程
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
//    public static Bitmap doBlurJniArray(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
//        Bitmap bitmap;
//        if (canReuseInBitmap) {
//            bitmap = sentBitmap;
//        } else {
//            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
//        }
//
//        if (radius < 1) {
//            return (null);
//        }
//
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//
//        int[] pix = new int[w * h];
//        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
//        //Jni Pixels
//        ImageBlur.blurIntArray(pix, w, h, radius);
//
//        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
//        return (bitmap);
//    }


    /**
     * JNI Bitmap处理模糊过程
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
//    public static Bitmap doBlurJniBitMap(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
//        Bitmap bitmap;
//        if (canReuseInBitmap) {
//            bitmap = sentBitmap;
//        } else {
//            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
//        }
//
//        if (radius < 1) {
//            return (null);
//        }
//        //Jni BitMap
//        ImageBlur.blurBitMap(bitmap, radius);
//
//        return (bitmap);
//    }


    /**
     * Java处理模糊过程
     * 原生JAVA处理模糊
     * @param sentBitmap
     * @param radius
     * @param canReuseInBitmap
     * @return
     */
    public static Bitmap javaBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
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

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
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

    /**
     * RenderScript 模糊处理
     * 推荐使用,OPENGL 渲染速度快
     * @param ctx
     * @param bkg
     * @param radius  range 0 < radius <= 25
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap rsBlur(Context ctx,Bitmap bkg,int radius) {
        try {
            RenderScript rs = RenderScript.create(ctx);

            Allocation overlayAlloc = Allocation.createFromBitmap(rs, bkg);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
            blur.setRadius(radius);
            blur.setInput(overlayAlloc);
            blur.forEach(overlayAlloc);
            overlayAlloc.copyTo(bkg);

            rs.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bkg;
    }

    /**
     * 根据图片的的宽高 截取中间宽的部分  （for 繁星直播间上下切换模糊背景）
     *
     * @param bmp       原图，待模糊
     * @param dstWidth  最终需要的图片宽度
     * @param dstHeight 最终需要的图片高度
     * @param color     覆盖在图片上层的遮罩颜色
     * @param radius    模糊度
     * @param size   先将图片缩小在处理的倍数
     */
    public static Bitmap makeBlur(Context ctx, int color, Bitmap bmp, int dstWidth, int dstHeight, int radius, int size) {
        Bitmap overlay = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        Paint p = new Paint();
        double bmpW = (double) bmp.getHeight() * (double) dstWidth / (double) dstHeight;
        double startx = (Math.abs((double) bmp.getWidth() - bmpW)) / 2;
        double endx = startx + bmpW;
        canvas.drawBitmap(bmp, new Rect((int) startx, 0, (int) endx, bmp.getHeight()), new RectF(0, 0, dstWidth, dstHeight), p);
        p.setColor(color);//会不会影响模糊效果？
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, dstWidth, dstHeight, p);
        if (radius == 0) {//模糊半径为0，不需要模糊，直接返回
            return overlay;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return fxRsBlur(ctx, overlay, radius, size);
        } else {
            return javaBlur(bmp, 50, true); // java 模糊度设置50
        }
    }

    /**
     * 繁星 高斯模糊 先将图片缩小再进行模糊处理
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap fxRsBlur(Context ctx, Bitmap bkg, int radius, int size) {
        try {
            int width = bkg.getWidth();
            int height = bkg.getHeight();
            int scaledWidth = width / size;
            int scaledHeight = height / size;
            Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(1 / (float) size, 1 / (float) size);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(bkg, 0, 0, paint);

            RenderScript rs = RenderScript.create(ctx);

//            Allocation overlayAlloc = Allocation.createFromBitmap(rs, bitmap);

            Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(bitmap);
            rs.destroy();
            return bitmap;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 模糊效果生成
     * @param ctx
     * @param bmp 原图，待模糊
     * @param dstWidth 最终需要的图片宽度
     * @param dstHeight 最终需要的图片高度
     * @param radius
     * @param alpha 0<= alpha <= 255
     * @return
     */
    public static Bitmap makeBlur(Context ctx,Bitmap bmp,int dstWidth,int dstHeight, int radius, int alpha){
        Bitmap overlay = Bitmap.createBitmap(dstWidth,dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        int wh = Math.max(dstWidth,dstHeight);
        int x = dstWidth/2 - wh/2;
        int y = dstHeight/2 - wh/2;
        int w = x + wh;
        int h = y + wh;
        Paint p = new Paint();
        canvas.drawBitmap(bmp,new Rect(0,0,bmp.getWidth(),bmp.getHeight()),new Rect(x,y,w,h),p);
        p.setColor(Color.argb(alpha, 0, 0, 0));
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, dstWidth, dstHeight, p);
        if(radius == 0){//模糊半径为0，不需要模糊，直接返回
            return overlay;
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            return rsBlur(ctx,overlay,radius);
        }else{
            return javaBlur(overlay, radius, true);
        }
    }

    /**
     * 模糊效果生成
     * @param ctx
     * @param bmp 原图，待模糊
     * @param dstWidth 最终需要的图片宽度
     * @param dstHeight 最终需要的图片高度
     * @param radius
     * @param color 底色
     * @return
     */
    public static Bitmap makeBlurWithColor(Context ctx, Bitmap bmp, int dstWidth, int dstHeight, int radius, int color) {
        Bitmap overlay = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        int wh = Math.max(dstWidth, dstHeight);
        int x = dstWidth / 2 - wh / 2;
        int y = dstHeight / 2 - wh / 2;
        int w = x + wh;
        int h = y + wh;
        Paint p = new Paint();
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new Rect(x, y, w, h), p);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, dstWidth, dstHeight, p);
        if (radius == 0) { //模糊半径为0，不需要模糊，直接返回
            return overlay;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return rsBlur(ctx, overlay, radius);
        } else {
            return javaBlur(overlay, radius, true);
        }
    }

    /**
     *
     * @param bmp
     * @param dstWidth
     * @param dstHeight
     * @param radius
     * @param alpha
     * @return
     */
    public static Bitmap javaMakeBlur2(Bitmap bmp, int dstWidth, int dstHeight, int radius, int alpha) {
        Bitmap overlay = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        int wh = Math.max(dstWidth, dstHeight);
        int x = dstWidth / 2 - wh / 2;
        int y = dstHeight / 2 - wh / 2;
        int w = x + wh;
        int h = y + wh;
        Paint p = new Paint();
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new Rect(x, y, w, h), p);
        p.setColor(Color.argb(alpha, 0, 0, 0));
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, dstWidth, dstHeight, p);
        if (radius == 0) {//模糊半径为0，不需要模糊，直接返回
            return overlay;
        }
        return javaBlur(overlay, radius, true);
    }
    /**
     * 模糊效果生成
     * @param ctx
     * @param bmp 原图，待模糊
     * @param dstWidth 最终需要的图片宽度
     * @param dstHeight 最终需要的图片高度
     * @return
     */
    public static Bitmap makeBlur(Context ctx,Bitmap bmp,int dstWidth,int dstHeight){
        return makeBlur(ctx, bmp, dstWidth, dstHeight, 20, 0x33);
    }

    public static String getCacheKey(String url){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        return MD5Util.getMd5(KEY_CACHE_BASE+url);
    }

//    /**
//     * 加入缓存
//     * @param url
//     * @param blurBitmap
//     */
//    public static void putCacheBitmap(String url,Bitmap blurBitmap){
//        BitmapCache.getInstance().putBitmap(getCacheKey(url),blurBitmap);
//    }
//
//    /**
//     *从缓存中获取
//     * @param url
//     */
//    public static Bitmap getCachedBitmap(String url){
//        return BitmapCache.getInstance().getBitmap(getCacheKey(url));
//    }

    public static Bitmap makeBlurNotChange(Context ctx, Bitmap bmp, int dstWidth, int dstHeight, int radius, int color) {
        Bitmap overlay = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        int wh = Math.max(dstWidth, dstHeight);
        int x = 0;
        int y = 0;
        int w = dstWidth;
        int h = dstHeight;
        Paint p = new Paint();
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new Rect(x, y, w, h), p);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, dstWidth, dstHeight, p);
        if (radius == 0) { //模糊半径为0，不需要模糊，直接返回
            return overlay;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return rsBlur(ctx, overlay, radius);
        } else {
            return javaBlur(overlay, radius, true);
        }
    }

}
