package com.brins.commom.loading;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import com.brins.commom.widget.CommonLoadingView;
import com.brins.commom.widget.LoadingManager;
import com.brins.commom.widget.TimeSpec;
import java.lang.ref.WeakReference;

public class LoadingPresenter implements ILoadingPresenter {

    private final static String TAG = "LoadingPresenter";
    private CommonLoadingView mLoadingView;

    private int mTimeSpec = LoadingManager.getInstance().getDefaultTime();
    private LoadingCallback loadingCallback;
    private boolean inTimer;
    private int[] mLocationOnScreen = new int[2];

    @Override
    public void attachView(CommonLoadingView view) {
        this.mLoadingView = view;
    }

    @Override
    public void setTimerCallback(LoadingCallback callback) {
        loadingCallback = callback;
    }

    @Override
    public void setTimeSpec(int timeSpec) {
//        this.mTimeSpec = timeSpec;
        int primaryTime = TimeSpec.getPrimaryTime(timeSpec);
        if (mLoadingView != null) mLoadingView.setChangeTime(primaryTime);
    }

    @Override
    public void startAnim() {
        /*AnimationDrawable anim = mLoadingView.getAnim();
        if (anim != null) {
            resetData();
            anim.start();
            if (loadingCallback != null) {
                loadingCallback.onStart();
            }
        }*/
    }

    @Override
    public void startAnimWithTimer() {
        /*startAnim();
        enableTimer();*/
    }

    private void resetData() {
        /*inTimer = false;
        LoadingManager.getInstance().removeTimer(weakCallback);
        mLoadingView.removeCallbacks(primarySwitcher);
        mLoadingView.removeCallbacks(secondarySwitcher);
        mLoadingView.setText(mLoadingView.getPrimaryText());
        mLoadingView.setIconColor(mLoadingView.getPrimaryColor());
        if (inListViewFooter()) refreshFooterText(mLoadingView.getPrimaryText());*/
    }

    @Override
    public void stopAnim() {
        if (mLoadingView != null){
            mLoadingView.endRefresh();
        }
        /*resetData();
        AnimationDrawable anim = mLoadingView.getAnim();
        if (anim != null) {
            anim.stop();
        }
        if (mLoadingApmHelper != null) {
            mLoadingApmHelper.onEnd();
            mLoadingApmHelper = null;
        }
        if (loadingCallback != null) {
            loadingCallback.onStop();
        }*/
    }

    @Override
    public void startTimer() {
        /*LoadingManager.getInstance().postUIAction(new Runnable() {
            @Override
            public void run() {
                enableTimer();
            }
        });*/
    }

    @Override
    public void cancelTimer() {
        //resetData();
        if (mLoadingView != null){
            mLoadingView.endRefresh();
        }
    }

    @Override
    public boolean checkLocation() {
        /*try {
            mLoadingView.getLocationOnScreen(mLocationOnScreen);
        } catch (NullPointerException e) {
            // http://kgmedit.kugou.com/mobileservice/crash/info?id=16632036&table=crash_201707

            if (KGLog.isDebug()) com.kugou.common.utils.KGLog.uploadException(e);
            return true;
        }

        checkTimer();*/
        return outOfScreen();
    }

    private void enableTimer() {
       /* this.inTimer = true;

        if (KGLog.isDebug()) KGLog.i(TAG, "enableTimer post a timer!");
        if (mLoadingView != null) {
            mLoadingView.setIconColor(mLoadingView.getPrimaryColor());
        }
        LoadingManager.getInstance().postTimer(weakCallback, mTimeSpec);*/
    }

    /**
     * 添加了isViewInParentVisibleRect判断，主要针对歌手详情页退出时会开启结束动画的问题
     * 结束的时候刚好有个View在试图的正中间，但是因为父布局在旁边不可见，导致那个Loading不可见，但是会参与绘制
     */
    private void checkTimer() {
        if (inTimer) return;

        if ((inListViewFooter()
                || inListViewHeader()
                || inScreenCenter())
                && isViewInParentVisibleRect()) {
            enableTimer();
        }
    }

    private boolean isViewInParentVisibleRect() {
        if (mLoadingView == null) return false;
        Rect loadingRect = getDrawingRectGlobal(mLoadingView);
        ViewParent parent = mLoadingView.getParent();
        while (parent != null) {
            if (parent instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) parent;
                Rect parentRect = getDrawingRectGlobal(parentView);
                if (!parentRect.contains(loadingRect) && !parentRect.intersect(loadingRect))
                    return false;
            }
            parent = parent.getParent();
        }
        return true;
    }

    private Rect getDrawingRectGlobal(View view) {
        Rect rect = new Rect();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        view.getDrawingRect(rect);
        rect.offset(-view.getScrollX(), -view.getScrollY());
        rect.offset((int) view.getTranslationX(), (int) view.getTranslationY());
        rect.offset(location[0], location[1]);
        return rect;
    }

    private boolean outOfScreen() {
        //if (KGLog.isDebug()) KGLog.i(TAG, mLocationOnScreen[0] + " - " + mLocationOnScreen[1]);
        return mLocationOnScreen[0] > LoadingManager.SCREEN_WIDTH || mLoadingView.getWidth() + mLocationOnScreen[0] < 0;
    }

    private boolean inScreenCenter() {
        return Math.abs(LoadingManager.SCREEN_WIDTH
                - mLocationOnScreen[0] * 2 - mLoadingView.getWidth())
                < LoadingManager.SCREEN_WIDTH >> 5;
    }

    private Runnable secondarySwitcher = new Runnable() {
        @Override
        public void run() {
            if (loadingCallback != null) loadingCallback.onSecondaryTrigger();
        }
    };

    private Runnable primarySwitcher = new Runnable() {
        @Override
        public void run() {
            /*if (loadingCallback != null) loadingCallback.onPrimaryTrigger();

            if (mLoadingView == null) return;

            if (inListViewFooter()) refreshFooterText(mLoadingView.getSecondaryText());

            mLoadingView.setText(mLoadingView.getSecondaryText());
            ObjectAnimator colorAnimator = ObjectAnimator.ofInt(mLoadingView, "iconColor",
                    mLoadingView.getPrimaryColor(), mLoadingView.getSecondaryColor());
            colorAnimator.setDuration(500);
            colorAnimator.setEvaluator(new ArgbEvaluator());
            colorAnimator.start();*/
        }
    };

    private TimerCallback innerTimerCallback = new TimerCallback() {
        @Override
        public void onStartTrigger() {
           /* if (KGLog.isDebug()) KGLog.i(TAG, "onStartTrigger");
            if (canStartNewApm()) {
                // 创建一个红菊率统计对象，并开始计时
                mLoadingApmHelper = new LoadingApmHelper(mLoadingView);
            } else {
                if (KGLog.DEBUG) {
                    KGLog.i(
                            LoadingApmSampler.LOG_TAG,
                            String.format(
                                    Locale.ENGLISH,
                                    "cannot start loading apm. isInViewTree: %b",
                                    PageInfoUtil.isInViewTree(mLoadingView)
                            )
                    );
                }
            }*/
        }

        @Override
        public void onPrimaryTrigger() {
           /* if (KGLog.isDebug()) KGLog.i(TAG, "onPrimaryTrigger");
            if (mLoadingView != null) LoadingManager.getInstance().postUIAction(primarySwitcher);
            if (mLoadingApmHelper != null) {
                mLoadingApmHelper.onChangeColor();
            }*/
        }

        @Override
        public void onSecondaryTrigger() {
            /*if (KGLog.isDebug()) KGLog.i(TAG, "onSecondaryTrigger");
            if (mLoadingView != null) LoadingManager.getInstance().postUIAction(secondarySwitcher);*/
        }
    };

    private WeakReference<TimerCallback> weakCallback = new WeakReference<>(innerTimerCallback);

    // 多级超时回调，目前只有一个等级
    public interface TimerCallback {
        void onStartTrigger();
        void onPrimaryTrigger();
        void onSecondaryTrigger();
    }

    public interface LoadingCallback extends TimerCallback {
        void onStart();
        void onStop();
    }

    private boolean inListViewFooter() {
        if (mLoadingView.getParent() instanceof ViewGroup) {
            return R.id.progress_footer == ((View) mLoadingView.getParent()).getId();
        }

        return false;
    }

    private void refreshFooterText(String text) {
        ViewGroup footer = (ViewGroup) mLoadingView.getParent();
        if (footer != null) {
            TextView loadingText = (TextView) footer.findViewById(R.id.progress_info);
            loadingText.setText(text);
        }
    }

    private boolean inListViewHeader() {
        if (mLoadingView.getParent() instanceof ViewGroup) {
            return R.id.pull_to_refresh_progress == ((View) mLoadingView.getParent()).getId();
        }

        return false;
    }

    /**
     * 判断是否应该开始一次新的统计, 要开始一次新的统计必须满足以下条件：
     * 1. loadingView必须在View树中(http://bugfree.kugou.net/index.php/bug/90868)
     * 2. 没有正在统计的loadingView 或者 存在统计的loadingview但是时间长度已经超过2分钟(怀疑可能存在只开始不结束的loadingView)
     * @return 可以开始新的统计返回 true，否则返回 false
     */
    private boolean canStartNewApm() {
        return PageInfoUtil.isInViewTree(mLoadingView);
    }
}