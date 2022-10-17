package com.brins.commom.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.widget.SeekBar;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.constant.EmptyConstants;
import com.brins.commom.entity.ScreenSizeType;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.preference.AbstractSharedPreference;
import com.brins.commom.utils.DrawableUtil;
import com.brins.commom.utils.FileUtil;
import com.brins.commom.utils.GlobalEnv;
import com.brins.commom.utils.ImageUtil;
import com.brins.commom.utils.SystemUtils;
import java.util.ArrayList;

/**
 * 皮肤相关设置
 *
 * @author heyangbin
 * @version 2012-3-5下午7:49:47
 */
public class SkinSetting {

    /**
     * 非播放界面背景皮肤
     */
    public final static int sThemeBg[][] = {new int[]{
            R.drawable.skin_player_bg, R.drawable.skin_player_bg,
            R.drawable.skin_player_bg}};

    /**
     * 默认皮肤颜色（蓝红两套）
     */
    // public final static int sThemeColor[] = {
    // R.color.theme_color_0_default, R.color.theme_color_1_default,
    // R.color.theme_color_2_default, R.color.theme_color_3_default,
    // R.color.theme_color_4_default, R.color.theme_color_5_default,
    // R.color.theme_color_6_default
    // };
    //
    public final static int sThemeColor[] = {
            R.color.theme_skin_color_0_default,
            R.color.theme_skin_color_1_default,
            R.color.theme_skin_color_2_default,
            R.color.theme_skin_color_3_default,
            R.color.theme_skin_color_4_default,
            R.color.theme_skin_color_5_default,
            R.color.theme_skin_color_6_default,
            R.color.theme_skin_color_7_default,
            R.color.theme_skin_color_8_default,
            R.color.theme_skin_color_9_default,
            R.color.theme_skin_color_10_default,
            R.color.theme_skin_color_11_default,
            R.color.theme_skin_color_12_default,
            R.color.theme_skin_color_13_default,
            R.color.theme_skin_color_14_default,
            R.color.theme_skin_color_15_default,
            R.color.theme_skin_color_16_default,
            R.color.theme_skin_color_17_default,
            R.color.theme_skin_color_18_default,
            R.color.theme_skin_color_19_default,
            R.color.theme_skin_color_20_default,
            R.color.theme_skin_color_21_default};

    public final static int sThemeClassListSelector[] = {
            R.drawable.theme_class_color_list_0,
            R.drawable.theme_class_color_list_1};

    /**
     * 蓝色皮肤 *
     */
    public static final int SKIN_BLUE = 1;

    /**
     * 粉红色皮肤 *
     */
    public static final int SKIN_PINK = 2;

    /**
     * 浅色皮肤 *
     */
    public static final int SKIN_LOW = 3;

    /**
     * 深色皮肤 *
     */
    public static final int SKIN_HIGH = 4;

    /**
     * 浅色皮肤背景的蒙层 *
     */
    private static final String LOW_TRANSPARENT_BG = "#B2FFFFFF";

    /**
     * 深色皮肤背景的蒙层 *
     */
    private static final String HIGH_TRANSPARENT_BG = "#99000000";

    /**
     * 获取自定义背景图资源图片（有加蒙层， 深色皮肤配70%透明白色蒙层， 浅色皮肤配70%透明黑色蒙层）
     *
     * @param context
     * @return
     */
    public static Drawable getBgResDrawable(Context context) {
        Drawable drawable = null;
        boolean isUseCustomBg = isUseCustomBg(context);
        boolean isUserDefaultBg = isUseDefaultBg(context);
        boolean isUseDownloadBg = isUseDownloadBg(context);
        if (isUseCustomBg || isUserDefaultBg || isUseDownloadBg) {
            if (isUseCustomBg) {
                drawable = DrawableUtil
                        .createDrawableFromPath(getCustomBg(context));
            } else if (isUseDownloadBg) {
                drawable = DrawableUtil
                        .createDrawableFromPath(getDownloadBg(context));
            } else {
                drawable = getDefaultBgDrawable(context);
            }
            if (drawable != null) {
            } else {
                // 可能图片被删除，此时将背景图还原为默认
                restoreDefaultSkinBackground(context);
                drawable = DrawableUtil.createDrawableFromResId(context,
                        getDefaultBgResId(context));
            }
        } else {
            drawable = DrawableUtil.createDrawableFromResId(context,
                    getDefaultBgResId(context));
        }
        return drawable;
    }

    public static Bitmap getBgResBitmap(Context context) {
        Bitmap bitmap = null;
        boolean isUseCustomBg = isUseCustomBg(context);
        boolean isUserDefaultBg = isUseDefaultBg(context);
        boolean isUseDownloadBg = isUseDownloadBg(context);
        if (isUseCustomBg || isUserDefaultBg || isUseDownloadBg) {
            if (isUseCustomBg) {
                bitmap = DrawableUtil
                        .createBitmapFromPath(getCustomBg(context));
            } else if (isUseDownloadBg) {
                bitmap = DrawableUtil
                        .createBitmapFromPath(getDownloadBg(context));
            } else {
                bitmap = getDefaultBgBitmap(context);
            }
            if (bitmap != null) {
            } else {
                // 可能图片被删除，此时将背景图还原为默认
                restoreDefaultSkinBackground(context);
                bitmap = DrawableUtil.createBitmapFromResId(context,
                        getDefaultBgResId(context));
            }
        } else {
            bitmap = DrawableUtil.createBitmapFromResId(context,
                    getDefaultBgResId(context));
        }
        return bitmap;
    }

    public static String getBgResPath(Context context) {
        boolean isUseCustomBg = isUseCustomBg(context);
        boolean isUseDownloadBg = isUseDownloadBg(context);
        if (isUseCustomBg) {
            return getCustomBg(context);
        } else if (isUseDownloadBg) {
            return getDownloadBg(context);
        }
        return null;
    }

    /**
     * 获取播放界面蒙层
     *
     * @param context
     * @return
     */
    public static Drawable getTransparentBg(Context context) {
        boolean isUseCustomBg = isUseCustomBg(context);
        boolean isUserDefaultBg = isUseDefaultBg(context);
        if (isUseCustomBg || isUserDefaultBg) {
            if (getSkinBrightness(context) == SKIN_BRIGHTNESS_LOW) {
                /**
                 * 深色皮肤配白色蒙层(80%透明)
                 */
                return new ColorDrawable(Color.parseColor(LOW_TRANSPARENT_BG));
            } else {
                /**
                 * 浅色皮肤配黑色蒙层(70%透明)
                 */
                return new ColorDrawable(Color.parseColor(HIGH_TRANSPARENT_BG));
            }
        } else {
            return null;
        }

    }

    /**
     * 获取分类的那些色块的背景（加蒙层）
     *
     * @param context
     * @param drawable
     * @return
     * @deprecated 无使用
     */
    public static Drawable getLessTransparentBg(Context context,
                                                Drawable drawable) {
        if (drawable == null) {
            return drawable;
        }
        Drawable bg = null;
        boolean isUseCustomBg = isUseCustomBg(context);
        boolean isUserDefaultBg = isUseDefaultBg(context);
        if (isUseCustomBg || isUserDefaultBg) {
            bg = new LayerDrawable(new Drawable[]{drawable,
                    new ColorDrawable(Color.parseColor("#22000000"))});
        }
        return bg;
    }

    /**
     * 获取默认皮肤背景
     *
     * @param context
     * @return
     */
    private static Drawable getDefaultBgDrawable(Context context) {
        return DrawableUtil.createDrawableFromResId(context,
                getDefaultBgResId(context));
    }

    private static Bitmap getDefaultBgBitmap(Context context) {
        return DrawableUtil.createBitmapFromResId(context,
                getDefaultBgResId(context));
    }

    /**
     * 获取高亮背景图片资源
     *
     * @param context
     * @return
     */
    public static Drawable getTransBgResDrawable(Context context) {
        return new LayerDrawable(new Drawable[]{getBgResDrawable(context),
                new ColorDrawable(Color.parseColor("#E5F5F5F5"))});
    }

    /**
     * 获取6.0下载背景图路径集合
     *
     * @param context
     * @return
     */
    public static String[] getDownloadBgArray(Context context) {
        String[] bgs = new String[mDownBgFileName.length];
        for (int i = 0; i < mDownBgFileName.length; i++) {
            bgs[i] = GlobalEnv.IMAGE_CUSTOM_BG_FOLDER_V6 + mDownBgFileName[i];
        }

        return bgs;
    }

    /**
     * 6.0下载背景图路径集合
     */
    public static final String mDownBgFileName[] = {
            "bg_skin_download_new1.jpg", "bg_skin_download_new2.jpg",
            "bg_skin_download_new3.jpg", "bg_skin_download_new4.jpg",
            "bg_skin_download_new5.jpg", "bg_skin_download_new6.jpg",
            "bg_skin_download_0.jpg", "bg_skin_download_1.jpg",
            "bg_skin_download_2.jpg", "bg_skin_download_3.jpg",
            "bg_skin_download_4.jpg", "bg_skin_download_5.jpg",
            "bg_skin_download_6.jpg",};

    /**
     * 获取7.0待下载背景图路径集合
     *
     * @param context
     * @return
     */
    public static String[] getNewDownloadBgArray(Context context) {
        String[] bgs = new String[mNewDownBgFileName.length];
        for (int i = 0; i < mNewDownBgFileName.length; i++) {
            bgs[i] = GlobalEnv.IMAGE_CUSTOM_BG_FOLDER_V6
                    + mNewDownBgFileName[i];
        }

        return bgs;
    }

    private static String mOnlineArrayStr;

    private static Object[] mNameArrayLocker = new Object[0];
    
    private static String mColorArray;

    private static Object[] mColorLocker = new Object[0];
    

    /**
     * 7.0下载背景图路径集合
     */
    public static final String mNewDownBgFileName[] = {"bg_android_v7_1.jpg",
            "bg_android_v7_2.jpg", "bg_android_v7_3.jpg",
            "bg_android_v7_4.jpg", "bg_android_v7_5.jpg",
            "bg_android_v7_6.jpg", "bg_android_v7_7.jpg",
            "bg_android_v7_8.jpg", "bg_android_v7_9.jpg",
            "bg_android_v7_10.jpg", "bg_android_v7_11.jpg",
            "bg_android_v7_12.jpg", "bg_android_v7_13.jpg",
            "bg_android_v7_14.jpg", "bg_android_v7_15.jpg",
            "bg_android_v7_16.jpg", "bg_android_v7_17.jpg",
            "bg_android_v7_18.jpg", "bg_android_v7_19.jpg"};

    public static void clearDownloadBgJPG(Context context) {
        String[] bgs = getDownloadBgArray(context);
        for (String path : bgs) {
            FileUtil.deleteFile(path);
        }
    }

    // ***********************下面是颜色相关设置*******************************

    /**
     * 按下状态的蒙层
     */
    public static final String PRESSED_LAYER_COLOR = "#19000000";

    /**
     * 获取有按下State颜色（按钮等）
     *
     * @param context
     * @return
     */
    public static Drawable getStateColorResDrawable(Context context) {
        Drawable drawable = null;

        int defaultColor = getSkinColor(context);
        LayerDrawable defaultCD;
        LayerDrawable pressedCD;
        pressedCD = new LayerDrawable(new Drawable[]{
                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{defaultColor, defaultColor}),
                new ColorDrawable(Color.parseColor(PRESSED_LAYER_COLOR))});
        defaultCD = new LayerDrawable(new Drawable[]{new ColorDrawable(
                defaultColor)});
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }

    /**
     * 获取列表Selector图片
     *
     * @param context
     * @return
     */
    public static Drawable getListViewSelectorDrawable(Context context) {
        Drawable drawable = null;
        // int pressedColor = getSkinColor(context);
        int pressedColor = context.getResources().getColor(
                R.color.listview_item_press_color);
        int defaultColor = context.getResources().getColor(R.color.transparent);

        LayerDrawable defaultCD = new LayerDrawable(new Drawable[]{
                new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{defaultColor, defaultColor}),
                new ColorDrawable(defaultColor)});
        LayerDrawable pressedCD = new LayerDrawable(
                new Drawable[]{new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                        pressedColor, pressedColor})});

        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }

    /**
     * 获取电台分类列表Selector图片
     *
     * @param context
     * @return
     */
    public static Drawable getClassListViewSelectorDrawable(Context context) {
        if (getSkinBrightness(context) == SKIN_BRIGHTNESS_HIGH) {
            return context.getResources().getDrawable(
                    sThemeClassListSelector[1]);
        } else {
            return context.getResources().getDrawable(
                    sThemeClassListSelector[0]);
        }
    }

    /**
     * 获取色块bar条按钮背景
     *
     * @param context
     * @return
     */
    public static Drawable getTitleButtonStateColorResDrawable(Context context) {
        Drawable drawable = null;
        int defaultColor;
        int transparentColor;
        int pressedTransparentColor;
        defaultColor = getSkinColor(context);
        transparentColor = Color.parseColor("#4CFFFFFF");
        pressedTransparentColor = Color.parseColor("#99FFFFFF");
        LayerDrawable defaultCD = new LayerDrawable(new Drawable[]{
                new ColorDrawable(defaultColor),
                new ColorDrawable(transparentColor)});
        LayerDrawable pressedCD = new LayerDrawable(new Drawable[]{
                new ColorDrawable(defaultColor),
                new ColorDrawable(pressedTransparentColor)});
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }

    public static Drawable getDialogButtonStateColorResDrawable(Context context) {
        Drawable drawable = null;
        int defaultColor;
        int transparentColor;
        defaultColor = getSkinColor(context);
        transparentColor = Color.parseColor("#B2FFFFFF");
        LayerDrawable defaultCD = new LayerDrawable(new Drawable[]{
                new ColorDrawable(defaultColor),
                new ColorDrawable(transparentColor)});
        LayerDrawable pressedCD = new LayerDrawable(
                new Drawable[]{new ColorDrawable(defaultColor)});
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }

    /**
     * 获取颜色
     *
     * @param context
     * @return
     */
    public static Drawable getColorResDrawable(Context context) {
        return new ColorDrawable(getSkinColor(context));
    }

    /**
     * @param context
     * @return
     * @deprecated
     */
    public static int getHighLightColor(Context context) {
        return Color.parseColor(getSkinHighLightColor(context));
    }

    /**
     * 获取字母控件的按下背景颜色
     *
     * @param context
     * @return
     */
    public static int getLetterViewSelectedColor(Context context) {
        StringBuffer color = new StringBuffer();
        String defaultColor = getSkinColorStr(context);
        String transparent = "CC";
        if (defaultColor.length() == 9) {
            color.append(defaultColor.substring(0, 1));
            color.append(transparent);
            color.append(defaultColor.substring(3, defaultColor.length()));
        } else {
            color.append(defaultColor.substring(0, 1));
            color.append(transparent);
            color.append(defaultColor.substring(1, defaultColor.length()));
        }
        return Color.parseColor(color.toString());
    }

    /**
     * 获取主题色的半透明效果
     *
     * @param context
     * @return
     */
    public static int getTranslucentSkinColor(Context context) {
        StringBuffer color = new StringBuffer();
        String defaultColor = getSkinColorStr(context);
        String transparent = "80";
        if (defaultColor.length() == 9) {
            color.append(defaultColor.substring(0, 1));
            color.append(transparent);
            color.append(defaultColor.substring(3, defaultColor.length()));
        } else {
            color.append(defaultColor.substring(0, 1));
            color.append(transparent);
            color.append(defaultColor.substring(1, defaultColor.length()));
        }
        return Color.parseColor(color.toString());
    }

    /**
     * 自定义颜色个数
     */
    public static final int CUSTOM_COLOR_COUNT = 3;

    /**
     * 自定义的默认颜色
     */
    public static final String DEFAULT_CUSTOM_COLOR = "#60000000";

    /**
     * 若选项为酷狗默认的皮肤，则用此方法设置
     *
     * @param context
     * @param index
     * @return
     */
    public static boolean setDefaultSkinBackground(Context context, int index) {
        if (index >= 0 && index < sThemeBg.length) {
            setUseDeafaultBg(context, false);

            setUseCustomBg(context, false);
            setUseDownloadBg(context, false);
            setCustomBg(context, "");
            setDownloadBg(context, "");
            setDefaultBgResIndex(context, index);
            return true;
        }
        return false;
    }

    /**
     * 酷狗提供下载的皮肤设置
     *
     * @param context
     * @param path    背景图路径
     * @return
     */
    public static boolean setDownloadSkinBackground(Context context, String path) {

        setUseCustomBg(context, false);
        setUseDeafaultBg(context, false);
        setDefaultBgResIndex(context, -1);
        setCustomBg(context, "");

        setUseDownloadBg(context, true);
        setDownloadBg(context, path);
        return true;
    }

    /**
     * 用户自定义皮肤设置
     *
     * @param context
     * @param path    背景图路径
     * @return
     */
    public static boolean setUserCustomSkin(Context context, String path) {
        setUseCustomBg(context, true);
        setCustomBg(context, path);

        setUseDeafaultBg(context, false);
        setUseDownloadBg(context, false);
        setDownloadBg(context, "");

        return true;
    }

    /**
     * 恢复默认皮肤背景
     *
     * @param context
     */
    public static void restoreDefaultSkinBackground(Context context) {

        setUseDeafaultBg(context, false);
        setUseDownloadBg(context, false);
        setUseCustomBg(context, false);
        setDefaultBgResIndex(context, 0);
        setCustomBg(context, "");
        setDownloadBg(context, "");

    }

    public static void restoreDefaultSkinColor(Context context) {
        setSkinColorIndexInner(context, 0);
    }

    /**
     * 获取按钮的背景（按钮默认背景透明，按下状态为色块，如导航栏上的登录按钮，列表弹出菜单的按下颜色）
     *
     * @param context
     * @return
     */
    public static Drawable getTitleButtonDrawable(Context context) {
        Drawable drawable = null;
        int defaultColor = Color.TRANSPARENT;
        int pressedColor = getSkinColor(context);
        Drawable defaultCD;
        LayerDrawable pressedCD;
        defaultCD = new ColorDrawable(defaultColor);
        pressedCD = new LayerDrawable(new Drawable[]{new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                pressedColor, pressedColor})});
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;

    }

    public static Drawable getPlayingBarButtonDrawable(Context context) {
        return getTitleButtonDrawable(context);
    }

    
    public static Drawable getNT30Drawable(){
        Drawable drawable = null;
        int defaultColor = Color.TRANSPARENT;
        int pressedColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.COMMON_WIDGET);
        GradientDrawable defaultCD;
        GradientDrawable pressedCD;
        defaultCD = new GradientDrawable();
        defaultCD.setColor(defaultColor);
        defaultCD.setCornerRadius(8);
        defaultCD.setStroke(1, SkinResourcesUtils.getInstance().getColor(SkinColorType.PRIMARY_DISABLE_TEXT));
        pressedCD = new GradientDrawable();
        pressedCD.setColor(pressedColor);
        pressedCD.setCornerRadius(8);
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }

    public static Drawable getFxTypeDrawable(){
        Drawable drawable = null;
        int defaultColor = Color.TRANSPARENT;
        int pressedColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.COMMON_WIDGET);
        GradientDrawable defaultCD;
        GradientDrawable pressedCD;
        defaultCD = new GradientDrawable();
        defaultCD.setColor(defaultColor);
        defaultCD.setCornerRadius(SystemUtils.dip2px(DRCommonApplication.getContext(),4));
        defaultCD.setStroke(SystemUtils.dip2px(DRCommonApplication.getContext(),1), SkinResourcesUtils.getInstance().getColor(SkinColorType.COMMON_WIDGET));
        pressedCD = new GradientDrawable();
        pressedCD.setColor(pressedColor);
        pressedCD.setCornerRadius(SystemUtils.dip2px(DRCommonApplication.getContext(),4));
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        return drawable;
    }
    /**
     * 判断是否是深色皮肤
     *
     * @param context
     * @return false为浅色皮肤，true为深色皮肤
     */
    public static boolean isHighSkin(Context context) {
        // return getSkinBrightness(context) == SKIN_BRIGHTNESS_HIGH;
        return false;
    }

    /**
     * 获取皮肤的颜色类型
     *
     * @param context
     * @return <ul>
     * <li>SKIN_BULE 蓝色皮肤</li>
     * <li>SKIN_PINK 粉红色皮肤</li>
     * <li>SKIN_LOW 浅色皮肤（不包含蓝色和粉红在内）</li>
     * <li>SKIN_HIGH 深色皮肤（不包含蓝色和粉红在内）</li>
     * </ul>
     */
    public static int getSkinColorType(Context context) {
        // if (isUseCustomColor(context)) {
        // return isHighSkin(context) ? SKIN_HIGH : SKIN_LOW;
        // } else {
        // int index = getStateColorIndex();
        // if (index == 1) {
        // return SKIN_PINK;
        // } else {
        // return SKIN_BLUE;
        // }
        // }
        return SKIN_LOW;
    }

    /**
     * user just like : calculateRGB("#b5b5b5", 0.3)
     *
     * @param rgbColor     颜色值
     * @param transparency 透明度 1为100%
     * @return
     */
    public static String calculateRGB(String rgbColor, double transparency) {
        String transparencyStr = Integer
                .toHexString((int) (255 * transparency)).toUpperCase();
        return "#" + transparencyStr
                + rgbColor.substring(rgbColor.length() - 6, rgbColor.length());
    }

    public static Drawable getPreferenceListViewTopItemDrawable(Context context) {
        Drawable drawable = null;
        // if (mPreferenceListViewTopItemDrawable != null) {
        // drawable = mPreferenceListViewTopItemDrawable.get();
        // if (drawable != null) {
        // return drawable;
        // }
        // }
        Resources res = context.getResources();
        GradientDrawable defaultCD = (GradientDrawable) res
                .getDrawable(R.drawable.top_round_default);
        GradientDrawable pressedCD = (GradientDrawable) res
                .getDrawable(R.drawable.top_round_pressed);
        int pressedColor;
        pressedColor = res.getColor(R.color.theme_color_0_pressed);
        pressedCD.setColor(pressedColor);
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        // mPreferenceListViewTopItemDrawable = new
        // WeakReference<Drawable>(drawable);
        return drawable;
    }

    public static Drawable getPreferenceListViewBottomItemDrawable(
            Context context) {
        Drawable drawable = null;
        // if (mPreferenceListViewBottomItemDrawable != null) {
        // drawable = mPreferenceListViewBottomItemDrawable.get();
        // if (drawable != null) {
        // return drawable;
        // }
        // }
        Resources res = context.getResources();
        GradientDrawable defaultCD = (GradientDrawable) res
                .getDrawable(R.drawable.bottom_round_default);
        GradientDrawable pressedCD = (GradientDrawable) res
                .getDrawable(R.drawable.bottom_round_pressed);

        int pressedColor;
        pressedColor = res.getColor(R.color.theme_color_0_pressed);
        pressedCD.setColor(pressedColor);
        drawable = DrawableUtil.getStateDrawable(defaultCD, pressedCD);
        // mPreferenceListViewBottomItemDrawable = new
        // WeakReference<Drawable>(drawable);
        return drawable;

    }

    /**
     * 获取默认皮肤背景资源id
     *
     * @param context
     * @return
     */
    public static int getDefaultBgResId(Context context) {
        int index = getDefaultBgResIndex();
        int screenSizeType = 0;
        switch (SystemUtils.getScreenSizeType(context)) {
            case ScreenSizeType.XSCREEN:
                screenSizeType = 2;
                break;
            case ScreenSizeType.HSCREEN:
                screenSizeType = 1;
                break;
            default:
                screenSizeType = 0;
                break;
        }
        if (index < 0 || index > sThemeBg.length) {
            return sThemeBg[0][screenSizeType];
        } else {
            return sThemeBg[index][screenSizeType];
        }
    }

    /**
     * ------------------------------------------
     *
     * @author heyangbin 2013-4-18
     */

    public static class SkinPreferenceUtil extends AbstractSharedPreference {

        private static volatile SkinPreferenceUtil me;

        private SkinPreferenceUtil(String name) {
            super(name);
        }

        /**
         * 获取实例
         *
         * @return
         */
        public static synchronized SkinPreferenceUtil getInstance() {
            if (me == null) {
                String name = "skin_setting";
                me = new SkinPreferenceUtil(name);
            }
            return me;
        }
    }

    /**
     * 读取boolean用int的形势是想用-1代表没有读取过配置
     *
     * @param value
     * @param key
     * @param defaultValue
     * @return
     */
    private static int commondReadDataBoolean(int value, int key,
                                              boolean defaultValue) {
        if (value == -1) {
            if (key != 0x0)
                value = SkinPreferenceUtil.getInstance().getBoolean(key,
                        defaultValue) ? 1 : 0;
        }
        return value;
    }

    private static String commondReadDataString(String value, int key,
                                                String defaultValue) {
        if (value == null) {
            value = SkinPreferenceUtil.getInstance().getString(key,
                    defaultValue);
        }
        return value;
    }

    private static int commondReadDataInt(int value, int key, int defaultValue) {
        if (value == -1) {
            value = SkinPreferenceUtil.getInstance().getInt(key, defaultValue);
        }
        return value;
    }

    private static int isUseDownloadBg = -1;

    /**
     * 设置是否使用用户自定义背景
     */
    public static void setUseDownloadBg(Context context, boolean usecb) {
        SkinPreferenceUtil.getInstance().putBoolean(
                R.string.st_use_download_bg_key, usecb);
        isUseDownloadBg = usecb ? 1 : 0;
    }

    public static boolean isUseDownloadBg(Context ctx) {
        isUseDownloadBg = commondReadDataBoolean(isUseDownloadBg,
                R.string.st_use_download_bg_key, false);
        return isUseDownloadBg == 1 ? true : false;
    }

    private static int isUseCustomBg = -1;

    /**
     * 设置是否使用用户自定义背景
     */
    public static void setUseCustomBg(Context context, boolean usecb) {
        SkinPreferenceUtil.getInstance().putBoolean(
                R.string.st_use_custom_bg_key, usecb);
        isUseCustomBg = usecb ? 1 : 0;
    }

    public static boolean isUseCustomBg(Context ctx) {
        isUseCustomBg = commondReadDataBoolean(isUseCustomBg,
                R.string.st_use_custom_bg_key, false);
        return isUseCustomBg == 1 ? true : false;
    }

    private static int isUseDefaultBg = -1;

    /**
     * 设置是否使用了默认自定义皮肤的背景
     */
    public static void setUseDeafaultBg(Context context, boolean useBg) {
        SkinPreferenceUtil.getInstance().putBoolean(
                R.string.st_use_default_bg_key, useBg);
        isUseDefaultBg = useBg ? 1 : 0;
    }

    public static boolean isUseDefaultBg(Context context) {
        isUseDefaultBg = commondReadDataBoolean(isUseDefaultBg,
                R.string.st_use_default_bg_key, false);
        return isUseDefaultBg == 1 ? true : false;
    }

    /**
     * 浅色背景, 黑色字体
     */
    public static final int SKIN_BRIGHTNESS_LOW = 0;

    /**
     * 深色背景，白色字体
     */
    public static final int SKIN_BRIGHTNESS_HIGH = 1;

    /**
     * 设置皮肤深浅
     *
     * @param context
     * @param type
     * @deprecated
     */
    public static void setSkinBrightness(Context context, int type) {
        // SkinPreferenceUtil.getInstance().putInt(R.string.st_skin_brightness_key,
        // type);
        // skinBrightness = type;
    }

    public static int getSkinBrightness(Context context) {
        // skinBrightness = commondReadDataInt(skinBrightness,
        // R.string.st_skin_brightness_key,
        // SKIN_BRIGHTNESS_LOW);
        // return skinBrightness;
        return SKIN_BRIGHTNESS_LOW;
    }

    /**
     * 改变图片像素的颜色
     *
     * @param Source 颜色值
     * @param L      要改变的亮度值增量 （0.0f - 1。0f）
     * @return 改变后的颜色值
     */
    private static int ChangeColorLight(int Source, float L) {
        return ImageUtil.ChangeColorLight(Source, L);
    }


    private final static float BOUND(float x, float mn, float mx) {

        return ((x) < (mn) ? (mn) : ((x) > (mx) ? (mx) : (x)));
    }

    private static int colorIndex = -1;

    /**
     * 设置皮肤的颜色索引
     *
     * @param context
     * @param index   0开始
     */
    public static void setSkinColorIndexInner(Context context, int index) {
        colorIndex = index;
        int skinColor = context.getResources().getColor(sThemeColor[index]);
        setSkinColorStr(context, context.getString(sThemeColor[index]));

        int skinLightColor = ChangeColorLight(skinColor, 0.1f);
        String lightColor = String.format("#%06X",
                (0xFFFFFFFF & skinLightColor));
        setSkinLightColor(context, lightColor);

        int skinHighColor;
        skinHighColor = ChangeColorLight(skinColor, 0.3f);
        String highLightColor = String.format("#%06X",
                (0xFFFFFFFF & skinHighColor));
        setSkinHighLightColor(context, highLightColor);
    }

    public static int getSkinColorIndex(Context context) {
        colorIndex = commondReadDataInt(colorIndex,
                R.string.st_color_index_key, 0);
        return colorIndex;
    }

    private static String colorString = null;

    /**
     * 设置皮肤的颜色为在线皮肤颜色
     *
     * @param context
     * @param color   0开始
     */
    public static void setOnlineSkinColorStringInner(Context context,
                                                     String color) {
        colorString = color;
        if (!TextUtils.isEmpty(color)) {
            try {
                int skinColor = Color.parseColor(color);
                setSkinColorStr(context, color);

                int skinLightColor = ChangeColorLight(skinColor, 0.1f);
                String lightColor = String.format("#%06X",
                        (0xFFFFFFFF & skinLightColor));
                setSkinLightColor(context, lightColor);

                int skinHighColor;
                skinHighColor = ChangeColorLight(skinColor, 0.3f);
                String highLightColor = String.format("#%06X",
                        (0xFFFFFFFF & skinHighColor));
                setSkinHighLightColor(context, highLightColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getOnlineSkinColorString(Context context) {
        colorString = commondReadDataString(colorString,
                R.string.skin_online_bg_color_string_key, "");
        return colorString;
    }

    private static String skinColor = null;

    /**
     * 设置默认皮肤颜色的字符串值
     *
     * @param colorHexString #AARRGGBB
     */
    public static void setSkinColorStr(Context context, String colorHexString) {
        SkinPreferenceUtil.getInstance().putString(R.string.st_str_color_key,
                colorHexString);
        skinColor = colorHexString;
    }

    /**
     * 获取默认皮肤颜色的字符串值
     *
     * @param context
     * @return #AARRGGBB
     */
    public static String getSkinColorStr(Context context) {
        skinColor = commondReadDataString(skinColor, R.string.st_str_color_key,
                context.getString(sThemeColor[0]));
        return skinColor;
    }

    public static int getSkinColor(Context context) {
        String colorString = getOnlineSkinColorString(context);
        try {
            if (!TextUtils.isEmpty(colorString)) {
                int color = Color.parseColor(colorString);
                return color;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int color = context.getResources().getColor(
                sThemeColor[getSkinColorIndex(context)]);
        return color;

    }

    public static ColorFilter getSkinColorFilter(Context context) {
        int iColor = getSkinColor(context);

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, 0, 0, red, 0, 0, 0, 0, green, 0, 0, 0, 0,
                blue, 0, 0, 0, 1, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    public static ColorFilter getWhiteColorFilter(Context context) {
        int iColor = context.getResources().getColor(R.color.white);

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, -1, 0, red, 0, 0, -1, 0, green, 0, 0, -1, 0,
                blue, 0, 0, 0, 1, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    public static ColorFilter getSkinTransferDarkColorFilter(Context context) {
        int iColor = getSkinColor(context);

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, -1, 0, red, 0, 0, -1, 0, green, 0, 0, -1, 0,
                blue, 0, 0, 0, 1, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    public static ColorFilter getSkinTransferLightColorFilter(Context context) {
        int iColor = getSkinColor(context);

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, 1, 0, red, 0, 0, 1, 0, green, 0, 0, 1, 0,
                blue, 0, 0, 0, 1, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    public static int getSkinColorResourceId(Context context) {
        int index = getSkinColorIndex(context);
        return sThemeColor[index];
    }

    private static String skinLightColor = null;

    /**
     * @param colorHexString #AARRGGBB
     */
    private static void setSkinLightColor(Context context, String colorHexString) {
        SkinPreferenceUtil.getInstance().putString(
                R.string.st_skin_light_color_key, colorHexString);
        skinLightColor = colorHexString;
    }

    /**
     * @param context
     * @return #AARRGGBB
     */
    public static String getSkinLightColor(Context context) {
        skinLightColor = commondReadDataString(skinLightColor,
                R.string.st_skin_light_color_key, getSkinColorStr(context));
        return skinLightColor;
    }

    private static String skinHighLightColor = null;

    /**
     * @param context
     * @param colorHexString
     */
    private static void setSkinHighLightColor(Context context,
                                              String colorHexString) {
        SkinPreferenceUtil.getInstance().putString(
                R.string.st_skin_high_light_color_key, colorHexString);
        skinHighLightColor = colorHexString;
    }

    /**
     * @param context
     * @return
     */
    public static String getSkinHighLightColor(Context context) {
        skinHighLightColor = commondReadDataString(skinHighLightColor,
                R.string.st_skin_high_light_color_key, getSkinColorStr(context));
        return skinHighLightColor;
    }

    /**
     * ---------------------------------------------------
     */

    private static int defaultColorIndex = -1;

    /**
     * 设置默认皮肤的颜色索引
     *
     * @param context
     * @param index
     * @deprecated
     */
    public static void setDefaultColorIndex(Context context, int index) {
        SkinPreferenceUtil.getInstance().putInt(R.string.st_color_id_index_key,
                index);
        defaultColorIndex = index;
    }

    /**
     * @param context
     * @return
     * @deprecated
     */
    public static int getDefaultColorIndex(Context context) {
        defaultColorIndex = commondReadDataInt(defaultColorIndex,
                R.string.st_color_id_index_key, 0);
        return defaultColorIndex;
    }

    private static int isUseCustomColor = -1;

    /**
     * @param context
     * @param useCustomColor
     * @deprecated 设置是否使用自定义颜色
     */
    public static void setUseCustomColor(Context context, boolean useCustomColor) {
        SkinPreferenceUtil.getInstance().putBoolean(
                R.string.st_use_custom_color_key, useCustomColor);
        isUseCustomColor = useCustomColor ? 1 : 0;
    }

    /**
     * @param context
     * @return
     * @deprecated 获取是否使用自定义颜色
     */
    public static boolean isUseCustomColor(Context context) {
        isUseCustomColor = commondReadDataBoolean(isUseCustomColor,
                R.string.st_use_custom_color_key, false);
        return isUseCustomColor == 1 ? true : false;
    }

    private static int defaultBgIndex = -1;

    /**
     * 设置默认皮肤背景图资源索引号
     *
     * @param context
     * @param bgResIndex
     */
    public static void setDefaultBgResIndex(Context context, int bgResIndex) {
        SkinPreferenceUtil.getInstance().putInt(R.string.st_bg_id_index_key,
                bgResIndex);
        defaultBgIndex = bgResIndex;
    }

    /**
     * 获取默认皮肤背景图资源索引号
     *
     * @param context
     * @param bgResIndex
     */
    public static int getDefaultBgResIndex() {
        defaultBgIndex = commondReadDataInt(defaultBgIndex,
                R.string.st_bg_id_index_key, 0);
        return defaultBgIndex;
    }

    /**
     * 皮肤是否改变
     *
     * @param context
     * @return
     */
    public static boolean isSkinChanged(Context context) {
        // isSkinChanged = commondReadDataBoolean(isSkinChanged,
        // R.string.st_skin_changed_key, false);
        // return isSkinChanged == 1 ? true : false;
        return true;
    }

    /**
     * 是否改变皮肤
     *
     * @param context
     * @param isChanged
     * @deprecated
     */
    public static void setSkinChanged(Context context, boolean isChanged) {
        // SkinPreferenceUtil.getInstance().putBoolean(R.string.st_skin_changed_key,
        // isChanged);
        // isSkinChanged = isChanged ? 1 : 0;
    }

    private static String customBg = null;

    /**
     * 保存自定义背景路径
     *
     * @param context
     * @param path
     * @return
     */
    public static void setCustomBg(Context context, String path) {
        SkinPreferenceUtil.getInstance().putString(R.string.st_custom_bg_key,
                path);
        customBg = path;
    }

    /**
     * 获取自定义背景路径
     *
     * @param context
     * @return
     */
    public static String getCustomBg(Context context) {
        customBg = commondReadDataString(customBg, R.string.st_custom_bg_key,
                "");
        return customBg;
    }

    private static String downloadBg = null;

    /**
     * 保存自定义背景路径
     *
     * @param context
     * @param path
     * @return
     */
    public static void setDownloadBg(Context context, String path) {
        SkinPreferenceUtil.getInstance().putString(R.string.st_download_bg_key,
                path);
        downloadBg = path;
    }

    /**
     * 获取下载类型的背景路径
     *
     * @param context
     * @return
     */
    public static String getDownloadBg(Context context) {
        downloadBg = commondReadDataString(downloadBg,
                R.string.st_download_bg_key, "");
        return downloadBg;
    }

    public static final int CUSTOM_BG_COUNT = 2;

    private static String bgArrayStr = null;

    /**
     * 设置自定义背景数组
     *
     * @param context
     * @param array   路径数组
     */
    public static void setCustomBgArray(Context context, String[] array) {
        if (array == null) {
            return;
        }
        String bgArrayString;
        if (array.length == 0) {
            bgArrayString = EmptyConstants.EMPTY_STRING;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                try {
                    builder.append(array[i]);
                } catch (IndexOutOfBoundsException e) {
                    builder.append("");
                }
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            bgArrayString = builder.toString();
        }
        SkinPreferenceUtil.getInstance().putString(
                R.string.st_custom_bg_array_new_key, bgArrayString);
        bgArrayStr = bgArrayString;
    }

    /**
     * 获取背景图路径集合
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getCustomBgArray(Context context) {
        ArrayList<String> bgArray = new ArrayList<String>();
        bgArrayStr = commondReadDataString(bgArrayStr,
                R.string.st_custom_bg_array_new_key,
                EmptyConstants.EMPTY_STRING);
        if (!TextUtils.isEmpty(bgArrayStr)) {
            String[] array = bgArrayStr.split(",");
            // 是否有图片被删除，有的话，要剔除掉
            boolean delete = false;
            for (String bgPath : array) {
                if (FileUtil.isExist(bgPath)) {
                    bgArray.add(bgPath);
                } else {
                    delete = true;
                }
            }
            if (delete) {
                array = new String[bgArray.size()];
                setCustomBgArray(context, bgArray.toArray(array));
            }
        }
        return bgArray;
    }

    private static int stateColorIndex = -1;

    /**
     * 设置State颜色
     *
     * @param context
     * @return
     * @deprecated
     */
    public static void setStateColorIndex(Context context, int index) {
        SkinPreferenceUtil.getInstance().putInt(R.string.st_state_color_id_key,
                index);
        stateColorIndex = index;
    }

    /**
     * @return
     * @deprecated
     */
    public static int getStateColorIndex() {
        stateColorIndex = commondReadDataInt(stateColorIndex,
                R.string.st_state_color_id_key, 0);
        return stateColorIndex;
    }

    private static String customColorStr = null;

    /**
     * 获取自定义颜色
     *
     * @param context
     * @return
     */
    public static String[] getCustomColorArray(Context context) {
        customColorStr = commondReadDataString(customColorStr,
                R.string.st_custom_color_array_key,
                "#60000000,#60000000,#60000000");
        String[] array = customColorStr.split(",");
        String[] colors = new String[CUSTOM_COLOR_COUNT];
        for (int i = 0; i < CUSTOM_COLOR_COUNT; i++) {
            try {
                colors[i] = array[i];
            } catch (IndexOutOfBoundsException e) {
                colors[i] = "#60000000";
            }
        }
        return colors;
    }

    /**
     * 保存颜色
     *
     * @param context
     * @param array
     */
    public static void setCustomColorArray(Context context, String[] array) {
        if (array == null || array.length < 1) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CUSTOM_COLOR_COUNT; i++) {
            try {
                builder.append(array[i]);
            } catch (IndexOutOfBoundsException e) {
                builder.append("#60000000");
            }
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        SkinPreferenceUtil.getInstance().putString(
                R.string.st_custom_color_array_key, builder.toString());
        customColorStr = builder.toString();
    }

    public static void setPlayerBarSkin(Context context, SeekBar seekbar,
                                        boolean reSetProgress) {
        // setSeekBarThumbDrawable(context, seekbar,
        // SkinResourceUtil.getPlayerSeekBarProgressDrawable(),
        // context.getResources()
        // .getDrawable(SkinResourceUtil.getPlayerSeekBarThumb()), context
        // .getResources().getDimensionPixelSize(R.dimen.player_seeker_thumb_offset),
        // reSetProgress);
    }

    public static void setPlayerVolumeBarSkin(Context context, SeekBar seekbar,
                                              boolean reSetProgress) {
        // setSeekBarThumbDrawable(context, seekbar,
        // SkinResourceUtil.getPlayerVolumeSeekBarProgressDrawable(),
        // context.getResources()
        // .getDrawable(SkinResourceUtil.getPlayerSeekBarThumb()), context
        // .getResources().getDimensionPixelSize(R.dimen.player_seeker_thumb_offset),
        // reSetProgress);
    }

    public static void setPlayingBarSkin(Context context, SeekBar seekbar) {
        // setSeekBarThumbDrawable(
        // context,
        // seekbar,
        // SkinResourceUtil.getPlayingSeekBarProgressDrawable(),
        // context.getResources().getDrawable(SkinResourceUtil.getPlayingSeekBarThumb()),
        // context.getResources().getDimensionPixelSize(
        // R.dimen.playing_bar_seeker_thumb_offset), true);
    }

    /**
     * 获取圆角按钮（默认为主题色，按下为主题色加透明度）
     *
     * @param context
     * @param angle
     * @return
     */
    public static Drawable getCommonRoundCornerStatedDrawable(Context context,
                                                              int angle) {
        int defaultColor = getSkinColor(context);

        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setCornerRadius(angle);
        defaultDrawable.setColor(defaultColor);

        GradientDrawable sd = new GradientDrawable();
        sd.setCornerRadius(angle);
        sd.setColor(Color.parseColor(PRESSED_LAYER_COLOR));

        LayerDrawable pressedDrawable = new LayerDrawable(new Drawable[]{
                defaultDrawable, sd});
        return DrawableUtil.getStateDrawable(defaultDrawable, pressedDrawable);
    }

    public static Drawable getRoundCornerStatedDrawable(Context context,
                                                        int angle, int defaultColor, int pressedColor) {
        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setCornerRadius(angle);
        defaultDrawable.setColor(defaultColor);

        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setCornerRadius(angle);
        pressedDrawable.setColor(pressedColor);
        return DrawableUtil.getStateDrawable(defaultDrawable, pressedDrawable);
    }

    /**
     * 获取空心圆角按钮（默认为主题色，按下为主题色加透明度）
     *
     * @param context
     * @param angle
     * @return
     */
    public static Drawable getStrokeRoundCornerStatedDrawable(Context context, int angle,
                                                              boolean isStroke, int width) {
        int defaultColor = SkinSetting.getSkinColor(context);

        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setCornerRadius(angle);
        if (isStroke) {
            defaultDrawable.setColor(Color.TRANSPARENT);
            defaultDrawable.setStroke(width, defaultColor);
        } else
            defaultDrawable.setColor(defaultColor);

        GradientDrawable sd = new GradientDrawable();
        sd.setCornerRadius(angle);
        sd.setColor(defaultColor);

        LayerDrawable pressedDrawable = new LayerDrawable(new Drawable[]{
                defaultDrawable, sd
        });
        return DrawableUtil.getStateDrawable(defaultDrawable, pressedDrawable);
    }

    /**
     * 获取注册那边特殊的圆角按钮
     *
     * @param context
     * @return
     */
    public static Drawable getRegisterRoundCornerStatedDrawable(Context context) {
        int skinColor = getSkinColor(context);

        int defaultColor = Color.parseColor(calculateRGB(
                getSkinColorStr(context), 0.7));
        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setCornerRadius(5);
        defaultDrawable.setColor(defaultColor);

        GradientDrawable sd = new GradientDrawable();
        sd.setCornerRadius(5);
        sd.setColor(Color.parseColor(calculateRGB("#00000000", 0.2)));

        GradientDrawable sd2 = new GradientDrawable();
        sd2.setCornerRadius(5);
        sd2.setColor(skinColor);

        LayerDrawable pressedDrawable = new LayerDrawable(new Drawable[]{sd2,
                sd});
        return DrawableUtil.getStateDrawable(defaultDrawable, pressedDrawable);
    }

    /**
     * 获取dialog的圆角分割线（分割线颜色为主题颜色）
     *
     * @param context
     * @return
     */
    public static Drawable getDialogCornerDividerDrawable(Context context) {
        int defaultColor = getSkinColor(context);

        GradientDrawable defaultDrawable = new GradientDrawable();
        int radius = context.getResources().getDimensionPixelSize(
                R.dimen.menu_dialog_line_radius);
        defaultDrawable.setCornerRadii(new float[]{radius, radius, radius,
                radius, 0, 0, 0, 0});
        defaultDrawable.setColor(defaultColor);

        return defaultDrawable;
    }

    /**
     * 默认字体颜色为主题颜色，按下为白色
     *
     * @param context
     * @return
     */
    public static ColorStateList getDefaultThemeColor(Context context) {
        int defaultColor = getSkinColor(context);
        int pressedColor = Color.WHITE;
        ColorStateList colorList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_selected}, new int[]{}},
                new int[]{pressedColor, pressedColor, pressedColor,
                        defaultColor});
        return colorList;
    }

    /**
     * Tabbar font color
     *
     * @param context
     * @return
     */
    public static ColorStateList getThemeTabTextColor(Context context) {
        int defaultColor = getSkinColor(context);
        int pressedColor = Color.WHITE;
        ColorStateList colorList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_selected}, new int[]{},},
                new int[]{pressedColor, pressedColor, pressedColor,
                        pressedColor, defaultColor});
        return colorList;
    }

    /**
     * 默认字体颜色为白色，按下为主题颜色
     *
     * @param context
     * @return
     */
    public static ColorStateList getDefaultPressedThemeColor(Context context) {
        int defaultColor = Color.WHITE;
        int pressedColor = getSkinColor(context);
        ColorStateList colorList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_selected}, new int[]{}},
                new int[]{pressedColor, pressedColor, pressedColor,
                        defaultColor});
        return colorList;
    }

    /**
     * 默认字体颜色为黑色，按下为主题颜色
     *
     * @param context
     * @return
     */
    public static ColorStateList getDefaultBlackPressedThemeColor(
            Context context) {
        int defaultColor = Color.parseColor("#333333");
        int pressedColor = getSkinColor(context);
        ColorStateList colorList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_selected}, new int[]{}},
                new int[]{pressedColor, pressedColor, pressedColor,
                        defaultColor});
        return colorList;
    }

    /**
     * 默认字体颜色为灰色，按下为主题颜色
     *
     * @param context
     * @return
     */
    public static ColorStateList getDefaultGrayPressedThemeColor(Context context) {
        int defaultColor = Color.parseColor("#848484");
        int pressedColor = getSkinColor(context);
        ColorStateList colorList = new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_selected}, new int[]{}},
                new int[]{pressedColor, pressedColor, pressedColor,
                        defaultColor});
        return colorList;
    }

    public static int getTabTextColor(Context context) {
        if (getSkinColorResourceId(context) == R.color.theme_color_6_default) {
            return context.getResources().getColor(sThemeColor[0]);
        } else {
            return getSkinColor(context);
        }
    }
	/**
	 * 默认字体颜色为主题颜色，按下为自定义颜色
	 *
	 * @param context
	 * @return
	 */
	public static ColorStateList getDefaultThemePressedCustomColor(Context context,int custom) {
		int pressedColor = custom;
		int defaultColor = getSkinColor(context);
		ColorStateList colorList = new ColorStateList(new int[][] {
				new int[] { android.R.attr.state_pressed },
				new int[] { android.R.attr.state_checked },
				new int[] { android.R.attr.state_selected }, new int[] {} },
				new int[] { pressedColor, pressedColor, pressedColor,
						defaultColor });
		return colorList;
	}


	/**
	 * 获取默认皮肤透明度
	 * @return
	 */
	public static int getDefaultSkinOpacity(){
		return 191;
	}

}
