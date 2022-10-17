package com.brins.commom.app;

import android.util.Log;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class DRPackage {
    public static final String OFFICIAL_PKG_NAME = "com.brins.dreamify";
    public static final String PKG_NAME = OFFICIAL_PKG_NAME;

    public static final String PKG_PATH = "/data/data/" + PKG_NAME;

    public static boolean isDreamifyPackage() {
        boolean value = DRCommonApplication.getAppPackageName().equals(OFFICIAL_PKG_NAME);
        Log.d("KGPackage", "KGPackage.isKugouPackage() = " + value);
        return value;
    }

}
