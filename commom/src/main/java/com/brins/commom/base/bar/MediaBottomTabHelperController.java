package com.brins.commom.base.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.base.AdditionalLayout;
import com.brins.commom.base.MainFragmentContainer;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.event.BottomTabChangeEvent;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.bumptech.glide.Glide;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 首页底部的BottomTab的辅助类 如开播提醒等
 *
 * @Description: From one bug to another bug
 * @Author: icewei
 * @Date: 3/26/21 2:05 PM
 */
public class MediaBottomTabHelperController {


    private int mTabAvatarSize = SystemUtils.dip2px(38);
    private int mIvLogoBottomMargin = SystemUtils.dip2px(4);

    private final AbsBaseActivity mActivity;//MediaActivity
    private boolean isActivityAlive = true;
    private AdditionalLayout mMainContentView;
    //private AdditionalLayout mMainContentView;
    /**
     * 首页是否可见
     */
    private boolean mHomePageVisible;
    private boolean isRequesting = false;
    private boolean showTip4KGeTabWithCheckAfterRequesting = false;

    public MediaBottomTabHelperController(AbsBaseActivity activity) {
        log(" create MediaBottomTabHelperController for " + activity);
        mActivity = activity;

        if (mActivity != null) {
            EventBus.getDefault().register( this);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            };

            IntentFilter filter = new IntentFilter();
            BroadcastUtil.registerMultiReceiver(broadcastReceiver, filter);

            activity.getLifecycle().addObserver(new GenericLifecycleObserver() {
                @Override
                public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                    log(" activity生命周期发生变化 event = " + event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        EventBus.getDefault().unregister(MediaBottomTabHelperController.this);
                        isActivityAlive = false;
                        BroadcastUtil.unregisterMultiReceiver(broadcastReceiver);
                    }
                }
            });
        }
    }

    public static void log(String msg) {
        DrLog.d("MediaBottomTabHelperController", "msg = [" + msg + "]");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BottomTabChangeEvent event) {

    }


    public void setMainContentView(AdditionalLayout mainContentView) {
        mMainContentView = mainContentView;
    }


    private int getMeasureWidthBeforeAdd2ViewGroup(View view) {
        DisplayMetrics dm = DRCommonApplication.getContext().getResources().getDisplayMetrics();
        view.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));
        return view.getMeasuredWidth();
    }

    private int getMeasureHeightBeforeAdd2ViewGroup(View view) {
        DisplayMetrics dm = DRCommonApplication.getContext().getResources().getDisplayMetrics();
        view.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));
        return view.getMeasuredHeight();
    }

    public boolean isHostInvalid() {
        Activity activity = mActivity;
        if (null == activity) {
            return true;
        }
        if (activity.isFinishing()) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return true;
        }
        return !isActivityAlive;
    }

}
