package com.brins.commom.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.brins.commom.BuildConfig;
import com.brins.commom.utils.AppUtil;

import static com.brins.commom.app.DRPackage.FORE_PROCESS_NAME;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class DRCommonApplication extends Application {

    private static DRCommonAppImpl mCommonAppImpl;

    protected static Application mContext;

    private static Thread mMainThread;

    public static boolean isExiting = false;

    public static int processType = -1;
    public static String processName;

    private static final int BACK_SUPPORT_PROCESS = 0; // 也就是后台进程support
    private static final int FORE_PROCESS = 1; // 前台进程,主进程


    public static Context getContext() {
        return mContext;
    }

    public Application getApplication() {
        return this;
    }
    /**
     * 当前运行时是否是前台进程
     */
    public static boolean isForeProcess() {
        return processType == FORE_PROCESS;
    }

    public static boolean isSupportProcess() {
        return processType == BACK_SUPPORT_PROCESS;
    }

    public static String getAppPackageName() {
        return mContext.getPackageName();
    }

    /**
     * 主线程
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
        mMainThread = Thread.currentThread();
        mCommonAppImpl = new DRCommonAppImpl();
        mCommonAppImpl.attachBaseContext(this);
        initProcessType();
    }

    private void initProcessType() {
        processName = AppUtil.getCurrentProcessName(getApplication());
        if (FORE_PROCESS_NAME.equals(processName)) {// 主进程
            processType = FORE_PROCESS;
        } else {
            processType = -1;
        }
        Log.i("DRCommonApplication", "initProcessType " + processName + ", " + processType);
    }


    @Override public void onCreate() {
        super.onCreate();
        mCommonAppImpl.onCreate();
    }

    public static boolean hasBasicPermission() {
        if (!isForeProcess()) {
            return DRCommonAppImpl.checkHasPermissionForOtherProcess();
        }
        return DRCommonAppImpl.isHasBasicPermission();
    }
}
