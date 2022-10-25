package com.brins.commom.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import com.brins.commom.R;
import com.brins.commom.skin.ColorUtil;
import com.brins.commom.toast.LoadingTypes;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import java.util.concurrent.TimeUnit;

public class XCommonLoadingView extends ImageView implements ISkinViewUpdate {

    private final String TAG = "wwh-XCommonLoadingView";

    public final static int LOADING_TYPE_NORMAL = 0;
    public final static int LOADING_TYPE_X_MODE = 1;

    public final static int SIZE_NORMAL = 0;
    public final static int SIZE_SMALL = 1;

    private boolean isCircleStype = false;

    private Paint mPaint;

    private RectF mArcRectF = new RectF();

    private final int DEFAULT_ANGELE1 = 359;
    private final int DEFAULT_ANGELE2 = 179;
    private final int DEFAULT_ARC_LENGTH = 1;

    private final int TOTAL_ANGELE = 360;

    private final int ANGELE_CHANGE_RADIO = 5;

    private final int ARC_NORMAL_MAX_LENGTH = 100;
    private final int ARC_SMALL_MAX_LENGTH = 50;

    private int curAngele1 = DEFAULT_ANGELE1,curAngele2 = DEFAULT_ANGELE2;
    private int curArcLength = DEFAULT_ARC_LENGTH;

    private boolean isRefreshing = false;

    private boolean isAutoStartRefreshByAttach = false;


    private int curArcColor;

    ValueAnimator angeleAnimator;

    private CountDownTimer mTimer;
    private long startTime;

    private boolean useLoadingApm = true;


    private int iconNormalColor,arcNormalColor,changeColor;

    Drawable bgDrawable = null;

    private boolean isChangeColor=false;

    private Drawable mShadowDrawable;

    private int viewSize = SIZE_NORMAL;

    private int mNormalBgSize,mSmallBgSize;
    private boolean mIsPullMode=false;
    private int colorMode=0;
    private boolean isDetachedFromWindow = false;
    private int[] mLocationOnScreen = new int[2];
    private int mLoadingType = LoadingTypes.DEFAULT;

    private int mChangeTime = TimeSpec.getPrimaryTime(LoadingManager.getInstance().getDefaultTime());

    private boolean allowStatistics = true;


    public int getViewSize() {
        return viewSize;
    }

    public void setViewSize(int viewSize) {
        this.viewSize = viewSize;
        initSizeState();
    }

    private void initSizeState() {
        if (viewSize == SIZE_NORMAL){
            mPaint.setStrokeWidth(SystemUtils.dip2px(2f));
            mShadowDrawable = getResources().getDrawable(R.drawable.x_refresh_loading_pic31).mutate();
            mShadowDrawable.setColorFilter(ColorUtil.getAlphaColor(Color.BLACK,0.1f), PorterDuff.Mode.SRC_IN);
        }else {
            mPaint.setStrokeWidth(SystemUtils.dip2px(1f));
            mShadowDrawable = getResources().getDrawable(R.drawable.x_refresh_loading_pic_small_31).mutate();
            mShadowDrawable.setColorFilter(ColorUtil.getAlphaColor(Color.BLACK,0.1f), PorterDuff.Mode.SRC_IN);
        }
        if (bgDrawable != null && bgDrawable instanceof GradientDrawable){
            ((GradientDrawable) bgDrawable).setSize(viewSize == SIZE_NORMAL ? mNormalBgSize : mSmallBgSize, viewSize == SIZE_NORMAL ? mNormalBgSize : mSmallBgSize);
            setBackgroundDrawable(bgDrawable);
        }

        invalidate();
    }

    private int[] loadingDrawableIds = new int[]{R.drawable.x_refresh_loading_pic1,R.drawable.x_refresh_loading_pic2,R.drawable.x_refresh_loading_pic3,
            R.drawable.x_refresh_loading_pic4,R.drawable.x_refresh_loading_pic5,R.drawable.x_refresh_loading_pic6,R.drawable.x_refresh_loading_pic7,
            R.drawable.x_refresh_loading_pic8,R.drawable.x_refresh_loading_pic9,R.drawable.x_refresh_loading_pic10,R.drawable.x_refresh_loading_pic11,
            R.drawable.x_refresh_loading_pic12,R.drawable.x_refresh_loading_pic13,R.drawable.x_refresh_loading_pic14,R.drawable.x_refresh_loading_pic15,R.drawable.x_refresh_loading_pic16,
            R.drawable.x_refresh_loading_pic17,R.drawable.x_refresh_loading_pic18,R.drawable.x_refresh_loading_pic19,R.drawable.x_refresh_loading_pic20,
            R.drawable.x_refresh_loading_pic21,R.drawable.x_refresh_loading_pic22,R.drawable.x_refresh_loading_pic23,R.drawable.x_refresh_loading_pic24,
            R.drawable.x_refresh_loading_pic25,R.drawable.x_refresh_loading_pic26,R.drawable.x_refresh_loading_pic27,R.drawable.x_refresh_loading_pic28,R.drawable.x_refresh_loading_pic29,
            R.drawable.x_refresh_loading_pic30,R.drawable.x_refresh_loading_pic31};

    private int[] smallLoadingDrawableIds = new int[]{R.drawable.x_refresh_loading_pic_small_1,R.drawable.x_refresh_loading_pic_small_2,R.drawable.x_refresh_loading_pic_small_3,
            R.drawable.x_refresh_loading_pic_small_4,R.drawable.x_refresh_loading_pic_small_5,R.drawable.x_refresh_loading_pic_small_6,R.drawable.x_refresh_loading_pic_small_7,
            R.drawable.x_refresh_loading_pic_small_8,R.drawable.x_refresh_loading_pic_small_9,R.drawable.x_refresh_loading_pic_small_10,R.drawable.x_refresh_loading_pic_small_11,
            R.drawable.x_refresh_loading_pic_small_12,R.drawable.x_refresh_loading_pic_small_13,R.drawable.x_refresh_loading_pic_small_14,R.drawable.x_refresh_loading_pic_small_15,R.drawable.x_refresh_loading_pic_small_16,
            R.drawable.x_refresh_loading_pic_small_17,R.drawable.x_refresh_loading_pic_small_18,R.drawable.x_refresh_loading_pic_small_19,R.drawable.x_refresh_loading_pic_small_20,
            R.drawable.x_refresh_loading_pic_small_21,R.drawable.x_refresh_loading_pic_small_22,R.drawable.x_refresh_loading_pic_small_23,R.drawable.x_refresh_loading_pic_small_24,
            R.drawable.x_refresh_loading_pic_small_25,R.drawable.x_refresh_loading_pic_small_26,R.drawable.x_refresh_loading_pic_small_27,R.drawable.x_refresh_loading_pic_small_28,R.drawable.x_refresh_loading_pic_small_29,
            R.drawable.x_refresh_loading_pic_small_30,R.drawable.x_refresh_loading_pic_small_31};

    private XCommonLoadingLayout.OnLoadingListener onLoadingListener;


    private void resetState() {
        curAngele1 = DEFAULT_ANGELE1;
        curAngele2 = DEFAULT_ANGELE2;
        curArcLength = DEFAULT_ARC_LENGTH;
        isChangeColor = false;
        mShadowDrawable.setColorFilter(ColorUtil.getAlphaColor(Color.BLACK,0.1f), PorterDuff.Mode.SRC_IN);
    }

    public void setUseLoadingApm(boolean useLoadingApm) {
        this.useLoadingApm = useLoadingApm;
    }


    public XCommonLoadingView(Context context) {
        this(context, null);
    }

    public XCommonLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public XCommonLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mNormalBgSize = SystemUtils.dip2px(44);
        mSmallBgSize = SystemUtils.dip2px(34);
        mShadowDrawable = getResources().getDrawable(R.drawable.x_refresh_loading_pic31).mutate();
        mShadowDrawable.setColorFilter(ColorUtil.getAlphaColor(Color.BLACK,0.1f), PorterDuff.Mode.SRC_IN);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SystemUtils.dip2px(2f));
        mArcRectF.set(0, 0, getWidth(), getArcRectFY());
        setScaleType(ScaleType.CENTER_INSIDE);
    }

    public float getArcRectFY() {
        return getBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mArcRectF.set(SystemUtils.dip2px(3), SystemUtils.dip2px(3), getWidth() - SystemUtils.dip2px(3), getArcRectFY() - SystemUtils.dip2px(3));
        mPaint.setColor(isChangeColor ? changeColor : arcNormalColor);
        canvas.drawArc(mArcRectF, curAngele1, curArcLength, false, mPaint);
        canvas.drawArc(mArcRectF, curAngele2, curArcLength, false, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DrLog.isDebug()){
            DrLog.d("XCommonLoadingView", "onAttachedToWindow:%s, %b", this, isAutoStartRefreshByAttach);
        }
        isDetachedFromWindow = false;
        if (isAutoStartRefreshByAttach && getVisibility() == View.VISIBLE){
            long originalStartTime = startTime;
            long remain = getRemainChangeTime();
            DrLog.d(TAG, "getRemainChangeTime: %d , st:%d", remain, startTime);
            startRefresh(Math.max(remain, 0), true);
            startTime = originalStartTime; //startRefresh()中会修改startTime，这里要恢复它真正的开始的时间
        }
    }

    private long getRemainChangeTime() {
        return startTime + TimeUnit.SECONDS.toMillis(mChangeTime) - SystemClock.elapsedRealtime();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDetachedFromWindow = true;
        long st = this.startTime;
        if (DrLog.isDebug()){
            DrLog.d("XCommonLoadingView","onDetachedFromWindow :" + toString());
        }
        endRefresh(true);
        this.startTime = st;
    }

    @Override
    public void updateSkin() {

    }

    public void setColorStyle(int iconNorColor,int arcNorColor,int changedColor,Drawable newDrawable){
        this.iconNormalColor = iconNorColor;
        this.arcNormalColor = arcNorColor;
        this.changeColor = changedColor;
        if (newDrawable != null) {
            this.bgDrawable = newDrawable.mutate();
        }
        initSizeState();
        if (getDrawable() != null){
            Drawable drawable = getDrawable();
            if (drawable instanceof LayerDrawable){
                ((LayerDrawable)drawable).getDrawable(0).mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }else {
                drawable.mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }
        }
//        setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
        invalidate();
    }


    public void setCircleStype(boolean circleStype) {
        isCircleStype = circleStype;
        invalidate();
    }

    @Deprecated
    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }

    public void startRefresh() {
        if (startRefresh(TimeUnit.SECONDS.toMillis(mChangeTime), false)) {
            startTime = SystemClock.elapsedRealtime();
        }
    }

    public boolean startRefresh(long changeColorTimeMillis, boolean forceRestart) {
        DrLog.d(TAG, "startRefresh (%s) %b , %d", this, forceRestart, changeColorTimeMillis);
        if (isRefreshing && !forceRestart){
            return false;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        initSizeState();
        resetState();
        DrLog.d(TAG, "startRefresh (%s) isChangeColor=%b", this, isChangeColor);
        if (getDrawable() != null){
            Drawable drawable = getDrawable();
            if (drawable instanceof LayerDrawable){
                ((LayerDrawable)drawable).getDrawable(0).mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }else {
                drawable.mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }
        }
        if (angeleAnimator != null && angeleAnimator.isRunning()){
            angeleAnimator.cancel();
            angeleAnimator = null;
        }
        angeleAnimator = ValueAnimator.ofInt(DEFAULT_ARC_LENGTH, viewSize == SIZE_NORMAL ? ARC_NORMAL_MAX_LENGTH : ARC_SMALL_MAX_LENGTH, DEFAULT_ARC_LENGTH);
        angeleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curAngele1 = ((curAngele1 + ANGELE_CHANGE_RADIO) % TOTAL_ANGELE);
                curAngele2 = ((curAngele2 + ANGELE_CHANGE_RADIO) % TOTAL_ANGELE);
                curArcLength = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        angeleAnimator.setDuration(2000);
        angeleAnimator.setRepeatCount(-1);
        isRefreshing = true;
        angeleAnimator.start();
//        if (DrLog.isDebug()){
//            //此处打印较频繁，先注释掉
//            DrLog.printException(TAG + " startRefresh--" + hashCode() + "  -- visible :" + (getVisibility() == VISIBLE),new Throwable());
//        }
        if (mTimer == null) {
            mTimer = new CountDownTimer(Math.max(changeColorTimeMillis, 0L), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    DrLog.d(TAG, "Timer onFinish");
                    startChangeColorRefresh();
                }
            };
        }
        mTimer.start();
        return true;

//        mTimer = new Timer();
//        mTimer.schedule(new ChangeColorTimerTask(), TimeUnit.SECONDS.toMillis(mChangeTime));
//        startTime = SystemClock.elapsedRealtime();
    }

    private void startChangeColorRefresh() {
        DrLog.d(TAG, "%s , startChangeColorRefresh", this);
        isChangeColor = true;
        if (getDrawable() != null){
            Drawable drawable = getDrawable();
            if (drawable instanceof LayerDrawable){
                ((LayerDrawable)drawable).getDrawable(0).mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }else {
                drawable.mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }
        }
        if (onLoadingListener != null){
            onLoadingListener.onChangeColor();
        }
//                    setColorFilter(changeColor, PorterDuff.Mode.SRC_IN);

        invalidate();
    }

    public void endRefresh(boolean autoEnd){
        isRefreshing = false;
//        if (DrLog.isDebug()){
        //此处打印较频繁，先注释掉
//            DrLog.printException(TAG + " ******finishRefresh--" + hashCode() + "  -- visible :" + (getVisibility() == VISIBLE),new Throwable());
//        }
        if (angeleAnimator != null){
            angeleAnimator.cancel();
            angeleAnimator = null;
        }
        resetState();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getDrawable() != null){
            Drawable drawable = getDrawable();
            if (drawable instanceof LayerDrawable){
                ((LayerDrawable)drawable).getDrawable(0).mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }else {
                drawable.mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
            }
        }
//        setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
        invalidate();
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    private String fo;

    public String getFo() {
        return fo;
    }

    public void setFo(String fo) {
        this.fo = fo;
    }


    public void setPullScale(float scale) {
        mIsPullMode = true;
        Drawable drawable = getResources().getDrawable(viewSize == SIZE_NORMAL ? loadingDrawableIds[(int) (scale * (loadingDrawableIds.length - 1))] : smallLoadingDrawableIds[(int) (scale * (smallLoadingDrawableIds.length - 1))]).mutate();
        drawable.setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
        if (bgDrawable != null && bgDrawable instanceof GradientDrawable) {
            ((GradientDrawable) bgDrawable).setSize(viewSize == SIZE_NORMAL ? mNormalBgSize : mSmallBgSize, viewSize == SIZE_NORMAL ? mNormalBgSize : mSmallBgSize);
        }
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable,mShadowDrawable});
        setImageDrawable(layerDrawable);
        setScaleX(scale);
        setScaleY(scale);
    }

    public void restartRefresh() {
        restartRefresh(false);
    }

    public void restartRefresh(boolean resetApm) {
        endRefresh(false);
        startRefresh();
    }

    public void setOnLoadingListener(XCommonLoadingLayout.OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    public XCommonLoadingLayout.OnLoadingListener getOnLoadingListener() {
        return onLoadingListener;
    }


//    private class ChangeColorTimerTask extends TimerTask {
//
//        @Override
//        public void run() {
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    isChangeColor = true;
//                    if (getDrawable() != null){
//                        Drawable drawable = getDrawable();
//                        if (drawable instanceof LayerDrawable){
//                            ((LayerDrawable)drawable).getDrawable(0).mutate().setColorFilter(changeColor, PorterDuff.Mode.SRC_IN);
//                        }else {
//                            drawable.mutate().setColorFilter(isChangeColor ? changeColor : iconNormalColor, PorterDuff.Mode.SRC_IN);
//                        }
//                    }
//                    if (onLoadingListener != null){
//                        onLoadingListener.onChangeColor();
//                    }
////                    setColorFilter(changeColor, PorterDuff.Mode.SRC_IN);
//                    if (mLoadingApmHelper != null) {
//                        mLoadingApmHelper.onChangeColor();
//                    }
//                    invalidate();
//                }
//            });
//        }
//    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        boolean isVisible = false;
        isVisible = ((visibility == View.VISIBLE) && (getVisibility() == View.VISIBLE));
        if (isVisible) {
            isVisible = isShown();
        }
        super.onVisibilityChanged(changedView, visibility);
        if (!isVisible) {
            return;
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }


    private boolean isViewInParentVisibleRect() {
        Rect loadingRect = getDrawingRectGlobal(this);
        ViewParent parent = getParent();
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

    private boolean inScreenCenter() {
        return Math.abs(LoadingManager.SCREEN_WIDTH
                - mLocationOnScreen[0] * 2 - getWidth())
                < LoadingManager.SCREEN_WIDTH >> 5;
    }

    public int getLoadingType() {
        return mLoadingType;
    }

    public void setLoadingType(int loadingType) {
        mLoadingType = loadingType;
    }

    public void setChangeTime(int seconds) {
        mChangeTime = seconds;
    }

    public int getChangeTime() {
        return mChangeTime;
    }

    public void setAllowStatistics(boolean allowStatistics) {
        this.allowStatistics = allowStatistics;
    }

    public boolean isAnimating() {
        return angeleAnimator != null && angeleAnimator.isRunning();
    }

    public void setResumeRefreshByAttach(boolean resumeRefreshByAttach) {
        isAutoStartRefreshByAttach = resumeRefreshByAttach;
    }

    public void resetStartTime() {
        DrLog.d(TAG, "resetStartTime %s", this);
        startTime = SystemClock.elapsedRealtime();
    }
}
