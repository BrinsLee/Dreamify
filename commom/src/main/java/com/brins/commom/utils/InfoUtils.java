package com.brins.commom.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.entity.NetworkType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public class InfoUtils {

    /**
     * 获取设备屏幕大小
     *
     * @param context
     * @return 0 width,1 height
     */
    public static int[] getScreenSize(Context context) {
        if (context == null) {
            context = DRCommonApplication.getContext();
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return new int[]{
            screenWidth, screenHeight
        };
    }

    /**
     * 获取当前app版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    public static byte[] getFileData(String path) {
        byte[] data = null;// 返回的数据
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                data = new byte[(int) file.length()];
                fis = new FileInputStream(file);
                int ret = fis.read(data);
                if (ret < 0) {
                    Log.e("InfoUtils", "getFileData read failed ret: " + ret);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 获取网络类型,最高支持4G网络
     * @return 网络类型
     */
    @SuppressLint("MissingPermission")
    public static String getNetworkType(Context context) {
        ConnectivityManager cm = null;
        try {
            if (context == null) {
                context = DRCommonApplication.getContext();
            }
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cm == null) {
            return NetworkType.UNKNOWN;
        }
        try {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null) {
                return NetworkType.NONETWORK;
            }
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.WIFI;
            }
            // 手机通过数据线连接电脑上网，网络类型当做WiFi处理。
            if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return NetworkType.WIFI;
            }
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm.getNetworkType();
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
                    Log.e("kugou", "getNetworkType returns a unknown value:" + netType);
                    return NetworkType.NET_4G;
            }
        } catch (SecurityException e) {
            KGAssert.fail(e);
            return NetworkType.UNKNOWN;
        } catch (Exception e) {
            // 修改崩溃 机型8185
            return NetworkType.UNKNOWN;
        } catch (OutOfMemoryError e) { // kg-suppress REGULAR.ERROR 合并重复代码，保留之前改动
            return NetworkType.UNKNOWN;
        }
    }

}
