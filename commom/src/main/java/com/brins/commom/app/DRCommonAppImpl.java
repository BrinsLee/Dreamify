package com.brins.commom.app;

import android.app.Application;
import com.brins.commom.BuildConfig;
import com.brins.commom.page.callback.KGCrashHandlerCallback;
import com.brins.commom.page.callback.KGFrameworkCallback;
import com.brins.commom.permission.PermissionHandler;
import com.brins.commom.skin.SkinManager;
import com.kugou.common.permission.KGPermission;
import com.kugou.page.FrameworkCallbacks;
import com.kugou.uilib.KGUI;

import static com.brins.commom.app.DRCommonApplication.getContext;
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
                getContext().getClassLoader());
        } catch (ClassNotFoundException e) {
            hasBasicPermission = false;
            return;
        }
        if (isForeProcess()) {
            KGPermission.enableChecker = true;
            hasBasicPermission = PermissionHandler.hasBasicPermission(getContext());
        } else {
            hasBasicPermission = true;
        }
    }

    static boolean isHasBasicPermission() {
        return hasBasicPermission;
    }

    static boolean checkHasPermissionForOtherProcess() {
        return hasBasicPermission;
    }

    static void setHasBasicPermission(boolean hasBasicPermission) {
        DRCommonAppImpl.hasBasicPermission = hasBasicPermission;
    }

    void attachBaseContext(DRCommonApplication host) {
        mHost = host;
        mApp = host.getApplication();

        //LottieAssist.initLottieNetWork();
        //Retrofit.initHttpVarsClassName(KGHttpVariables.class.getName());
        if (DRCommonApplication.isForeProcess() && isHasBasicPermission()) {

        }
    }

    void onCreate() {
        KGUI.getInstance().init()
            .setImageAccessibility(null)
            .setUploadExceptionListener(null)
            .setKGUILog(false) // 这里不要使用KGLog.DEBUG
            .setAppContext(mApp)
            .build();
        initSkin(true);
        initThirdSdk();
    }

    //void initKGLog() {
    //    DrLog.init();
    //}

    void initSkin(boolean hasBasicPermission) {
        if (DRCommonApplication.isForeProcess()) {
            if (hasBasicPermission) {
                SkinManager.getInstance().init();
            } else {
                SkinManager.getInstance().setSkinEnv(false);
            }
        }
    }

/*    public void removeSkinLoadListener(SkinLoader.SkinLoadListener listener) {
        checkSkinLoader();
        mSkinLoader.removeSkinLoadListener(listener);
    }

    public void addSkinLoadListener(SkinLoader.SkinLoadListener listener) {
        checkSkinLoader();
        mSkinLoader.addSkinLoadListener(listener);
    }*/

    void initThirdSdk() {
        FrameworkCallbacks.getInstance().setCallback(new KGFrameworkCallback());
        FrameworkCallbacks.getInstance().setCrashHandlerCallback(new KGCrashHandlerCallback());
    }
}
