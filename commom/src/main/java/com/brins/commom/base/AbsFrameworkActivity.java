
package com.brins.commom.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.brins.commom.R;
import com.brins.commom.toast.LoadingTypes;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.commom.widget.KGProgressDialog;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

public class AbsFrameworkActivity extends AbsSkinActivity {

    private final static long DEFAULT_VALUE = -1;

    private long mOnResumeTime = DEFAULT_VALUE;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private boolean mIsWindowLevelHWA = true;

    /**
     * 在onCreate()中执行super.onCreate()之前调用该方法，可以关闭activity级别硬件加速
     */
    public void disableWindowLevelHWA() {
        mIsWindowLevelHWA = false;
    }

    // end----------------硬件加速代码----------------

    public ViewPagerFrameworkDelegate getDelegate() {
        return null;
    }
    
    private float curFontScale;

    private boolean isActivityResumed = false;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Configuration config=getResources().getConfiguration();
        curFontScale = config.fontScale;
        // 该块执行时间 10ms
        super.onCreate(savedInstanceState);
        if (SystemUtils.isEnableHardwareAccelerated() && SystemUtils.hasHoneycomb()
                && mIsWindowLevelHWA) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }
    

    @Override
    protected void onResume() {
        mOnResumeTime = System.currentTimeMillis();
        super.onResume();
        if (!careBootSpeed()) {
            doOnResume();
        }
        isActivityResumed = true;
    }

    /**
     * 覆盖该方法并返回true时，{@link #doOnResume} 必须在合适的时机自己调用。
     * 目前引入只为 MediaActivity
     * @return
     */
    protected boolean careBootSpeed() {
        return false;
    }

    public boolean isActivityResumed() {
        return isActivityResumed;
    }

    /**
     * 该方法必须允许在子线程调用
     */
    protected void doOnResume() {
        //updateIsNotBackgroundForPlayBackWhenOnResume();
    }

    Handler playBackStateHandler = new StackTraceHandler(Looper.myLooper());
    //同步是否后台的状态到播放上报，
    static boolean isShouldSyncLastActivityPauseToPlayBack = true;
    public static boolean isAfterScreenOff = false;
/*    public void updateIsNotBackgroundForPlayBackWhenOnResume() {
        if (!KGCommonApplication.isForeProcess()) {
            return;
        }
        try {
            //因为5.0 以上只能拿到自己的activity
            if (!KGSystemUtil.getTopTaskInfo(true).className.equals("com.kugou.android.app.lockscreen.LockScreenActivity")) {
                // 设置为前台进程
                AppRunStateUtil.updateBackgroundForDataCollector(false);
                PlaybackTask.setBackground(false);
                // 重新进入前台，设置成使用状态
                SharedPreferencesForeProcess.saveAppState(2);
                isShouldSyncLastActivityPauseToPlayBack = false;
            }
        } catch (Exception e) {
            com.kugou.common.utils.DrLog.uploadException(e);
        }
    }*/


    @Override
    protected void onPause() {
        isActivityResumed = false;

        super.onPause();
        if (DrLog.DEBUG) DrLog.i("StatisticsBI onPause",this.getClass().getName());
        isShouldSyncLastActivityPauseToPlayBack = true;
        updateIsBackgroundForPlayBackWhenOnPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityResumed = false;
    }

    /**
     * 由于5.0以上获取顶层acticity都只能获取自己的 activity 所以在跳到别的app是无法检测出来的
     * 所以5.0以上每个activity pause都更新一次在后台。
     */
    @TargetApi(LOLLIPOP)
    public void updateIsBackgroundForPlayBackWhenOnPause() {
        /*if (isShouldSyncLastActivityPauseToPlayBack && !isAfterScreenOff) {
            AppRunStateUtil.updateBackgroundForDataCollector(true);
        }
        if (Build.VERSION.SDK_INT < LOLLIPOP) {
            return;
        }

        if (!KGCommonApplication.isForeProcess()) {
            return;
        }

        playBackStateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //本应用页面跳转 或者 锁屏都去掉 1s中之后为了同步锁屏广播 锁屏不算影响前后台的因数
                if (isShouldSyncLastActivityPauseToPlayBack&&!isAfterScreenOff) {
                    PlaybackTask.setBackground(true);
                    // 后台隐藏时，设置成非崩溃状态
                    SharedPreferencesForeProcess.saveAppState(0);
                }
            }
        },1000);*/
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Configuration config=getResources().getConfiguration();
        if (DrLog.DEBUG) DrLog.i("StatisticsBI onDestroy",this.getClass().getName());
        dismissProgressDialog();
        if (mWorker != null) {
            mWorker.quit();
            mWorker = null;
	    }

    }

    /**
     * 从外部打开页面的回调
     *
     * @param args
     */
    public void onNewBundle(Bundle args) {

    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        super.startActivityFromFragment(fragment, intent, requestCode);
        onStartActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        onStartActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
            onStartActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onStartActivity(Intent intent) {
        // ToastCompat.makeText(this, "startactivity", Toast.LENGTH_SHORT).show();
        boolean isDialogActivity = false;
        try {
            Context ctx = createPackageContext(intent.getComponent().getPackageName(),
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class clazz = ctx.getClassLoader().loadClass(intent.getComponent().getClassName());
            if (ActivityDialogable.class.isAssignableFrom(clazz)) {
                isDialogActivity = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isDialogActivity) {
            overridePendingTransition(R.anim.comm_activity_open_enter, R.anim.comm_activity_open_exit);
        }
    }

    @Override
    public void finish() {

        overridePendingTransition(R.anim.comm_activity_close_enter, R.anim.comm_activity_close_exit);
        super.finish();
    }

    public void finishWithoutAnimation() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    // start--------------后台handler的looper代码----------------
    protected HandlerThread mWorker;

    /**
     * 获得后台线程的looper
     *
     * @return 后台线程的looper
     */
    public Looper getWorkLooper() {
        if (mWorker == null) {
            mWorker = new HandlerThread(this.getClass().getName(), getWorkLooperThreadPriority());
            mWorker.start();
        }
        return mWorker.getLooper();
    }

    /**
     * 获得后台线程的优先级 默认是Thread.NORM_PRIORITY
     *
     * @return 优先级@see #Thread.NORM_PRIORITY
     */
    protected int getWorkLooperThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    // end----------------后台handler的looper代码----------------

    // end----------------显示toast代码----------------

    // start--------------显示等待progress代码----------------
    protected KGProgressDialog mProgressDialog;

    protected void setDialogText(View v) {

        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                setDialogText(child);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // 是textview，设置字体dp
        }
    }


    private void showProgressDialog(final boolean cancelable, final boolean canCancelOutside,
                                    final String text,
                                    final int loadingPageId,
                                    final int loadingType,
                                    final DialogInterface.OnKeyListener keyListener,
                                    final DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(cancelable, canCancelOutside, text, null, loadingPageId, loadingType, keyListener, dismissListener);
    }

    private void showProgressDialog(final boolean cancelable, final boolean canCancelOutside,
                                    final String text, String secondStr,
                                    final int loadingPageId,
                                    final int loadingType,
                                    final DialogInterface.OnKeyListener keyListener,
                                    final DialogInterface.OnDismissListener dismissListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null)
                    mProgressDialog = new KGProgressDialog(AbsFrameworkActivity.this);

                mProgressDialog.setCancelable(cancelable);
                mProgressDialog.setCanceledOnTouchOutside(canCancelOutside);
                mProgressDialog.setLoadingText(text, secondStr);
                mProgressDialog.setOnKeyListener(keyListener);
                mProgressDialog.setOnDismissListener(dismissListener);
                mProgressDialog.setLoadingType(loadingType);
                if (!isFinishing() && !mProgressDialog.isShowing())
                    mProgressDialog.show();

                View v = mProgressDialog.getWindow().getDecorView();
                setDialogText(v);
            }
        });
    }
    private void showProgressDialog(final boolean cancelable, final boolean canCancelOutside,
                                    final String text,
                                    final int loadingType,
                                    final DialogInterface.OnKeyListener keyListener,
                                    final DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(cancelable, canCancelOutside, text,
                loadingType,
                keyListener, dismissListener);
    }
    public void showProgressDialog(final boolean cancelable, final boolean canCancelOutside,
                                    final String text,
                                    final DialogInterface.OnKeyListener keyListener,
                                    final DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(cancelable, canCancelOutside, text,
                LoadingTypes.FLOATING,
                keyListener, dismissListener);
    }

    /**
     * 请指明弹窗所属的页面
     * {@link AbsFrameworkActivity#showProgressDialog(int, int, boolean, boolean)}
     */
    @Deprecated
    public void showProgressDialog(boolean cancelable, boolean canCancelOutside) {
        showProgressDialog(cancelable, canCancelOutside, getString(R.string.waiting), null, null);
    }

    /**
     * 请指明弹窗所属的页面
     * {@link AbsFrameworkActivity#showProgressDialog(int, int)}
     */
    @Deprecated
    public void showProgressDialog() {
        showProgressDialog(true, false, getString(R.string.waiting), null, null);
    }


    /**
     * 请指明弹窗所属的页面与类型
     * {@link AbsFrameworkActivity#showProgressDialog(int, int, boolean)}
     */
    @Deprecated
    public void showProgressDialog(boolean canCancel) {
        showProgressDialog(true, canCancel, getString(R.string.waiting), null, null);
    }

    /**
     * 请指明弹窗所属的页面与类型
     * {@link AbsFrameworkActivity#showProgressDialog(int, int, DialogInterface.OnDismissListener)}
     */
    @Deprecated
    public void showProgressDialog(DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(true, true, getString(R.string.waiting), null, dismissListener);
    }

    /**
     * 请指明弹窗所属的页面与类型
     */
    @Deprecated
    public void showProgressDialog(boolean canCancel, String text) {
        showProgressDialog(true, canCancel, text, null, null);
    }

    /**
     * 请指明弹窗所属的页面与类型
     */
    @Deprecated
    public void showProgressDialog(String text, DialogInterface.OnKeyListener onKeyListener, boolean isCancelable) {
        showProgressDialog(isCancelable, false, text, onKeyListener, null);
    }

    /**
     * 请指明弹窗所属的页面与类型
     */
    @Deprecated
    public void showCannotCacenlProgressDialog(int titleResID, DialogInterface.OnKeyListener onKeyListener) {
        showProgressDialog(false, false, getString(titleResID), onKeyListener, null);
    }

    /**
     * 请指明弹窗所属的页面与类型
     */
    @Deprecated
    public void showCannotCacenlProgressDialog() {
        showProgressDialog(false, false, getString(R.string.waiting), null, null);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(int pageId, int loadingType) {
        showProgressDialog(true, false, getString(R.string.waiting), pageId, loadingType, null, null);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel) {
        showProgressDialog(true, canCancel, getString(R.string.waiting), pageId, loadingType, null, null);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(int pageId, int loadingType, DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(true, true, getString(R.string.waiting), pageId, loadingType, null, dismissListener);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(boolean canCancelOutside, int pageId, int loadingType, DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(true, canCancelOutside, getString(R.string.waiting), pageId, loadingType, null, dismissListener);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text) {
        showProgressDialog(true, canCancel, text, pageId, loadingType, null, null);
    }

    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text, String secondStr) {
        showProgressDialog(true, canCancel, text, secondStr, pageId, loadingType, null, null);
    }

    /**
     * 给菊花弹窗设置页面类型与菊花类型
     * @param pageId 页面id
     * @param loadingType 菊花业务类型
     */
    public void showProgressDialog(int pageId, int loadingType, boolean cancelable, boolean canCancelOutside) {
        showProgressDialog(cancelable, canCancelOutside, getString(R.string.waiting),pageId, loadingType, null, null);
    }

    public void showProgressDialog(int pageId, int loadingType, boolean cancelable, boolean canCancelOutside, DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(cancelable, canCancelOutside, getString(R.string.waiting),pageId, loadingType, null, dismissListener);
    }
    public void showProgressDialog(int pageId, int loadingType, boolean cancelable, boolean canCancelOutside, String text) {
        showProgressDialog(cancelable, canCancelOutside, text,pageId, loadingType, null, null);
    }
    public void dismissProgressDialog() {
        if (DrLog.DEBUG) {
            DrLog.d("zhpu_dis",  "dismissProgressDialog \n" + DrLog.getStack());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    try {
                        mProgressDialog.dismiss();
                    } catch (Exception e) {
                        // got view not attached to window manager exception
                    }
                }
            }
        });
    }

    public boolean isProgressDialogShowing() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return true;
        return false;
    }

    public void showCheckFeeProgressDialog(int titleResID, DialogInterface.OnKeyListener onKeyListener) {
        showProgressDialog(false, false, getString(titleResID),
                 LoadingTypes.FEE_CHECK, onKeyListener, null);
    }
    // end----------------显示等待progress代码----------------

}
