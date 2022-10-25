package com.brins.commom.statusbar;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.support.annotation.IntDef;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarHelper {

    public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0x00002000;
    /**
     * oppo colorOs 3.0 适配
     */
    public static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    private static final String TAG = "SVLightModeHelper";
    private static String miuiVersionName;
    private static String flymeVersionName;

    private static boolean isDark = false;

    @IntDef({ Type.MIUI, Type.FLYME, Type.OTHER,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{
        int MIUI = 1;
        int FLYME = 2;
        int OTHER = 3;
        int COLOROS3 = 4;
    }

    /**
     * 修改状态栏为全透明
     *
     * @param activity
     */
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int setStatusBarLightMode(Activity activity, boolean dark) {
        int result = 0;
        View decorView = activity.getWindow().getDecorView();
        int oldVis = decorView.getSystemUiVisibility();
        int newVis = oldVis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), dark)) {
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), dark)) {
                result = 2;
            }
            //6.0以上miui不支持老的方法了，统一使用系统自己的api
            if (Build.VERSION.SDK_INT >= 23) {

                if (dark) {
                    newVis |= SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    newVis &= ~SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (newVis != oldVis) {
                    decorView.setSystemUiVisibility(newVis);
                }
                result = 3;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (dark) {
                    newVis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                } else {
                    newVis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                }
                if (newVis != oldVis) {
                    decorView.setSystemUiVisibility(newVis);
                }
                return Type.COLOROS3;
            }
        }
        if (result > 0) {
            isDark = dark;
        }
        return result;
    }

    public static boolean isDark() {
        return isDark;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class<? extends Window> clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                field.setAccessible(true);
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.setAccessible(true);
                extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static @Type int getType(){
        String os = getMIUIVerionName();
        if(!TextUtils.isEmpty(os)){
            return Type.MIUI;
        }
        os = getFlymeUIVerionName();
        if(!TextUtils.isEmpty(os)){
            return Type.FLYME;
        }
        return Type.OTHER;
    }

    private static String getCustomVerionName(String propStr) {
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec(propStr);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String result =  reader.readLine();
//            FxLog.d(TAG, "getCustomVerionName: "+result);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取MIUI版本号
     *
     * @return V5、V6、其他
     */
    public static String getMIUIVerionName() {
        if (miuiVersionName != null) {
            return miuiVersionName;
        }
        return miuiVersionName = getCustomVerionName("getprop ro.miui.ui.version.name");
    }


    /**
     * 获取FlyMe版本号
     *
     * @return Flyme OS
     */
    public static String getFlymeUIVerionName() {
        if (flymeVersionName != null)
            return flymeVersionName;

        return flymeVersionName = getCustomVerionName("getprop ro.build.display.id");
    }


    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体，即白色字体
     */
    public static void clearStatusBarDarkMode(Activity activity, int type) {
        if (type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }

}
