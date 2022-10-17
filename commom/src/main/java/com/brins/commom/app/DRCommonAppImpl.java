package com.brins.commom.app;

import android.app.Application;
import com.kugou.common.permission.KGPermission;

import static com.brins.commom.app.DRCommonApplication.isForeProcess;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class DRCommonAppImpl {

    private final static String TG = "vz-DRCommomAppImpl";
    private DRCommonApplication mHost;

    private Application mApp;
    private IApplication debugApplication;
    private static boolean hasBasicPermission = false;

    DRCommonAppImpl() {
        initAndCheckBasicPermission();
        //initKGKV();

    }

    private void initAndCheckBasicPermission() {
        try {
            Class.forName("com.kugou.common.permission.KGPermission", false,
                DRCommonApplication.getContext().getClassLoader());
        } catch (ClassNotFoundException e) {
            hasBasicPermission = false;
            return;
        }
        if (isForeProcess()) {
            KGPermission.enableChecker = true;

        }
    }
}
