package com.brins.commom.swipetap;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.brins.commom.utils.log.DrLog;
import com.kugou.common.base.ViewPager;


/**
 * Created by mingzhihuang on 2016/4/20.
 */
public class MainFragmentViewPage extends ViewPager {
    private float mDownX;

    private SwipeCallback mSwipeCallback;

    private SwipeViewPage.DisallowInterceptCallback mDisallowInterceptCallback;

    private int mTouchSlop;

    //是否可以进行滑动
    private boolean mCanSlide = true;
    private float mDownYPreScroll;
    private PreScrollCallBack preScrollCallBack;

    public void registerDisallowInterceptCallback(SwipeViewPage.DisallowInterceptCallback callback) {
        this.mDisallowInterceptCallback = callback;
    }

    public MainFragmentViewPage(Context context) {
        super(context);
        init(context);
    }

    public MainFragmentViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mIsDisableLeftOverscroll = true;
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context));
    }

    public void setCanSlide(boolean slide) {
        mCanSlide = slide;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mCanSlide) {
            return false;
        }
        float curX = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                if (DrLog.DEBUG) DrLog.i("MotionEvent", "DOWN-------x=" + ev.getX() + ",y=" + ev.getY());
                if (mSwipeCallback != null){
                    mSwipeCallback.onViewPagerTouch();
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = curX - mDownX;
                if (mSwipeCallback != null) {
                    if (offsetX > mTouchSlop && !mSwipeCallback.canLeftSwipe()) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        if (mDisallowInterceptCallback != null){
                            mDisallowInterceptCallback.requestDisallowInterceptTouchEvent();
                        }
                    } else if (offsetX < -mTouchSlop
                            && !mSwipeCallback.canRightSwipe()) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        if (mDisallowInterceptCallback != null){
                            mDisallowInterceptCallback.requestDisallowInterceptTouchEvent();
                        }
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                if (DrLog.DEBUG) DrLog.i("MotionEvent", "MOVE-------x=" + ev.getX() + ",y=" + ev.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (DrLog.DEBUG)
                    DrLog.i("MotionEvent", "UP-------x=" + ev.getX() + ",y=" + ev.getY());
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float curY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownYPreScroll = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetY = curY - mDownYPreScroll;
                if (preScrollCallBack != null) {
                    preScrollCallBack.preScroll(offsetY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    public void registerVerticalPreScrollListener(PreScrollCallBack preScrollCallBack) {
        this.preScrollCallBack = preScrollCallBack;
    }

    public void registerSwipeCallback(SwipeCallback swipeCallback) {
        mSwipeCallback = swipeCallback;
    }

    public void removeSwipeCallback() {
        mSwipeCallback = null;
    }

    public interface SwipeCallback {
        public boolean canLeftSwipe();

        public boolean canRightSwipe();

        public void onViewPagerTouch();
    }

    public interface PreScrollCallBack {
        public void preScroll(float deltaY);
    }
}
