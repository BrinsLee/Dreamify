
package com.brins.commom.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bitmap创建类
 *
 * @author chenys
 */
public class KGBitmapUtil {

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
                if (DrLog.DEBUG) DrLog.printException("torahlog", e);
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
                } catch (OutOfMemoryError e1) {
                    if (DrLog.DEBUG) DrLog.printException("torahlog", e1);
                }
            } catch (Exception e2) {
                if (DrLog.DEBUG) DrLog.printException("torahlog", e2);
            }
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    public static Bitmap createBitmap2(Bitmap source, int x, int y, int width, int height, Matrix m,
                                       boolean filter) {
        Bitmap bitmap = null;
        if (source != null && !source.isRecycled()) {
            try {
                bitmap = Bitmap.createBitmap(source, x, y, width, height, m, filter);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(source, x, y, width, height, m, filter);
                } catch (OutOfMemoryError e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
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
        return decodeFile(pathName, false);
    }

    public static Bitmap decodeFile(String pathName, boolean isNullEnable) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            InputStream is = null;
            try {
                try {
                    is = FileUtil.getFileInputStream(pathName);
                    bitmap = decodeStream(is);
                } finally {
                    if (is != null) {
                        is.close();
                    } else {
                        bitmap = null;
                    }
                }
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    try {
                        is = FileUtil.getFileInputStream(pathName);
                        bitmap = decodeStream(is);
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

            if (bitmap == null && !isNullEnable) {
                //路径存在且又读取不到的情况(也可能是图片解码失败，请注意区分) 本地debug包抛出异常
                if (DrLog.isDebug()) {
                    KGAssert.fail("请检查是否在无存储权限下，使用了外部路径。");
                }
            }
        }
        if (bitmap == null && !isNullEnable) {
            try {
                bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
            } catch (OutOfMemoryError oom) {
                oom.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap decodeFile(String pathName, int width, int height) {
        return decodeFile(pathName, width, height, false);
    }

    public static Bitmap decodeFile(String pathName, int width, int height, boolean isDftRGB565) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, width * height);
            if (isDftRGB565) {
                opts.inPreferredConfig = Config.RGB_565;
            }
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
        }else
            if (DrLog.DEBUG) DrLog.i("torahlog","文件不存在");
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
    }

    /**
     * 创建图片失败返回null
     *
     * @param pathName
     * @return
     */
    public static Bitmap decodeFileMayNull(String pathName) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            InputStream is = FileUtil.getFileInputStream(pathName);
            bitmap = decodeStream(is);
        }
        return bitmap;
    }

    public static Bitmap decodeFileMemory(String pathName) {
        Bitmap bitmap = null;
        if (FileUtil.isExist(pathName)) {
            InputStream is = FileUtil.getFileInputStream(pathName);
            bitmap = decodeStreamMemory(is);
        }
        return bitmap;
    }

    public static Bitmap decodeResource(Context context, Resources res, int id) {
        Bitmap bitmap = null;
        try {
            InputStream is = context.getResources().openRawResource(id);
            bitmap = decodeStream(is);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeResource(Resources res, int resId, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, width * height);
        opts.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeResource(res, resId, opts);
        } catch (OutOfMemoryError e) {
            System.gc();
            try {
                bitmap = BitmapFactory.decodeResource(res, resId, opts);
            } catch (OutOfMemoryError e1) {
            }
        } catch (Exception e2) {
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        }
        return bitmap;
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
            try{
                bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
            }catch(OutOfMemoryError oom){
                System.gc();
                try {
                    bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
                } catch (OutOfMemoryError oom2) {
                }
            }catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     *
     * @param is 注意：不支持reset()的流，不适合调用该方法;
     * @param options
     * @return
     */
    private static Bitmap decodeBitmap_TryTwice(InputStream is, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        if (is != null) {
            try {
                bitmap = BitmapFactory.decodeStream(is, null, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    is.reset();//有些流不支持reset方法
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    bitmap = BitmapFactory.decodeStream(is, null, options);
                } catch (OutOfMemoryError e1) {
                    e1.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static Bitmap decodeNetStream(InputStream is) {
        return decodeBitmap_TryTwice(is, null);
    }

    /**
     * @param is         注意：不支持reset()的流，调用该方法获得不到对象。提别提示：FileInputStream不行
     * @param recommandW
     * @param recommandH
     * @param needRecord 如果所给资源超出推荐值，是否上报
     * @return
     */
    public static Bitmap decodeNetStream(InputStream is, int recommandW, int recommandH, boolean needRecord) {
        if (is == null) {
            return null;
        }
        if (recommandW <= 0 || recommandH <= 0) {
            return decodeNetStream(is);
        }
        //1.判断图片是否超标。
        //  不超标，正常解析
        //超标
        //  1.是否做统计
        //  2.精简后使用
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeBitmap_TryTwice(is, options);
            if (options.outHeight > 0 && options.outWidth > 0) {//有效尺寸
                try {
                    is.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (options.outHeight <= recommandH && options.outWidth <= recommandW) {//合格尺寸
                    return decodeBitmap_TryTwice(is, null);
                } else {
                    options.inSampleSize = calculateInSampleSize(options, recommandW, recommandH);
                    options.inJustDecodeBounds = false;
                    return decodeBitmap_TryTwice(is, options);
                }
            } else {//无效尺寸
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap decodeStreamMemory(InputStream is) {
        Bitmap bitmap = null;
        if (is != null) {
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (OutOfMemoryError e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 判断这个图片是不是幽灵图片
     *
     * @param bitmap
     * @return
     */
    public static boolean isGhostBitmap(Bitmap bitmap) {
        // 如果这个bitmap是这样创建的：Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        if (bitmap != null && bitmap.getWidth() == 1 && bitmap.getHeight() == 1
                && bitmap.getConfig() == Config.ALPHA_8) {
            return true;
        }
        return false;
    }

    public static boolean isLegalBmp(Bitmap bmp) {
        return null != bmp && !bmp.isRecycled()
                && !isGhostBitmap(bmp);
    }

    /**
     * 加载图片
     *
     * @param res
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources res, int id) {
        Bitmap bitmap = null;
        try {
            Context context = DRCommonApplication.getContext();
            InputStream is = context.getResources().openRawResource(id);
            bitmap = decodeStream(is);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 解码压缩一个图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
            int reqHeight) {
        Bitmap bitmap = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeResource(res, resId, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 计算sampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            inSampleSize *= 2;
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmapZoomByWidth(Bitmap bmp, int width) {
        float scale = 1.0f * width / bmp.getWidth();
        int height = (int) (scale * bmp.getHeight());
        bmp = ImageUtil.zoomBitmap(bmp, width, height);
        return bmp;
    }
}
