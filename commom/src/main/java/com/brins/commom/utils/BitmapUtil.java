
package com.brins.commom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.RawRes;
import androidx.core.graphics.ColorUtils;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.skin.ColorUtil;
import com.brins.commom.utils.log.DrLog;
import com.kugou.uilib.widget.imageview.delegate.palette.Palette;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 描述:包含Bitmap的创建复制等基本操作
 * 
 * @author haichaoxu
 * @since 2014-6-3 下午5:54:53
 */
public final class BitmapUtil {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int getBitmapWidth(int id){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(DRCommonApplication.getContext().getResources(), id,options);
        return options.outWidth;
    }

    public static int getBitmapHeight(int id){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(DRCommonApplication.getContext().getResources(), id, options);
        return options.outHeight;

    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
            int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
        Bitmap bitmap = null;
        if (source != null && !source.isRecycled()) {
            try {
                bitmap = Bitmap.createBitmap(source, x, y, width, height);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(source, x, y, width, height);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m,
            boolean filter) {
        Bitmap bitmap = null;
        if (source != null && !source.isRecycled()) {
            try {
                bitmap = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap createBitmap(int width, int height, Config config) {
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

    public static Bitmap createBitmap(int[] colors, int width, int height, Config config) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(colors, width, height, config);
        } catch (OutOfMemoryError e) {
            System.gc();
            try {
                bitmap = Bitmap.createBitmap(colors, width, height, config);
            } catch (OutOfMemoryError e1) {
            }
        } catch (Exception e2) {
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            InputStream is = null;
            try {
                try {
                    is = new BufferedInputStream(FileUtil.getFileInputStream(pathName));
                    bitmap = decodeStream(is);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    /**
     * 从图片文件加载Bitmap，加载异常返回纯色Bitmap
     * @param pathName  图片文件路径
     * @param defaultColor 异常情况下的颜色
     * @return
     */
    public static Bitmap decodeFileWithColor(String pathName, int defaultColor) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            InputStream is = null;
            try {
                is = FileUtil.getFileInputStream(pathName);
                try {
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (OutOfMemoryError e) {
                    bitmap = null;
                }
            } catch (Exception e) {
                bitmap = null;
            } finally {
                FileUtil.closeIS(is);
            }
        }
        if (bitmap == null) {
            bitmap = createColorBitmap(defaultColor);
        }
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName, int inSampleSize) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = inSampleSize;
            opts.inJustDecodeBounds = false;
            try {
                bitmap = BitmapFactory.decodeFile(pathName, opts);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeFile(pathName, opts);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName, int width, int height) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            opts.inJustDecodeBounds = false;
            try {
                bitmap = BitmapFactory.decodeFile(pathName, opts);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeFile(pathName, opts);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap decodeFileMayReturnNull(String pathName, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        Bitmap bitmap = null;
        if (DrLog.DEBUG)
            DrLog.e("jamesNotify", pathName + "--FileUtil.isExist(pathName) :" + FileUtil.isExist(pathName));
        if (FileUtil.isExist(pathName)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            opts.inJustDecodeBounds = false;
            try {
                bitmap = BitmapFactory.decodeFile(pathName, opts);
                if (DrLog.DEBUG) DrLog.e("jamesNotify", "bitmap :" + bitmap);
            } catch (OutOfMemoryError e) {
                System.gc();
            } catch (Exception e) {
                if (DrLog.DEBUG) DrLog.e("jamesNotify", "Exception:" + e.getLocalizedMessage());
            }
        }
        return bitmap;
    }


    /**
     * 获取渐变的bitmap
     */
    public static Bitmap getGradientBitmap(Bitmap originalBitmap, int width, int height, int startColor, int endColor, int angle){
        if (width <=0 || height <= 0){
            return null;
        }
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);

        if (originalBitmap != null){
            canvas.drawBitmap(originalBitmap, 0, 0, null);
        }

        Paint paint = new Paint();
        Point[] points = getGradientPoints(angle, width, height);
        LinearGradient shader = null;
        if (points != null && points.length > 1){
            shader = new LinearGradient(points[0].x, points[0].y, points[1].x, points[1].y, startColor, endColor, Shader.TileMode.CLAMP);
        }
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);
        return updatedBitmap;
    }

    private static Point[] getGradientPoints(int gradientAngle, int width, int height){
        int x0 = 0, y0 = 0, x1 = width, y1 = 0;
        int direction = (gradientAngle % 360) / 45;
        if (direction < 0) {
            direction += 8;
        }
        switch (direction) {
            case 0:
                x0 = 0;
                y0 = 0;
                x1 = width;
                y1 = 0;
                break;
            case 1:
                x0 = 0;
                y0 = height;
                x1 = width;
                y1 = 0;
                break;
            case 2:
                x0 = 0;
                y0 = height;
                x1 = 0;
                y1 = 0;
                break;
            case 3:
                x0 = width;
                y0 = height;
                x1 = 0;
                y1 = 0;
                break;
            case 4:
                x0 = width;
                y0 = 0;
                x1 = 0;
                y1 = 0;
                break;
            case 5:
                x0 = width;
                y0 = 0;
                x1 = 0;
                y1 = height;
                break;
            case 6:
                x0 = 0;
                y0 = 0;
                x1 = 0;
                y1 = height;
                break;
            case 7:
                x0 = 0;
                y0 = 0;
                x1 = width;
                y1 = height;
                break;
        }
        return new Point[]{new Point(x0, y0), new Point(x1, y1)};
    }

    public static Bitmap decodeResource(Resources res, int id) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            try {
                is = new BufferedInputStream(res.openRawResource(id));
                bitmap = decodeStream(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap decodeResourceMayReturnNull(Resources res, @RawRes @IntRange(from = Integer.MIN_VALUE) int id) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            try {
                is = new BufferedInputStream(res.openRawResource(id));
                bitmap = decodeStream(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (Exception e) {
            DrLog.printException(e);
        }
        return bitmap;
    }

    public static Bitmap createColorBitmap(int color) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        bitmap.setPixel(0, 0, color);
        return bitmap;
    }

    public static Bitmap createColorBitmap(int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.eraseColor(color);
        return bitmap;
    }

    public static Bitmap createColorBitmap(int startColor,int endColor, GradientDrawable.Orientation orientation,
                                           int width,int height){
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, new int[]{startColor,endColor});
        gradientDrawable.setBounds(0, 0, width, height);
        Config config = Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        gradientDrawable.setSize(width,height);
        gradientDrawable.setDither(true);
        gradientDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromFile(String resPath, int maxWidth) {
        if (maxWidth <= 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resPath, options);
        int oldHeight = options.outHeight;
        int oldWidth = options.outWidth;
        int oldMaxWidth = (oldHeight > oldWidth) ? oldHeight : oldWidth;

        if (oldMaxWidth > maxWidth) {
            options.inSampleSize = Math.round((float) oldMaxWidth / (float) maxWidth);
        } else {
            options.inSampleSize = 1;
        }
        options.inJustDecodeBounds = false;
        return rotateBitmap(BitmapFactory.decodeFile(resPath, options),readPictureDegree(resPath));
    }

    public static Bitmap decodeSampledBitmapFromFile(String resPath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resPath, options);
    }

    /**
     * mImageView.setImageBitmap(
     * decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
            int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeStream(InputStream is) {
        Bitmap bitmap = null;
        if (is != null) {
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (OutOfMemoryError e1) {
                }
            } catch (Exception e2) {
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }
    
    public static Bitmap imageZoom(Bitmap bitMap, double maxSize) {
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = (double)(b.length / 1024);
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			double height = bitMap.getHeight() / Math.sqrt(i);
			double width = bitMap.getWidth() / Math.sqrt(i);
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitMap = zoomImage(bitMap, width,
                    height > (2 * width) ? (2 * width) : height);
		}
		return bitMap;
	}

    /**
     * 设置图片 Bitmap任意透明度
     * @param sourceImg
     * @param number
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Config.ARGB_8888);

        return sourceImg;
    }

    public static void recycleBitmap(Bitmap... bms) {
        for (Bitmap bm : bms) {
            recycleBitmap(bm);
        }
    }

    public static void recycleBitmap(Bitmap bm) {
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
    }

    /**
     * 既带模糊又带缩放的功能
     * @param bitmap
     * @param radius 模糊半径
     * @param inSampleSize 缩放大小
     * @return
     */
    public static Bitmap blurBitmapSampleSize(Bitmap bitmap,int radius,int inSampleSize){
        if (bitmap==null || bitmap.isRecycled())
            return null;
        Bitmap blurSource = Bitmap.createScaledBitmap(bitmap, Math.max(bitmap.getWidth() / inSampleSize, 1), Math.max(bitmap.getHeight() / inSampleSize, 1), true);
        if (radius <= 0)
            return blurSource;
        return Blur.fastblur(DRCommonApplication.getContext(), blurSource, radius);
    }

    /**
     *在图片上盖一层颜色的蒙层
     *
     * **/
    public static Bitmap overColorToBitmap(Bitmap blurBitmap, int color) {
        Rect srcR = new Rect(0, 0, blurBitmap.getWidth(), blurBitmap.getHeight());
        Bitmap overlayBitmap = Bitmap.createBitmap(srcR.width(), srcR.height(), Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(overlayBitmap);
        canvas.drawBitmap(blurBitmap, srcR, srcR, new Paint());
        canvas.drawColor(color);
        canvas.setBitmap(null);
        return overlayBitmap;
    }

    //居中裁剪
    public static Bitmap scaleBitmap(Bitmap bitmap,float w,float h){
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float x = 0,y = 0,scaleWidth = width,scaleHeight = height;
        Bitmap newbmp;
        //Log.e("gacmy","width:"+width+" height:"+height);
        if(w > h){//比例宽度大于高度的情况
            float scale = w/h;
            float tempH = width/scale;
            if(height > tempH){//
                x = 0;
                y=(height-tempH)/2;
                scaleWidth = width;
                scaleHeight = tempH;
            }else{
                scaleWidth = height*scale;
                x = (width - scaleWidth)/2;
                y= 0;
            }
//            Log.e("gacmy","scale:"+scale+" scaleWidth:"+scaleWidth+" scaleHeight:"+scaleHeight);
        }else if(w < h){//比例宽度小于高度的情况
            float scale = h/w;
            float tempW = height/scale;
            if(width > tempW){
                y = 0;
                x = (width -tempW)/2;
                scaleWidth = tempW;
                scaleHeight = height;
            }else{
                scaleHeight = width*scale;
                y = (height - scaleHeight)/2;
                x = 0;
                scaleWidth = width;
            }

        }else{//比例宽高相等的情况
            if(width > height){
                x= (width-height)/2;
                y = 0;
                scaleHeight = height;
                scaleWidth = height;
            }else {
                y=(height - width)/2;
                x = 0;
                scaleHeight = width;
                scaleWidth = width;
            }
        }
        try {
            newbmp = Bitmap.createBitmap(bitmap, (int) x, (int) y, (int) scaleWidth, (int) scaleHeight, null, false);// createBitmap()方法中定义的参数x+width要小于或等于bitmap.getWidth()，y+height要小于或等于bitmap.getHeight()
            //bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }

    /**
     *
     * @param bm 图像
     * @param hue 色相
     * @param saturation 饱和度
     * @param lum 亮度
     * @return new bitmap
     */
    public static Bitmap handleImageEffect(final Bitmap bm, float hue, float saturation, float lum) {

        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);

        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue); // R
        hueMatrix.setRotate(1, hue); // G
        hueMatrix.setRotate(2, hue); // B

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lum, lum, lum, 1);

        //融合
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);

        return bmp;
    }

    /***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

    /**
     * 将图片变成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        int roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static BitmapFactory.Options getOptions(String path) {
        File mFile = new File(path);
        if (!mFile.exists()) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            return options;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 拍摄图片的完整路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }

    public static Bitmap getThumbnailBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {
        if (null == bitmap) {
            return bitmap;
        }
        Bitmap bmp = ThumbnailUtils.extractThumbnail(bitmap, targetWidth, targetHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bmp;
    }

    public static boolean bitmapUsable(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    /**
     * 圆角图片
     *
     * @param toTransform
     * @param radiusArray
     * @return
     */
    public static Bitmap roundRectBitmap(Bitmap toTransform, float[] radiusArray) {
        RectF srcR = new RectF(0, 0, toTransform.getWidth(), toTransform.getHeight());
        Path path = new Path();
        path.addRoundRect(srcR, radiusArray, Path.Direction.CW);

        Bitmap bitmap = Bitmap.createBitmap((int) srcR.width(), (int) srcR.height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawPath(path, paint);
        return bitmap;
    }

    /**
     * 设置四个角的圆角半径
     */
    public static float[] getRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        float[] radiusArray = new float[8];
        radiusArray[0] = leftTop;
        radiusArray[1] = leftTop;
        radiusArray[2] = rightTop;
        radiusArray[3] = rightTop;
        radiusArray[4] = rightBottom;
        radiusArray[5] = rightBottom;
        radiusArray[6] = leftBottom;
        radiusArray[7] = leftBottom;
        return radiusArray;
    }
	/**
	 * 图片叠加
	 * @param background
	 * @param foreground
	 * @return
	 */
    public static Bitmap superimposedBitmap(Bitmap background, Bitmap foreground) {
		if(!bitmapUsable(background) && !bitmapUsable(foreground)) {
			return null;
		}
		int bgWidth = 0;
		int bgHeight = 0;
		if (bitmapUsable(background)) {
			bgWidth = background.getWidth();
			bgHeight = background.getHeight();
		} else if (bitmapUsable(foreground)){
			bgWidth = background.getWidth();
			bgHeight = background.getHeight();
		} else {
			return null;
		}

		if (bgWidth <= 0 || bgHeight <= 0) {
			return null;
		}
		Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		if (bitmapUsable(background)) {
			cv.drawBitmap(background, 0, 0, null);
		}
		if (bitmapUsable(foreground)) {
			foreground = BitmapUtil.zoomImage(foreground, bgWidth, bgHeight);
			cv.drawBitmap(foreground, 0, 0, null);
		}
		cv.save();
		cv.restore();
		return newbmp;
	}


    /**
     * 融合图裁剪
     * @param big  底图
     * @param small 小图
     * @param xInRatio 小图在底图中的横坐标（相对于底图宽的比例）
     * @param yInRatio 小图在底图中的纵坐标（相对于底图高的比例）
     * @param alpha 小图的透明度 0~255
     * @return
     */
    public static Bitmap mergeWithCrop(Bitmap big,Bitmap small,float xInRatio,float yInRatio,int alpha){


        int smallW = small.getWidth();
        int smallH = small.getHeight();
        int bigW = big.getWidth();
        int bigH = big.getHeight();

        int smallX = (int)(bigW*xInRatio);
        int smallY = (int)(bigH*yInRatio);
        int bigX = 0;
        int bigY = 0;

        Rect sRect = new Rect(smallX, smallY, smallX+smallW, smallY+smallH);
        Rect bRect = new Rect(bigX, bigY, bigX+bigW, bigY+bigH);
        Rect overlap = intersect(sRect, bRect);

        int overlayW = overlap.right - overlap.left;
        int overlayH = overlap.bottom - overlap.top;

        //防止红外图完全覆盖可见光图时报错。
        if (overlap.left == 0 && overlap.top == 0){
            overlayW--;
            overlayH--;
        }

        Bitmap newBmp = Bitmap.createBitmap(big,overlap.left,overlap.top,overlayW,overlayH);
        big.recycle();

        Canvas canvas = new Canvas(newBmp);
        Paint paint = new Paint();
        //设置画笔为取交集模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setAlpha(alpha);
        canvas.drawBitmap(small, smallX >= 0 ? 0 : smallX, smallY >= 0 ? 0 : smallY, paint);
        small.recycle();

        return newBmp;
    }

    /**
     * 求矩形的重叠区域
     * @param r1
     * @param r2
     * @return
     */
    public static Rect intersect(Rect r1,Rect r2) {
        Rect r = new Rect();
        r.left = Math.max(r1.left, r2.left);
        r.top = Math.max(r1.top, r2.top);
        r.right = Math.min(r1.right, r2.right);
        r.bottom = Math.min(r1.bottom, r2.bottom);

        if (r.left > r.right || r.top > r.bottom)
            r = null;

        return r;
    }

    public static Bitmap compressBitmap(Bitmap bitmap, long sizeLimit) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        // 循环判断压缩后图片是否超过限制大小
        while(baos.toByteArray().length / 1024 > sizeLimit) {
            // 清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 10;
        }

        Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null);

        return newBitmap;
    }

    public static Bitmap base642bitmap(String base64) {
        int index = base64.indexOf(";base64,");
        if (index >= 0) {
            base64 = base64.substring(index + ";base64,".length(), base64.length());
        }
        byte[] iconBytes = Base64.decode(base64);
        return BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
    }

    /**
     * 合成多张图片，生成长图
     * @param width
     * @param bitmaps
     * @return
     */
    public static Bitmap compositeLongBitmap(int width, Bitmap... bitmaps) {
        if (bitmaps == null || width <= 0) {
            return null;
        }
        int height = 0;
        for (Bitmap bitmap : bitmaps) {
            if (bitmapUsable(bitmap)) {
                height += bitmap.getHeight() * width / bitmap.getWidth();
            }
        }
        if (height <= 0) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        Rect src = new Rect();
        Rect dst = new Rect();
        int curHeight = 0;
        for (Bitmap bitmap : bitmaps) {
            if (bitmapUsable(bitmap)) {
                int h = bitmap.getHeight() * width / bitmap.getWidth();
                src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                dst.set(0, curHeight, width, curHeight + h);
                cv.drawBitmap(bitmap, src, dst, null);
                curHeight += h;
            }
        }
        cv.save();
        cv.restore();
        return newBitmap;
    }

    /**
     * 图片居中显示
     */
    public static Bitmap createDefaultBitmap(int width, int height, Bitmap bitmap, int dw, int dh) {
        if (!bitmapUsable(bitmap) || width <= 0 || height <= 0 || width < dw || height < dh) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        Rect src = new Rect();
        Rect dst = new Rect();
        src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        int t = (width - dw) / 2;
        int l = (height - dh) / 2;
        dst.set(t, l, t + dw, l + dh);
        cv.drawBitmap(bitmap, src, dst, null);
        return newBitmap;
    }

    public static Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);
        return outBitmap;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    public static Bitmap changeBitmapColor(Bitmap toTransform, Map<Integer, Integer> colorMapping) {
        Bitmap src = toTransform;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 0; x < pixels.length; ++x) {
            pixels[x] = (colorMapping.containsKey(pixels[x])) ? colorMapping.get(pixels[x]) : pixels[x];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    public static Bitmap changeBitmapColorIgnoreAlpha(Bitmap toTransform, Map<Integer, Integer> colorMapping) {
        Bitmap src = toTransform;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 0; x < pixels.length; ++x) {
            int colorNoAlpha = ColorUtil.argbToRgb(pixels[x]);
            pixels[x] = colorMapping.containsKey(colorNoAlpha) ? ColorUtil.tranlateAlpha(pixels[x], colorMapping.get(colorNoAlpha)) : pixels[x];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 裁剪图片图片
     *
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        int rawW = bitmap.getWidth();
        int rawH = bitmap.getHeight();
        if (x >= 0 && y >= 0 && width > 0 && height > 0 && x < rawW && y < rawH && width <= rawW && height <= rawH) {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
            return newBitmap;
        }
        return bitmap;
    }


    /**
     * 纵向透明度渐变
     */
    public static Bitmap changeBitmapAlphaVerticalRange(Bitmap bitmap, float[] percents, float[] alphas) {
        return changeBitmapAlpha(bitmap, percents, alphas, DIRECTION_TOP_BOTTOM);
    }


    public static int DIRECTION_LEFT_RIGHT = 0;
    public static int DIRECTION_LEFTTOP_RIGHTBOTTOM = 1;
    public static int DIRECTION_TOP_BOTTOM = 2;
    public static int DIRECTION_RIGHTTOP_LEFTBOTTOM = 3;
    /**
     * 纵向透明度渐变
     */
    public static Bitmap changeBitmapAlpha(Bitmap bitmap, float[] percents, float[] alphas, int direction) {
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

        int currentLevel = 0;
        int currentStart = 0;
        int currentEnd = 0;
        float currentStartAlpha = 0;
        float currentEndAlpha = 0;

        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        float ratio = (float)width / height;
        int totalLength = 0;
        if (direction == DIRECTION_TOP_BOTTOM) {
            totalLength = height;
        } else if (direction == DIRECTION_LEFT_RIGHT) {
            totalLength = width;
        } else if (direction == DIRECTION_LEFTTOP_RIGHTBOTTOM
                || direction == DIRECTION_RIGHTTOP_LEFTBOTTOM) {
            totalLength = (int) Math.sqrt(width * width + height * height);
        }

        for (int x = 0; x < width; x++) {
            currentLevel = 0;
            for (int y = 0; y < height; y++) {
                int i = y * width + x;
                color = oldPx[i];

                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);
                a = Color.alpha(color);

                int distance = 0;
                if (direction == DIRECTION_TOP_BOTTOM) {
                    distance = y;
                } else if (direction == DIRECTION_LEFT_RIGHT) {
                    distance = x;
                } else if (direction == DIRECTION_LEFTTOP_RIGHTBOTTOM) {
                    distance = (int) Math.sqrt(x * x + y * y);
                } else if (direction == DIRECTION_RIGHTTOP_LEFTBOTTOM) {
                    distance = (int) Math.sqrt((width - x) * (width - x) + y * y);
                }

                if (percents.length > currentLevel + 1) {
                    currentStart = (int) (totalLength * percents[currentLevel]);
                    currentEnd = (int) (totalLength * percents[currentLevel + 1]);
                    currentStartAlpha = alphas[currentLevel];
                    currentEndAlpha = alphas[currentLevel + 1];

                    if (distance >= currentStart && distance <= currentEnd) {
                        int offset = distance - currentStart;
                        if (currentEnd - currentStart != 0) {
                            float offsetPercent = (float) offset / (currentEnd - currentStart);
                            float targetAlpha = currentStartAlpha + offsetPercent * (currentEndAlpha - currentStartAlpha);

                            a = (int)(a * targetAlpha);
                            if (a > 255) {
                                a = 255;
                            } else if (a < 0) {
                                a = 0;
                            }
                        }
                    }
                    if (distance == currentEnd) {
                        currentLevel++;
                    }
                }

                color = Color.argb(a, r, g, b);
                newPx[i] = color;
            }
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    /**
     * 纵向混合颜色
     */
    public static Bitmap changeBitmapMixColorVerticalRange(Bitmap bitmap, float[] percents, int[] colors, boolean useBackAlpha) {
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

        int currentLevel = 0;
        int currentStartY = 0;
        int currentEndY = 0;

        int currentStartA= 0;
        int currentStartR= 0;
        int currentStartG= 0;
        int currentStartB= 0;
        int currentEndA = 0;
        int currentEndR = 0;
        int currentEndG = 0;
        int currentEndB = 0;

        bitmap.getPixels(oldPx, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            currentLevel = 0;
            for (int y = 0; y < height; y++) {
                int i = y * width + x;
                color = oldPx[i];

                a = Color.alpha(color);

                int foreColor = color;

                if (percents.length > currentLevel + 1) {
                    currentStartY = (int)(height * percents[currentLevel]);
                    currentEndY = (int) (height * percents[currentLevel + 1]);

                    int currentStartColor = colors[currentLevel];
                    int currentEndColor = colors[currentLevel + 1];

                    currentStartA = Color.alpha(currentStartColor);
                    currentEndA = Color.alpha(currentEndColor);
                    currentStartR = Color.red(currentStartColor);
                    currentEndR = Color.red(currentEndColor);
                    currentStartG = Color.green(currentStartColor);
                    currentEndG = Color.green(currentEndColor);
                    currentStartB = Color.blue(currentStartColor);
                    currentEndB = Color.blue(currentEndColor);

                    if (y >= currentStartY && y <= currentEndY) {
                        int offsetY = y - currentStartY;
                        if (currentEndY - currentStartY != 0) {
                            float offsetPercent = (float) offsetY / (currentEndY - currentStartY);
                            int targetA = currentStartA + (int)(offsetPercent * (currentEndA - currentStartA));
                            int targetR = currentStartR + (int)(offsetPercent * (currentEndR - currentStartR));
                            int targetG = currentStartG + (int)(offsetPercent * (currentEndG - currentStartG));
                            int targetB = currentStartB + (int)(offsetPercent * (currentEndB - currentStartB));
                            foreColor = Color.argb(useBackAlpha ? a : targetA, targetR, targetG, targetB);
                        }
                    }
                    if (y == currentEndY) {
                        currentLevel++;
                    }
                }

                color = ColorUtils.compositeColors(foreColor, color);
                newPx[i] = color;
            }
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    // 获取像素最多的
    public static int getMostPopulateColorInBitmap(Bitmap bitmap){
        Palette palette = Palette.generate(bitmap);
        List<Palette.Swatch> swatchList = palette.getSwatches();
        int lastBigIndex = 0;
        int lastBigPopulation = 0;

        for (int i = 0; i < swatchList.size(); i++) {
            Palette.Swatch swatch = swatchList.get(i);
            int population = swatch.getPopulation();
            if (population > lastBigPopulation) {
                lastBigPopulation = population;
                lastBigIndex = i;
            }
        }
        return swatchList.size() > 0 ? swatchList.get(lastBigIndex).getRgb() : Color.WHITE;
    }

    public static Bitmap whiterBitmap(Bitmap toTransform, float whiter) {
        if (toTransform == null) {
            return null;
        }
        Bitmap src = toTransform;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 0; x < pixels.length; x++) {
            float[] hsl = new float[3];
            ColorUtils.colorToHSL(pixels[x], hsl);
            hsl[2] = hsl[2] * whiter;
            if (hsl[2] > 1f) {
                hsl[2] = 1f;
            }
            pixels[x] = ColorUtils.HSLToColor(hsl);
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            if (DrLog.DEBUG){
                DrLog.e("BitmapUtil", "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            }
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap,boolean isCenter) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            if (DrLog.DEBUG){
                DrLog.e("BitmapUtil", "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            }
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int did) {
        return BitmapFactory.decodeResource(context.getResources(), did);
    }

    public static Bitmap getBitmap(@DrawableRes int did) {
        return getBitmap(DRCommonApplication.getContext(), did);
    }
}
