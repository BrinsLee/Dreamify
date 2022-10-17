package com.brins.commom.skin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.utils.BitmapUtil;
import com.brins.commom.utils.KGAssert;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.skinlib.attrs.AndroidViewAttr;
import com.kugou.skinlib.attrs.drawable.SkinBgDrawable;
import java.lang.ref.SoftReference;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public class SkinResourcesUtils {

    private Resources mAppResource;

    private Resources mSkinPackageResources;

    private Rect mBgRect,mMainBgRect;

    private boolean hasRetryResource = false;

    private boolean isCancelByPermission = false;   //如果是因权限问题导致的换肤问题，则当成默认皮肤来处理

    private final Map<String, Map<String, Integer>> resourceMap = new HashMap<>();

    private SkinResourcesUtils() {

    }

    private static class InstanceHolder {
        private final static SkinResourcesUtils sInstance = new SkinResourcesUtils();
    }

    public static SkinResourcesUtils getInstance() {
        return InstanceHolder.sInstance;
    }

    public void setAppResource(Resources resource) {
        this.mAppResource = resource;
    }

    public void setSkinPackageResources(Resources resource) {
        releaseBg();
        this.mSkinPackageResources = resource;
        resourceMap.clear();
    }

    public boolean isCancelByPermission() {
        return isCancelByPermission;
    }

    public void setCancelByPermission(boolean cancelByPermission) {
        isCancelByPermission = cancelByPermission;
    }

    public int getColor(AndroidViewAttr attr) {
        return getColor(attr.attrValueName, attr.attrValueId);
    }

    public Drawable getDrawable(AndroidViewAttr attr) {
        return getDrawable(attr.attrValueName, attr.attrValueId);
    }

    public ColorStateList getColorStateList(AndroidViewAttr attr) {
        return getColorStateList(attr.attrValueName, attr.attrValueId);
    }

    private int getExtIdentifier(String attrValueName, String attrValueTypeName) {
        Map<String, Integer> attrValueMap = resourceMap.get(attrValueTypeName);
        if (attrValueMap == null) {
            attrValueMap = new HashMap<>();
            resourceMap.put(attrValueName, attrValueMap);
        }
        Integer id = attrValueMap.get(attrValueName);
        if (id == null && mSkinPackageResources != null) {
            id = mSkinPackageResources.getIdentifier(attrValueName, attrValueTypeName, SkinConfig.SKIN_PACKAGE_NAME);
            attrValueMap.put(attrValueName, id);
        }
        if (id == null) {
            return 0;
        }
        return id;
    }

    public String getExtSkinVersion() {
        if (mSkinPackageResources != null) {
            int extId = getExtIdentifier("skin_version", "string");
            if (extId != 0) {
                return getString(mSkinPackageResources, extId);
            }
        }

        return "";
    }

    public String getInnerSkinVersion() {
        if (mAppResource != null) {
            try {
                return mAppResource.getString(R.string.skin_version);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return "1.15.0";
            }
        }

        return "1.15.0";
    }

    private boolean getBoolean(Resources resources, int id,String resName) {
        try {
            return resources.getBoolean(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getInteger(Resources resources, int id) {
        try {
            return resources.getInteger(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getString(Resources resources, int id) {
        try {
            return resources.getString(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getColor(Resources resources, int id) {
        try {
            return resources.getColor(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ColorStateList getColorStateList(Resources resources, int id) {
        try {
            return resources.getColorStateList(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Drawable getDrawable(Resources resources, int id, String drawableName) {
        try {
            return resources.getDrawable(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Drawable getRippleDrawable() {
        Drawable content = getDrawable("skin_list_selector", R.drawable.skin_list_selector);
        return getRippleDrawable(content, false);
    }

    public Drawable getRippleDrawable(Drawable content) {
        return getRippleDrawable(content, false);
    }

    public Drawable getRippleDrawable(Drawable content, boolean borderless) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int rippleColor = ColorUtil.getAlphaColor(getColor("skin_list_selected", R.color.skin_list_selected), 0.2f);
            return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, borderless ? null : new ColorDrawable(
                Color.WHITE));
        }

        return content;
    }

    public ColorStateList getColorStateList(String colorListName, int intRefId) {

        if (mSkinPackageResources == null)
            return getColorStateList(mAppResource, intRefId);

        int attrExtValueId = getExtIdentifier(colorListName, "drawable");

        if (attrExtValueId == 0)
            return getColorStateList(mAppResource, intRefId);

        return getColorStateList(mSkinPackageResources, attrExtValueId);
    }

    public Drawable getDrawable(String drawableName, int intRefId) {

        if (mSkinPackageResources == null) {
            return getDrawable(mAppResource, intRefId,drawableName);
        }

        int attrExtValueId = getExtIdentifier(drawableName, "drawable");

        if (attrExtValueId == 0) {
            return getDrawable(mAppResource, intRefId, drawableName);
        }

        return getDrawable(mSkinPackageResources, attrExtValueId,drawableName);
    }

    public int getColorWithoutDef(String colorName, int intRefId) {
        if (mSkinPackageResources == null) {
            checkAppResources();
            return getColor(mAppResource, intRefId);
        }
        int attrExtValueId = getExtIdentifier(colorName, "color");

        return getColor(mSkinPackageResources, attrExtValueId);
    }

    private void checkAppResources() {
        if (mAppResource == null) {
            mAppResource = DRCommonApplication.getContext().getResources();
        }
    }

    public int getCustomColorOrZero(String colorName) {
        if (mSkinPackageResources != null) {
            return getColor(mSkinPackageResources, getExtIdentifier(colorName, "color"));
        }
        return 0;
    }

    public int getColor(String colorName, int intRefId) {

        if (mSkinPackageResources == null) {
            checkAppResources();
            return getColor(mAppResource, intRefId);
        }

        int attrExtValueId = getExtIdentifier(colorName, "color");

        if (attrExtValueId == 0)
            return getColor(mAppResource, intRefId);

        return getColor(mSkinPackageResources, attrExtValueId);
    }

    public String getString(String stringName, int refId) {
        if (mSkinPackageResources == null)
            return getString(mAppResource, refId);

        int attrExtValueId = getExtIdentifier(stringName, "string");

        if (attrExtValueId == 0)
            return getString(mAppResource, refId);

        return getString(mSkinPackageResources, attrExtValueId);
    }

    public Boolean getBoolean(String booleanName, int refId) {
        if (mSkinPackageResources == null) {
            return getBoolean(mAppResource, refId,booleanName);
        }

        int attrExtValueId = getExtIdentifier(booleanName, "bool");

        if (attrExtValueId == 0) {
            return getBoolean(mAppResource, refId,booleanName);
        }

        return getBoolean(mSkinPackageResources, attrExtValueId,booleanName);
    }

    public ColorFilter getColorFilter(int colorId) {
        return color2ColorFilter(mAppResource.getColor(colorId));
    }

    public static ColorFilter color2ColorFilter(int color) {
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = {0, 0, 0, 0, red, 0, 0, 0, 0, green, 0, 0, 0, 0,
            blue, 0, 0, 0, 1, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    public static ColorFilter colorWithAlpha2ColorFilter(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float alpha = Color.alpha(color) % 255 / 255f;
        float[] matrix = {0, 0, 0, 0, red, 0, 0, 0, 0, green, 0, 0, 0, 0,
            blue, 0, 0, 0, alpha, 0};
        return new ColorMatrixColorFilter(matrix);
    }

    public int getColor(SkinColorType type, @FloatRange(from = 0f, to = 1f) float alpha) {
        int color = getColor(type);
        return ColorUtil.getAlphaColor(color, alpha);
    }

    public int getColorAccumulation(SkinColorType type, float alpha) {
        int color = getColor(type);

        int oldAlpha = Color.alpha(color);
        if (oldAlpha > 255) {
            oldAlpha = 255;
        }
        alpha = oldAlpha / 255f * alpha;
        return ColorUtil.getAlphaColor(color, alpha);
    }

    public ColorFilter colorAlphaColorFilter(int color,float alpha) {
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = {0, 0, 0, 0, red, 0, 0, 0, 0, green, 0, 0, 0, 0,
            blue, 0, 0, 0, alpha, 0};

        return new ColorMatrixColorFilter(matrix);
    }

    /**
     * 这个方法是为了修正 {@link SkinResourcesUtils#colorWithAlpha2ColorFilter(int)} 方法
     * 当color的alpha为1(alpha通道0xFF) 的时候 变透明的问题
     * @param color
     * @return
     */
    public static ColorFilter colorWithAlpha2ColorFilter_2(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float alpha = Color.alpha(color) % 256 / 255f;
        float[] matrix = {0, 0, 0, 0, red, 0, 0, 0, 0, green, 0, 0, 0, 0,
            blue, 0, 0, 0, alpha, 0};
        return new ColorMatrixColorFilter(matrix);
    }

    public int getColor(SkinColorType type) {
        switch (type) {
            case COMMON_WIDGET:
                return getColor("skin_common_widget", R.color.skin_common_widget);
            case PRIMARY_TEXT:
                return getColor("skin_primary_text", R.color.skin_primary_text);
            case SECONDARY_TEXT:
                return getColor("skin_secondary_text", R.color.skin_secondary_text);
            case HEADLINE_TEXT:
                return getColor("skin_headline_text", R.color.skin_headline_text);
            case PRIMARY_DISABLE_TEXT:
                return getColor("skin_primary_disable_text", R.color.skin_primary_disable_text);
            case HEADLINE_PRESSED_TEXT:
                return getColor("skin_headline_pressed_text", R.color.skin_headline_pressed_text);
            case LOCAL_TEXT:
                return getColor("skin_local_text", R.color.skin_local_text);
            case BASIC_WIDGET:
                return getColor("skin_basic_widget", R.color.skin_basic_widget);
            case BASIC_ALPHA_WIDGET:
                return getColor("skin_basic_alpha_widget", R.color.skin_basic_alpha_widget);
            case TAB:
                return getColor("skin_tab", R.color.skin_tab);
            case TAB_COLOR:
                return getColor("skin_tab_color", R.color.skin_tab_color);
            case LINE:
                return getColor("skin_line", R.color.skin_line);
            case BOLD_LINE:
                return getColor("skin_bold_line", R.color.skin_bold_line);
            case TITLE:
                return getColor("skin_title", R.color.skin_title);
            case PLAYINGBAR_PROGRESS:
                return getColor("skin_playing_bar_progress", R.color.skin_playing_bar_progress);
            case PLAYINGBAR_PRIMARY_TEXT:
                return getColor("skin_playerbar_primary_text", R.color.skin_playerbar_primary_text);
            case MSG_BOX:
                return getColor("skin_msg_box", R.color.skin_msg_box);
            case LABEL:
                return getColor("skin_label", R.color.skin_label);
            case MSG_LABEL_SHADOW:
                return getColor("skin_mb_lb_shadow", R.color.skin_mb_lb_shadow);
            case PLAYERPAGE_CONTROL:
                return getColor("skin_playerpage_control",R.color.skin_playerpage_control);
            case DATE_TEXT:
                return getColor("skin_date_text", R.color.skin_date_text);
            case DATE_PRESSED_TEXT:
                return getColor("skin_date_pressed_text", R.color.skin_date_pressed_text);
            case DATE_UNSELECTED_TEXT:
                return getColor("skin_date_unselected_text",R.color.skin_date_unselected_text);
            case USER_RANK:
                return getColor("skin_user_rank", R.color.skin_user_rank);
            case COMMENT_NAME:
                return getColor("skin_comment_name",R.color.skin_comment_name);
            case BASIC_WIDGET_DISABLE:
                return getColor("skin_basic_disable_widget",R.color.skin_basic_disable_widget);
            case GRADIENT_COLOR:
                return getColor("skin_gradient_color", R.color.skin_gradient_color);
            case TITLE_PRIMARY_COLOR:
                return getColor("skin_title_primary_color",R.color.skin_title_primary_color);
            case LIST_TITLE_COLOR:
                return getColor("skin_list_title_color",R.color.skin_list_title_color);
            case INPUT_BOX:
                return getColor("skin_Input_box",R.color.skin_Input_box);
            case LIST_TITLE_UNSELECTED_COLOR:
                return getColor("skin_list_title_unselected_color", R.color.skin_list_title_unselected_color);
            case RADIO_RIGHTTRIANGLE:
                return getColor("skin_radio_righttriangle", R.color.skin_radio_righttriangle);
            case SWITCH_BTN:
                return getColor("skin_switch_btn", R.color.skin_switch_btn);
            default:
                return 0;
        }
    }

    public ColorStateList getColorStateList(SkinColorType type) {
        switch (type) {
            case PRIMARY_TEXT:
                return getColorStateList("skin_primary_text", R.drawable.skin_primary_text);
            case SECONDARY_TEXT:
                return getColorStateList("skin_secondary_text", R.drawable.skin_secondary_text);
            case HEADLINE_TEXT:
                return getColorStateList("skin_headline_text", R.drawable.skin_headline_text);
            default:
                return null;
        }
    }

/*    public boolean getSwitch(@SwitchType int type) {
        switch (type) {
            case SwitchType.A:
                return getBoolean("skin_switch_a", R.bool.skin_switch_a);
            case SwitchType.B:
                return getBoolean("skin_switch_b", R.bool.skin_switch_b);
            case SwitchType.C:
                return getBoolean("skin_switch_c", R.bool.skin_switch_c);
            case SwitchType.LOADING:
                return getBoolean("skin_switch_loading", R.bool.skin_switch_loading);
            case SwitchType.FUNCTION_ICON:
                return getBoolean("skin_function_icon", R.bool.skin_function_icon);
            default:
                return false;
        }
    }*/

    private SoftReference<SkinBgDrawable> mSkinMainBgSoftRef = new SoftReference<>(null);

    private SoftReference<SkinBgDrawable> mSkinMenuBgSoftRef = new SoftReference<>(null);

    private SoftReference<SkinBgDrawable> mSkinPlayerBgSoftRef = new SoftReference<>(null);

    private SoftReference<SkinBgDrawable> mSkinDialogBgSoftRef = new SoftReference<>(null);

    private SoftReference<Bitmap> titleBgSoftRef = new SoftReference<>(null);
    private SoftReference<Bitmap> mSkinTitleBgSoftRef = new SoftReference<>(null);
    private SoftReference<Bitmap> mSkinTabBgSoftRef = new SoftReference<>(null);
    private SoftReference<Bitmap> mCustomTitleTabBgSoftRef = new SoftReference<Bitmap>(null);

    private SoftReference<Bitmap> mOnlineBottomTabBgSoftRef = new SoftReference<>(null);
    private SoftReference<Bitmap> mOnlinePlayingBarBgSoftRef = new SoftReference<>(null);

    public void releaseBg() {
        releaseSoftReference(titleBgSoftRef);
        releaseSoftReference(mSkinMainBgSoftRef);
        releaseSoftReference(mSkinMenuBgSoftRef);
        releaseSoftReference(mSkinPlayerBgSoftRef);
        releaseSoftReference(mSkinDialogBgSoftRef);
        if (mSkinTitleBgSoftRef!=null){
            mSkinTitleBgSoftRef.clear();
        }
        if (mSkinTabBgSoftRef!=null){
            mSkinTabBgSoftRef.clear();
        }
        if (mCustomTitleTabBgSoftRef != null){
            mCustomTitleTabBgSoftRef.clear();
        }
        if (mOnlineBottomTabBgSoftRef != null){
            mOnlineBottomTabBgSoftRef.clear();
        }
        if (mOnlinePlayingBarBgSoftRef != null){
            mOnlinePlayingBarBgSoftRef.clear();
        }
        //        if (customSkinConfigEntiry != null){
        //            customSkinConfigEntiry.clear();
        //        }

    }

    public void storeBgRect(int left, int top, int right, int bottom, boolean isReset, boolean isMainBg) {
        if (mBgRect == null) {
            mBgRect = new Rect();
        }
        if (mMainBgRect == null) {
            mMainBgRect = new Rect();
        }
        if (isMainBg) {
            mMainBgRect.set(left, top, right, bottom);
        } else {
            mBgRect.set(left, top, right, bottom);
        }
        if (DrLog.DEBUG) DrLog.i(SkinConfig.LOG_TAG, "storeBgRect@" + mBgRect.toString());
        if (DrLog.DEBUG) DrLog.i(SkinConfig.LOG_TAG, "storeMainBgRect@" + mMainBgRect.toString());
        if (isReset) {
            releaseBg();
        }
    }

    private void releaseSoftReference(SoftReference softReference) {
        if (softReference != null) {
            softReference.clear();
        }
    }

    public BitmapDrawable getTitle() {
        if (titleBgSoftRef.get() == null) {
            Bitmap bitmap = getBitmapBg(SkinBgType.MAIN);
            if (bitmap == null) {
                return null;
            }
            int screenHeight = SystemUtils
                .getScreenSizeWithNavBar(DRCommonApplication.getContext())[1];
            int statusBarExtraHeight = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? SystemUtils
                .getStatusBarHeight(DRCommonApplication.getContext()) : 0;

            float p = SystemUtils
                .getTitleAndStatusBarHeight() * 1.0f / (screenHeight - statusBarExtraHeight);
            int height = (int) (bitmap.getHeight() * p);
            Matrix matrix = new Matrix();
            matrix.setScale(1, 1);
            Bitmap top = Bitmap.createBitmap(bitmap, 0, 0, Math.max(bitmap.getWidth(), 1), Math
                .max(height, 1), matrix, true);
            titleBgSoftRef = new SoftReference<>(top);
        }
        return new BitmapDrawable(titleBgSoftRef.get());
    }

    public BitmapDrawable getCustomTitle() {
        if (mSkinTitleBgSoftRef.get() == null) {
            Bitmap bitmap = getCustomTitleTabBg();
            if (bitmap == null || bitmap.isRecycled()) {
                //java.lang.NullPointerException: Attempt to invoke virtual method 'int android.graphics.Bitmap.getHeight()' on a null object reference
                //在生成后，资源可能被其他地方刚好清除，重新生成一遍
                if (mSkinTitleBgSoftRef.get() != null) {
                    mSkinTitleBgSoftRef.clear();
                }
                bitmap = getCustomTitleTabBg();
            }
            float p = SystemUtils.getTitleAndStatusBarHeight() / ((SystemUtils.getCommonTabHeight() + SystemUtils.getTitleAndStatusBarHeight()));
            int height = (int) (bitmap.getHeight() * p);
            Matrix matrix = new Matrix();
            matrix.setScale(1,1);
            Bitmap top = Bitmap.createBitmap(bitmap, 0, 0, Math.max(bitmap.getWidth(),1), Math.max(height,1),matrix,true);
            mSkinTitleBgSoftRef = new SoftReference<>(top);
        }
        return new BitmapDrawable(mSkinTitleBgSoftRef.get());
    }

    public Drawable getCustomTab(){
        if (mSkinTabBgSoftRef.get() == null || mSkinTabBgSoftRef.get().isRecycled()){//todo wainning 因为复用对象，同一个页面使用2次时，第二个的大小会影响第一个的大小。
            Bitmap bitmap = getCustomTitleTabBg();
            if (bitmap == null || bitmap.isRecycled()) {
                //java.lang.NullPointerException: Attempt to invoke virtual method 'int android.graphics.Bitmap.getHeight()' on a null object reference
                //在生成后，资源可能被其他地方刚好清除，重新生成一遍
                if (mSkinTabBgSoftRef.get() != null) {
                    mSkinTabBgSoftRef.clear();
                }
                bitmap = getCustomTitleTabBg();
            }

            float titleP = SystemUtils.getTitleAndStatusBarHeight()
                * 1.0f / ((SystemUtils.getCommonTabHeight() + SystemUtils.getTitleAndStatusBarHeight()));
            int titleHeight = (int) (bitmap.getHeight() * titleP);
            int tabHeight = bitmap.getHeight() - titleHeight;
            Matrix matrix = new Matrix();
            matrix.setScale(1,1);
            Bitmap top = Bitmap.createBitmap(bitmap,
                0,titleHeight
                , Math.max(bitmap.getWidth(),1),Math.max(tabHeight,1),matrix,true);
            mSkinTabBgSoftRef = new SoftReference<Bitmap>(top);
        }

        return new BitmapDrawable(mSkinTabBgSoftRef.get());
    }

    private Bitmap getCustomTitleTabBg(){
        if (mCustomTitleTabBgSoftRef.get() == null || mCustomTitleTabBgSoftRef.get().isRecycled()) {
            int tabAlpha = 0;
            try {
                tabAlpha = 0;
                if (tabAlpha < 0){
                    tabAlpha = 0;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            String path = DRCommonApplication.getContext().getFilesDir() + "/skin/main_bg.jpg";
            Bitmap bitmap = null;

            int[] screenSize  = SystemUtils.getScreenSizeWithNavBar(DRCommonApplication.getContext());
            int screenHeight = screenSize[1];
            int screenWidth = screenSize[0];
            int statusBarExtraHeight = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ? SystemUtils.getStatusBarHeight(DRCommonApplication.getContext()) : 0;


            float cutSourceHeight = (SystemUtils.getCommonTabHeight() + SystemUtils.getTitleAndStatusBarHeight());
            float p = cutSourceHeight * 1.0f / (screenHeight - statusBarExtraHeight);
            Matrix matrix = new Matrix();
            matrix.setScale(1, 1);
            if (screenWidth != bitmap.getWidth()){
                //图片尺寸不一样，得按比例缩放到一样
                Rect mainRect = getDrawableBg(SkinBgType.MAIN).getSrcRect();
                if (mainRect.width() >0 && mainRect.height() > 0 && mainRect.width() <= bitmap.getWidth() && mainRect.height() <= bitmap.getHeight()){
                    bitmap = Bitmap.createBitmap(bitmap,mainRect.left,mainRect.top,mainRect.width(),mainRect.height(),matrix,true);
                }
            }
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, Math.max(bitmap.getWidth(),1), Math.max((int) (((screenHeight - statusBarExtraHeight) * 1.0f / screenHeight) * bitmap.getHeight()),1), matrix, true);
            int height2 = (int) (bitmap1.getHeight() * (p));
            Bitmap top = Bitmap.createBitmap(bitmap1,
                0, 0
                , Math.max(bitmap1.getWidth(), 1), Math.max(height2, 1));
            BitmapUtil.recycleBitmap(bitmap);
            Bitmap overBitmap;
            Bitmap finalBitmap;
            if (top == null || top.isRecycled()) {
                //理论上这里不可能走到
                top = BitmapUtil.createColorBitmap(Color.GRAY);
            }
            if (tabAlpha > 0) {
                overBitmap = BitmapUtil.overColorToBitmap(top, Color.argb(tabAlpha, 0, 0, 0));
            } else {
                overBitmap = top;
            }
            finalBitmap = overBitmap;
            mCustomTitleTabBgSoftRef = new SoftReference<Bitmap>(finalBitmap);
        }
        return mCustomTitleTabBgSoftRef.get();
    }

    public Drawable getBottomTabBg() {
        int tabHeight = DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.common_bottom_bar_height);
        int barHeight = DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.kg_x_play_bar_bg_height);
        return getBottomTabBg(tabHeight + barHeight);
    }

    public Drawable getBottomTabBg(@IntRange(from = 0) int barHeight) {
        try {
            if (mOnlineBottomTabBgSoftRef.get() == null || mOnlineBottomTabBgSoftRef.get().isRecycled()) {
                Drawable mainDrawable = getDrawable("skin_kg_playing_bar_right_bg", R.drawable.skin_kg_playing_bar_right_bg);
                if (mainDrawable instanceof BitmapDrawable) {
                    Bitmap mainBmp = ((BitmapDrawable) mainDrawable).getBitmap();
                    int[] screenSize = SystemUtils.getScreenSize(DRCommonApplication.getContext());
                    if (screenSize[0] > 0 && screenSize[1] > 0)
                        mainBmp = Bitmap.createScaledBitmap(mainBmp, screenSize[0], (int) (screenSize[0] * 1.0f / mainBmp.getWidth() * mainBmp.getHeight()), true);
                    int bmpHeight = mainBmp.getHeight();
                    if (bmpHeight > barHeight) {
                        // 图片比控件高，取上面
                        Bitmap tabBmp = Bitmap.createBitmap(mainBmp, 0, 0, mainBmp.getWidth(), barHeight);
                        mOnlineBottomTabBgSoftRef = new SoftReference<>(tabBmp);
                    } else {
                        float scale = barHeight * 1.0f / bmpHeight;
                        Bitmap scaleBmp = Bitmap.createScaledBitmap(mainBmp, (int) (mainBmp.getWidth() * scale), barHeight, true);
                        // 截取中间部分
                        int diff = (scaleBmp.getWidth() - mainBmp.getWidth()) / 2;
                        Bitmap tabBmp = Bitmap.createBitmap(scaleBmp, diff, 0, mainBmp.getWidth(), barHeight);
                        mOnlineBottomTabBgSoftRef = new SoftReference<>(tabBmp);
                    }
                }
            }
            return new BitmapDrawable(mOnlineBottomTabBgSoftRef.get());
        } catch (Exception e) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            return gradientDrawable;
        }
    }

    public Drawable getBottomPlayingBarBg(){
        if (mOnlinePlayingBarBgSoftRef.get() == null || mOnlinePlayingBarBgSoftRef.get().isRecycled()) {
            Drawable mainDrawable = getDrawable("skin_kg_playing_bar_right_bg", R.drawable.skin_kg_playing_bar_right_bg);
            if (mainDrawable != null && mainDrawable instanceof BitmapDrawable) {
                Bitmap mainBmp = ((BitmapDrawable) mainDrawable).getBitmap();
                int[] screenSize = SystemUtils.getScreenSize(DRCommonApplication.getContext());
                if (screenSize[0] > 0 && screenSize[1] > 0)
                    mainBmp = Bitmap.createScaledBitmap(mainBmp, screenSize[0], (int) (screenSize[0] * 1.0f / mainBmp.getWidth() * mainBmp.getHeight()), true);
                int tabHeight = DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.common_bottom_bar_height);
                int barHeight = DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.kg_x_play_bar_bg_height);
                int bmpHeight = mainBmp.getHeight();
                Matrix matrix = new Matrix();
                matrix.setScale(1, 1);
                if (bmpHeight > (tabHeight + barHeight)){
                    //图片比bar跟tab总高度还高，取上边
                    Bitmap tabBmp = Bitmap.createBitmap(mainBmp,0,bmpHeight - tabHeight - barHeight,mainBmp.getWidth(),barHeight);
                    mOnlinePlayingBarBgSoftRef = new SoftReference<>(tabBmp);
                }else {
                    float scale = (tabHeight + barHeight) * 1.0f / bmpHeight;
                    Bitmap scaleBmp = Bitmap.createScaledBitmap(mainBmp, (int) (mainBmp.getWidth() * scale),tabHeight + barHeight,true);
                    Bitmap tabBmp = Bitmap.createBitmap(scaleBmp,0,0,mainBmp.getWidth(),barHeight);
                    mOnlinePlayingBarBgSoftRef = new SoftReference<>(tabBmp);
                }
            }
        }
        return new BitmapDrawable(mOnlinePlayingBarBgSoftRef.get());
    }


    /**
     * 获取背景图
     * @param type
     * @return
     */
    public SkinBgDrawable getDrawableBg(SkinBgType type) {
        if (!DRCommonApplication.isForeProcess()) {
            KGAssert.fail("not fore process");
        }
        SkinBgDrawable drawable = null;
        switch (type) {
            case MAIN:
                if (mSkinMainBgSoftRef.get() == null) {
                    Drawable originDrawable = null;
                    originDrawable = getDrawable("skin_main_bg", R.drawable.skin_main_bg);
                    if (!(originDrawable instanceof BitmapDrawable)) {
                        originDrawable = new BitmapDrawable(BitmapUtil.createColorBitmap(Color.GRAY));
                    }
                    mSkinMainBgSoftRef = new SoftReference<>(new SkinBgDrawable(originDrawable,
                        SkinProfileUtil.specialPagePaletteEnabled() && !SkinProfileUtil.isCustomSkin(),true));
                }
                drawable = mSkinMainBgSoftRef.get();
                int mainAlpha = 0;
                try {
                    mainAlpha = 0;
                    if (mainAlpha < 0){
                        mainAlpha = 0;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (drawable != null) {
                    drawable.setOverlayColorAlpha(0);
                }
                if (!hasRetryResource) {
                    hasRetryResource = true;
                    releaseBg();
                    return getDrawableBg(SkinBgType.MAIN);
                }
                hasRetryResource = false;
                break;
            case PLAYER:
                if (mSkinPlayerBgSoftRef.get() == null) {
                    Drawable originDrawable = getDrawable("skin_player_bg", R.drawable.skin_main_bg);
                    if (!(originDrawable instanceof BitmapDrawable)) {
                        originDrawable = new BitmapDrawable(BitmapUtil.createColorBitmap(Color.GRAY));
                    }
                    mSkinPlayerBgSoftRef = new SoftReference<>(new SkinBgDrawable(originDrawable, isPlayerDftSaveMemory(), false));
                }
                drawable = mSkinPlayerBgSoftRef.get();
                break;
            case DIALOG:
                if (mSkinDialogBgSoftRef.get() == null) {
                    Drawable originDrawable = getDrawable("skin_dialog_bg", R.drawable.skin_dialog_bg);
                    if (originDrawable == null || !(originDrawable instanceof BitmapDrawable)) {
                        originDrawable = new BitmapDrawable(BitmapUtil.createColorBitmap(Color.GRAY));
                    }
                    mSkinDialogBgSoftRef = new SoftReference<>(new SkinBgDrawable(originDrawable, false,false));
                }
                drawable = mSkinDialogBgSoftRef.get();
                break;
            default:
                break;
        }
        if (drawable != null) {
            //异常赋值参照SkinManager的init方法
            int[] screenSize = SystemUtils.getPhysicalSS(DRCommonApplication.getContext());
            if (screenSize[0] > screenSize[1]) {//正常情况下宽比高小，如果宽比高大可能是小窗模式，用getScreenSize再获取一次
                screenSize = SystemUtils.getScreenSize(DRCommonApplication.getContext());
            }
            int viewTopStart = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                try {
                    viewTopStart = SystemUtils.getStatusBarHeight(DRCommonApplication.getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            storeBgRect(0, 0, screenSize[0], screenSize[1] - viewTopStart, mBgRect == null || mMainBgRect == null, drawable.ismMainBg());
            drawable.setSkinBGBounds(drawable.ismMainBg() ? mMainBgRect : mBgRect, 2);
        }
        return drawable;
    }

    public Bitmap getBitmapBg(SkinBgType type) {
        SkinBgDrawable drawable = getDrawableBg(type);
        if (drawable != null) {
            return drawable.getBitmap();
        }
        return null;
    }

    public static String getColorName(Context context, int id) {
        String colorName = context.getResources().getResourceName(id);
        if (!TextUtils.isEmpty(colorName)) {
            String []names = colorName.split("/");
            return names[names.length - 1];
        }
        return null;
    }

    public static boolean isPlayerDftSaveMemory() {
        return !"OPPO R9km".equals(android.os.Build.MODEL);
    }
}
