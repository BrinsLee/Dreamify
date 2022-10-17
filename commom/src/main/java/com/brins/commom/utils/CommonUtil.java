package com.brins.commom.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.brins.commom.utils.log.DrLog;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public class CommonUtil {

    public static void Log(String tag, String log) {
        if(DrLog.isDebug()) {
            DrLog.d(tag, log);
        }
    }

    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        int flag = PackageManager.PERMISSION_GRANTED;
        try {
            flag = localPackageManager.checkPermission(permission, context.getPackageName());
        } catch (Exception e) {
        }
        return flag == PackageManager.PERMISSION_GRANTED;
    }

    public static String getOsVersion(Context context) {
        String osVersion = "";
        if(checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            osVersion = android.os.Build.VERSION.RELEASE;
            Log("android_osVersion", "OsVerson" + osVersion);
            return osVersion;
        } else {
            Log("android_osVersion", "OsVerson get failed");
            Log(" lost  permission", "lost----> android.permission.READ_PHONE_STATE");
            return null;
        }
    }
}
