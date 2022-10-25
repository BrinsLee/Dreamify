
package com.brins.commom.utils;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

/**
 * 描述:APP通用工具
 * 
 * @author haichaoxu
 * @since 2014-6-3 上午10:57:45
 */
public class AppUtil {

    /**
     * 获取应用名称
     * 
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String appName = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            appName = context.getString(ai.labelRes);
        } catch (NameNotFoundException e) {
        }
        return appName;
    }

    /**
     * 获取versionName
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String result = "1.0";

        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException var3) {

        }

        return result;
    }

    public static String getCurrentProcessName(Context context) {
        String processName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            processName = Application.getProcessName();
        }
        // firstly, get form ActivityThread
        if (TextUtils.isEmpty(processName)) {
            try {
                Object activityThread = getCurrentActivityThread(context);
                if (activityThread != null) {
                    Method getProcessName = activityThread
                            .getClass().getDeclaredMethod("getProcessName");
                    getProcessName.setAccessible(true);
                    processName = (String) getProcessName.invoke(activityThread);
                    Log.i("burone-pn", "p1 = " + processName);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        // secondly, get from cmdline file
        if (TextUtils.isEmpty(processName)) {
            int pid = android.os.Process.myPid();
            BufferedReader cmdlineReader = null;
            try {
                cmdlineReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream("/proc/" + pid + "/cmdline"),
                        "iso-8859-1"));
                int c;
                StringBuilder tempName = new StringBuilder();
                while ((c = cmdlineReader.read()) > 0) {
                    tempName.append((char) c);
                }
                processName = tempName.toString().trim();
                Log.i("burone-pn", "p2 = " + processName);
            } catch (Throwable t2) {
                t2.printStackTrace();
            } finally {
                if (cmdlineReader != null) {
                    try {
                        cmdlineReader.close();
                    } catch (IOException ignore) {}
                }
            }
        }
        return processName == null ? "" : processName.trim();
    }

    public static Object getCurrentActivityThread(Context context) {
        Object currentActivityThread = null;
        try {
            Class activityThread = Class.forName("android.app.ActivityThread");
            Method m = activityThread.getMethod("currentActivityThread");
            m.setAccessible(true);
            currentActivityThread = m.invoke(null);
            if (currentActivityThread == null && context != null) {
                // In older versions of Android (prior to frameworks/base 66a017b63461a22842)
                // the currentActivityThread was built on thread locals, so we'll need to try
                // even harder
                Application app = (Application) context.getApplicationContext();
                Field mLoadedApk = app.getClass().getField("mLoadedApk");
                mLoadedApk.setAccessible(true);
                Object apk = mLoadedApk.get(app);
                Field mActivityThreadField = apk.getClass().getDeclaredField("mActivityThread");
                mActivityThreadField.setAccessible(true);
                currentActivityThread = mActivityThreadField.get(apk);
            }
        } catch (Exception ignore) {}
        return currentActivityThread;
    }

    /**
     * @param context
     * @param path
     * @return
     */
    public static InputStream getAssetsFile(Context context, String path) {
        AssetManager am = context.getAssets();
        InputStream is;
        try {
            is = am.open(path);
        } catch (IOException e) {
            is = null;
        }
        return is;
    }

    //public static String getIMEI(Context context) {
    //    return InfoUtils.getIMEI(context);
    //}

    /**
     * 获取手机内部存储
     * 
     * @param context
     * @return
     */
    public static long getInternalAvailableBlocks(Context context) {
        // String mTotalSize = "内部总容量：";
        // String mAvailSize = "内部剩余容量：";
        StatFs statInternal = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statInternal.getBlockSize();
        // long totalBlocks = statInternal.getBlockCount() * blockSize;
        long availableBlocks = statInternal.getAvailableBlocks() * blockSize;
        // mTotalSize += Formatter.formatFileSize(context, totalBlocks *
        // blockSize);
        // mAvailSize += Formatter.formatFileSize(context, availableBlocks *
        // blockSize);
        return availableBlocks;
    }

    /**
     * 获取AndroidManifest.xml中的meta_date值
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getMetaData(Context context, String key) {
        String metaData = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            metaData = bundle.getString(key);
        } catch (NameNotFoundException e) {
        }
        return metaData;
    }

    /**
     * @return
     */
    public static long getRAMTotalSize() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;
        try {
            localFileReader = new FileReader(str1);
            localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            if (str2 != null) {
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            }
            return initial_memory;
        } catch (IOException e) {
            return -1;
        } finally {
            IOUtils.closeQuietly(localFileReader);
            IOUtils.closeQuietly(localBufferedReader);
        }
    }

    /**
     * 读取RAW文件内容
     * 
     * @param context
     * @param resid
     * @param encoding
     * @return
     */
    public static String getRawFileContent(Context context, int resid, String encoding) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resid);
        } catch (Exception e) {
            // Resource NotFoundException
        }
        if (is != null) {
            ByteArrayBuffer bab = new ByteArrayBuffer(1024);
            int read;
            try {
                while ((read = is.read()) != -1) {
                    bab.append(read);
                }
                return EncodingUtils.getString(bab.toByteArray(), encoding);
            } catch (UnsupportedEncodingException e) {
            } catch (IOException e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }

    /**
     * @return
     */
    public static long getROMTotalSize() {
        StatFs statInternal = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statInternal.getBlockSize();
        return statInternal.getBlockCount() * blockSize;
    }

    @SuppressWarnings("deprecation")
    public static int[] getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        return new int[] {
                display.getWidth(), display.getHeight()
        };
    }

    /**
     * 判断是否锁屏
     * 
     * @return true 锁屏状态， false 非锁屏状态
     */
    public static boolean isScreenOff(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    /**
     * 获取应用签名
     * 
     * @param context
     * @param packageName
     * @return
     */
    public static String getSignature(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            Signature[] asignature = pi.signatures;
            int j = asignature.length;
            for (int i = 0; i < j; i++) {
                Signature signature = asignature[i];
                String s =  new MD5Util().getMD5StrOfBytes(signature.toByteArray());
                if (!TextUtils.isEmpty(s)) {
                    return s;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
