package com.brins.commom.statusbar;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.brins.commom.utils.SystemBarUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import static com.brins.commom.statusbar.StatusBarHelper.SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

class LightStatusBarCompat {

    interface ILightStatusBar {
        /**
         * Set whether ths status bar is light
         *
         * @param window         The window to set
         * @param lightStatusBar True if the status bar color is light
         */
        void setLightStatusBar(Window window, boolean lightStatusBar);
    }

    private static final ILightStatusBar IMPL;

    static {
        if (MIUILightStatusBarImpl.isMe()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                IMPL = new MLightStatusBarImpl() {
                    private final ILightStatusBar DELEGATE = new MIUILightStatusBarImpl();

                    @Override
                    public void setLightStatusBar(Window window, boolean lightStatusBar) {
                        super.setLightStatusBar(window, lightStatusBar);
                        DELEGATE.setLightStatusBar(window, lightStatusBar);
                    }
                };
            } else {
                IMPL = new MIUILightStatusBarImpl();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            IMPL = new MLightStatusBarImpl();
        } else if (MeizuLightStatusBarImpl.isMe()) {
            IMPL = new MeizuLightStatusBarImpl();
        } else if (VIVOLightStatusBarImpl.isMe()) {
            IMPL = new VIVOLightStatusBarImpl();
        } else if (OPPOLightStatusBarImpl.isMe()) {
            IMPL = new OPPOLightStatusBarImpl();
        } else {
            IMPL = new ILightStatusBar() {
                @Override
                public void setLightStatusBar(Window window, boolean lightStatusBar) {
                }
            };
        }
    }

    static void setLightStatusBar(Window window, boolean lightStatusBar) {
        IMPL.setLightStatusBar(window, lightStatusBar);
    }

    private static class MLightStatusBarImpl implements ILightStatusBar {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            // 设置浅色状态栏时的界面显示
            View decor = window.getDecorView();
            int ui = decor.getSystemUiVisibility();
            if (lightStatusBar) {
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            SystemBarUtil.setSystemUiVisibility(decor, ui, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private static class VIVOLightStatusBarImpl implements ILightStatusBar {

        static boolean isMe() {
            return "vivo".equalsIgnoreCase(Build.BRAND)
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1;
        }

        @Override
        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (lightStatusBar) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }
    }

    private static class OPPOLightStatusBarImpl implements ILightStatusBar {

        static boolean isMe() {
            return "oppo".equalsIgnoreCase(Build.BRAND)
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1;
        }

        @Override
        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    int vis = window.getDecorView().getSystemUiVisibility();
                    if (lightStatusBar) {
                        vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                    } else {
                        vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                    }
                    window.getDecorView().setSystemUiVisibility(vis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MIUILightStatusBarImpl implements ILightStatusBar {

        private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
        private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
        private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

        static boolean isMe() {
            FileInputStream is = null;
            try {
                is = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                Properties prop = new Properties();
                prop.load(is);
                return prop.getProperty(KEY_MIUI_VERSION_CODE) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE) != null;
            } catch (final IOException e) {
                return false;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignore all exception
                    }
                }
            }
        }

        @Override
        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            Class<? extends Window> clazz = window.getClass();
            try {
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, lightStatusBar ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class MeizuLightStatusBarImpl implements ILightStatusBar {
        static boolean isMe() {
            return Build.DISPLAY.startsWith("Flyme");
        }

        @Override
        public void setLightStatusBar(Window window, boolean lightStatusBar) {
            WindowManager.LayoutParams params = window.getAttributes();
            try {
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(params);
                if (lightStatusBar) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(params, value);
                window.setAttributes(params);
                darkFlag.setAccessible(false);
                meizuFlags.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
