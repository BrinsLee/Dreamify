package com.brins.commom.app;

import android.app.Application;
import android.content.Context;

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

    private static final int BACK_SUPPORT_PROCESS = 0; // 也就是后台进程support
    private static final int FORE_PROCESS = 1; // 前台进程,主进程


    public static Context getContext() {
        return mContext;
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

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
        mMainThread = Thread.currentThread();

        initProcessType();
    }

    private void initProcessType() {

    }
}
