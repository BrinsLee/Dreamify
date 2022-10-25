package com.brins.commom.profile;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.preference.CommonSettingPrefs;
import com.brins.commom.skin.ColorUtil;
import com.brins.commom.skin.SkinConfig;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.FileUtil;
import com.brins.commom.utils.log.DrLog;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

public class SkinProfileUtil {

    private static volatile String mSkinStorePath;
    private static volatile String lastStorePath;

    /**
     * 歌单页及相关页面，计算图片颜色开关
     */
    public static boolean specialPagePaletteEnabled() {
        return SkinResourcesUtils.getInstance().getBoolean("skin_deep_flag", R.bool.skin_deep_flag);
    }

    /**
     * 判断当前皮肤是否为自定义皮肤
     */
    public static boolean isCustomSkin() {
        return SkinResourcesUtils.getInstance().getBoolean("skin_is_custom", R.bool.skin_is_custom);
    }

    /**
     * 判断是否为 极简、耀夜、经典 皮肤
     *
     * @return
     */
    public static boolean isSimpleNightClassicSkin() {
        // 极简(白色) / 耀夜(黑色)  /  经典(蓝色)
        return isDefaultLocalSimpleSkin() || isDefaultLocalDarkNightSkin() || isDefaultSkin();
    }

    /**
     * 皮肤类型：默认三套：蓝色、白色、黑色，纯色，自定义，主题皮肤
     *
     * @return
     */

    //蓝色
    public static boolean isDefaultSkin() {
        if (SkinResourcesUtils.getInstance().isCancelByPermission()) {
            return true;
        }
        makeSureSkinPath();
        return (!TextUtils.isEmpty(mSkinStorePath) && (mSkinStorePath.equals(SkinConfig.DEFAULT_SKIN_PATH) || mSkinStorePath.equals(SkinColorLib.solidColorSkinNames[0])));
    }

    private static void makeSureSkinPath() {
        /*if (TextUtils.isEmpty(mSkinStorePath) || !mSkinStorePath.equals(lastStorePath)) {
            mSkinStorePath = SkinProfileUtil.resotreSkinName();
        }*/
    }

    //白色
    public static boolean isDefaultLocalSimpleSkin() {
        if (SkinResourcesUtils.getInstance().isCancelByPermission()) {
            return true;
        }
        makeSureSkinPath();
        return (!TextUtils.isEmpty(mSkinStorePath) && mSkinStorePath.endsWith(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH));
    }

    //黑色
    public static boolean isDefaultLocalDarkNightSkin() {
        if (SkinResourcesUtils.getInstance().isCancelByPermission()) {
            return true;
        }
        makeSureSkinPath();
        return (!TextUtils.isEmpty(mSkinStorePath) && mSkinStorePath.endsWith(SkinConfig.DEFAULT_SKIN_DARK_NIGHT_PATH));
    }

    //本地纯色皮肤统一用这一套
    public static boolean isLocalDefaultSkin() {
        return isDefaultSkin() || isDefaultLocalSimpleSkin() || isDefaultLocalDarkNightSkin();
    }

    public static boolean isLocalSolidSkin() {
        if (SkinResourcesUtils.getInstance().isCancelByPermission()) {
            return false;
        }
        return SkinResourcesUtils.getInstance().getBoolean("skin_is_solid", R.bool.skin_is_solid);
    }

    public static boolean isOnlineSkin() {
        return !isLocalDefaultSkin() && !isLocalSolidSkin() && !isCustomSkin();
    }

    //很多换肤规则经典跟纯色用同一个
    public static boolean isBlurOrSolidSkin() {
        return isDefaultSkin() || isLocalSolidSkin();
    }

    public static boolean isSolidSkin() {
        return isLocalSolidSkin();
    }

    //纯色+极简
    public static boolean isBlurOrSolidOrSimpleSkin() {
        return isBlurOrSolidSkin() || isDefaultLocalSimpleSkin();
    }

    /**
     * 是否是黑色状态栏文字，默认是false
     */
    public static boolean isDarkTxtStatusBarSkin(){
        if (isBlurOrSolidOrSimpleSkin()) {
            return true;
        }
        boolean isDrak = SkinResourcesUtils.getInstance().getBoolean("skin_is_dark_status_bar", R.bool.skin_is_dark_status_bar);
        return isDrak;
    }

    public static boolean isSVIPSkin(@IntRange(from = -1) int privilege) {
        return privilege == 1;
    }

    public static boolean isSVIPLevelSkin(@IntRange(from = -1) int privilege) {
        return privilege == 6;
    }

    public static int getSkinThemeId() {
        int themeId = 0;
        try {
            makeSureSkinPath();
            themeId = Integer.parseInt(FileUtil.getFileNameWithoutExt(mSkinStorePath));
        } catch (Exception e) {
            DrLog.printException(e);
        }
        return themeId;
    }
    

    public static String getThemeIdFromName(String skinName) {
        if (TextUtils.isEmpty(skinName) || TextUtils.isEmpty(skinName.trim())) {
            return "0";
        }
        if (skinName.contains("custom/"))
            return String.valueOf(-1);

        //defalut_skin是旧版的默认皮肤路径。
        if (skinName.contains(SkinConfig.DEFAULT_SKIN_PATH) || skinName.contains("solid_default") || skinName.contains("defalut_skin")) {
            return "-2";
        }
        if (skinName.contains(SkinConfig.DEFAULT_SKIN_SIMPLE_PATH)) {
            return "-3";
        }
        if (skinName.contains(SkinConfig.DEFAULT_SKIN_DARK_NIGHT_PATH)) {
            return "-4";
        }

        if (skinName.contains(SkinConfig.SKIN_SOLID_SKIN_PATH_START))   //纯色皮肤当0处理
            return "0";

        String themeId = FileUtil.getFileNameWithoutExt(skinName);

        return TextUtils.isEmpty(themeId) ? "0" : themeId;
    }
    

    //本地保存的自定义皮肤数量。
    public static int getCustomSkinCount() {
        return 0;
    }

    public static String getSolidColorStrByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "null";
        }
//        if (SkinColorLib.solidColorSkinNames[0].equals(path)){
//            return "#FF2299ED";
//        }else
        if (SkinColorLib.solidColorSkinNames[SkinColorLib.solidColorSkinNames.length - 1].equals(path)) {
            return "-1";
        } else {
            int index = 0;
            boolean isCorrect = false;
            for (String colorPath : SkinColorLib.solidColorSkinNames) {
                if (colorPath.equals(path)) {
                    isCorrect = true;
                    break;
                }
                index++;
            }
            if (isCorrect) {
                return (SkinColorLib.colorOnlineThemeColorsStr[index].replace("#", ""));
            } else {
                return "error";
            }
        }
    }

    public static String getCustomColorStrByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return "null";
        }
        if (path.contains(SkinColorLib.customColorSkinNames[0])) {
            return "-1";
        } else {
            int index = 0;
            boolean isCorrect = false;
            for (String colorPath : SkinColorLib.customColorSkinNames) {
                if (path.contains(colorPath)) {
                    isCorrect = true;
                    break;
                }
                index++;
            }
            if (isCorrect) {
                return (SkinColorLib.commonWeightStrColors[Math.max(0, index - 1)].replace("#", ""));
            } else {
                return "error";
            }
        }
    }

    public static boolean usingParty100YearSkin() {
        return !TextUtils.isEmpty(mSkinStorePath) && mSkinStorePath.endsWith("599.ks");
    }

    public static int getLocalMainBgColor() {
        int resource = 0;
        if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin()) {
            resource = R.color.kg11_main_bg_color_simple;
        } else if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
            resource = R.color.kg11_main_bg_color_dark;
        }
        if (resource != 0) {
            return ContextCompat.getColor(DRCommonApplication.getContext(), resource);
        } else {
            return Color.TRANSPARENT;
        }
    }

    /**
     * 酷狗 11 通用弹窗背景色
     */
    public static int getDialogBgColor() {
        if (isCustomSkin()) {
            return getDialogCustomBgColor();
        } else if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin()) {
            return Color.WHITE;
        } else if (SkinProfileUtil.isDefaultLocalDarkNightSkin() || SkinProfileUtil.isCustomSkin()) {
            return SkinResourcesUtils.getInstance().getColor(SkinColorType.INPUT_BOX);
        } else {
            return SkinResourcesUtils.getInstance().getColor(SkinColorType.USER_RANK);
        }
    }

    /**
     * 酷狗 11 通用弹窗背景色
     */
    public static int getDialogCustomBgColor() {
        if (isCustomSkin()) {
            String skinName = CommonSettingPrefs.getInstance().getCustomColorName();
            switch (skinName) {
                case "custom_red":
                    return Color.parseColor("#8a2b2e");
                case "custom_pink":
                    return Color.parseColor("#7b3b4f");
                case "custom_orange":
                    return Color.parseColor("#76401c");
                case "custom_yellow":
                    return Color.parseColor("#7d7362");
                case "custom_green":
                    return Color.parseColor("#47773b");
                case "custom_cyans":
                    return Color.parseColor("#173437");
                case "custom_blue":
                    return Color.parseColor("#476e8c");
                case "custom_purple":
                    return Color.parseColor("#3b2747");
                case "custom_coffe":
                    return Color.parseColor("#4d3329");
                case "custom_gray":
                    return Color.parseColor("#7e7e7e");
                default:
                    return getDarkGCColor();
            }
        } else {
            return getDarkGCColor();
        }
    }

    /**
     * 获取深色主题色，规则是降低 GC 亮度
     */
    public static int getDarkGCColor() {
        int gc = SkinResourcesUtils.getInstance().getColor(SkinColorType.GRADIENT_COLOR);
        float[] hsv = new float[3];
        Color.colorToHSV(gc, hsv);
        hsv[2] -= 0.3f;
        if (hsv[2] < 0) {
            hsv[2] = 0;
        }
        return Color.HSVToColor(hsv);
    }

    public static String changeColor(int color) {
        StringBuilder stringBuffer = new StringBuilder();
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);

        stringBuffer.append("#ff");
        if (red < 16){
            stringBuffer.append("0");
        }
        stringBuffer.append(Integer.toHexString(Math.min(red,255)));
        if (green < 16){
            stringBuffer.append("0");
        }
        stringBuffer.append(Integer.toHexString(Math.min(green,255)));
        if (blue < 16){
            stringBuffer.append("0");
        }
        stringBuffer.append(Integer.toHexString(Math.min(blue,255)));
        if (DrLog.isDebug()) {
            DrLog.e("wwhLogChangeColor", "changecolor=" + stringBuffer.toString() + " ** R:" + red + " ** G:" + green + " ** B :" + blue);
        }
        return stringBuffer.toString();
    }
}

