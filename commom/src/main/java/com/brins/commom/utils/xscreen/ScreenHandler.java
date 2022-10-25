package com.brins.commom.utils.xscreen;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.Utils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.commom.utils.xscreen.vivo.FingerprintInsets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 所有特殊屏幕的适配都通过这个类来适配
 *
 *  文档：
 *      小米 https://dev.mi.com/console/doc/detail?pId=1293
 *
 * 判断是不是留海屏幕手机
 * OPPO 留海屏手机型号列表：
 *   PAAM00
 ×   PAAT00
 ×   CPH1831
 ×   CPH1833
 ×
 × |-------|-------|-------|-------|
 ×                                1  // CONCAVE_SCREEN_FLAG
 ×                               1   // ROUND_CORNER_FLAG
 * Created by jasonzuo.
 */

public class ScreenHandler {

    /*挖孔屏全屏显示FLAG*/
    public static final int FLAG_NOTCH_SUPPORT =0x00010000;

    private static final int CONCAVE_SCREEN_FLAG = 0x00000001;
    private static final int ROUND_CORNER_FLAG = 0x00000002;

    /**********************************   OPPO   *********************************/
    // 80 OPPO的凹口高度80像素 324宽
    private static final int OPPO_CONCAVE_HEIGHT_PX = 80;
    private static final int OPPO_CONCAVE_WIDTH_PX = 324;

    /**********************************   VIVO   *********************************/
    // VIVO X20P X21 状态栏高度32dp大于凹口高度24dp
    private static final int VIVO_CONCAVE_STATUSBAR_HEIGHT_DP = 32;
    private static final int VIVO_CONCAVE_WIDTH_DP = 100;

    // VIVO 凹口屏幕Flag
    private static final int VIVO_CONCAVE_FLAG = 0x00000020;
    // VIVO 圆角Flag
    private static final int VIVO_ROUND_CORNER_FLAG = 0x00000008;
    private boolean isMeasuredVivoFpLoc = false;
    private int vivoRadius = 0;
    private int vivoFpY = 0;

    private ScreenConfig screenConfig;
    private FingerprintInsets mInsets;

    public static ScreenHandler getInstance() {
        return Holder.INSTANCE;
    }

    private ScreenHandler() {
        initConfig();
    }

    /**
     * 优先Model适配，没有匹配的情况下，再按照厂商的私有API来适配
     *
     * UI 设计要求，屏幕指纹，上下间距80px
     * X20Plus UD: 屏幕指纹圆心540、2006，直径160
     * X21 UD: 屏幕指纹圆心540、1924，直径170
     */
    private void initConfig() {
        if (!fitScreenByModel()
                && !fitScreenByManufactureer()
                && !fitScreenByPrivateApi()) {
            if (DrLog.DEBUG) DrLog.d("zlx_screen", "非特殊屏幕，不做特殊处理");
        }

        if (screenConfig == null) { // 防御性代码
            screenConfig = getNormalScreenConfig();
        }
    }

    /**
     * 通过私有API来适配
     */
    private boolean fitScreenByPrivateApi() {
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) { // 小米私有API判断凹口屏

            // 判断是否为屏下指纹设备
            boolean isFpFod = "true".equals(SystemUtils.getCustomVersionName("getprop ro.hardware.fp.fod"));
            boolean isNotch = "1".equals(SystemUtils.getCustomVersionName("getprop ro.miui.notch"));

            if (isNotch) {
                screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | ROUND_CORNER_FLAG,
                        getNormalStatusBarHeight(DRCommonApplication.getContext()));
            }
            if (isFpFod) {
                if (screenConfig == null)
                    screenConfig = getNormalScreenConfig();
                try {
                    String fpFodLoc = SystemUtils.getCustomVersionName("getprop persist.sys.fp.fod.location.X_Y"); // 453,1640
                    String fpFodSize = SystemUtils.getCustomVersionName("getprop persist.sys.fp.fod.size.width_height"); // 173,173

                    int locY = Integer.parseInt(fpFodLoc.split(",")[1]);
                    int sizeHeight = Integer.parseInt(fpFodSize.split(",")[1]);

                    int[] layoutParams = fpBetweenLayout(locY, sizeHeight / 2);
                    screenConfig.setLockControlBMargin(layoutParams[0]);
                    screenConfig.setLockSlideBMargin(layoutParams[1]);
                } catch (Exception e) {
                    screenConfig.setLockControlBMargin(0);
                    screenConfig.setLockSlideBMargin(0);
                    DrLog.printException(e);
                }
            }
        }
        return screenConfig != null;
    }

    /**
     * MANUFACTURER 已经转小写，注意case的字符串都是小写
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean fitScreenByManufactureer() {
        switch (Build.MANUFACTURER.toLowerCase()) {
            case "huawei":
                if (hasHuaweiNotchInScreen()) {
                    int[] huaweiNotchSize = getHuaweiNotchSize();
                    screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG, huaweiNotchSize[1]);
                    screenConfig.setConcaveWidth(huaweiNotchSize[0]);
                }
                return true;

            case "vivo":
                boolean isVivoConfigSetted = false;
                if (isVivoScreenConfig(VIVO_CONCAVE_FLAG)) {
                    boolean isVivoRoundCorner = isVivoScreenConfig(VIVO_ROUND_CORNER_FLAG);
                    screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | (isVivoRoundCorner ? ROUND_CORNER_FLAG : 0),
                            SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_STATUSBAR_HEIGHT_DP));
                    screenConfig.setConcaveWidth(SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_WIDTH_DP));
                    isVivoConfigSetted = true;
                }
                if (mInsets == null && !isMeasuredVivoFpLoc) {
                    isMeasuredVivoFpLoc = true;
                    new StackTraceHandler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mInsets = FingerprintInsets.create(DRCommonApplication.getContext(), new InsetsListener());
                        }
                    });
                    isVivoConfigSetted = true;
                } else if (vivoFpY > 0 && vivoRadius > 0) {
                    if (screenConfig == null) { // 用于处理非刘海屏，圆角屏幕，却有屏下指纹的设备
                        screenConfig = getNormalScreenConfig();
                    }
                    int[] vivoBetweenParams = fpBetweenLayout(vivoFpY + vivoRadius, vivoRadius);
                    screenConfig.setLockControlBMargin(vivoBetweenParams[0]);
                    screenConfig.setLockSlideBMargin(vivoBetweenParams[1]);
                    isVivoConfigSetted = true;
                }
                return isVivoConfigSetted;

            case "oppo":
                try {
                    boolean isOPPONotchScreen = DRCommonApplication.getContext().getPackageManager()
                            .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
                    if (isOPPONotchScreen) {
                        screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | ROUND_CORNER_FLAG,
                                OPPO_CONCAVE_HEIGHT_PX);
                        screenConfig.setConcaveWidth(OPPO_CONCAVE_WIDTH_PX);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }


    /**
     * 注意大小写和空格
     */
    private boolean fitScreenByModel() {

        switch (Build.MODEL) {
            case "PAAM00":
            case "PAAT00":
            case "CPH1831":
            case "CPH1833":
                screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | ROUND_CORNER_FLAG,
                        OPPO_CONCAVE_HEIGHT_PX);
                screenConfig.setConcaveWidth(OPPO_CONCAVE_WIDTH_PX);
                return true;

            case "vivo X21UD":
            case "vivo X21UD A":
                screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | ROUND_CORNER_FLAG,
                        SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_STATUSBAR_HEIGHT_DP));

                int[] betweenParams = fpBetweenLayout(1924, 85);
                screenConfig.setLockControlBMargin(betweenParams[0]); // 1924 + 85 + 80
                screenConfig.setLockSlideBMargin(betweenParams[1]); // 1924 - 85 - 80 - 50
                screenConfig.setConcaveWidth(SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_WIDTH_DP));
                return true;

            case "vivo X20Plus UD":
                screenConfig = new ScreenConfig(CONCAVE_SCREEN_FLAG | ROUND_CORNER_FLAG,
                        SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_STATUSBAR_HEIGHT_DP));

                int[] belowParams = fpBelowLayout(2006, 85);
                screenConfig.setLockControlBMargin(belowParams[0]); // 2006 - 85 - 80 - 50： 指纹圆心 - 圆半径 - UI要求 - 控件高度
                screenConfig.setLockSlideBMargin(belowParams[1]); // 2006 - 85 - 80
                screenConfig.setConcaveWidth(SystemUtils.dip2px(DRCommonApplication.getContext(), VIVO_CONCAVE_WIDTH_DP));
                return true;

            default:
                return false;
        }
    }

    /**
     * 1924 + 85 + 80
     * 1924 - 85 - 80 - 50
     * @return [0]:ctrl, [1]:slide
     */
    private int[] fpBetweenLayout(int fpYLoc, int radius) {
        int screenHeight = SystemUtils.getScreenHeight(DRCommonApplication.getContext());
        return new int[] {
                screenHeight - (fpYLoc - radius - 80),
                screenHeight - (fpYLoc + radius + 80 + 50)
        };
    }

    /**
     * 2006 - 85 - 80 - 50： 指纹圆心 - 圆半径 - UI要求 - 控件高度
     * 2006 - 85 - 80
     * @return [0]:ctrl, [1]:slide
     */
    private int[] fpBelowLayout(int fpYLoc, int radius) {
        int screenHeight = SystemUtils.getScreenHeight(DRCommonApplication.getContext());
        int controlOriBottom = DRCommonApplication.getContext().getResources()
                .getDimensionPixelSize(R.dimen.kg_lock_screen_play_control_panel_margin_bottom);
        return new int[] {
                screenHeight - (fpYLoc - radius - 80 - 50) + controlOriBottom,
                screenHeight - (fpYLoc - radius - 80)
        };
    }

    @NonNull
    private ScreenConfig getNormalScreenConfig() {
        return new ScreenConfig(0,
                getNormalStatusBarHeight(DRCommonApplication.getContext()));
    }

    public void refreshConfig() {
        initConfig();
    }

    public static int getNormalStatusBarHeight(Context context) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.dip2px(context, 25);
    }

    public int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId=resources.getIdentifier("navigation_bar_height","dimen","android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 判断是不是华为凹口屏
     */
    private static boolean hasHuaweiNotchInScreen() {
        boolean ret = false;
        try {
            ClassLoader cl = DRCommonApplication.getContext().getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.hwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("hasHuaweiNotchInScreen");
            ret = (boolean) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException ignore) {
        } catch (NoSuchMethodException ignore) {
        } catch (Exception ignore) {
        }
        return ret;
    }

    /**
     * 华为凹口屏获取凹口尺寸
     * @return [0]: width [1]: height
     */
    private static int[] getHuaweiNotchSize() {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = DRCommonApplication.getContext().getClassLoader();
            Class hwNotchSizeUtil =
                    cl.loadClass("com.huawei.android.util.hwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("getHuaweiNotchSize");
            ret = (int[]) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException ignore) {
        } catch (NoSuchMethodException ignore) {
        } catch (Exception ignore) {
        }
        return ret;
    }

    /**
     * VIVO私有API，判断有没有凹口和圆角
     * 0x00000020 // 凹槽
     * 0x00000008 // 圆角
     */
    private static boolean isVivoScreenConfig(int mask) {
        boolean ret = false;
        try {
            ClassLoader cl = DRCommonApplication.getContext().getClassLoader();
            Class feFeatureUtil = cl.loadClass("android.util.FtFeature");
            Method get = feFeatureUtil.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(feFeatureUtil, mask);
        } catch (ClassNotFoundException ignore) {
        } catch (NoSuchMethodException ignore) {
        } catch (Exception ignore) {
        }
        return ret;
    }

    /**
     * 判断是不是异形屏幕
     */
    public boolean isXScreen() {
        return (screenConfig.screenConfigSpec & CONCAVE_SCREEN_FLAG) != 0;
    }

    /**
     * 判断MIUI设备是否隐藏刘海
     */
    public boolean isMIUIHideNotch(Context context) {
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(context.getContentResolver(), "force_black", 0) == 1;
        } else {
            return true;
        }
    }

    /**
     * 设置应用窗口在华为挖孔屏手机使用挖孔区
     * @param window 应用页面 window 对象
     */
    public static void setHuaweiWindowInCutout(Window window) {
        if (window == null || !Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName ("com.huawei.android.view.LayoutParamsEx");
            Constructor con=layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj=con.newInstance(layoutParams);
            Method method=layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT );
        } catch (Exception ignore) {
        }
    }

    /**
     * 获取异形屏幕状态栏高度
     */
    public int getStatusBarHeight() {
        return screenConfig.statusBarHeight;
    }

    public boolean isRoundCorner() {
        return (screenConfig.screenConfigSpec & ROUND_CORNER_FLAG) != 0;
    }

    public boolean isFixLockScreenViewLoc() {
        return screenConfig.getLockControlBMargin() != 0
                || screenConfig.getLockSlideBMargin() != 0;
    }

    public int getLockControlBMargin() {
        return screenConfig.getLockControlBMargin();
    }

    public int getLockSlideBMargin() {
        return screenConfig.getLockSlideBMargin();
    }

    public void destroy() {
    }

    class InsetsListener implements
            FingerprintInsets.FingerprintInsetsListener {
        @Override
        public void onReady() {
            try {
                boolean hasUdFeature = mInsets.hasUnderDisplayFingerprint();
                if (hasUdFeature && mInsets.isReady()) {
                    final Rect rect =
                            mInsets.getFingerprintIconPosition();
                    vivoRadius = rect.bottom / 2;
                    vivoFpY = rect.top;
                    refreshConfig();
                    if (mInsets != null) {
                        mInsets.setFingerprintInsetsListener(null);
                        mInsets.destroy();
                        mInsets = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onIconStateChanged(int state) {
        }
    }


    static class Holder {
        static ScreenHandler INSTANCE = new ScreenHandler();
    }
}
