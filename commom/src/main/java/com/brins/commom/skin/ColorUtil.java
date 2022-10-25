package com.brins.commom.skin;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinColorLib;
import com.brins.commom.utils.ImageUtil;
import com.kugou.skinlib.utils.KGSkinColorUtil;
import com.kugou.uilib.widget.imageview.delegate.palette.Palette;

/**
 * Created by ericpeng on 2015/10/29.
 */
public class ColorUtil {

    public static int getAlphaColor(int color, float alpha) {
        return KGSkinColorUtil.getReplacedAlphaColor(color,alpha);
    }

    public static int getHybridAlphaColor(int color, float alpha) {
        return KGSkinColorUtil.getHybridAlphaColor(color,alpha);
    }

    public static final int MASK_ALPHA = 0xFF000000;

    public static final int MASK_NO_ALPHA = 0x00FFFFFF;

    public static int argbToRgb(int argb) {
        return argb & MASK_NO_ALPHA;
    }

    public static int tranlateAlpha(int src, int tar) {
        return (tar & MASK_NO_ALPHA) | (src & MASK_ALPHA);
    }

    @ColorInt
    public static int blendARGB(@ColorInt int[] colors, @FloatRange(from = 0.0D, to = 1.0D) float[] ratios) {
        if (colors == null || ratios == null || colors.length != ratios.length) {
            return Color.TRANSPARENT;
        }
        float a = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            float ratio = ratios[i];
            a += (float) Color.alpha(color) * ratio;
            r += (float) Color.red(color) * ratio;
            g += (float) Color.green(color) * ratio;
            b += (float) Color.blue(color)  * ratio;
        }
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    /**
     * @param alpha1 0.0 - 1.0
     * @param alpha2 0.0 - 1.0
     * @return
     */
    public static int mixColor(int color1, int color2, float alpha1, float alpha2) {
        return (int) ((color1 * alpha1 * (1 - alpha2) + color2 * alpha2) / (alpha1 + alpha2 - alpha1 * alpha2));
    }

    public static int getSC_Color(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = Math.max(0.2f, hsv[1] - 0.6f);
        hsv[2] = 1.0f;
        return Color.HSVToColor(hsv);
    }

    public static int getSC_Color() {
        int CC_Color = SkinResourcesUtils.getInstance().getColor(SkinColorType.COMMON_WIDGET);
        return getSC_Color(CC_Color);
    }
    //根据color id 获取颜色16进制值
    public static String changeColor(int color){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("#");
        String alpha = Integer.toHexString(Color.alpha(color));
        alpha = alpha.length() < 2 ? ("0" + alpha) : alpha;
        stringBuffer.append(alpha);
        String red = Integer.toHexString(Color.red(color));
        red = red.length() < 2 ? ("0" + red) : red;
        stringBuffer.append(red);
        String green = Integer.toHexString(Color.green(color));
        green = green.length() < 2 ? ("0" + green) : green;
        stringBuffer.append(green);
        String blue = Integer.toHexString(Color.blue(color));
        blue = blue.length() < 2 ? ("0" + blue) : blue;
        stringBuffer.append(blue);
        return stringBuffer.toString();
    }

    /**
     * 返回色值表中的位置
     * @param bitmap
     * @return
     */
    public static int getBitmapColor(Bitmap bitmap) {
        int imageColor;
        Palette.Swatch vibrantSwatch = Palette.generate(bitmap).getVibrantSwatch();
        if (null == vibrantSwatch)
            vibrantSwatch = Palette.generate(bitmap).getDarkVibrantSwatch();
        if (null == vibrantSwatch)
            imageColor = ImageUtil.matchColorByImage(bitmap, ImageUtil.SkinColor);
        else
            imageColor = ImageUtil.matchNearColor(vibrantSwatch.getRgb(), ImageUtil.SkinColor);
        for (int i = 0; i < SkinColorLib.colors.length; i++) {
            if (imageColor == SkinColorLib.colors[i]) {
                return i;   //0为自选色
            }
        }
        return 0;
    }

    public static String colorInt2HexString(int color){
        return "#"+Integer.toHexString(color).substring(2);
    }

    public static int getProgressColor(float progress,int startColor,int endColor){
        int startInt = (Integer) startColor;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endColor;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(progress * (endA - startA))) << 24) |
                (int)((startR + (int)(progress * (endR - startR))) << 16) |
                (int)((startG + (int)(progress * (endG - startG))) << 8) |
                (int)((startB + (int)(progress * (endB - startB))));
    }

    public static String toHexEncoding(int color) {
        return KGSkinColorUtil.toHexEncoding(color);
    }

    /**
     * 改变颜色的Alpha值
     *
     * @param RGBValues
     * @param alpha
     * @return
     */
    public static int colorWithAlpha(int RGBValues, int alpha) {
//        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        return Color.argb(alpha, red, green, blue);
    }

    public static boolean isLightColor(int color) {
        double darkness = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color);
        if (darkness >= 100) {  //100是UI给的，好像跟网上提供的有出入
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }

    public static float getAlphaByDarkness(int color) {
        double darkness = 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color);
        if (darkness <= 90) {
            return 0f;
        } else if (darkness <= 128) {
            return 0.01f;
        } else {
            return 0.02f;
        }
    }
}
