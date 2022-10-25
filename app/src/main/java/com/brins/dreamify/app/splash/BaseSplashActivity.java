package com.brins.dreamify.app.splash;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.os.Bundle;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.permission.PermissionHandler;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.dreamify.R;
import com.kugou.common.widget.base.NavigationBarCompat;
import java.lang.reflect.Field;

public abstract class BaseSplashActivity extends Activity {
    private static final String TAG = "BaseSplashActivity";
    protected int mAnimIn = R.anim.activity_splash_in;
    protected int mAnimOut = R.anim.activity_splash_out;
    protected H mH;
    protected boolean mInterceptedInit = false;
    protected boolean mDirectIntercept = false;
    protected boolean isPause = false;
    protected boolean isResume = false;
    private boolean isResumeCheckedPermissionDialogPopped = false;
    private long AD_TIME_OUT = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        if (checkInterceptInitialize()) {
            mInterceptedInit = true;
        }
        mH = new H();
        mH.sendEmptyMessageDelayed(H.UI_MSG_SHOW_MAIN, AD_TIME_OUT);
    }

    @Override protected void onResume() {
        super.onResume();
        if (DrLog.DEBUG) {
            DrLog.d(TAG, "zlx_permission splash onResume");
            DrLog.i(TAG, "splash_lifecycle onResume");
        }
        if (isResumeCheckedPermissionDialogPopped) {
            if (PermissionHandler.hasBasicPermission(this)) {
                if (!DRCommonApplication.hasBasicPermission()){
                    //需要重新走换肤
                    /*SkinFileUtil.logToFile("权限申请完毕，有可能是用户去权限管理手动打开的","启动初始化",false);
                    KGCommonApplication.setHasBasicPermission(true);
                    loadSkin();*/
                }
                //gotoMediaActivity();
            } else {
                /*loadSkin();
                KGCommonApplication.setHasBasicPermission(true);
                selectSplashAndShow();*/
            }
        }
        
    }

    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                WindowInsets windowInsets = decorView.getRootWindowInsets();
                if (windowInsets != null) {
                    NavigationBarCompat.updateWindowBottomInset(windowInsets.getStableInsetBottom());
                    try {
                        //全面屏手机 支持开启指示器线的时候高度会小于sdk设置的最大值 如小米就是44 所以我们反射把标准降低点
                        Field max_bottom_inset = NavigationBarCompat.class.getDeclaredField("MAX_BOTTOM_INSET");
                        max_bottom_inset.setAccessible(true);
                        max_bottom_inset.setInt(null, 30);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Intent getIntent() {
        Intent intent = super.getIntent();
        if (intent == null) {
            intent = new Intent();
        }
        return intent;
    }

    protected boolean checkInterceptInitialize() {
        return false;
    }

    protected class H extends StackTraceHandler {
        public static final int UI_MSG_SHOW_MAIN = 0;

        public static final int UI_MSG_SHOW_MAIN_WITHOUT_FINISH = 1;
        public static final int UI_MSG_FINISH = 2;
        public static final int UI_MSG_POLL_SHOW_MAIN = 3;
        //        public static final int SEND_BI_DATA = 4;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UI_MSG_SHOW_MAIN) {
                if (!mDirectIntercept) {
                    gotoMediaActivity();
                }
            } else if (msg.what == UI_MSG_SHOW_MAIN_WITHOUT_FINISH) {
                gotoMediaActivity(false);
            } else if (msg.what == UI_MSG_FINISH) {
                BaseSplashActivity.this.finish();
            } else if (msg.what == UI_MSG_POLL_SHOW_MAIN) {
                if (isResume) {
                    gotoMediaActivity();
                }
            }
        }
    }

    protected abstract void gotoMediaActivity();
    protected abstract void gotoMediaActivity(boolean withFinish);

}