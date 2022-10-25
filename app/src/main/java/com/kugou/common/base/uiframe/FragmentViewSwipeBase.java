package com.kugou.common.base.uiframe;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import com.brins.commom.base.FrameworkUtil;
import com.brins.commom.base.ViewPagerFrameworkDelegate;
import com.brins.commom.utils.log.DrLog;

/**
 * 这个基类用来处理可以滑动退出的 FragmentView
 * 处理的页面包括：
 * {@link FragmentViewNormal} 普通右划退出的页面
 *
 * @author 于晓飞
 * @date 2017/10/9.
 */
public abstract class FragmentViewSwipeBase extends FragmentViewBase {

    protected static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    private static final int DEFAULT_GUTTER_SIZE = 16; // dips
    private static final float INVALID_VALUE = -1F;
    private static final int INVALID_POINTER = -1;
    private static final int INVALID_INT = -1;

    protected float mInitialMotionX = INVALID_VALUE;
    protected float mInitialMotionY = INVALID_VALUE;
    protected float mLastMotionX = INVALID_VALUE;
    protected float mLastMotionY = INVALID_VALUE;

    protected int mSettlingDirection = SETTLING_NONE;

    private static int sTouchSlop;
    private static int sMaximumVelocity;
    private static int sMinimumVelocity;
    private static int sMinimumFlyingDistance;
    private static int sDefaultGutterSize;
    private static int sGutterSize = INVALID_INT;
    private static boolean sInitialized = false;

    protected VelocityTracker mVelocityTracker;
    protected Scroller mScroller;

    protected boolean mIsBeingDragged = false;
    protected boolean mIsUnableToDrag = false;
    private int mActivePointerId = INVALID_POINTER;
    private boolean mEnableScrollLeft = true;

    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    FragmentViewSwipeBase(@NonNull Context context) {
        super(context);
        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(context, sInterpolator);
        if (!sInitialized) {
            initParams(context);
            sInitialized = true;
        }
    }

    private void initParams(@NonNull Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        sTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        sMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        sMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        sMinimumFlyingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
        sDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mIsTop) {
            return false;
        }
        if (!mSlidingEnabled) {
            return false;
        }
        if (mViewState == STATE_SETTLING) {
            return true;
        }
        final int action = MotionEventCompat.getActionMasked(ev);

        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            mActivePointerId = INVALID_POINTER;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return false;
        }
        if(DrLog.DEBUG) DrLog.i("FragmentViewSwipeBase2","FragmentViewSwipeBase->>>>>>>>: onInterceptTouchEvent " + "mIsBeingDragged: " + mIsBeingDragged + " mIsUnableToDrag: " + mIsUnableToDrag);
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
            if (mIsUnableToDrag) {
                return false;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) break;

                final int pointerIndex = findPointerIndexCompat(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = Math.abs(y - mInitialMotionY);

                if (cannotScrollLeft(dx) || dx != 0 && !isGutterDrag(mLastMotionX, dx) && canScroll((int) x, (int) y) || isCanDrag) {
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsUnableToDrag = true;
                    return false;
                }
                if(DrLog.DEBUG) DrLog.i("FragmentViewSwipeBase2","FragmentViewSwipeBase->>>>>>>>: onInterceptTouchEvent " + ", yDiff: " + yDiff
                        + ", x: " + x + ", dx: " + dx + ", xDiff: " + xDiff);
                if (xDiff > sTouchSlop && xDiff > yDiff) {
                    mIsBeingDragged = true;
                    setViewState(STATE_DRAGGING, true);
                    mLastMotionX = dx > 0 ? mInitialMotionX + sTouchSlop : mInitialMotionX - sTouchSlop;
                    mLastMotionY = y;
                } else if (yDiff > sTouchSlop) {
                    mIsUnableToDrag = true;
                }
                if (mIsBeingDragged) {
                    if (performDrag(x)) {
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                }
                if(DrLog.DEBUG) DrLog.i("FragmentViewSwipeBase2","FragmentViewSwipeBase->>>>>>>>: onInterceptTouchEvent " + "mIsBeingDragged: " + mIsBeingDragged + " mIsUnableToDrag: " + mIsUnableToDrag);
                break;
            case MotionEvent.ACTION_DOWN:
                onActionDown(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            default:
                break;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        return mIsBeingDragged;
    }

    private boolean cannotScrollLeft(float dx) {
        return !mEnableScrollLeft && dx < 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsTop) {
            return false;
        }
        if (!mSlidingEnabled) {
            return false;
        }
        if (mViewState == STATE_SETTLING) {
            return true;
        }
        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 在settling状态下，页面不可以滑动，所以可能出现down事件到不了这里的情况。
                // 当这里第一收到down事件，并且这两个值没有被初始化的时候，认为这次MOVE事件就是DOWN事件
                if (mLastMotionX < 0 || mInitialMotionX < 0) {
                    onActionDown(event);
                }
                final int pointerIndex = findPointerIndexCompat(event, mActivePointerId);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float xDiff = Math.abs(x - mLastMotionX);
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float yDiff = Math.abs(y - mLastMotionY);
                if (!mIsBeingDragged) {
                    if (xDiff > sTouchSlop && xDiff > yDiff) {
                        mIsBeingDragged = true;
                        setViewState(STATE_DRAGGING, true);
                        mLastMotionX = x - mInitialMotionX > 0 ? mInitialMotionX + sTouchSlop : mInitialMotionX - sTouchSlop;
                        mLastMotionY = y;
                    } else if (yDiff > sTouchSlop) {
                        mIsUnableToDrag = true;
                    }
                }
                if (mIsBeingDragged) {
                    performDrag(x);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    endDrag();
                    startScroll(event.getX());
                }
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(event);
                mLastMotionX = MotionEventCompat.getX(event, index);
                mActivePointerId = MotionEventCompat.getPointerId(event, index);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(event);
                mLastMotionX = MotionEventCompat.getX(event, findPointerIndexCompat(event, mActivePointerId));
                break;
            }
            default:
                break;
        }
        return true;
    }

    /**
     * down 事件分发下来的时候，初始化整个事件状态
     * @param event
     */
    private void onActionDown(MotionEvent event) {
        mLastMotionX = mInitialMotionX = event.getX();
        mLastMotionY = mInitialMotionY = event.getY();
        mActivePointerId = MotionEventCompat.getPointerId(event, 0);
        mIsUnableToDrag = false;
        //marcoma@kugou.net 小程序栈管理时，防止空的FragmentMiniAppStackPage未被滑走
        isCanDrag = false;
    }

    public int findPointerIndexCompat(MotionEvent event, int pointerId) {
        int index = MotionEventCompat.findPointerIndex(event, pointerId);
        if (index == -1) return 0;
        return index;
    }

    private boolean isGutterDrag(float x, float dx) {
        return (x < sGutterSize && dx > 0) || (x > getWidth() - sGutterSize && dx < 0);
    }

    private boolean canScroll(int x, int y) {
        return isHitIgnoreView(x, y);
    }

    private boolean isCanDrag;

    private boolean isHitIgnoreView(int x, int y) {
        if (mIgnoredViews != null) {
            Rect rect = new Rect();
            for (View v : mIgnoredViews) {
                getChildRectInPagerCoordinates(rect, v);
                if (rect.contains(x, y)) return true;
            }
        }
        return false;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = (pointerIndex == 0 ? 1 : 0);
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private Rect getChildRectInPagerCoordinates(Rect outRect, View child) {
        if (outRect == null) {
            outRect = new Rect();
        }
        if (child == null) {
            outRect.set(0, 0, 0, 0);
            return outRect;
        }
        outRect.left = child.getLeft();
        outRect.top = child.getTop();

        ViewParent parent = child.getParent();
        while (parent instanceof ViewGroup && parent != this) {
            final ViewGroup group = (ViewGroup) parent;
            outRect.left += group.getLeft();
            outRect.top += group.getTop();

            parent = group.getParent();
        }
        outRect.left -= getScrollX();
        outRect.top -= getScrollY();
        outRect.right = outRect.left + child.getWidth();
        outRect.bottom = outRect.top + child.getHeight();
        return outRect;
    }

    private boolean performDrag(final float x) {
        final float deltaX = mLastMotionX - x;
        float oldScrollX = getScrollX();
        float scrollX = oldScrollX + deltaX;
        mLastMotionX = x;
        mLastMotionX += scrollX - (int) scrollX;
        scrollX = scrollX > 0 ? 0 : scrollX;
        final int screenWidth = getWidth();
        if (Math.abs(scrollX) >= screenWidth) {
            scrollX = -screenWidth;
        }
        scrollTo((int) scrollX, getScrollY());
        return false;
    }

    private void endDrag() {
        mInitialMotionX = INVALID_VALUE;
        mLastMotionX = INVALID_VALUE;
        mIsBeingDragged = false;
        mIsUnableToDrag = false;
        mActivePointerId = INVALID_POINTER;
    }

    private void startScroll(final float x) {
        mVelocityTracker.computeCurrentVelocity(1000, sMaximumVelocity);
        final float velocity = mVelocityTracker.getXVelocity();
        final int currScrollX = getScrollX();
        final int width = getMeasuredWidth();

        if ((x - mInitialMotionX) > sMinimumFlyingDistance && Math.abs(velocity) > sMinimumVelocity) {
            mSettlingDirection = velocity > 0 ? SETTLING_RIGHT : SETTLING_LEFT;
        } else {
            mSettlingDirection = Math.abs(currScrollX) / (float) width > 0.4f ? SETTLING_RIGHT : SETTLING_LEFT;
        }
        final int distanceX = mSettlingDirection == SETTLING_RIGHT ? -currScrollX - width : -currScrollX;
        setViewState(STATE_SETTLING, true);
        // 没有距离就不用经过 scroller 滑动了
        // update: 当向右滑动的时候，还是需要回调的
        if (distanceX == 0 && mSettlingDirection == SETTLING_LEFT) {
            setViewState(STATE_IDLE);
        } else {
            if (mScrollListener != null && mSettlingDirection == SETTLING_RIGHT) {
                mScrollListener.onScrollRightStart(this);
            }
            mScroller.startScroll(currScrollX, 0, distanceX, 0);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int currScrollX = mScroller.getCurrX();
            scrollTo(currScrollX, 0);
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (mScrollListener != null) {
                if (mSettlingDirection == SETTLING_LEFT) {
                    mScrollListener.onScrollLeftFinished(this);
                    setViewState(STATE_IDLE, true);
                    mSettlingDirection = SETTLING_NONE;
                } else if (mSettlingDirection == SETTLING_RIGHT) {
                    removeCallbacks(mScrollRightFinishedRunnable);
                    post(mScrollRightFinishedRunnable);
                }
            }
        }
    }

    private Runnable mScrollRightFinishedRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScrollListener != null) {
                mScrollListener.onScrollRightFinished(FragmentViewSwipeBase.this, null);
            }
            setViewState(STATE_IDLE, false);
            mSettlingDirection = SETTLING_NONE;
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (sGutterSize == INVALID_INT) {
            final int measuredWidth = getMeasuredWidth();
            final int maxGutterSize = measuredWidth / 10;
            sGutterSize = Math.min(maxGutterSize, sDefaultGutterSize);
        }
    }

    public void clearUnableToDrag() {
        mIsUnableToDrag = false;
    }

    public void setEnableScrollLeft(boolean enableScrollLeft) {
        mEnableScrollLeft = enableScrollLeft;
    }
}
