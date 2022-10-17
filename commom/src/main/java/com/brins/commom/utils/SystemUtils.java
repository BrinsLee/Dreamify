package com.brins.commom.utils;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.app.DRPackage;
import com.brins.commom.constant.DRIntent;
import com.brins.commom.entity.NetworkType;
import com.brins.commom.entity.ScreenSizeType;
import com.brins.commom.entity.ScreenType;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.permission.PermissionHandler;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.toast.ToastCompat;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.xscreen.ScreenHandler;
import com.kugou.common.permission.Action;
import com.kugou.common.permission.KGPermission;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.Long.parseLong;

public class SystemUtils {

    private final static String TAG = "SystemUtils";

    private final static Pattern PATTERN_OS_TYPE_MIUI = Pattern.compile("MIUI_.*");

    private final static Pattern PATTERN_OS_TYPE_FLYME = Pattern.compile("FLYME_.*");

    private final static Pattern PATTERN_OS_TYPE_EMUI = Pattern.compile("EMUI_.*");

    private final static Pattern PATTERN_OS_TYPE_FUNCTION = Pattern.compile("VIVO_.*");

    private static final int ENOUGH_SPACE_SIZE = 10; // 10M

    public static void challengeGrayApi(String... methods) {
        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntimeMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
            Method setHiddenApiExemptionsMethod = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            Object vmRuntimeObject = getRuntimeMethod.invoke(null);
            setHiddenApiExemptionsMethod.invoke(vmRuntimeObject, new Object[]{methods});
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static String hmOSVersion = null;
    private static final String HARMONY_OS = "harmony";

    public static String getHMOSVersion() {
        if (hmOSVersion == null) {
            try {
                // ELE-AL00 10.1.0.162(C00E160R2P11)
                if (isHMOS()) {
                    String displayId = SystemPropertyUtil.getSystemProperty("ro.build.display.id");
                    if (!TextUtils.isEmpty(displayId)) {
                        String[] splitStr = displayId.split(" ");
                        if (splitStr.length > 1) {
                            String version = splitStr[1];
                            String maybeHMVersion = version.substring(0, version.indexOf("("));
                            if (!TextUtils.isEmpty(maybeHMVersion)) {
                                if (!maybeHMVersion.startsWith(String.valueOf(Build.VERSION.RELEASE))) {
                                    hmOSVersion = maybeHMVersion;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                DrLog.printException(e); // do nothing
            }
            if (hmOSVersion == null) {
                hmOSVersion = "";
            }
            if (DrLog.DEBUG) {
                DrLog.d("zlx_hm", "hm version: " + hmOSVersion);
            }
        }
        return hmOSVersion;
    }

    public static boolean isHMOS() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            return HARMONY_OS.equals(method.invoke(clz));
        } catch (Exception e) {
            DrLog.printException(e);
        }
        return false;
    }

    /**
     * 是否离线模式
     *
     * @param context
     * @return
     */
/*    public static boolean isOfflineMode(Context context) {
        boolean isOfflineMode = DefaultPrefs.getInstance().getOfflineMode();
        boolean isWifi = NetworkType.WIFI.equals(getNetworkType(context));
        // 不能使用网络的必须符合的2个条? // 1、开启了省流量模? // 2、当前网络环境不是WIFI网络
        return isOfflineMode && !isWifi;
    }*/



    private static PowerManager.WakeLock mWakeLock = null;

    public static final String SCREEN_ON_KEY = "screen_on";

    public static final String EXTRA_RETURN = "extra_return";

    public static final String CONTINUE_PLAY = "继续播放";

    public static final String CONTINUE_DOWNLOAD = "继续下载";

    public static final String CONTINUE_BROWSE = "继续浏览";

    public static final int CONTINUE_DOWNLOAD_TYPE_MUSIC = 0;

    public static final int CONTINUE_DOWNLOAD_TYPE_MV = 1;

    public static final int CONTINUE_FROM_OFFLINE_DIALOG = 2;

    public static final int CONTINUE_FROM_INVOKE_BY_CHANG = 3;//唱模块直接调起

    public static final String KUQUN_BI_FO = "kuqun_bi_fo";

    public static final String FROM_GUIDE = "from_guide";

    public static int mNeedHideContact = -1;// 0代表不需要隐藏，1代表需要隐藏

    private static Boolean isMIUISys = null;

    /**
     * 删除通知(Activity退出时可调用)
     *
     * @param activity
     * @param id
     */
    public static void cancelNotification(Activity activity, int id) {
        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    /**
     * 检查容量是否小于100K,小于则提示
     *
     * @param contect
     * @return
     */
    public static boolean checkDataFull(Context contect) {
        File data = Environment.getDataDirectory(); // 获得data的路径
        StatFs data_stat = new StatFs(data.getPath()); // 创建StatFs对象
        long data_blockSize = data_stat.getBlockSize(); // 获取block的size
        long data_availableBlocks = data_stat.getAvailableBlocks(); //
        // 获取可用block的个数
        float data_percent = (int) (data_blockSize * data_availableBlocks / 1024);// 计算可用容量
        if (DrLog.DEBUG) DrLog.e("test", "data_percent==" + data_percent);
        return data_percent > 100;

    }

    /**
     * 检测服务是否正在运行
     *
     * @param serviceName 服务的名字，例如
     *                    com.kugou.android.service.KugouBackgroundService
     * @return 确认服务正在运行返回 true。否则返回 false
     */
    public static boolean checkServiceIsRunning(Context context, String serviceName) {
        try {
            int intGetTastCounter = 1000;

            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                return false;
            }
            List<ActivityManager.RunningServiceInfo> mRunningService = activityManager
                    .getRunningServices(intGetTastCounter);

            for (ActivityManager.RunningServiceInfo amService : mRunningService) {
                if (0 == amService.service.getClassName().compareTo(serviceName)) {
                    return true;
                }
            }
        } catch (SecurityException e) {

        } catch (Exception e) {

        }

        return false;
    }

    /**
     * check whether the process of pid has a running foreground service
     *
     * @param sb more info when return false
     * @return has froreground service
     */
    /**
     * check whether the process of pid has a running foreground service
     * @param sb more info when return false
     * @return has froreground service
     */
    public static boolean hasForegroundService(Context context, int pid, StringBuilder sb) {
        try {
            int intGetTastCounter = 1000;

            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) {
                if (sb != null) {
                    sb.append("activityManager == null");
                }
                return false;
            }
            List<ActivityManager.RunningServiceInfo> mRunningService = activityManager
                    .getRunningServices(intGetTastCounter);

            boolean foreground = false;
            for (ActivityManager.RunningServiceInfo amService : mRunningService) {
                if ((amService.foreground
                        && (pid <= 0 ||
                        pid > 0 &&
                                amService.pid == pid))) {
                    foreground = true;
                    break;
                }
            }
            if (foreground) {
                return true;
            } else if (sb != null) {
                for (ActivityManager.RunningServiceInfo serviceInfo : mRunningService) {
                    if ((pid <= 0 ||
                            pid > 0 &&
                                    serviceInfo.pid == pid)) {
                        sb.append("{").append(serviceInfo.service).append(": pid=").append(serviceInfo.pid)
                                .append(", foreground=").append(serviceInfo.foreground)
                                .append(", activeSince=").append(serviceInfo.activeSince)
                                .append(", started=").append(serviceInfo.started)
                                .append(", clientCount=").append(serviceInfo.clientCount)
                                .append(", crashCount=").append(serviceInfo.crashCount)
                                .append(", lastActivityTime=").append(serviceInfo.lastActivityTime)
                                .append(", restarting=").append(serviceInfo.restarting)
                                .append(", now=").append(SystemClock.uptimeMillis()).append("}");
                    }
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (sb != null) {
                sb.append(e);
            }
        }
        return false;
    }



    public static int getColor(@ColorRes int colorResId) {
        return DRCommonApplication.getContext().getResources().getColor(colorResId);
    }

    public static int dip2px(float dipValue) {
        return dip2px(DRCommonApplication.getContext(), dipValue);
    }

    public static float dip2pxf(float dipValue) {
        final float scale = DRCommonApplication.getContext().getResources().getDisplayMetrics().density;
        return dipValue * scale;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int getDimenValue(Context context, int dimen) {
        return (int) (context.getResources().getDimension(dimen));
    }

    /**
     * 检查设备是否有导航栏
     * @return
     */
    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;

        // get from config_showNavigationBar.
        Resources resources = DRCommonApplication.getContext().getResources();
        int configId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (configId > 0) hasNavigationBar = resources.getBoolean(configId);

        try {
            // get from qemu.hw.mainkeys.
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            // do nothing!!!
        }

        return hasNavigationBar;
    }

    /**
     * 获取底部导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBarHeightWhenExist(Context context) {
        try {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0 && !hasMenuKey && checkDeviceHasNavigationBar()) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        try {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0 && !hasMenuKey) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 是否有navigationBar
     *
     * @param context
     * @return
     */
    public static boolean hasNavBar(Context context) {
        try {
            int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                return context.getResources().getBoolean(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 获取系统相册路径,耗时操作
     *
     * @param context
     * @return
     */
    public static String getAlbumPath(Context context) {
        if (context == null) {
            return null;
        }
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "title");
        values.put(ImageColumns.DESCRIPTION, "description");
        values.put(MediaColumns.MIME_TYPE, "image/jpeg");
        Uri url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        // 查询系统相册数据
        Cursor cursor = null;
        try {
            cursor = Images.Media.query(cr, url, new String[]{
                    MediaColumns.DATA
            });
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        String albumPath = null;
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            albumPath = cursor.getString(column_index);
            try {
                cursor.close();
            } catch (Exception e) {
            }
        }
        cr.delete(url, null, null);
        if (albumPath == null) {
            return null;
        }

        File albumDir = new DelFile(albumPath);
        if (albumDir.isFile()) {
            albumDir = albumDir.getParentFile();
        }
        // 如果系统相册目录不存在,则创建此目录
        if (!albumDir.exists()) {
            albumDir.mkdirs();
        }
        albumPath = albumDir.getAbsolutePath();
        return albumPath;
    }

    public static String getAppName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.applicationInfo.name;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public static String getAppEnglishName(){
        return "Dreamify";
    }

    /**
     * 储存卡剩余空间，单位MB
     *
     * @return　剩余空间
     */
    public static long getAvailableSpace() {
        try {
            StatFs statFs = new StatFs(GlobalEnv.ROOT_DIR);//change to relative path
            return statFs.getBlockSize() * ((long) statFs.getAvailableBlocks()) / (1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("NewApi")
    public static long getTotalSpace() {
        try {
            StatFs statFs = new StatFs(GlobalEnv.ROOT_DIR);
            return ((long) statFs.getTotalBytes()) / (1024 * 1024);
        } catch (Exception e) {
            DrLog.printException(e);
        }
        return 0;
    }

    /**
     * 根据屏幕分辨率得到头像尺寸
     *
     * @param context
     * @return
     */
    public static int[] getAvatarFullScreenSize(Context context) {
        int[] picSize = new int[2];
        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                picSize[0] = 640;
                picSize[1] = 480;
                break;
            case ScreenType.MSCREEN:
                picSize[0] = 320;
                picSize[1] = 240;
                break;
            case ScreenType.LSCREEN:
                picSize[0] = 240;
                picSize[1] = 180;
                break;
            default:
                picSize[0] = 640;
                picSize[1] = 480;
                break;
        }
        return picSize;
    }

    private static int picSize = 0;

    /**
     * 根据屏幕分辨率得到头像尺寸
     *
     * @param context
     * @return
     */
    public static int getAvatarSize(Context context) {
        if (picSize > 0)
            return picSize;

        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                picSize = 260;
                break;
            case ScreenType.MSCREEN:
                picSize = 160;
                break;
            default:
                picSize = 120;
                break;
        }
        return picSize;
    }

    /**
     * 获取背光灯时间
     *
     * @param context
     */
    public static int getBackgroundLightDelay(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据屏幕宽度计算出精选集的banner的图片url
     *
     * @param context
     * @param imgurl
     * @return
     */
    public static String getBillsBannerUrlByScreenSize(Context context, String imgurl) {
        if (TextUtils.isEmpty(imgurl)) {
            return "";
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int size = 400;
        if (dm.widthPixels >= 480)
            size = 480;
        return imgurl.replace("{size}", size + "");
    }

    /**
     * 获取缓冲大小
     *
     * @param totalSize 　音频文件大小（单位KB）
     * @return 缓冲大小（单位KB）
     */
    public static int getBufferSize(int totalSize) {
        if (totalSize < 50) {
            return totalSize;
        } else if (totalSize < 100) {
            return 10;
        } else if (totalSize < 200) {
            return 20;
        } else if (totalSize < 500) {
            return 30;
        } else if (totalSize < 1000) {
            return 40;
        } else if (totalSize < 2000) {
            return 64;
        } else if (totalSize < 3000) {
            return 96;
        } else if (totalSize < 4000) {
            return 128;
        } else if (totalSize < 5000) {
            return 160;
        } else if (totalSize < 6000) {
            return 192;
        } else if (totalSize < 7000) {
            return 224;
        } else if (totalSize < 8000) {
            return 256;
        } else if (totalSize < 9000) {
            return 288;
        } else if (totalSize < 10000) {
            return 320;
        } else if (totalSize < 11000) {
            return 352;
        } else if (totalSize < 12000) {
            return 384;
        } else if (totalSize < 13000) {
            return 416;
        } else if (totalSize < 14000) {
            return 448;
        } else if (totalSize < 15000) {
            return 480;
        } else {
            return 600;
        }
    }

    /**
     * 获取推荐图标大小
     *
     * @param context
     * @return
     */
    public static int getClassIconSize(Context context) {
        int iconSize;
        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                iconSize = 110;
                break;
            case ScreenType.LSCREEN:
                iconSize = 60;
                break;
            default:
                iconSize = 80;
                break;
        }
        return iconSize;
    }

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context != null) {
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == myPid()) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }
    


    /**
     * 判断是否相等，均为null或""值 不认为其相等
     *
     * @param para1 para1
     * @param para2 para2
     * @return result
     */
    public static boolean judgeNotNullEqual(String para1, String para2) {
        if (TextUtils.isEmpty(para1) || TextUtils.isEmpty(para2))
            return false;
        return para1.equals(para2);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = new Date(System.currentTimeMillis());
        return formatter.format(currentTime);
    }

    /**
     * 音乐当前音量
     *
     * @param context
     * @return
     */
    public static int getCurrVolume(Context context) {
        try {
            return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                    .getStreamVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 音量每一阶的变化量
     */
    public static int getVolumeInterval() {
        int interval = getMaxVolume(DRCommonApplication.getContext()) / 15;
        if (interval == 0) {
            interval = 1;
        }
        return interval;
    }

    /**
     * 返回指定格式的日期
     *
     * @param format
     * @param milliseconds 毫秒数
     * @return
     */
    public static String getDate(String format, long milliseconds) {
        try {
            SimpleDateFormat formater = new SimpleDateFormat(format);
            return formater.format(new Date(milliseconds));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 默认缓存数
     *
     * @param context
     * @return
     */
    public static int getDefaultCacheNum(Context context) {
        return 20;
    }

    /**
     * 获取手机屏幕密度
     *
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    }

    /**
     * 获取手机屏幕宽度
     *
     * @return
     */
    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    }

    public static int getDisplayWidth() {
        return getDisplayWidth(DRCommonApplication.getContext());
    }

    public static String getExceptionMessage(Exception e) {
        String exceptionMessage = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        exceptionMessage = baos.toString();
        return exceptionMessage;
    }

    public static String getExceptionString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replace("\n", "<br />");
    }

    /**
     * 解析文件所在的文件夹
     *
     * @param filePath 文件路径
     * @return 文件所在的文件夹路径
     */
    public static String getFileFolderPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        if (last == -1) {
            return null;
        }
        return filePath.substring(0, last + 1);
    }

    /**
     * 解析文件全名,不带扩展名
     *
     * @param filePath 文件路径
     * @return 文件全名
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        int last = filePath.lastIndexOf("/");
        int index = filePath.lastIndexOf(".");
        if (last == -1 && index == -1) {
            return filePath;
        } else if (index > last) {
            return filePath.substring(last + 1, index);
        } else {
            return filePath.substring(last);
        }
    }

    /**
     * 获取列表图标大小
     *
     * @param context
     * @return
     */
    public static int getIconSize(Context context) {
        int iconSize;
        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                iconSize = 80;
                break;
            case ScreenType.LSCREEN:
                iconSize = 48;
                break;
            default:
                iconSize = 54;
                break;
        }
        return iconSize;
    }

    @IntDef({ KUGOU_ICON_SIZE.NORMAL_165, KUGOU_ICON_SIZE.NORMAL_400, KUGOU_ICON_SIZE.BIG_640})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KUGOU_ICON_SIZE {
        int NORMAL_165 = 165;
        /**
         * 首页瀑布流需要大图，165已经无法满足要求
         */
        int NORMAL_400 = 400;
        int BIG_640 = 640;
        //还有400,100等等很多尺寸，为了复用缓存（无论是客户端还是服务端都有），只推荐这2种，或者原大小
    }

    /**
     * http://imge.kugou.com/kugouicon/20200927/20200927052824141267.jpg
     * http://imge.kugou.com/kugouicon/165/20200927/20200927052824141267.jpg
     * 根据上面的url生成下面的
     *
     * @param url 原始url
     * @return
     */
    public static String changeKugouiconUrlSize(String url, @KUGOU_ICON_SIZE int size) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        Matcher matcher = KUGOU_CION_PATTERN.matcher(url);
        if (matcher.find()) {
            int start1 = matcher.start(1);// 取(\\d{8})的位置
            StringBuilder builder = new StringBuilder(url);
            builder.insert(start1, size + "/");
            return builder.toString();
        }
        return url;
    }

    private static Pattern KUGOU_CION_PATTERN = Pattern.compile("/kugouicon/(\\d{8})/");

    /**
     * 自动根据屏幕尺寸替换出准确大小的图片url
     *
     * @param imgurl
     * @param colNum       列数
     * @param isDetailPage 是否详情页
     * @return
     */
    public static String getImageUrlByScreenSize(Context context, String imgurl, int colNum,
                                                 boolean isDetailPage) {
        String resultUrl = "";
        if (imgurl != null && imgurl.length() != 0) {
            resultUrl = imgurl.replace("{size}", String.valueOf(getNetImageFitSize(context, colNum, isDetailPage)));
        }
        return resultUrl;
    }


    /**
     * 自动根据屏幕尺寸替换出准确大小的图片url
     * (通过地址后增加_400X400.jpg格式裁剪 比如http://bsspublic.bssdl.kugou.com/bsstest2.jpg_400x400.jpg)
     * 图片大于2048*2048的转不了
     *
     * @param imgurl
     * @param colNum       列数
     * @param isDetailPage 是否详情页
     * @return
     */
    public static String getImageUrlByScreenSize2(Context context, String imgurl, int colNum,
                                                  boolean isDetailPage) {
        String resultUrl = "";
        if (imgurl != null && imgurl.length() != 0) {
            if (DrLog.isDebug()) DrLog.d("dfs", "请求显示图片url " + imgurl);
            String last = imgurl.substring(imgurl.length() - 4);
            String size = String.valueOf(getNetImageFitSize(context, colNum, isDetailPage));
            resultUrl = new StringBuilder(imgurl).append("_").append(size).append("x").append(size).append(last).toString();
            if (DrLog.isDebug()) DrLog.d("dfs", "适配屏幕后的图片url " + resultUrl);
        }
        return resultUrl;
    }

    public static String getImageUrlByImageSize(String imgurl, List<Integer> imageSizeList, int recommedSize) {
        String resultUrl = "";

        if (!TextUtils.isEmpty(imgurl)) {
            resultUrl = imgurl.replace("{size}", String.valueOf(getNetImageFitSize(imageSizeList, recommedSize)));
        }
        return resultUrl;
    }


    private static int getNearSizeIndex(List<Integer> list, int target) {
        if (list == null || list.isEmpty()) return -1;

        if (list.size() == 1) return 0;

        Collections.sort(list);  //需要升序排列

        int min = 0;
        int max = list.size() - 1;
        int mid = (min + max) / 2;
        while (max - min > 1) {
            if (target == list.get(mid)) return mid;
            if (target < list.get(mid)) max = mid;
            if (target > list.get(mid)) min = mid;
            mid = (min + max) / 2;
        }
        return list.get(max) - target < target - list.get(min) ? max : min;
    }

    public static int getNetImageFitSize(List<Integer> imageSizeList, int recommedSize) {


        int index = getNearSizeIndex(imageSizeList, recommedSize);

        if (index < 0 || index >= imageSizeList.size()) {
            return 400; //默认是400
        }

        return imageSizeList.get(index);
    }

    /**
     * 精选专题专用
     *
     * @param context
     * @param imgurl
     * @param colNum
     * @param isDetailPage
     * @return
     */
    public static String getImageUrlByScreenSizeWithTopics(Context context, String imgurl, int colNum,
                                                           boolean isDetailPage) {
        String resultUrl = "";
        if (imgurl != null && imgurl.length() != 0) {
            resultUrl = imgurl.replace("{size}",
                    String.valueOf(getNetImageFitSizeWithTopics(context, colNum, isDetailPage)));
        }
        return resultUrl;
    }

    /**
     * 排行榜专用
     */
    public static String getImageUrlByScreenSizeWithRank(Context context, String imgurl) {
        String resultUrl = "";
        if (imgurl != null && imgurl.length() != 0) {
            resultUrl = imgurl.replace("{size}",
                    String.valueOf(getRecRankNetImageFitSize(context)));
        }
        return resultUrl;
    }

    public static String getImageUrlByScreenSizeWithRecMv(Context context, String imgurl, int colNum,
                                                          boolean isDetailPage) {
        String resultUrl = "";
        if (imgurl != null && imgurl.length() != 0) {
            resultUrl = imgurl.replace("{size}",
                    String.valueOf(getRecMvNetImageFitSize(context, colNum, isDetailPage)));
        }
        return resultUrl;
    }

    public static Intent getLaunchIntentForPackage(Context mContext, String pkgName) {
        try {
            if (mContext == null) return null;
            PackageManager pm = mContext.getPackageManager();
            if (pm == null) return null;
            return pm.getLaunchIntentForPackage(pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //拉起酷狗App
    public static void launchKGApp(Context mContext) {
        if (mContext == null) return;
        Intent intent = getLaunchIntentForPackage(mContext, mContext.getPackageName());
        if (intent == null) return;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) { // may caused crash: b7329a3c92aaf4e2a4b8bb18b99d5e5a or 3171282557b5cdbf6a4289d993bd8fc0
            e.printStackTrace();
        }
    }

    /**
     * 音乐最大音量
     *
     * @param context
     * @return
     */
    public static int getMaxVolume(Context context) {
        return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getCurrentVolumePercent(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (maxVolume != 0) {
                return (volume * 100) / maxVolume;
            } else {
                return 0;
            }
        } catch (RuntimeException e) {
            if (DrLog.DEBUG) e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前系统每个app的内存等级，即最大使用内存
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass();
    }

    /**
     * 获取音乐空间用户头像大小
     *
     * @return
     */
    public static int getMusicZoneUserHead() {
        return 165;
    }

    /**
     * 获取MV图标大小
     *
     * @param context
     * @return
     */
    public static int getMVIconSize(Context context) {
        int iconSize;
        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                iconSize = 156;
                break;
            case ScreenType.LSCREEN:
                // iconSize = 75; 减低服务器压力
                iconSize = 100;
                break;
            default:
                iconSize = 100;
                break;
        }
        return iconSize;
    }

    /**
     * 精选专题专用
     *
     * @param colNum       列数
     * @param isDetailPage 是否详情页
     * @return
     */
    public static int getNetImageFitSizeWithTopics(Context context, int colNum, boolean isDetailPage) {
        if (isDetailPage)
            return 480;
        else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (colNum == 1) {
                if (dm.widthPixels <= 480) {
                    return 150;
                } else if (dm.widthPixels <= 800) {
                    return 240;
                } else if (dm.widthPixels <= 1280) {
                    return 480;
                } else {
                    return 480;
                }
            } else if (colNum == 2) {
                if (dm.widthPixels <= 480) {
                    return 150;
                } else if (dm.widthPixels <= 800) {
                    return 240;
                } else if (dm.widthPixels <= 1280) {
                    return 400;
                } else {
                    return 400;
                }
            } else if (colNum > 3) {
                if (dm.widthPixels <= 1280) {
                    return 150;
                } else {
                    return 240;
                }
            } else if (colNum > 2) {
                if (dm.widthPixels <= 800) {
                    return 150;
                } else if (dm.widthPixels <= 1280) {
                    return 240;
                } else {
                    return 240;
                }
            } else {
                return 240;
            }
        }
    }

    public static int getNetImageFitSize(Context context, int colNum, boolean isDetailPage) {
        if (isDetailPage)
            return 400;
        else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (colNum == 1) {
                return 400;
            } else if (colNum == 2) {
                if (dm.widthPixels <= 480) {
                    return 150;
                } else if (dm.widthPixels <= 800) {
                    return 240;
                } else if (dm.widthPixels <= 1280) {
                    return 400;
                } else {
                    return 400;
                }
            } else if (colNum > 3) {
                if (dm.widthPixels <= 1280) {
                    return 150;
                } else {
                    return 240;
                }
            } else if (colNum > 2) {
                if (dm.widthPixels <= 800) {
                    return 150;
                } else if (dm.widthPixels <= 1280) {
                    return 240;
                } else {
                    return 240;
                }
            } else {
                return 240;
            }
        }
    }

    public static int getRecMvNetImageFitSize(Context context, int colNum, boolean isDetailPage) {
        if (isDetailPage)
            return 400;
        else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (colNum == 1) {
                if (dm.widthPixels < 480) {
                    return 400;
                } else if (dm.widthPixels < 720) {
                    return 400;
                } else if (dm.widthPixels < 1080) {
                    return 600;
                } else {
                    return 600;
                }
            }
            return 400;
        }
    }

    public static int getRecRankNetImageFitSize(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (dm.widthPixels < 480) {
            return 400;
        } else if (dm.widthPixels < 720) {
            return 400;
        } else if (dm.widthPixels < 1080) {
            return 640;
        } else {
            return 720;
        }
    }

    /**
     * 获取OS信息
     *
     * @return
     */
    public static String getOSInfo() {
        StringBuilder osInfo = new StringBuilder();
        osInfo.append("Build.ID=").append(Build.ID).append('\r').append('\n')
                .append("Build.DISPLAY=").append(Build.DISPLAY).append('\r').append('\n')
                .append("Build.BOARD=").append(Build.BOARD).append('\r').append('\n')
                .append("Build.BRAND=").append(Build.BRAND).append('\r').append('\n')
                .append("Build.CPU_ABI=").append(Build.CPU_ABI).append('\r').append('\n')
                .append("Build.DEVICE=").append(Build.DEVICE).append('\r').append('\n')
                .append("Build.FINGERPRINT=").append(Build.FINGERPRINT).append('\r').append('\n')
                .append("Build.HOST=").append(Build.HOST).append('\r').append('\n')
                .append("Build.MANUFACTURER=").append(Build.MANUFACTURER).append('\r').append('\n')
                .append("Build.MODEL=").append(Build.MODEL).append('\r').append('\n')
                .append("Build.PRODUCT=").append(Build.PRODUCT).append('\r').append('\n')
                .append("Build.TAGS=").append(Build.TAGS).append('\r').append('\n')
                .append("Build.TIME=").append(Build.TIME).append('\r').append('\n')
                .append("Build.TYPE=").append(Build.TYPE).append('\r').append('\n')
                .append("Build.USER=").append(Build.USER).append('\r').append('\n')
                // .append("Build.OLD_VERSION=").append(CommonSettingPrefs.getInstance().getCoverDateTemp())
                .append('\r').append('\n');
        return osInfo.toString();
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

//    K歌机型号比较多，不能使用全匹配
//    public static boolean isK9Model() {
//        String k9Model = "Kugou-K9";
//        String model = getPhoneModel();
//        if (TextUtils.isEmpty(model)) {
//            return false;
//        }
//        return TextUtils.equals(k9Model.toLowerCase(), model.toLowerCase());
//    }

    public static boolean isK9ModelSeries() {
        String k9Model = "Kugou-K9";
        String model = getPhoneModel();
        if (TextUtils.isEmpty(model)) {
            return false;
        }
        return model.contains(k9Model);
    }

    /**
     * 如果是非手机设备，则隐藏通讯录相关模块
     * 此处暂且用MODEL来过滤k9(K歌机)
     * <p>
     * 以下注释的三种区分是否手机设备的方法，有待进一步考证。
     *
     * @return
     */
    public static boolean needHideContact() {
        // 多次调用防止每次都去获取MODEL对比
        if (mNeedHideContact > -1) {
            return mNeedHideContact == 1;
        }
        if (isK9ModelSeries()) {
            mNeedHideContact = 1;
            return true;
        }
//        TelephonyManager tm = (TelephonyManager) DRCommonApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        int phoneType = tm.getPhoneType();
//        if (phoneType == TelephonyManager.PHONE_TYPE_NONE) {
//            return true;
//        }
//        UiModeManager uiModeManager = (UiModeManager) DRCommonApplication.getContext().getSystemService(Context.UI_MODE_SERVICE);
//        int modeType = uiModeManager.getCurrentModeType();
//        if (modeType != Configuration.UI_MODE_TYPE_NORMAL) {
//            return true;
//        }
//        String imei = getIMEI(DRCommonApplication.getContext());
//        if (TextUtils.isEmpty(imei)) {
//            return true;
//        }
        mNeedHideContact = 0;
        return false;
    }

    /**
     * 获取手机号码
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
//        TelephonyManager tm = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        String phoneNumber = tm.getLine1Number();
//        if (TextUtils.isEmpty(phoneNumber))
//            return "";
//        if (isPhoneNumber(phoneNumber))
//            return tm.getLine1Number();
//        else
        return "";
    }

    public static boolean hasSmartBar() {
        if ("mx3".equals(Build.DEVICE) && Build.VERSION.SDK_INT >= 19) {
            return false;
        }
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }



    public static String getApkPath(@NonNull final Context context) {
        String apkPath = null;
        try {
            final ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo == null) {
                return null;
            }
            apkPath = applicationInfo.sourceDir;
        } catch (Throwable e) {
        }
        return apkPath;
    }

    //-1 未初始化 1为：32 2为64;
    public static String packageAbis = "";

    public static String getPackageAbisLoacl() {
        return packageAbis;
    }

    public static String getPackageAbis() {
        String assetsFile = getFromAssets("isarm64");
        if (assetsFile.length() > 0) {
            return "arm64-v8a";
        } else {
            return "armeabi";
        }
    }
    /**
     * 如果apk安装错误，32位手机安装64位的包。加载so会失败这里返回false。
     * 走x86 找不到so流程。
     * @return
     */
    public static boolean isApkInstallError(){
        return (Build.VERSION.SDK_INT < 21 && SystemUtils.getPackageAbis().equals("arm64-v8a"));
    }
    

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static String sChannel = null;
    

    /**
     * 获取推荐图标大小
     *
     * @param context
     * @return
     */
    public static int getRecommendIconSize(Context context) {
        int iconSize;
        @ScreenType int mScreenType = getScreenType(context);
        switch (mScreenType) {
            case ScreenType.HSCREEN:
                iconSize = 150;
                break;
            case ScreenType.LSCREEN:
                iconSize = 75;
                break;
            default:
                iconSize = 100;
                break;
        }
        return iconSize;
    }

    private static int getScreenOnType(Context context) {
        // return
        // Integer.parseInt(PreferenceUtil.getInstance(context).getString(
        // R.string.st_screen_on_key, "3"));
        // 返回3代表默认播放界面屏幕常亮
        return 3;
    }
    

    public static int[] getDecorViewSize(Activity activity) {
        if (activity == null || activity.getWindow() == null || activity.getWindow().getDecorView() == null) {
            return getScreenSize(DRCommonApplication.getContext());
        }
        View decorView = activity.getWindow().getDecorView();
        int[] size = new int[2];
        size[0] = decorView.getWidth();
        size[1] = decorView.getHeight();
        return size;
    }

    /**
     * 获取设备屏幕大小
     *
     * @param 
     * @return 0 width,1 height
     */
    @SuppressWarnings("deprecation")
    public static int[] getScreenSize(Activity activity) {
        return InfoUtils.getScreenSize(activity.getApplication());
    }

    /**
     * 获取设备屏幕大小
     *
     * @param context
     * @return 0 width,1 height
     */
    public static int[] getScreenSize(Context context) {
        return InfoUtils.getScreenSize(context);
    }

    /**
     * 获取设备屏幕大小(高度上带上虚拟按键)
     *
     * @param context
     * @return 0 width,1 height
     */
    public static int[] getScreenSizeWithNavigationBar(Context context) {
        try {
            long startTimeStamp1 = System.currentTimeMillis();
            int[] sizes = getPhysicalSS(context);
            long startTimeStamp2 = System.currentTimeMillis();
            if (DrLog.DEBUG) {
                DrLog.e(TAG, "getPhysicalSS cost:" + (startTimeStamp2 - startTimeStamp1));
            }
            return sizes;
        }//预防 getPhysicalSS空指针
        catch (Exception e) {
            e.printStackTrace();
            return InfoUtils.getScreenSize(context);
        }
    }


    public static int[] getScreenSizeWithNavBar(Context context) {
//        int[] screenSize = getScreenSize(context);
//        if (SystemUtils.hasNavBar(DRCommonApplication.getContext())) {
//            screenSize[1] += getNavigationBarHeight(DRCommonApplication.getContext());
//        }
        return SystemUtils.getPhysicalSS(DRCommonApplication.getContext());
    }

    /**
     * 获取设备屏幕宽度
     *
     * @param context
     * @return width
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            context = DRCommonApplication.getContext();
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.x;
    }


    public static int getScreenWindowWidth(Context context) {
        if (context == null) {
            context = DRCommonApplication.getContext();
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        return p.x;
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = DRCommonApplication.getContext().getApplicationContext().getResources().getDisplayMetrics();
        if (dm == null) {
            return 0;
        }
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = DRCommonApplication.getContext().getApplicationContext().getResources().getDisplayMetrics();
        if (dm == null) {
            return 0;
        }
        return dm.heightPixels;
    }

    public static float getDeviceHwRatio() {
        int widthPixels, heightPixels;
        WindowManager w = (WindowManager) DRCommonApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception exception) {
                DrLog.e(exception);
                return -1f;
            }
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception exception) {
                DrLog.e(exception);
                return -1f;
            }
        float v1 = heightPixels * 1.0f / widthPixels;
        if (DrLog.DEBUG) {
            DrLog.d(TAG, "getDeviceHwRatio: ratio1=" + v1 + " v1H=" + heightPixels + " v1W=" + widthPixels);
        }
        return v1;
    }

    private static final float FULL_FACE_SCREEN_HW_RATIO = 1.8F;
    private static Boolean isFullFaceScreen = null;
    private static Boolean isSvFullFaceScreen = null;

    /**
     * 判断是不是全面屏
     */
    public static boolean isFullFaceScreen() {
        if (isFullFaceScreen == null) {
            int[] sizeArr = getScreenSize(DRCommonApplication.getContext());
            // prevent divide zero
            if (DrLog.isDebug() && sizeArr[0] != 0) {
                DrLog.d("jwh ratio:" + (float) sizeArr[1] / sizeArr[0]);
            }
            isFullFaceScreen = sizeArr[0] != 0
                    && (float) sizeArr[1] / sizeArr[0] >= FULL_FACE_SCREEN_HW_RATIO;
        }
        return isFullFaceScreen;
    }

    /**
     * 判断短视频视频播放页是不是全面屏
     */
    public static boolean isSVFullFaceScreen() {
        if (isSvFullFaceScreen == null) {
            int[] sizeArr = getPhysicalSS(DRCommonApplication.getContext());
            // prevent divide zero
            isSvFullFaceScreen = sizeArr[0] != 0
                    && (float) sizeArr[1] / sizeArr[0] >= FULL_FACE_SCREEN_HW_RATIO;
        }
        return isSvFullFaceScreen;
    }

    /**
     * 获取设备屏幕高度
     *
     * @param context
     * @return height
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            context = DRCommonApplication.getContext();
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);

        int height = p.y;
        int width = p.x;
        return height;
    }

    /**
     * 获取设备屏幕大小
     *
     * @param context
     * @return 0 width, 1 height(includes status bar、navigation bar)
     */
    public static int[] getPhysicalSS(Context context) {
        int widthSize = -1;
        int heightSize = -1;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return new int[]{widthSize, heightSize};
        }
        Display display = windowManager.getDefaultDisplay();

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            widthSize = displayMetrics.widthPixels;
            heightSize = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthSize = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                heightSize = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                widthSize = realSize.x;
                heightSize = realSize.y;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new int[]{
                widthSize, heightSize
        };
    }

    public static @ScreenSizeType
    int getScreenSizeType(Context context) {
        int[] sizes = getScreenSize(context);
        int width = sizes[0];
        int height = sizes[1];
        if (width >= 540 && height >= 960) {
            return ScreenSizeType.XSCREEN;
        } else if (width >= 480 && height >= 800) {
            return ScreenSizeType.HSCREEN;
        } else if (width >= 320 && height >= 480) {
            return ScreenSizeType.MSCREEN;
        } else {
            return ScreenSizeType.LSCREEN;
        }
    }

    /**
     * 屏幕类型
     *
     * @param context
     * @return
     */
    public static @ScreenType
    int getScreenType(Context context) {
        // int[] info = getScreenSize(context);
        // if (info[0] > 320 && info[1] > 480) {
        // return ScreenType.HSCREEN;
        // } else if ((info[0] > 240 && info[0] <= 320) && (info[1] > 320 &&
        // info[1] <= 480)) {
        // return ScreenType.MSCREEN;
        // } else if (info[0] <= 240 && info[1] <= 400) {
        // return ScreenType.LSCREEN;
        // } else {
        // return ScreenType.MSCREEN;
        // }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if (dm.densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            return ScreenType.HSCREEN;
        } else if (dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
            return ScreenType.MSCREEN;
        } else if (dm.densityDpi == DisplayMetrics.DENSITY_LOW) {
            return ScreenType.LSCREEN;
        } else {
            return ScreenType.MSCREEN;
        }
    }

    public static boolean isXScreenOrHigher() {
        DisplayMetrics dm = DRCommonApplication.getContext().getResources().getDisplayMetrics();
        if (dm.densityDpi >= DisplayMetrics.DENSITY_XHIGH) {
            return true;
        }
        return false;
    }


    public static String getScreenTypeIntro(Context context) {
        int type = getScreenOnType(context);
        String result = "";
        switch (type) {
            case 1:
                // 不常亮
                result = "不常亮";
                break;
            case 2:
                // 所有界面常亮
                result = "所有界面常亮";
                break;
            case 3:
                // 播放界面常亮
                result = "仅播放界面常亮";
                break;
            default:
                break;
        }
        return result;

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */

    public static String getSDCardDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            return file.getPath();
        }
        return null;
    }

    /**
     * 获取系统版本，如1.5,2.1
     *
     * @return　SDK版本号
     */
    public static String getSDK() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取SDK版本号
     *
     * @return
     */
    public static int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取文件名，带后缀名
     *
     * @param filePath
     * @return
     */
    public static String getShortFileName(String filePath) {
        if (filePath == null) {
            return null;
        }
        if (filePath.indexOf("/") == -1) {
            return filePath;
        }
        int begin = filePath.lastIndexOf("/");
        return filePath.substring(begin + 1);
    }

    /**
     * 获取文件名，不带后缀名
     *
     * @param filePath
     * @return
     */
    public static String getShortFileNameWithoutSuffix(String filePath) {
        if (filePath == null) {
            return null;
        }
        if (filePath.indexOf("/") == -1) {
            return filePath;
        }
        int begin = filePath.lastIndexOf("/");
        String fileName = filePath.substring(begin + 1);
        int end = fileName.lastIndexOf(".");
        if (end != -1)
            fileName = fileName.substring(0, end);
        return fileName;
    }

    public static String getFileSuffix(String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            return null;
        }
        int index = pathName.lastIndexOf(".");
        int length = pathName.length();
        String suffix = "";
        if (index != -1) {
            suffix = pathName.substring(index, length);
        }
        return suffix;
    }

    /**
     * 单位换算
     *
     * @param fileSize
     * @return
     */
    public static String getSizeText(long fileSize) {
        if (fileSize <= 0) {
            return "0.0M";
        }
        if (fileSize < 100 * 1024) {
            // 大于0小于100K时，直接返回“0.1M”（产品需求）
            return "0.1M";
        }
        float result = fileSize;
        String suffix = "M";
        result = result / 1024 / 1024;
        return String.format("%.1f", result) + suffix;
    }

    /**
     * 单位换算
     *
     * @param fileSize
     * @return
     */
    public static String getSizeTextForKB(long fileSize) {
        if (fileSize <= 0) {
            return "0.0M";
        }
        if (fileSize > 0 && fileSize < 100) {
            // 大于0小于100K时，直接返回“0.1M”（产品需求）
            return "0.1M";
        }
        float result = fileSize;
        String suffix = "M";
        result = result / 1024;
        return String.format("%.1f", result) + suffix;
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     * @deprecated 亲测在 三星KOT49H api19 上拿不到状态栏高度，建议使用{{@link #getStatusBarHeight()}}代替
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.top;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (ScreenHandler.getInstance().isXScreen()) {
            return ScreenHandler.getInstance().getStatusBarHeight();
        } else {
            return ScreenHandler.getNormalStatusBarHeight(context);
        }
    }

    public static int getStatusBarHeightUseDisplayCutout(Activity context) {
        if (Build.VERSION.SDK_INT >= 28) {
            WindowInsets rootWindowInsets = context.getWindow().getDecorView().getRootWindowInsets();
            if (rootWindowInsets != null) {
                DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    return displayCutout.getSafeInsetTop();
                }
            }

        }
        return getStatusBarHeight();
    }


    /**
     * 参考 {@see https://blog.csdn.net/yuan7016/article/details/81101072}
     * 判断是否有刘海
     *
     * @param window window
     * @return DisplayCutout
     */
    public static DisplayCutout hasDisplayCutout(Window window) {
        if (window == null || window.getDecorView() == null) {
            return null;
        }

        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getDisplayCutout();
            }
        }
        return null;
    }


    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight2(Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SystemUtils.dip2px(context, 25);
    }

    public static int getStatusBarHeight() {
        return getStatusBarHeight(DRCommonApplication.getContext());
    }

    public static float getTitleAndStatusBarHeight() {
        float titleHeight = DRCommonApplication.getContext().getResources().getDimension(
                R.dimen.common_title_bar_height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            titleHeight = titleHeight + getStatusBarHeight(DRCommonApplication.getContext());
        }
        return titleHeight;
    }

    public static int getCommonTabHeight() {
        return DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.kg_common_swip_tab_height);
    }

    public static int getPlayBarHeight() {
        return DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.playing_bar_height_without_shadow);
    }

    public static int getPlayBarHeightWithShaDown() {
        return DRCommonApplication.getContext().getResources().getDimensionPixelSize(R.dimen.playing_bar_height);
    }

    /**
     * 获取系统型号
     *
     * @return
     */
    public static String getSysModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机品牌
     * {使用时最好UrlEncode,防止获取到的字符串中出现空格等特殊符号导致异常}
     *
     * @return
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /*
     * 获取手机分辨率
     */
    public static String getPhoneDisplay(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);

        return p.x + "*" + p.y;
    }

    public static int[] getPhoneDisplaySize(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);

        return new int[]{p.x, p.y};
    }

    /**
     * 亮度当前值
     *
     * @param context
     * @return
     */
    public static int getSystemBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS, 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    public static final void collapseStatusBar(Context context) {
        Object sbservice = context.getSystemService("statusbar");
        try {
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method collapse;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                collapse = statusBarManager.getMethod("collapsePanels");
            } else {
                collapse = statusBarManager.getMethod("collapse");
            }
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchKGPlayerPage(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName componentName = new ComponentName(context, "com.kugou.android.app.splash.SplashActivity");
        intent.setComponent(componentName);
        intent.setPackage(DRCommonApplication.getAppPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DRIntent.KEY_OPEN_APP_WITHOUT_SPLASH, true);
        intent.setAction(DRIntent.ACTION_SHOW_PLAYPAGE);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
//            AndroidRuntimeException
        }
        //收起通知栏
        collapseStatusBar(context);
//        BroadcastUtil.sendBroadcast(new DRIntent(DRIntent.ACTION_NOTIFY_OPEN_PLAYER));
    }

    public static PendingIntent getPlayerPagePendingIntent(Context context) {
        try {
            Class<?> cls = Class.forName("com.kugou.android.app.MediaActivity");
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(DRIntent.ACTION_SHOW_PLAYPAGE);
            intent.setClass(context, cls);
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取平台
     *
     * @param context
     * @return
     */
    public static String getPlatform(Context context) {
        return "0";
    }

    /**
     * 获取当前app版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        return InfoUtils.getVersionCode(context);
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    public static String getVersionName(Context context) {
        final String packageName = context.getPackageName();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public static boolean isApkDebuggable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            DrLog.printException(e);
        }
        return false;
    }


    public static String getX86LibPath(Context context) {
        String libPath = context.getApplicationInfo().dataDir + "/lib_x86";
        return libPath;
    }

    /**
     * 是否有足够空间(一般歌曲在10M以内，少于10M提示空间不足)
     *
     * @return
     */
    public static boolean hasEnoughSpace() {
        return hasEnoughSpace(ENOUGH_SPACE_SIZE);
    }

    public static boolean hasEnoughSpace(int mSise) {
        try {
            return getAvailableSpace() > mSise;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean hasGingerbreadMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive()) {
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                inputManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param edit
     */
    public static void hideSoftInput(Context context, View edit) {
        if (context == null || edit == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive(edit)) {
            inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }
    }

    /**
     * 安装APK
     *
     * @param context
     * @param filePath APK存放路径
     */
    public static void installApk(final Context context, final String filePath) {
        final DelFile apkFile = new DelFile(filePath);
        KGPermission.with(context)
                .install()
                .file(apkFile)
                .onDenied(new Action<File>() {
                    @Override
                    public void onAction(File file) {
                        ToastUtil.show(context, "安装失败，请检查权限设置");
                    }
                })
                .start();
    }

    public static void installApkWithoutPermissionCheck(Context context, String path) {
        Log.d("UpdateAppNotiManager", "直接拉起系统安装界面2");
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = com.kugou.common.permission.util.FileUtil.getFileUri(context, new DelFile(path));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        context.startActivity(intent);
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param context
     * @return
     */
    public static boolean isAutoBrightness(Context context) {
        boolean automicBrightness = false;
        ContentResolver resolver = context.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(resolver, "screen_brightness_mode") == 1;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }


    /**
     * 渠道激活配置文件
     */
    public static boolean isChannelActivationFile(String path) {
        return FileUtil.isExist(path) ? true : false;
    }

    /**
     * 判断改后缀的歌曲是否可以添加到网络收藏
     *
     * @param extName
     * @param hash
     * @return
     */
    public static boolean isCloudExtName(String extName, String hash) {
        extName = extName.toUpperCase();
        if (extName.equals("MP3") || extName.equals("FLAC") || extName.equals("APE")) {
            return true;
        } else if (extName.equals("M4A") && !TextUtils.isEmpty(hash)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 当前音量是否处于最大值
     *
     * @return
     */
    public static boolean isCurVolumnEqualMax(Context context) {
        return getMaxVolume(context) == getCurrVolume(context);
    }

    /**
     * 是否启用硬件加速
     *
     * @return
     */
    public static boolean isEnableHardwareAccelerated() {
        return true;
    }

    /**
     * 是否乱码
     *
     * @param s
     * @return true 乱码
     */
    public static boolean isErrCode(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher matcher = pattern.matcher(s);
        return !matcher.find();
    }

    /**
     * 判断Intent是否有效
     *
     * @param context
     * @param intent
     * @return true 有效
     */
    public static boolean isIntentAvailable(Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 当前系统是否MIUIV5
     *
     * @param context
     * @return
     */
    @SuppressWarnings("rawtypes")
    // public static boolean isMIUIV5(Context context) {
    // if (isPackageExist(context, "miui")) {
    // if (android.os.Build.VERSION.SDK_INT >= 14) {
    // String miuiVer = null;
    // try {
    // Class cls;
    // cls = ClassReflectUtil.creatClassObject("android.os.SystemProperties");
    // miuiVer = ClassReflectUtil.getSystemProperties(cls,
    // "ro.miui.ui.version.name");
    // } catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // return false;
    // }
    // if (miuiVer.equals("V5")) {
    // return true;
    // }
    // }
    // }
    //
    // return false;
    //
    // }

    static String miuiVersionName = null;
    static String miuiDetailVersionName = null;
    public static String flymeVersionName = null;
    static String emuiVersionName = null;
    static String colorOsVersionName = null;
    static String VivoVersionName = null;
    static String k6Model = null;
    static String brand = null;


    private static boolean isDreamifyService() {
        try {
            return DRCommonApplication.getContext().getSystemService("dreamify_services") != null;
        } catch (Exception e) {

        }
        return false;
    }

    private static String getBrand() {
        if (brand != null) {
            return brand;
        }
        return brand = getCustomVersionName("getprop ro.product.manufacturer");
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
        return miuiVersionName = getCustomVersionName("getprop ro.miui.ui.version.name");
    }

    public static boolean isEnableMediaNotiInMiui() {
        String versionName = SystemUtils.getMIUIVerionName();
        return "V10".equals(versionName) || "V11".equals(versionName);
    }

    public static String getMIUIDetailVersion() {
        if (miuiDetailVersionName != null) {
            return miuiDetailVersionName;
        }
        return miuiDetailVersionName = getCustomVersionName("getprop ro.build.version.incremental");
    }

    public static boolean isEnableMediaNotiInEmui() {
        return getHuaweiApiLevel() >= 27; // 厂商给出的判断方式，27及以上支持媒体播控中心，需要默认使用系统通知栏
    }

    public static int getHuaweiApiLevel() {
        int currentApiLevel = 0;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getDeclaredMethod("get", new Class[]{String.class});
            currentApiLevel = Integer.parseInt((String) method.invoke(cls, new Object[]{"ro.build.hw_emui_api_level"}));
        } catch (Exception e) {
            DrLog.e(TAG, "getHuaweiApiLevel Exception\n" + Log.getStackTraceString(e));
        }
        return currentApiLevel;
    }

    public static boolean isHuaWeiSPModel() {
        return "NTS-AL00".equals(Build.MODEL);
    }

    /**
     * 获取FlyMe版本号
     *
     * @return Flyme OS
     */
    public static String getFlymeUIVerionName() {
        if (flymeVersionName != null)
            return flymeVersionName;

        return flymeVersionName = getCustomVersionName("getprop ro.build.display.id");
    }

    /**
     * 获取ColorOS版本号
     *
     * @return Color OS
     */
    public static String getColorOsVerionName() {
        if (colorOsVersionName != null)
            return colorOsVersionName;

        return colorOsVersionName = getCustomVersionName("getprop ro.build.version.opporom");
    }

    /**
     * 获取vivo版本号
     *
     * @return Color OS
     */
    public static String getVivoVerionName() {
        if (VivoVersionName != null)
            return VivoVersionName;

        return VivoVersionName = getCustomVersionName("getprop ro.vivo.os.build.display.id");
    }

    /**
     * 获取EMUI版本号,华为手机系统
     *
     * @return EmotionUI_
     */
    public static String getEMUIVerionName() {
        if (emuiVersionName != null)
            return emuiVersionName;
        return emuiVersionName = getCustomVersionName("getprop ro.build.version.emui");
    }

    public static String getCustomVersionName(String propStr) {
        BufferedReader reader = null;
        try {
            Process p = Runtime.getRuntime().exec(propStr);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String versionStr = reader.readLine();
            if (TextUtils.isEmpty(versionStr)) {
                return "UNKNOWN";
            }
            return versionStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "UNKNOWN";
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCustomOsType() {
        String verionName = getMIUIVerionName();
        if (verionName.equals("V6")) {
            return "MIUI_V6";
        } else if (verionName.equals("V7")) {
            return "MIUI_V7";
        } else if (verionName.equals("V8")) {
            return "MIUI_V8";
        } else if (verionName.equals("V9")) {
            return "MIUI_V9";
        }

        // 不是MIUI V5 V6,继续判断
        verionName = getFlymeUIVerionName();
        //适配flyme系统以Flyeme OS开头的
        if (verionName.startsWith("Flyeme OS 3")) {
            return "FLYME_3X";
        } else if (verionName.startsWith("Flyeme OS 4")) {
            return "FLYME_4X";
        } else if (verionName.startsWith("Flyme OS 5")) {
            return "FLYME_5X";
        } else if (verionName.startsWith("Flyme OS 6")) {
            return "FLYME_6X";
        } else if (verionName.startsWith("Flyme OS 7")) {
            return "FLYME_7X";
        }
        //适配flyme系统以Flyeme开头的
        if (verionName.startsWith("Flyeme 3")) {
            return "FLYME_3X";
        } else if (verionName.startsWith("Flyeme 4")) {
            return "FLYME_4X";
        } else if (verionName.startsWith("Flyme 5")) {
            return "FLYME_5X";
        } else if (verionName.startsWith("Flyme 6")) {
            return "FLYME_6X";
        } else if (verionName.startsWith("Flyme 7")) {
            return "FLYME_7X";
        }

        // 不是Flyme OS,继续判断
        verionName = getEMUIVerionName();
        String emuiName = "EmotionUI";
        //EmotionUI_3.单独处理
        if ("EmotionUI_3.0".equals(verionName)) {
            return "EMUI_3_0";
        }
        //EmotionUI_3.0以上
        if (verionName.startsWith("EmotionUI_3.")) {
            return "EMUI_3X";
        } else if (verionName.startsWith("EmotionUI_4.")) {
            return "EMUI_4X";
        } else if (verionName.startsWith("EmotionUI_5.")) {
            return "EMUI_5X";
        } else if (verionName.startsWith("EmotionUI_2.")) {
            String subVersion = verionName.substring(emuiName.length() + 3, emuiName.length() + 4);
            // 对EmotionUI_2.3以下不处理
            try {
                if (Integer.parseInt(subVersion) >= 3) {
                    return "EMUI_2X";
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //ColorOS
        verionName = getColorOsVerionName();

        if (verionName.startsWith("V2.")) {
            return "COLOR_2X";
        } else if (verionName.startsWith("V3.")) {
            return "COLOR_3X";
        } else if (verionName.startsWith("V4.")) {
            return "COLOR_4X";
        }

        //Vivo
        verionName = getVivoVerionName();

        if (verionName.startsWith("Funtouch OS_2")) {
            return "VIVO_2X";
        } else if (verionName.startsWith("Funtouch OS_3")) {
            return "VIVO_3X";
        } else if (verionName.startsWith("Funtouch OS_4")) {
            return "VIVO_4X";
        }

        return getCustomVersionName("getprop ro.build.display.id");
    }

    public static boolean isMIUIOSType(String type) {
        return (type != null && PATTERN_OS_TYPE_MIUI.matcher(type).matches());
    }

    public static boolean isFlymeOSType(String type) {
        return (type != null && PATTERN_OS_TYPE_FLYME.matcher(type).matches());
    }

    /**
     * 判断当前是否运行在主线程
     */
    public static boolean isOnMainthread() {
        Looper myLooper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        return (myLooper == mainLooper);
    }

    /**
     * 是否-去掉快捷方式特殊包
     *
     * @param context
     * @return
     */
    public static boolean isP2PPackage(Context context) {
        return true; // 全渠道P2P包
        /**
         * try { String channel = SystemUtil.getRawFileContent(context,
         * R.raw.channel, "GB2312"); String channels =
         * context.getString(R.string.p2p_page_channel); String[] channelArr =
         * channels.split(","); int channelInt = Integer.valueOf(channel); if
         * (channelInt % 2 == Integer.valueOf(channelArr[1])) { return true; } }
         * catch (Exception e) { e.printStackTrace(); } return false;
         */
    }

    /**
     * 判断某个安装包是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPackageExist(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        if (packageName != null) {
            try {
                PackageInfo packinfo = pm.getPackageInfo(packageName, 0);
                if (packinfo != null)
                    return true;
                else
                    return false;

            } catch (NameNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    public static String getAppNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        if (!TextUtils.isEmpty(packageName)) {
            try {
                PackageInfo packinfo = pm.getPackageInfo(packageName, 0);
                if (packinfo != null) {

                    return packinfo.applicationInfo.loadLabel(pm) + "";
                }
            } catch (Exception e) {

            }
        }
        return "";
    }

    /**
     * 判断是否手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNumber(String mobiles) {
        Pattern p = Pattern.compile("^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 抽样（约等于抽样比例，有误差）
     *
     * @param percent 百分比
     * @return
     */
    public static boolean isPicked(float percent) {
        if (DrLog.DEBUG) DrLog.d("SystemUtils", "isPicked percent= " + percent);
        if (percent <= 0) {
            return false;
        } else if (percent >= 100) {
            return true;
        }
        float n = new SecureRandom().nextFloat();
        return n < percent / 100.0f;
    }

    /**
     * 抽样 (约等于抽样比例，有误差)
     *
     * @param decimals 抽样比. 例如0.01 为1%的抽样比
     * @return
     */
    public static boolean isPickUp(float decimals) {
        if (DrLog.DEBUG) DrLog.d("SystemUtils", "isPickUp decimals= " + decimals);
        if (decimals >= 1) {
            return true;
        } else if (decimals <= 0) {
            return false;
        }

        float n = new SecureRandom().nextFloat();
        return n < decimals;
    }

    /**
     * 检测储存卡是否可用
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查当前这个系统是否屏蔽锁屏功能（针对ANDROID 4.0 ~ 4.1.1的MIUI v4进行屏蔽）
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isShowScreenLockMIUI(Context context) {
        if (isPackageExist(context, "miui")) {
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT <= 16) {
                String miuiVer = null;
                try {
                    Class cls;
                    cls = ClassReflectUtil.creatClassObject("android.os.SystemProperties");
                    miuiVer = ClassReflectUtil.getSystemProperties(cls, "ro.miui.ui.version.name");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return true;
                }
                if (miuiVer.equals("V5")) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 是否静音模式
     *
     * @param context
     * @return
     */
    public static boolean isSlientMode(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 是否插入了耳机
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isWiredHeadsetOn(Context context) {
        AudioManager localAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn();
    }

    @SuppressWarnings("deprecation")
    public static boolean isHeadsetOn(Context context) {
        AudioManager localAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn() || localAudioManager.isBluetoothA2dpOn();
    }

    public static boolean isSpeakerphoneOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return !audioManager.isWiredHeadsetOn() && !audioManager.isBluetoothA2dpOn();
    }

    public static String getAudioDeviceOutStat() {
        AudioManager audioManager = (AudioManager) DRCommonApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        String audioConnected = null;
        boolean isHeadset = audioManager.isWiredHeadsetOn();
        boolean isBluetooth = audioManager.isBluetoothA2dpOn();
        if (!isHeadset && !isBluetooth) {
            audioConnected = "扬声器";
        } else if (isHeadset && isBluetooth) {
            audioConnected = "耳机,蓝牙";
        } else if (isHeadset) {
            audioConnected = "耳机";
        } else if (isBluetooth) {
            audioConnected = "蓝牙";
        }
        return audioConnected;
    }

    /**
     * 获取进程ID
     *
     * @param
     * @return
     */
    public static int myPid() {
        return android.os.Process.myPid();
    }

    /**
     * 去除电量显示状态栏
     *
     * @param activity
     */
    public static void NoStatusBar(Activity activity) {
        final Window win = activity.getWindow();
        // No Statusbar
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void showStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 去除标题栏
     *
     * @param activity
     */
    public static void NoTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 打开邮箱
     *
     * @param address
     */
    public static void openEmail(Context ctx, String address) {
        try {
            if (TextUtils.isEmpty(address))
                return;
            Uri uri = Uri.parse("mailto:" + address);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            List<ResolveInfo> list = ctx.getPackageManager().queryIntentActivities(it,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (list != null && list.size() > 0) {
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(it);
            } else {
                ToastCompat.makeText(ctx.getApplicationContext(), "您手机没有安装邮箱应用", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (ActivityNotFoundException e) {

        }

    }

    /**
     * 打开url
     *
     * @param link
     */
    public static void openUri(Context ctx, String link) {
        try {
            if (TextUtils.isEmpty(link))
                return;
            Uri uri = Uri.parse(link);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            List<ResolveInfo> list = ctx.getPackageManager().queryIntentActivities(it,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (list != null && list.size() > 0) {
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(it);
            } else {
                ToastCompat.makeText(ctx.getApplicationContext(), "您手机没有安装浏览器应用", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (ActivityNotFoundException e) {

        }
    }

    /**
     * 将字节数转换为单位有MB的字符串
     *
     * @param byteLength 字节长度
     * @return
     */
    public static String parseByteToMbString(long byteLength) {
        if (byteLength <= 0) {
            return "0.00MB";
        }
        final DecimalFormat format = new DecimalFormat("#######0.00");
        long k = byteLength / 1024;
        if (k < 1024) {
            return format.format(k / 1024.00) + "MB";
        } else {
            return format.format((k / 1024 + (k % 1024) / 1024.00f)) + "MB";
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return px2dip(DRCommonApplication.getContext(), pxValue);
    }

    public static String replaceURLChar(String ch) {
        ch = ch.replace("%", "%25");

        ch = ch.replace("+", "%2B");
        ch = ch.replace(" ", "%20");
        ch = ch.replace("/", "%2F");
        ch = ch.replace("?", "%3F");
        ch = ch.replace("#", "%23");
        ch = ch.replace("&", "%26");
        ch = ch.replace("=", "%3D");
        return ch;
    }


    @SuppressWarnings("deprecation")
/*    public static void sendNotification(Activity activity, int id, int iconResId, String title,
                                        String tickerText, String content, boolean no_clear) {
        // 创建图标
        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(activity, 0, activity.getIntent(),
                Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        Notification notification = new NotificationCompat.Builder(activity, KGNotificationChannel.NORMAL_MSG_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(tickerText)
                .setContentIntent(pi)
                .build();

        notification.tickerText = tickerText;
        notification.when = System.currentTimeMillis();
        notification.icon = iconResId;
        if (no_clear)
            notification.flags = Notification.FLAG_NO_CLEAR;
//        notification.setLatestEventInfo(activity, title, tickerText, pi);
        try {
            if (notificationManager != null) {
                KGNotificationChannel.handleNotificationChannel(activity, notification);
                notificationManager.notify(id, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 设置背光灯时间
     *
     * @param context
     * @param timeDelay 延时(毫秒，1秒=1000毫秒),-1为永久不超时
     */
    public static void SetBackgroundLightDelay(Context context, int timeDelay) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                timeDelay);
    }

    /**
     * 设置 亮度调节
     *
     * @param activity
     * @param current
     */
    public static void setCurrentBrightness(Activity activity, int current) {
        if (current <= 255 && current >= 20) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = current / 255.0f;
            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 恢复手机默认值
     */
    public static void setDefaultBrightness(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = -1.0f;
        activity.getWindow().setAttributes(lp);
    }


    /**
     * 显示软键盘
     *
     * @param context
     * @param edit
     */
    public static void showSoftInput(Context context, View edit) {
        if (context == null || edit == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive(edit)) {
            inputManager.showSoftInput(edit, 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param activity
     */
    public static void showSoftInput(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive()) {
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                inputManager.showSoftInput(focusView, 0);
            }
        }
    }

    /**
     * 开启软键盘
     */
    public static void showSoftInput(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }

    /**
     * 开启自动亮度调节
     */
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(), "screen_brightness_mode", 1);
    }

    public static void startBrowser(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        } catch (Exception e) {
            ToastCompat.makeText(context, "浏览器打开网址失败,请检查手机是否安装浏览器", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 停止自动亮度调节
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(), "screen_brightness_mode", 0);
    }


    public static boolean isRunInUiThread() {
        Thread currThread = Thread.currentThread();
        if (currThread == DRCommonApplication.getMainThread()) {
            return true;
        }
        return false;
    }

    public static String getNameOfPlatformVersion() {
        Context context = DRCommonApplication.getContext();
        String version = String.valueOf(SystemUtils.getVersionCode(context));
        String app = SystemUtils.getPlatform(context);
        if ("0".equals(app)) {
            app = "AndroidPhone";
        } else if ("20".equals(app)) {
            app = "AndroidPad";
        }

        return app + "-" + version;
    }

    /**
     * 判断是否是大神手机，用于临时解决大神手机SP失效问题。
     *
     * @return
     */
    public static boolean isSpecialModel() {
        String phoneModel = SystemUtils.getPhoneModel();
        if (SystemUtils.getSdkInt() == 22
                && ("8676-M01".equals(phoneModel) || "8676-A01".equals(phoneModel))) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否是vivo X7
     */
    public static boolean isVivoX7() {
        String phoneModel = SystemUtils.getPhoneModel();
        return "vivo X7".equals(phoneModel);
    }


    public static boolean isOverPeriod(int period, long time) {
        if (period < 0) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);

        long curTime = System.currentTimeMillis();
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTimeInMillis(curTime);

        int curDay = curCalendar.get(Calendar.DAY_OF_YEAR);
        int curYear = curCalendar.get(Calendar.YEAR);

        if (DrLog.isDebug()) {
            DrLog.e("wwhConfig", "day :" + day + " && year:" + year + " && curDay :" + curDay + " && curYear:" + curYear);
        }

        if (year == curYear) {
            return curDay - day >= period;
        } else {
            return 365 - day + curDay >= period;
        }

    }

    /**
     * 判断是不是有效的网络链接 <br/>
     * 3G，wifi，GPRS（歌词，头像）
     *
     * @param context
     * @return
     */
    public static boolean isAvalidNetSetting(Context context) {
        //不校验ip
        return NetworkUtil.isNetworkAvailable(DRCommonApplication.getContext());
    }

    public static boolean isAvalidNetSetting() {
        return NetworkUtil.isNetworkAvailable(DRCommonApplication.getContext());
    }


    /**
     * 获取网络类型,最高支持4G网络
     *
     * @param context
     * @return 网络类型
     */
    public static String getNetworkType(Context context) {
        return getNetworkType4G(context);
    }

    private static ConnectivityManager getConnectivityManager() {
        return ((ConnectivityManager) DRCommonApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    /**
     * 获取网络类型，最高支持4G网络.
     *
     * @param context
     * @return 网络类型
     */
    private static String getNetworkType4G(Context context) {
        return getNetworkType4G(getConnectivityManager());
    }

    private static String getNetworkType4G(ConnectivityManager cm) {
        if (cm == null) {
            return NetworkType.UNKNOWN;
        }
        NetworkInfo netInfo;
        try {
            netInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            // 修改崩溃 机型8185
            return NetworkType.UNKNOWN;
        }
        if (netInfo == null) {
            return NetworkType.NONETWORK;
        }
        if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetworkType.WIFI;
        }
        //手机通过数据线连接电脑上网，网络类型当做WiFi处理。
        if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return NetworkType.WIFI;
        }
        TelephonyManager tm = (TelephonyManager) DRCommonApplication.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            int netType = TelephonyManager.NETWORK_TYPE_LTE;
            try {
                netType = tm.getNetworkType();
            } catch (SecurityException e) { // crash md5: 026b6f530cd5b1142ab9f8fbb1264a8e
                DrLog.printException(e);
            }

            switch (netType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkType.NET_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case 17: // Sony XPERIA 移动4G手机强指3G网络
                    return NetworkType.NET_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkType.NET_4G;
                default:
                    if (DrLog.DEBUG)
                        DrLog.d("kugou", "getNetworkType returns a unknown value:" + netType);
                    return NetworkType.NET_3G;
            }
        } else {
            return NetworkType.NET_3G;
        }
    }

    public static int getNetWorkType(Context context) {
        String netWorkType = getNetworkType(context);
        if (netWorkType == NetworkType.WIFI) {
            return NetworkType.NET_WIFI_INT;
        } else if (netWorkType == NetworkType.NET_2G) {
            return NetworkType.NET_2G_INT;
        } else if (netWorkType == NetworkType.NET_3G || netWorkType == NetworkType.NET_4G) {
            return NetworkType.NET_3G_INT;
        } else {
            return NetworkType.NET_OTHER_INT;
        }
    }

    /**
     * @param context context
     * @return 网络状态返回，比getNetWorkType方法多返回4G、5G
     */
    public static int getNetWorkTypeExtend(Context context) {
        String netWorkType = getNetworkType(context);
        if (netWorkType == NetworkType.WIFI) {
            return NetworkType.NET_WIFI_INT;
        } else if (netWorkType == NetworkType.NET_2G) {
            return NetworkType.NET_2G_INT;
        } else if (netWorkType == NetworkType.NET_3G) {
            return NetworkType.NET_3G_INT;
        } else if (netWorkType == NetworkType.NET_4G) {
            return NetworkType.NET_4G_INT;
        } else if (netWorkType == NetworkType.NET_5G) {
            return NetworkType.NET_5G_INT;
        } else {
            return NetworkType.NET_OTHER_INT;
        }
    }

    /**
     * 获取系统版本，如Android422
     *
     * @return
     */
    public static String getSystemSDK() {
        return "Android" + Build.VERSION.RELEASE.replace(".", "");
    }

    /**
     * 获取当前移动网络的类型（cmnet 或 cmwap 及 ctnet、ctwap、3gnet、3gwap 等）。
     * 需要注意的是，此名字可以被用户改成其它文字
     */
    public static String getCurrentMobileNetworkType(Context context) {
        String netType = "";
        try {
            ConnectivityManager connectMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectMgr != null) {
                NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobNetInfo != null && mobNetInfo.isConnected()) {
                    netType = mobNetInfo.getExtraInfo();
                }
            }
        } catch (Exception e) {
        }
        return netType;
    }

    public static String getCurrentNetworkIdentifier(Context context) {
        String networkType = getNetworkType(context);
        if (NetworkType.WIFI == networkType) {
            return NetworkType.WIFI;
        } else if (NetworkType.UNKNOWN == networkType) {
            return "";
        } else if (NetworkType.NONETWORK == networkType) {
            return "nonetwork";
        }
        String netName = getCurrentMobileNetworkType(context);
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm != null ? tm.getNetworkType() : 0;
            if (netType != TelephonyManager.PHONE_TYPE_NONE) {
                if (netNameError(netName)) {
                    if (!needReportError) {
                        needReportError = true;
                    }
                    netName = "Mobile(" + netType + ")";
                } else {
                    netName += "(" + netType + ")";
                }
            }
        } catch (Exception e) {
            if (DrLog.DEBUG) {
                DrLog.i("getCurrentNetworkIdentifier", "e:" + Log.getStackTraceString(e));
            }
        }
        return netName;
    }

    private static boolean needReportError;
    private static boolean netNameError(String netName) {
        if (TextUtils.isEmpty(netName)) {
            return true;
        }
        return !netName.matches("[0-9a-zA-Z.]+");
    }

    /**
     * 是否wap
     *
     * @return
     */
/*    public static boolean isWap() {
        Context context = DRCommonApplication.getContext();
        String net = NetworkUtil.getMobileNetworkType(context);
        if (TextUtils.isEmpty(net)) {
            return APNManager.isWap(context);
        } else if (net.toLowerCase().endsWith("net")) {
            return false;
        } else {
            return true;
        }
    }*/



    public static void loadLibrary(String libName) {
        System.loadLibrary(libName);
    }

    /**
     * 是否wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        return NetworkType.WIFI.equals(getNetworkType(context));
    }

    /**
     * 从url中获取host
     *
     * @param url
     * @return
     */
    public static String getHostIP(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            InetAddress[] ips = InetAddress.getAllByName(domain);
            if (ips == null || ips.length <= 0)
                return "";

            return ips[0].getHostAddress();
        } catch (URISyntaxException e) {
        } catch (UnknownHostException e) {
        } catch (Exception e) {
        }

        return "";
    }

    /**
     * 检查SD卡是否可用
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查SD卡的大小是否大于10M
     *
     * @return
     */
    public static boolean checkSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long size = (availableBlocks * blockSize) / 1024 / 1024;
        if (size < 10) {
            return false;
        } else {
            return true;
        }
    }

    public static int getSlideHeaderHeight() {
        return dip2px(DRCommonApplication.getContext(), 195);
    }

    public static int getSlideHeaderHeight(int height) {
        return dip2px(DRCommonApplication.getContext(), height);
    }

    /**
     * 这方法主要是彩铃使用
     *
     * @param fileres
     * @param name
     * @param extname
     * @return
     */
    public static File nameFile(File fileres, String name, String extname) {
        File filedes = null;
        if (!TextUtils.isEmpty(extname)) {
            filedes = new DelFile(fileres, name.trim() + "." + extname.trim());
        } else {
            filedes = new DelFile(fileres, name.trim() + ".mp3");
        }
        return filedes;
    }

    private static String sCacheWifiMac = "";


    //Android 4.4及以上版本沉浸式需要
    @TargetApi(21)
    public static void adjustStatusBar(View view, Context context, boolean isAddView) {
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (isAddView) {
                view.getLayoutParams().height += getStatusBarHeight(context);
            } else {
                view.setPadding(0, getStatusBarHeight(context), 0, 0);
            }
        }
    }

    @TargetApi(21)
    public static void reverseAdjustStatusBar(View view, Context context, boolean isRemoveView) {
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (isRemoveView) {
                view.getLayoutParams().height = 0;
            } else {
                view.setPadding(0, 0, 0, 0);
            }
        }
    }

    @TargetApi(21)
    public static void adjustStatusBar(View view, Context context, ViewParent viewParent) {
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                SystemBarUtil.setSystemUiVisibility(view, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            if (viewParent instanceof RelativeLayout) {
                LayoutParams params = new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof ConstraintLayout) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            }
        }
    }

    public static void adjustStatusBarUseDisplayCutout(View view, Activity context, ViewParent viewParent) {
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            int height = getStatusBarHeightUseDisplayCutout(context);
            if (viewParent instanceof RelativeLayout) {
                LayoutParams params = new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + height);
                view.setLayoutParams(params);
            } else if (viewParent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + height);
                view.setLayoutParams(params);
            } else if (viewParent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + height);
                view.setLayoutParams(params);
            } else if (viewParent instanceof ConstraintLayout) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + height);
                view.setLayoutParams(params);
            }
        }
    }

    public static void adjustStatusBarMarginUseDisplayCutout(View view, Activity context) {
        if (view == null) {
            return;
        }
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            int height = getStatusBarHeightUseDisplayCutout(context);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if(params instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) params).topMargin = height;
                view.setLayoutParams(params);
            }
        }
    }


    public static int getStatusBarAndTitleHeight() {
        return getStatusBarAndTitleHeight(DRCommonApplication.getContext());
    }

    public static int getStatusBarAndTitleHeight(Context context) {
        return (int) (context.getResources().getDimension(
                R.dimen.common_title_bar_height) + getStatusBarHeight(context));
    }

    public static int getStatusBarAndTitleHeightChecked(Context ctx) {
        int titleBarHeight = (int) ctx.getResources().getDimension(R.dimen.common_title_bar_height);
        return (getSdkInt() >= Build.VERSION_CODES.KITKAT)
                ? titleBarHeight + getStatusBarHeight(ctx)
                : titleBarHeight;
    }


    /**
     * 设置沉浸式Style
     *
     * @param view view
     */
    @TargetApi(21)
    public static void setImmersiveStyle(View view) {
        if (SystemUtils.getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (SystemUtils.getSdkInt() >= 21) {

                boolean isXScreen = ScreenHandler.getInstance().isXScreen();
                DrLog.i("setImmersiveStyle", "isXScreen:" + isXScreen);
                SystemBarUtil.setSystemUiVisibility(view, isXScreen ? View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : View.SYSTEM_UI_FLAG_FULLSCREEN, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }
    }

    @TargetApi(21)
    public static void adjustStatusBar(View view, Context context) {
        if (view == null) {
            return;
        }
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (view.getParent() == null) {
                return;
            }
            ViewParent viewParent = view.getParent();
            if (viewParent instanceof RelativeLayout) {
                LayoutParams params = new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            } else if (viewParent instanceof ConstraintLayout) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = (int) (context.getResources().getDimension(
                        R.dimen.common_title_bar_height) + getStatusBarHeight(context));
                view.setLayoutParams(params);
            }
        }
    }

    @TargetApi(21)
    public static void adjustStatusBar(View view, Context context, int height, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        if (view == null) {
            return;
        }
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            if (view.getParent() == null) {
                return;
            }
            ViewParent viewParent = view.getParent();
            if (viewParent instanceof RelativeLayout) {
                LayoutParams params = new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = height + getStatusBarHeight(context);
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                view.setLayoutParams(params);
            } else if (viewParent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = height + getStatusBarHeight(context);
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                view.setLayoutParams(params);
            } else if (viewParent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = height + getStatusBarHeight(context);
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
                view.setLayoutParams(params);
            }
        }
    }

    @TargetApi(21)
    public static void adjustStatusBar(View view, int height) {
        if (view == null) {
            return;
        }
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= 21) {
                SystemBarUtil.setSystemUiVisibility(view, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            if (view.getParent() == null) {
                return;
            }
            ViewParent viewParent = view.getParent();
            if (viewParent instanceof RelativeLayout) {
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                params.height = height + getStatusBarHeight(DRCommonApplication.getContext());
                view.setLayoutParams(params);
            } else if (viewParent instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.height = height + getStatusBarHeight(DRCommonApplication.getContext());
                view.setLayoutParams(params);
            } else if (viewParent instanceof FrameLayout) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.height = height + getStatusBarHeight(DRCommonApplication.getContext());
                view.setLayoutParams(params);
            }
        }
    }

    public static void adjustStatusBar(View view, Context context, int paddingTop) {
        if (view == null) {
            return;
        }
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= Build.VERSION_CODES.LOLLIPOP) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            view.setPadding(0, paddingTop + getStatusBarHeight(context), 0, 0);
        } else {
            view.setPadding(0, paddingTop, 0, 0);
        }
    }

    public static void adjustStatusBarMarginTop(View view, Context context, int marginTop) {
        if (view == null) {
            return;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (getSdkInt() >= Build.VERSION_CODES.KITKAT) {
            if (getSdkInt() >= Build.VERSION_CODES.LOLLIPOP) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            params.topMargin = marginTop + getStatusBarHeight(context);
        } else {
            params.topMargin = marginTop;
        }
        view.requestLayout();
    }

    public static boolean activityDone(Activity activity) {
        if (activity == null) return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        } else {
            return activity.isFinishing();
        }
    }

    /**
     * * 该方法部分全面屏手机，隐藏底部导航栏，仍可以获得导航栏高度
     *
     * @param activity
     * @return
     */
    public static int getPermanentMenuHeight(Activity activity) {
        if (getSdkInt() >= 21) {
            return getDpi(activity) - getScreenSize(activity)[1];
        } else {
            return 0;
        }
    }

    private static int getDpi(Activity activity) {
        int dpi = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取正确铃声 适配Nubia NX505J等机型
     *
     * @param context
     * @param type
     * @return
     */
    public static Uri getActualDefaultRingtoneUri(Context context, int type) {
        // 适配Nubia NX505J等机型
        try {
            if (type == RingtoneManager.TYPE_RINGTONE && Build.BRAND.contains("nubia")) {
                String value = Settings.System.getString(
                        context.getContentResolver(), "card_one_ring_tone");
                if (value != null) {
                    if (DrLog.DEBUG) DrLog.d("PanBC", "card_one_ring_tone:" + value);
                    return Uri.parse(value);
                }
            }
            return RingtoneManager.getActualDefaultRingtoneUri(context, type);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
        }
        return null;
    }


    /**
     * 屏幕是否为亮屏
     * {@link SystemUtils#isScreenOnByDisPlay(Context)}
     *
     * @param context
     * @return
     */
    @Deprecated
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            //如果为true，则表示屏幕“亮”了，否则屏幕“暗”了
            // java.lang.NullPointerException: Attempt to invoke interface method 'boolean android.os.IPowerManager.isInteractive()' on a null object reference
            //	at android.os.PowerManager.isInteractive(PowerManager.java:801)
            //	at android.os.PowerManager.isScreenOn(PowerManager.java:762)
            try {
                isScreenOn = pm.isScreenOn();
            } catch (Exception e) {
                // ignore
            }
        }
        return isScreenOn;
    }

    public static boolean isScreenOnByDisPlay(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = dm.getDisplays();
        for (Display display : displays) {
            if (display.getState() == Display.STATE_ON
                    || display.getState() == Display.STATE_UNKNOWN) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取系统可用内存大小
     *
     * @return MB
     */
    public static String getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        return String.valueOf(mi.availMem / 1024 / 1024);
    }

    /**
     * 获得机身内存总大小
     *
     * @return MB
     */
    @TargetApi(16)
    public static String getTotalMemory(Context context) {

        if (getSdkInt() >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return String.valueOf(mi.totalMem / 1024 / 1024);
        } else {
            return getTotalMemoryByFile();
        }

    }

    public static float getLeftMemoryPercent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            if (DrLog.DEBUG) {
                DrLog.d("musicQueue", "getLeftMemoryPercent:" + (mi.availMem * 1.0f  / mi.totalMem) + ",availMem:" + mi.availMem + ",totalMem：" + mi.totalMem);
            }
            if (mi.totalMem == 0) {
                return 0;
            }
            return mi.availMem * 1.0f / mi.totalMem;
        }
        return 0;
    }

    /**
     * 获得机身内存总大小
     *
     * @param context
     * @return MB
     */
    public static int getMachineMemory(Context context) {
        String machineMemoInf = SystemUtils.getTotalMemory(context);
        int memory = 0;
        try {
            memory = Integer.parseInt(machineMemoInf);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return memory;
    }

    public static String getTotalMemoryByFile() {
        return String.valueOf(getTotalMemoryLongByFile());
    }

    public static boolean hasGyroscope() {
        try {
            SensorManager sensorManager = (SensorManager) DRCommonApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
            Sensor defaultGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (defaultGyroscope != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取进程 占用的内存 PSS
     *
     * @return kb
     */
    public static int getProcPss(Context context, int pid) {

        int pss = 0;

        try {
            final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Debug.MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo(new int[]{pid});
            pss = memoryInfoArray[0].getTotalPss();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return pss;
    }

    /**
     * 判断当前app是否前台运行
     *
     * @param context
     * @return
     */
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前app是否前台运行
     *
     * @param context
     * @return
     */
    public static boolean isProcessRunning(Context context, String processName) {
        //fix crash#639694807421db319a25cf7515faaf37
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

            if (appProcesses == null)
                return false;

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(processName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断当前app是否前台运行 (保活进程专用)
     *
     * @param context
     * @return
     */
    public static boolean isProcessRunningForKeepAlive(Context context, String processName) {
        return isProcessRunningForKeepAlive(context,processName,true);
    }

    public static boolean isProcessRunningForKeepAlive(Context context, String processName,boolean defaultValue) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null || appProcesses.size() == 0) {
                return false;
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(processName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static long getMemTotalRAM() {
        Method _readProclines = null;
        try {
            Class procClass;
            procClass = Class.forName("android.os.Process");
            Class parameterTypes[] = new Class[]{
                    String.class, String[].class, long[].class
            };
            _readProclines = procClass.getMethod("readProcLines", parameterTypes);
            Object arglist[] = new Object[3];
            final String[] mMemInfoFields = new String[]{
                    "MemTotal:", "MemFree:", "Buffers:", "Cached:"
            };
            long[] mMemInfoSizes = new long[mMemInfoFields.length];
            mMemInfoSizes[0] = 30;
            mMemInfoSizes[1] = -30;
            arglist[0] = new String("/proc/meminfo");
            arglist[1] = mMemInfoFields;
            arglist[2] = mMemInfoSizes;
            if (_readProclines != null) {
                _readProclines.invoke(null, arglist);
                // for (int i=0; i < mMemInfoSizes.length; i++) {
                // Log.d("GetFreeMem", mMemInfoFields[i]+" :
                // "+mMemInfoSizes[i]/1024);
                // }
                if (DrLog.DEBUG) DrLog.d("exit", "MemTotal--" + mMemInfoSizes[0] / 1024 + "M");
                return mMemInfoSizes[0] / 1024;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return -1;
    }


    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            boolean result = realSize.y != size.y;
            return result;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 过滤中文字符
     *
     * @param source
     * @return
     */
    public static String filterChineseChar(String source) {
        String regEx = "[\u4e00-\u9fa5]";
        return source.replaceAll(regEx, " ");
    }

    /**
     * 只保留中文字符
     */
    public static String retentionChineseChar(String source) {
        String regEx = "[^\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(source);
        return m.replaceAll("").trim();
    }


    /**
     * 根据空格拆分成字符串数组
     *
     * @param source
     * @return
     */
    public static String[] spitStringBlank(String source) {
        Pattern pattern = Pattern.compile(" ");
        return pattern.split(source);
    }

    /**
     * 清除掉所有特殊字符
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static SpannableStringBuilder convertText2Span(String keyWord, String source) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(keyWord)) {
            return new SpannableStringBuilder(source != null ? source : "");
        }

        keyWord = stringFilter(keyWord).toLowerCase();
        String keyWordArray[] = spitStringBlank(filterChineseChar(keyWord));

        SpannableStringBuilder highLightHintText = new SpannableStringBuilder(source);
        String chineseChar = retentionChineseChar(keyWord);

        int highLightColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.HEADLINE_TEXT);

        //高亮中文字符
        ArrayList<Integer> matchPos = getMatchPos(chineseChar, source);
        Iterator<Integer> it = matchPos.iterator();
        try {
            while (it.hasNext()) {
                int pos = it.next();
                highLightHintText.setSpan(new ForegroundColorSpan(highLightColor), pos, pos + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
        }

        //为了能识别大小写，不要可以使用source
        String mLowerSource = source.toLowerCase();
        //高亮英文字符
        for (int i = 0; i < keyWordArray.length; i++) {
            if (TextUtils.isEmpty(keyWordArray[i])) {
                continue;
            }
            for (int j = 0; j < source.length(); j++) {
                int size = mLowerSource.indexOf(keyWordArray[i], j);
                if (size >= 0) {
                    j = size + keyWordArray[i].length();
                    highLightHintText.setSpan(new ForegroundColorSpan(highLightColor), size, size + keyWordArray[i].length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        }
        return highLightHintText;
    }


    public static ArrayList<Integer> getMatchPos(String keyWord, String source) {
        ArrayList<Integer> matchPos = new ArrayList<>();
        for (int i = 0; i < source.length(); i++) {
            if (keyWord.toLowerCase().contains(String.valueOf(source.charAt(i)).toLowerCase())) {
                matchPos.add(i);
            }
        }
        return matchPos;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     *
     * @param max 指定范围最大值
     * @param min 指定范围最小值
     * @param n   随机数个数
     * @return int[] 随机数结果集
     */
    public static int[] randomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        SecureRandom rd = new SecureRandom();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    /**
     * 获取当前进程线程数量
     *
     * @return
     */
    public static int getThreadCount() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
        // 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
        // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
        int slackSize = topGroup.activeCount() * 2;
        Thread[] slackThreads = new Thread[slackSize];
        // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
        int actualSize = topGroup.enumerate(slackThreads);
//        Thread[] atualThreads = new Thread[actualSize];
        // 复制slackThreads中有效的值到atualThreads
//        System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
        if (DrLog.DEBUG)
            DrLog.d("torahlog SystemUtils", "getThreadCount --- actualSize:" + actualSize);
//        for (Thread thread : atualThreads) {
//            System.out.println("Thread name : " + thread.getName());
//        }
        return actualSize;

    }

    /**
     * @param isFulllScreen true 全屏 false 非全屏
     */
    public static void togleFullScreen(Activity activity, boolean isFulllScreen) {
        try {
            if (activity == null)
                return;

            if (isFulllScreen) {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(params);
                if (SystemUtils.getSdkInt() >= Build.VERSION_CODES.KITKAT) {
                    View decorView = activity.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    decorView.setSystemUiVisibility(uiOptions);
                }
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            } else {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(params);
                if (SystemUtils.getSdkInt() >= Build.VERSION_CODES.KITKAT) {
                    View decorView = activity.getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
                    decorView.setSystemUiVisibility(uiOptions);
                }
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String validateHashValue(String hashValue) {
        if (hashValue != null && hashValue.trim().toLowerCase().equals("null")) {
            return null;
        } else {
            return hashValue;
        }
    }

    /**
     * 获取系统当前时间，单位秒
     *
     * @return
     */
    public static long currentSystemTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }


    public static String getKeyRaw(Object... intArray) {
        String result = assembleString(intArray);
        return result;
    }

    /**
     * 不同类型的参数 拼接字符串
     *
     * @param intArray
     * @return
     */
    public static String assembleString(Object... intArray) {
        StringBuffer data = new StringBuffer();

        for (Object obj : intArray) {
            data.append(obj);
        }

        if (DrLog.isDebug()) {
            String result = "";
            for (Object obj : intArray) {
                result += obj;
            }
            if (DrLog.DEBUG) DrLog.e("gehu-getKeyRaw", "getKeyRaw 0:" + result);
            if (DrLog.DEBUG) DrLog.e("gehu-getKeyRaw", "getKeyRaw 1:" + data.toString());
        }

        return data.toString();
    }

    /*
     * 判断通知权限是否打开
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        try {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass;
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isNotificationEnabledForAll(Context context, String channelId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            NotificationChannel channel = getNotificationChannel(context, channelId);
            return channel != null && SystemUtils.isNotificationEnabledForAll(context);
        } else {
            return SystemUtils.isNotificationEnabledForAll(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel getNotificationChannel(Context context, String channelId) {
        NotificationManager manager = (NotificationManager) DRCommonApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(channelId);
        if (channel == null) {
            KGNotificationChannel.handleNotificationChannel(context, channelId, true);
        }
        channel = manager.getNotificationChannel(channelId);
        return channel;
    }

    public static boolean isNotificationEnabledForAll(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return isNotificationEnabledV26(context);
        }
        return isNotificationEnabled(context);
    }

    /**
     * 版本>=26时的通知栏权限是否开启的逻辑判断
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabledV26(Context context) {
        if (context == null) {
            return true;
        }
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);
            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            return true;
        }
    }

    public static String getMsgFromThrowable(Throwable tw) {
        String Msg = "Stack: ";
        StackTraceElement[] stack = tw.getStackTrace();
        if (stack != null && stack.length > 0) {
            for (int i = 0; i < stack.length; i++) {
                Msg += stack[i].toString();
                Msg += "\n";
            }
        }
        return Msg;
    }

    public static String getMsgFromThrowable(Throwable tw, int getRows) {
        String Msg = "Stack: ";
        StackTraceElement[] stack = tw.getStackTrace();
        if (stack != null && stack.length > 0) {
            for (int i = 0; i < stack.length; i++) {
                if (i > getRows)
                    break;
                Msg += stack[i].toString();
                Msg += "\n";
            }
        }
        return Msg;
    }

    /**
     * 检查手机格式是否合格
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneCorrect(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.length() > 11)
                phoneNumber = phoneNumber
                        .substring(phoneNumber.length() - 11, phoneNumber.length());
            Pattern pattern = Pattern.compile("^[1][3-9]\\d{9}$");
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.find();
        }
        return false;
    }

    public static boolean isMIUISys() {
        if (isMIUISys == null) {
            try {
                isMIUISys = !TextUtils.isEmpty(SystemPropertyUtil.getSystemProperty("ro.miui.ui.version.code"))
                        || !TextUtils.isEmpty(SystemPropertyUtil.getSystemProperty("ro.miui.ui.version.name"))
                        || !TextUtils.isEmpty(SystemPropertyUtil.getSystemProperty("ro.miui.internal.storage"));
            } catch (Exception e) {
                DrLog.printException(e);
                isMIUISys = false;
            }
        }
        return isMIUISys;
    }

/*
    public static boolean isSupportProcessRunning(Context context,boolean isNeedSendException) {
        boolean processRunning = isProcessRunning(context, DRPackage.SUPPORT_PROCESS_NAME);
        boolean serviceRunning = isSupportServiceRunning(context);
        boolean isRunning = processRunning && serviceRunning;
        if (DrLog.DEBUG) DrLog.d("RConnector", "isSupportProcessRunning:" + processRunning + ",serviceRunning:" + serviceRunning);
        if (!isRunning && isNeedSendException) {
            if (DrLog.DEBUG) DrLog.e("RConnector", "检测到后台进程或服务不存在,发送异常统计");
            JSONObject js = new JSONObject();
            try {
                js.put("model", Build.MODEL);
                js.put("sdkversion", Build.VERSION.RELEASE);
                js.put("version", SystemUtils.getVersionCode(context));
                js.put("processisrun", processRunning);
                js.put("serviceisrun", serviceRunning);
//                ExceptionData e = new ExceptionData(ServiceCreateException.OID, ServiceCreateException.EID_CREATE_EXCEPTION);
//                e.setContent(js.toString());
//                StatisticsServiceUtil.execute(new KGExceptionStatisticsSend(context, e));
            } catch (Exception e) {
                DrLog.printException(e);
            }
            return false;
        }

        return isRunning;
    }
*/

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param
     * @return true 在运行 false 不在运行
     */
    public static boolean isSupportServiceRunning(Context mContext) {
        boolean isRunning = false;
        try {
            ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(1000);
            if (serviceList == null || !(serviceList.size() > 0)) {
                return false;
            }
            String serviceName;
            for (int i = 0; i < serviceList.size(); i++) {
                serviceName = serviceList.get(i).service.getClassName();
                if (TextUtils.equals(serviceName, SupportService.class.getName())
                        || TextUtils.equals(serviceName, CommandIntentAcceptor.class.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }catch (Exception e){
            DrLog.printException("RConnector", e);
        }
        return isRunning;
    }

    public static String getNoNetworkStringNoData(Context ctx) {
        if (!SystemUtils.isAvalidNetSetting(ctx)) {
            return ctx != null ? ctx.getString(R.string.ktv_service_error_no_net) : "加载失败，轻触屏幕重试";
        }
        return "";
    }

    public static String getNoNetworkString(Context ctx) {
        if (!SystemUtils.isAvalidNetSetting(ctx)) {
            return ctx != null ? ctx.getString(R.string.no_network) : "未找到可用的网络连接";
        }
        return "";
    }

    public static boolean doCheckNetwork(Context ctx, boolean quiet) {
        return doCheckNetwork(ctx, quiet, false);
    }

    public static boolean doCheckNetwork(Context ctx, boolean quiet, boolean toastWithIcon) {
        return doCheckNetwork(ctx, quiet, toastWithIcon, true);
    }

    public static boolean doCheckNetwork(Context ctx, boolean quiet, boolean toastWithIcon, boolean showOfflineDialog) {
        if (!SystemUtils.isAvalidNetSetting(ctx)) {
            if (!quiet) {
                if (toastWithIcon) {
                    try {
                        ToastUtil.showLoadFailureToast(ctx, ctx != null ? ctx.getString(R.string.no_network) : "未找到可用的网络连接");
                    } catch (Exception e) {
                        //e.printStackTrace();
                        ToastUtil.showToastShort(ctx, R.string.no_network);
                    }
                } else {
                    ToastUtil.showToastShort(ctx, R.string.no_network);
                }
            }
            return false;
        }
        return true;
    }

    public static boolean doCheckNetworkNoToast(Context ctx, boolean quiet) {
        if (!SystemUtils.isAvalidNetSetting(ctx)) {
            return false;
        }
        return true;
    }

    /**
     * @param ctx 必须为activity
     * @return
     */
    public static boolean checkNetwork(Context ctx) {
        return doCheckNetwork(ctx, false, false);
    }

    public static boolean checkNetworkQuietly(Context ctx) {
        return doCheckNetwork(ctx, true, false);
    }

    public static boolean checkNetWork(Context context, int flag) {
        if (!SystemUtils.isAvalidNetSetting(context)) {
            ToastUtil.showToastShort(context, R.string.no_network);
            return false;
        }
        return true;
    }

    public static void showNoNetworkToast(Context ctx) {
        ToastUtil.showToastShort(ctx, R.string.no_network);
    }

    /**
     * 返回小米推送的标志，用于区分事件上报标志
     *
     * @return
     */
    public static String getMiPushTag() {
        return "XIAOMI";
    }

    /**
     * 返回华为推送的标志，用于区分事件上报标志
     *
     * @return
     */
    public static String getHWPushTag() {
        return "HUAWEI";
    }

    public static String getOPPushTag() {
        return "OPPO";
    }

    public static String getOnePlusPushTag() {
        return "ONEPLUS";
    }

    public static String getRealMePushTag() {
        return "REALME";
    }

    public static String getVVPushTag() {
        return "VIVO";
    }


    public static boolean isComponentExist(Context context, Intent intent) {
        if (context == null || intent == null) return false;
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return !infos.isEmpty();
    }

    public static boolean isOppo() {
        final String manufacturer = Build.MANUFACTURER;
        return manufacturer != null && manufacturer.toLowerCase().contains("oppo");
    }

    public static boolean isRealme() {
        final String manufacturer = Build.MANUFACTURER;
        return manufacturer != null && manufacturer.toLowerCase().contains("realme");
    }

    public static boolean isVivo() {
        String vivoVerionName = getVivoVerionName();
        return !TextUtils.isEmpty(vivoVerionName) && !vivoVerionName.equalsIgnoreCase("UNKNOWN");
    }

    public static boolean isOnePlus() {
        final String manufacturer = Build.MANUFACTURER;
        return manufacturer != null && manufacturer.toLowerCase().contains("oneplus");
    }

    public static boolean isHuawei() {
        final String manufacturer = Build.MANUFACTURER;
        return manufacturer != null && manufacturer.toLowerCase().contains("huawei");
    }

    //队列都不为空，且队列hash mixID相同
/*    public static boolean compareSameHashMixIDQueue(String oldMd5) {
        if (TextUtils.isEmpty(oldMd5)) {
            return false;
        }
        KGSong[] currentQueueSongs = PlaybackServiceUtil.getQueueAndConvertToKGSongs();
        if (currentQueueSongs != null && currentQueueSongs.length > 0) {
            String currentQueueMd5 = SystemUtils.getKugouMusicListHash(currentQueueSongs);
            return oldMd5.equalsIgnoreCase(currentQueueMd5);
        }
        return false;
    }*/

    /*
    public static <T> String getKugouMusicListHash(T list) {
        return getKugouMusicListHash(list, -1);
    }

    public static <T> String getKugouMusicListHash(T list, int end) {
        StringBuilder data = new StringBuilder();
        Object[] objarray = new Object[0];
        if (list instanceof List<?>) {
            List objlist = (List) list;
            if (objlist.size() > 0) {
                objarray = new Object[objlist.size()];
                objlist.toArray(objarray);
            }
        } else if (list instanceof Object[]) {
            objarray = (Object[]) list;
        }
        if (objarray.length > 0) {
            if (end <= 0 || end > objarray.length) {
                end = objarray.length;
            }
            for (int i = 0; i < end; i++) {
                Object obj = objarray[i];
                if (obj != null) {
                    if (obj instanceof KGMusicWrapper) {
                        KGMusicWrapper kg = (KGMusicWrapper) obj;
                        if (kg.isConstructFromKGFile()) {
                            data.append(kg.getFileid());
                            if (!TextUtils.isEmpty(kg.getSource())) {
                                data.append(kg.getSource());
                            }
                        } else {
                            if (kg.getKgmusic() != null) {
                                data.append(kg.getKgmusic().getHashValue());
                            }
                        }
                        data.append(kg.getMusicSource());
                        data.append(kg.getMixId());
                        HashOffset hashOffset = kg.getHashOffset();
                        if (hashOffset != null) {
                            data.append(hashOffset.getOffset_hash());
                        }
                        TrackerInfo info = kg.getTrackerInfo();
                        if (info != null) {
                            data.append(info.getMode());
                            data.append(info.getRadioToken());
                            data.append(info.getRadioTimestamp());
                        }

                        String couponID = kg.getCouponID();
                        if (!TextUtils.isEmpty(couponID)) {
                            data.append(kg.getCouponID());
                        }
                        data.append(kg.getPlayChareStatus());


                        MusicTransParamEnenty enenty = kg.getMusicTransParamEnenty();
                        if (enenty != null) {
                            if (enenty.getFeeVoiceAdInfo() != null) {
                                data.append(enenty.getFeeVoiceAdInfo().hashCode());
                            }
                            data.append(enenty.getCpy_attr0());
                            data.append(enenty.getMusicpackAdvance());
                        }
                        if (kg.getFreeListenInfo() != null) {
                            data.append(kg.getFreeListenInfo().hashCode());
                        }
                    } else if (obj instanceof KGFile) {
                        data.append(((KGFile) obj).getFileid());
                        data.append(((KGFile) obj).getMixId());
                    } else if (obj instanceof LocalMusic) {
                        data.append(((LocalMusic) obj).getFileid());
                        data.append(((LocalMusic) obj).getMixId());
                    } else if (obj instanceof KGMusic) {
                        data.append(((KGMusic) obj).getHashValue());
                        data.append(((KGMusic) obj).getMixId());
                    } else if (obj instanceof KGSong) {
                        data.append(((KGSong) obj).getHashValue());
                        data.append(((KGSong) obj).getMixId());
                    }
                }
            }
        }

        String md5 = new MD5Util().getMD5ofStr(data.toString());
        data = null;
        return md5;
    }
    */

    /**
     * 获取华为Umui版本号
     *
     * @return 只要返回不是""，则是EMUI版本
     */
    public static String getEmuiOrMagicVersion() {
        String emuiVerion = "";
        Class<?>[] clsArray = new Class<?>[]{String.class};
        Object[] objArray = new Object[]{"ro.build.version.emui"};
        try {
            Class<?> SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method get = SystemPropertiesClass.getDeclaredMethod("get", clsArray);
            String version = (String) get.invoke(SystemPropertiesClass, objArray);
            if (DrLog.DEBUG) DrLog.d(TAG, "get EMUI version is:" + version);
            if (!TextUtils.isEmpty(version)) {
                return version;
            }
        } catch (ClassNotFoundException e) {
            if (DrLog.DEBUG) DrLog.e(TAG, " getEmuiVersion wrong, ClassNotFoundException");
        } catch (LinkageError e) {
            if (DrLog.DEBUG) DrLog.e(TAG, " getEmuiVersion wrong, LinkageError");
        } catch (NoSuchMethodException e) {
            if (DrLog.DEBUG) DrLog.e(TAG, " getEmuiVersion wrong, NoSuchMethodException");
        } catch (NullPointerException e) {
            if (DrLog.DEBUG) DrLog.e(TAG, " getEmuiVersion wrong, NullPointerException");
        } catch (Exception e) {
            if (DrLog.DEBUG) DrLog.e(TAG, " getEmuiVersion wrong");
        }
        return emuiVerion;
    }

    public static boolean isHonor() {
        return "HONOR".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static String getCustomOs() {
        String customOsType = SystemUtils.getCustomOsType();

        if (isMIUIOSType(customOsType)) {
            return "miui";
        } else if (isFlymeOS(customOsType)) {
            return "flyme";
        } else if (isEMUIOs(customOsType)) {
            return "emui";
        } else if (isFunctionOs(customOsType)) {
            return "function";
        } else if (isColorOsType()) {
            return "color_os";
        } else {
            return "android";
        }

    }

    public static boolean isNeedShortCutPermission() {
        return isNeedShortCutPermission(false);
    }

    public static boolean isNeedShortCutPermission(boolean extra) {
        String verionName = SystemUtils.getMIUIVerionName();
        if (!TextUtils.isEmpty(verionName) && verionName.startsWith("V")) {
            try {
                int version = Integer.parseInt(verionName.substring(1));
                if (version >= 8) {
                    return true;
                }
            } catch (NumberFormatException ex) {
            }
        }

        verionName = SystemUtils.getEMUIVerionName();
        if (verionName.startsWith("EmotionUI_5.")
                || verionName.startsWith("MagicUI")
                || verionName.startsWith("EmotionUI_6.")
                || verionName.startsWith("EmotionUI_7.")
                || verionName.startsWith("EmotionUI_8.")
                || verionName.startsWith("EmotionUI_9.")
                || verionName.startsWith("EmotionUI_10.")
                || verionName.startsWith("EmotionUI_11.")
                || verionName.startsWith("EmotionUI_12.")) {
            return true;
        }

        //ColorOS
        verionName = getColorOsVerionName();

        if (!TextUtils.isEmpty(verionName) && !verionName.equals("UNKNOWN")
                && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        //Vivo
        verionName = getVivoVerionName();

        if (!TextUtils.isEmpty(verionName) && !verionName.equals("UNKNOWN") && verionName.startsWith("Funtouch")
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }

        if (extra) {
            if (!TextUtils.isEmpty(verionName) && verionName.startsWith("OriginOS 1.0")) { //适配 vivo 10 系统
                return true;
            }
        }

        if (DrLog.DEBUG) DrLog.d("isNeedShortCutPermission", "verionName : " + verionName);
        return false;
    }

    private static boolean isColorOsType() {
        String type = SystemUtils.getColorOsVerionName();
        return !(TextUtils.isEmpty(type) || "UNKNOWN".equalsIgnoreCase(type));
    }

    private static boolean isEMUIOs(String osType) {
        return !TextUtils.isEmpty(osType) && PATTERN_OS_TYPE_EMUI.matcher(osType).matches();
    }

    private static boolean isFunctionOs(String osType) {
        return !TextUtils.isEmpty(osType) && PATTERN_OS_TYPE_FUNCTION.matcher(osType).matches();
    }

    public static boolean isFlymeOS(String osType) {
        return !TextUtils.isEmpty(osType) && (PATTERN_OS_TYPE_FLYME.matcher(osType).matches() ||
                osType.toLowerCase().contains("flyme"));
    }

    public static void enableViewDelay(View v, int time) {
        final View view = v;
        if (view != null) {
            view.setEnabled(false);
            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, time);
        }
    }

    public static float getScale(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    /**
     * 检查权限
     *
     * @param context    上下文
     * @param permission 需要检查的权限
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        int flag = localPackageManager.checkPermission(permission, context.getPackageName());
        return flag == PackageManager.PERMISSION_GRANTED;
    }



    /**
     * 获取屏幕高
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getDisplayHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int height = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Class<?> cls = Display.class;
            Class<?>[] parameterTypes = {Point.class};
            Point parameter = new Point();
            Method method = cls.getMethod("getSize", parameterTypes);
            method.invoke(display, parameter);
            height = parameter.y;
        } catch (Exception e) {
            height = display.getHeight();
        }
        return height;
    }

    public static boolean isImage(@NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        } else {
            String lowerAndTrim = name.toLowerCase().trim();
            return lowerAndTrim.endsWith(".jpeg") || lowerAndTrim.endsWith(".jpg") || lowerAndTrim.endsWith(".png") || lowerAndTrim.endsWith(".bmp");
        }
    }

    public static String splitString(String src, int length) {
        if (src.getBytes().length > length) {
            char[] chars = src.toCharArray();
            int size = chars.length;
            StringBuilder temp = new StringBuilder("");
            for (int i = 0; i < size; i++) {
                temp.append(chars[i]);
                if (temp.toString().getBytes().length > length) {
                    return temp.deleteCharAt(temp.length() - 1).toString();
                }
            }
        } else {
            return src;
        }
        return "";
    }

    /**
     * if the json is a correct value, return the timestamp, else , return
     * -1
     *
     * @param jsonString
     * @return
     */
    public static int getTimeStamp(String jsonString) {
        int timeStamp = -1;

        if (TextUtils.isEmpty(jsonString)) {
            return timeStamp;
        }

        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonString);
            int status = jsonObject.getInt("status");
            if (status == 1) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                timeStamp = dataObject.getInt("timestamp");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return timeStamp;
    }


    public static String toNotNull(String raw) {
        return TextUtils.isEmpty(raw) ? "" : raw;
    }

    /**
     * 截取指定长度汉字,避免截取半个汉字<br/>
     * ForExample：cutChineseString("aaa截取汉字",4) result:"aaa截取"
     *
     * @param rawString rawString
     * @param limit     汉字限制
     * @return
     */
    public static String cutChineseString(String rawString, int limit) {
        if (TextUtils.isEmpty(rawString))
            return rawString;
        if (rawString.length() < limit)
            return rawString;
        //转换成字符长度
        int BYTE_LIMIT = limit * 2;
        int byteCount = 0;
        try {
            for (int i = 0; i < Math.min(rawString.length(), BYTE_LIMIT); i++) {
                //中文和全角算两个字符，英文和半角算一个字符
                if (isChineseString(rawString.substring(i, i + 1))) {
                    byteCount += 2;
                } else {
                    byteCount += 1;
                }

                if (byteCount > BYTE_LIMIT) {
                    return rawString.substring(0, i);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return rawString;
        }
        return rawString;
    }

    /**
     * @param sigleString 单字符
     * @return true为汉字，false为数字或英文
     * @throws UnsupportedEncodingException
     */
    private static boolean isChineseString(String sigleString) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(sigleString))
            throw new IllegalStateException("para sigleString cannot be empty");
        if (sigleString.length() != 1)
            throw new IllegalStateException("para sigleString size must be 1");

        return sigleString.getBytes("GBK").length > 1;
    }


    public static String floatArrayToString(float[] array) {
        String st = "[";
        if (array != null && array.length > 0) {
            for (float f : array) {
                st += (String.valueOf(f) + ",");
            }
            st = st.substring(0, st.lastIndexOf(","));
        }
        return st + "]";
    }

    public static String byteArrayToString(byte[] array) {
        String st = "[";
        if (array != null && array.length > 0) {
            for (byte f : array) {
                st += (String.valueOf(f) + ",");
            }
            st = st.substring(0, st.lastIndexOf(","));
        }
        return st + "]";
    }

    public static boolean isMeituPhone() {
        return getPhoneBrand().contains("Meitu");
    }

    /**
     * 如果>0则有虚拟导航栏
     *
     * @param context
     * @return
     */
    public static int getNavibarHeight(Context context) {
        return getScreenHeight(context) - getDisplayHeight(context);
    }


    public static boolean isDarkNotificationTheme(Context context) {
        return !isSimilarColor(Color.BLACK, getNotificationColor(context));
    }

    /**
     * 获取通知栏颜色
     *
     * @param context
     * @return
     */
    public static int getNotificationColor(Context context) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, KGNotificationChannel.NORMAL_MSG_CHANNEL_ID);
            Notification notification = builder.build();
            int layoutId = notification.contentView.getLayoutId();
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null, false);
            if (viewGroup.findViewById(android.R.id.title) != null) {
                return ((TextView) viewGroup.findViewById(android.R.id.title)).getCurrentTextColor();
            }
            return findColor(viewGroup);
        } catch (Exception e) {
            e.printStackTrace();    //系统错误，用try捕获
        }
        return Color.TRANSPARENT;
    }

    public static int findColor(ViewGroup viewGroupSource) {
        int color = Color.TRANSPARENT;
        LinkedList<ViewGroup> viewGroups = new LinkedList<>();
        viewGroups.add(viewGroupSource);
        while (viewGroups.size() > 0) {
            ViewGroup viewGroup1 = viewGroups.getFirst();
            for (int i = 0; i < viewGroup1.getChildCount(); i++) {
                if (viewGroup1.getChildAt(i) instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) viewGroup1.getChildAt(i));
                } else if (viewGroup1.getChildAt(i) instanceof TextView) {
                    if (((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor() != -1) {
                        color = ((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor();
                    }
                }
            }
            viewGroups.remove(viewGroup1);
        }
        return color;
    }


    public static boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < 180.0) {
            return true;
        }
        return false;
    }

    /**
     * @param num          需格式化数
     * @param decimalCount 小数点后位数
     * @param isRound      是否四舍五入
     * @return
     */
    public static String formateDecimal(double num, int decimalCount, boolean isRound) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(decimalCount);
        formater.setGroupingSize(0);
        if (!isRound) {
            formater.setRoundingMode(RoundingMode.FLOOR);
        } else {
            formater.setRoundingMode(RoundingMode.HALF_UP);
        }
        return formater.format(num);
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.2f G", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f M" : "%.2f M", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f K" : "%.2f K", f);
        } else
            return String.format("%d B", size);
    }

    /**
     * 根据画笔获得不超过指定宽度的文本
     *
     * @param paint
     * @param name
     * @param w
     * @return
     */
    public static String getTextByMaxWidth(Paint paint, String name, float w) {
        float a = 0;
        if (paint == null || TextUtils.isEmpty(name) || w <= 0)
            return name;
        for (int i = 0; i < name.length(); i++) {
            a += paint.measureText(name, i, i + 1);
            if (a >= w) {
                int index = i > 0 ? (i - 1) : 0;
                return name.substring(0, index);
            }
        }
        return name;
    }

    public static String getSpecifiedPicSize(String url, int width, int height) {
        String result = "";
        if (TextUtils.isEmpty(url)) {
            return result;
        }
        result = url;
        try {
            if (url.contains("{size}")) {
                result = url.replace("{size}/", "");
            }
            String[] splitArray = result.split("/");
            if (splitArray.length <= 3) {
                return result;
            }
            String host = splitArray[0] + "//" + splitArray[2];
            String type = splitArray[3];
            String filename = result.substring(result.lastIndexOf("/"), result.length());
            String iconsize = "_" + width + "x" + height;
            String format = result.substring(result.lastIndexOf("."), result.length());
            if (result.contains("soft/collection")) {
                type = "soft_collection";
            }
            return host + "/v2/" + type + filename + iconsize + format;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


/*    public static Bitmap base64ToBitmap(String base64ImageString) {
        byte[] bytes = null;
        try {
            bytes = Base64.decode(base64ImageString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytes == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }*/


    /**
     * 判断AccessibilityService服务是否已经启动
     *
     * @param context
     * @param name
     * @return
     */
    public static boolean isStartAccessibilityService(Context context, String name) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }


    public static void openWechat(Context context) {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }

    /**
     * @param bitmap bitmap.isMutable() 必须为true
     * @return
     */
    public static Bitmap getMaskBitmap(Context context, Bitmap bitmap, boolean horizontal) {

        if (!bitmap.isMutable()) {
            if (DrLog.DEBUG)
                DrLog.e("getMaskBitmap", "bitmap.isMutable() 必须为true");
        }


        Canvas canvas = new Canvas(bitmap);
        //获取当前主题背景颜色，设置canvas背景
        //canvas.drawColor(SkinManager.getInstance().getResourceManager().getColor("future_bg"));

        if (horizontal) {
            drawTextToBitmap(context, canvas, bitmap.getHeight(), bitmap.getWidth());
        } else {
            //旋转后会有部分区域没覆盖，所以高度要增加
            drawTextToBitmap(context, canvas, bitmap.getWidth(), (int) (bitmap.getHeight() * 1.2f));
        }


        return bitmap;
        //绘制viewGroup内容
        // viewGroup.draw(canvas);
        //createWaterMaskImage为添加logo的代码，不需要的可直接返回bitmap
        // return createWaterMaskImage(bitmap, BitmapFactory.decodeResource(viewGroup.getResources(), R.drawable.icon_mark));
    }


    /**
     * 给图片添加水印
     *
     * @param context
     * @param canvas  画布
     * @param width   宽
     * @param height  高
     */
    public static void drawTextToBitmap(Context context, Canvas canvas, int width, int height) {
        //要添加的文字
        String logo = "音乐审核专用";
        //新建画笔，默认style为实心
        Paint paint = new Paint();
        //设置颜色，颜色可用Color.parseColor("#6b99b9")代替
        paint.setColor(Color.parseColor("#62b5b9"));
        //设置透明度
        paint.setAlpha(128);
        //抗锯齿
        paint.setAntiAlias(true);
        //画笔粗细大小
        paint.setTextSize((float) SystemUtils.dip2px(context, 18));
        //保存当前画布状态
        canvas.save();
        //画布旋转-30度
        canvas.rotate(-40);
        //获取要添加文字的宽度
        float textWidth = paint.measureText(logo);
        int index = 0;
        //行循环，从高度为0开始，向下每隔80dp开始绘制文字
        for (int positionY = -SystemUtils.dip2px(context, 30); positionY <= height; positionY += SystemUtils.dip2px(context, 50)) {
            //设置每行文字开始绘制的位置,0.83是根据角度算出tan40°,后面的(index++ % 2) * textWidth是为了展示效果交错绘制
            float fromX = -0.83f * height + (index++ % 2) * textWidth;
            //列循环，从每行的开始位置开始，向右每隔2倍宽度的距离开始绘制（文字间距1倍宽度）
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                //绘制文字
                canvas.drawText(logo, positionX, positionY, paint);
            }
        }
        //恢复画布状态
        canvas.restore();
    }


    public static void recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }


    /**
     * 检查手机剩余存储空间是否充足，如果手机剩余存储空间超过200M，返回true，否则返回false
     */
    public static boolean checkDiskAvailableSizeEnough() {
        File path = Environment.getDataDirectory();
        StatFs sfs = new StatFs(path.getPath());
        long blockSize;
        long availableBlock;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = sfs.getBlockSizeLong();
            availableBlock = sfs.getAvailableBlocksLong();
        } else {
            blockSize = sfs.getBlockSize();
            availableBlock = sfs.getAvailableBlocks();
        }
        long availableSize = blockSize * availableBlock;
        return availableSize > 200 * 1024 * 1024;
    }

    private static Boolean isFold;

    public static boolean isFoldMode() {
        if (isFold == null) {
            isFold = false;
            if (isHwFoldableDevice()) {
                isFold = true;
            }

        }
        return isFold;
    }


    static boolean isHwFoldableDevice() {
        if ("HUAWEI".equalsIgnoreCase(Build.MANUFACTURER)
                && DRCommonApplication.getContext().getPackageManager().hasSystemFeature("com.huawei.hardware.sensor.posture")) {
            return true;
        }
        return false;
    }



    public static void adjustNumOfSongsTextSize(TextView view) {
        //多少首已下载
        if (view != null) {
            float curTextSize = view.getTextSize();
            float newTextSize = curTextSize < 38f ? curTextSize : 38f;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        }
    }

    /**
     * 读取assets文件夹中读取字符串
     *
     * @param fileName
     * @return
     */
    public static String getFromAssets(String fileName) {

        BufferedReader bf = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            bf = new BufferedReader(new InputStreamReader(
                    DRCommonApplication.getContext().getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bf);
        }
        return stringBuilder.toString();
    }

    public static void saveAssetsToFile(String assetsFile, String targetFilePath, boolean override) {
        if (TextUtils.isEmpty(targetFilePath)) {
            return;
        }
        File file = new File(targetFilePath);
        if (override) {
            file.delete();
        } else if (file.exists()) {
            //文件已存在 且不是覆盖模式 不用再拷贝
            return;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = DRCommonApplication.getContext().getAssets().open(assetsFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            fos = new FileOutputStream(file);
            fos.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取屏幕真实高度（包括虚拟键盘）
     */
    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        int realHeight = dm.heightPixels;
        return realHeight;
    }


    public static boolean isExistNavigationBar(Context context) {
        if (getDisplayHeight(context) - getRealHeight(context) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void startNotifyActivity(Context context) {
        try {
            SystemUtils.startNotifyEnableActivity(context);
        } catch (Exception e) {
            ToastUtil.show(context, "抱歉！该系统版本不支持直接跳转到接收新消息通知页面");
        }
    }

    //开启系统通知界面，可能报Exception，自行try
    public static void startNotifyEnableActivity(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");// kg-suppress SE.INTENT2 只是用外部应用打开，没有涉及敏感内容
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static float getScreenHwRatio() {
        float v = getDisplayHeight(DRCommonApplication.getContext()) * 1.0f / getDisplayWidth(DRCommonApplication.getContext());
        float v1 = getScreenHeight() * 1.0f / getScreenWidth();
        if (DrLog.DEBUG) {
            DrLog.d(TAG, "getScreenHwRatio: ratio=" + v + " ratio1=" + v1 + " vH=" + getDisplayHeight(DRCommonApplication.getContext()) + " v1H=" + getScreenHeight());
        }
        return v1;
    }

    public static boolean isRootSystem() {
        if (isRootSystem1() || isRootSystem2()) {
            //TODO 可加其他判断 如是否装了权限管理的apk，大多数root 权限 申请需要app配合，也有不需要的，这个需要改su源码。因为管理su权限的app太多，无法列举所有的app，特别是国外的，暂时不做判断是否有root权限管理app
            //多数只要su可执行就是root成功了，但是成功后用户如果删掉了权限管理的app，就会造成第三方app无法申请root权限，此时是用户删root权限管理app造成的。
            //市场上常用的的权限管理app的包名   com.qihoo.permmgr  com.noshufou.android.su  eu.chainfire.supersu   com.kingroot.kinguser  com.kingouser.com  com.koushikdutta.superuser
            //com.dianxinos.superuser  com.lbe.security.shuame com.geohot.towelroot 。。。。。。
            return true;
        } else {
            return false;
        }
    }

    private static boolean isRootSystem1() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean isRootSystem2() {
        List<String> pros = Arrays.asList(System.getenv("PATH").split(":"));
        File f = null;
        try {
            for (int i = 0; i < pros.size(); i++) {
                f = new File(pros.get(i), "su");
                if (f != null && f.exists() && f.canExecute()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * @param context
     * @param packageName
     */
    public static Exception goToMarket(Context context, String packageName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(goToMarket);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ActivityNotFoundException) {
                ToastUtil.show(context, "您的手机没有安装Android应用市场");
            }
            return e;
        }
    }

    /**
     * 获取屏幕分辨率 字符串
     *
     * @param context
     * @return
     */
    public static String getScreenResolution(Context context) {
        int displayWidth = SystemUtils.getScreenWidth(context);
        int displayHeight = SystemUtils.getScreenHeight(context);
        return displayWidth + "x" + displayHeight;
    }


    public static int calculateTopMargin(Context context, Window window) {
        int marginTop;
        long startTs = SystemClock.elapsedRealtime();
        boolean isXScreen = judgeXScreen(window);
        //刘海片：statusBar + 5dp,非刘海：25dp
        if (isXScreen) {
            marginTop = SystemUtils.getStatusBarHeight() + SystemUtils.dip2px(context, 5);
        } else {
            marginTop = SystemUtils.dip2px(context, 25);
        }
        long endTs = SystemClock.elapsedRealtime();
        DrLog.i("lusonTest", String.format(Locale.CHINA, "margin top time cost:%d ,is Xscreen: %s", (endTs - startTs), isXScreen));

        return marginTop;
    }

    public static boolean judgeXScreen(Window window) {
        if (Build.VERSION.SDK_INT >= 28) {
            return SystemUtils.hasDisplayCutout(window) != null;
        } else {
            return ScreenHandler.getInstance().isXScreen();
        }
    }

    public static boolean isInMultiWindowMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return activity != null && activity.isInMultiWindowMode();
        }
        return false;
    }

//    //判断魅族机是否分屏   >>>>如该方法判断失效，请去魅族微信群沟通<<<<
//    public static boolean getMeiZuIsWindowMode(Activity mActivity) {
//        try {
//            if (mActivity == null) return false;
//            ActivityManager activityManager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
//            Class<?> mActivityManagerClass = ActivityManager.class;
//            Method method = mActivityManagerClass.getMethod("isWindowMode", new Class[]{Activity.class});
//            method.setAccessible(true);
//            Boolean isOpenScreenSplit = (Boolean) method.invoke(activityManager, mActivity);
//            if (BuildConfig.DEBUG) {
//                Log.i(TAG, "onClick  --->>>  isOpenScreenSplit:" + isOpenScreenSplit);
//            }
//            return (isOpenScreenSplit == null ? false : isOpenScreenSplit);
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

/*    public static boolean isInMultiWindowModeByCurrentActivity() {
//        Activity mCurrentActivity = ForegroundHelper.getInstance().getForegroundActivity();
        Activity mCurrentActivity = ActivityHolder.getInstance().getCurrentActivity();
        if (mCurrentActivity == null) {
            return false;
        }
        //getMeiZuIsWindowMode(mCurrentActivity)  魅族机不支持自定义状态栏歌词，所以无需判断
        return SystemUtils.isInMultiWindowMode(mCurrentActivity) ;
    }*/

    /**
     * 通过View获取可见区域，通过宽高判断手机是横向还是竖向
     *
     * @param mRootView
     * @return
     */
    public static Boolean judeLandscapePortraitByView(View mRootView) {
        Boolean isLandScape = null;
        Rect mVisibleRect = new Rect();
        mRootView.getWindowVisibleDisplayFrame(mVisibleRect);
        if (mVisibleRect.isEmpty()) {
            return isLandScape;
        }
        isLandScape = mVisibleRect.width() > mVisibleRect.height();//横屏
        return isLandScape;
    }

    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //底部导航栏颜色也要设置
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            //attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }

    /**
     * 代码实现android:fitsSystemWindows
     * 以便全屏显示
     *
     * @param activity
     */
    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows, int rootId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup winContent = activity.findViewById(rootId);
            if (winContent.getChildCount() > 0) {
                ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
                if (rootView != null) {
                    rootView.setFitsSystemWindows(fitSystemWindows);
                }
            }
        }

    }

    public static boolean getIsNightMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static Bitmap getBitmapFromString(String base64String) {
        byte[] byteArray = null;
        byteArray = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);


        if (byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bitmap;
        }
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }


    public static boolean isAdaptSystemDark() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return false;
        }
        return DefaultPrefs.getInstance().isAdaptSystemDarkMode() && SystemUtils.getIsNightMode(DRCommonApplication.getContext())
                && SkinProfileUtil.isDefaultLocalDarkNightSkin()/* && (CommonSettingPrefs.getInstance().getAdapterSystemDarkModeState() == 1)*/;
    }

    public static boolean needAdaptSystemDark() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return false;
        }
        return DefaultPrefs.getInstance().isAdaptSystemDarkMode() && SystemUtils.getIsNightMode(DRCommonApplication.getContext());
    }

    public static void adaptSystemUiMode() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            return;
        }
        //如果设置中适配系统深色模式开关打开
        if (DefaultPrefs.getInstance().isAdaptSystemDarkMode()) {
            boolean isNightModeOn = SystemUtils.getIsNightMode(DRCommonApplication.getContext());
            boolean isLocalSimpleSkin = SkinProfileUtil.isDefaultLocalSimpleSkin();
            boolean isLocalDarkSkin = SkinProfileUtil.isDefaultLocalDarkNightSkin();
            if (DrLog.DEBUG) {
                DrLog.i(TAG, "onConfigurationChanged: isNightModeOn=" + isNightModeOn + " simpleSkin=" + isLocalSimpleSkin
                        + " isLocalDarkSkin=" + isLocalDarkSkin);
            }
            //如果系统是深色模式
            if (isNightModeOn) {
                //酷狗皮肤当前是极简模式，则切换成酷狗耀黑皮肤模式
                if (isLocalSimpleSkin) {
                    // 欢迎视频播放完之前不显示 toast
                    boolean isGuideVideoShowed = SharedPreferencesForeProcess.hadShowGuide() &&
                            !SharedPreferencesForeProcess.isGuideVideoShowing();
                    if (CommonSettingPrefs.getInstance().getAdapterSystemDarkModeState() == -1 && isGuideVideoShowed) {
                        ToastUtil.show(DRCommonApplication.getContext(), "已适应系统自动切换为曜夜皮肤");
                    }
                    SkinManager.getInstance().adaptSystemDarkMode();
                    CommonSettingPrefs.getInstance().setAdaptSystemDarkModeState(1);
                }
            } else {
                //系统是非黑夜模式，并且当前是酷狗耀黑皮肤模式（并且这种耀黑模式是适配系统深色模式自动切换的）
                if (isLocalDarkSkin) {
                    if (CommonSettingPrefs.getInstance().isDefaultSimpleAdaptSystemDarkInUse()) {
                        SkinManager.getInstance().resetDefaultSkin(false);
                        CommonSettingPrefs.getInstance().setDefaultSimpleAdaptSystemDarkInUse(false);
                    }
                }
            }
        }
    }

    //public static void sendUiModeChangeNotify(Configuration newConfig) {
    //    try {
    //        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
    //            return;
    //        }
    //        boolean isNightModeOn = SystemUtils.getIsNightMode(DRCommonApplication.getContext());
    //        if (DrLog.DEBUG) {
    //            DrLog.i(TAG, "lxj onConfigurationChanged isNightModeOn=" + isNightModeOn);
    //        }
    //        JSONObject jsonObject = new JSONObject();
    //        jsonObject.put("flag", isNightModeOn ? 1 : 0);
    //        //为了防止h5重新load的导致没接收到的时间差
    //        KGThreadPool.postToMainThread(new Runnable() {
    //            @Override
    //            public void run() {
    //                EventBus.getDefault().postSticky(new CrossToH5Event("androidToggleUiMode", jsonObject.toString()));
    //            }
    //        }, 200);
    //    } catch (Throwable e) {
    //        e.printStackTrace();
    //    }
    //}



    public static String identityToString(Object obj){
        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    public static int identityToInteger(Object obj){
        return System.identityHashCode(obj);
    }


    public static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static boolean isEMUIOs() {
        String versionName = getEMUIVerionName();
        //EmotionUI_10.0.0
        if (versionName != null && versionName.startsWith("EmotionUI") && !isHMOS()) {
            return true;
        }
        return false;
    }

    /**
     * 在小米机型MIUI机型上（10.0及以上）和华为EMUI（10.0及以上）使用系统通知栏样式时，
     * 由于系统会将默认专辑图吸色，UI设计师特意设计了一个图片给这种场景。。。。。
     * @return
     */
    private static Boolean isUseSpecialIcon = null;

    public static boolean isUseSpecialNotifyDefIcon() {
        if (DrLog.DEBUG) {
            DrLog.d("jamesNotifyIcon", "getHuaweiApiLevel= " + getHuaweiApiLevel()
                    + ",isMIUISys()=" + isMIUISys() + ",isEMUIOs()=" + isEMUIOs() + isVivo() + ",isHMOS()=" + isHMOS()
                    + ",isColorOsType()=" + isColorOsType() + ",isVivo()=" + isVivo() + ",isUseSpecialIcon=" + isUseSpecialIcon);
        }
        if (isUseSpecialIcon != null) {
            return isUseSpecialIcon;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            isUseSpecialIcon = false;
            return isUseSpecialIcon;
        }
        if (isMIUISys() || isEMUIOs()) {
            isUseSpecialIcon = true;
            return isUseSpecialIcon;
        }
        isUseSpecialIcon = false;
        return isUseSpecialIcon;
    }



}
