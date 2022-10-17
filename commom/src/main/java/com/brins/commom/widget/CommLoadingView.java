package com.brins.commom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.brins.commom.utils.SystemUtils;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import java.util.concurrent.TimeUnit;

/**
 * Base of the Base
 * */
public class CommLoadingView extends LoadingArcView implements ISkinViewUpdate {

    // LoadingView 39dp

    public static final long ONE_SECOND = 1000;

    private static final int DEF_BACKGROUND_SIZE = SystemUtils.dip2px(26);
    private static final int DEF_KSIGN_SIZE = SystemUtils.dip2px(12);
    private LoadingKSignView viewKSign = null;
    private long timeDelay = TimeUnit.SECONDS.toMillis(TimeSpec.getPrimaryTime(LoadingManager.getInstance().getDefaultTime()));

    private CountDownTimer timer = null;
    private boolean isDelayState = false;
    private int[] mLocationOnScreen = new int[2];

    private OnDelayListener onDelayListener = null;
    // 自定义颜色
    private FixedColorsGetter mFixedColorGetter = null;
    // 部分LoadingView无需自动可见即开始
    private boolean autoLoadingWhileVisible = true;
    //
    private boolean autoLoadingWhileAttaching = true;

    public CommLoadingView(Context context) {
        super(context);
        init();
    }

    public CommLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewKSign = new LoadingKSignView(getContext());
        initKG11LoadingKSignView(viewKSign);
        addView(viewKSign);
        resetColor();
    }

    protected void initKG11LoadingKSignView(LoadingKSignView viewKSign) {
        LoadingArcView.LayoutParams params = new LoadingArcView.LayoutParams(DEF_BACKGROUND_SIZE, DEF_BACKGROUND_SIZE);
        params.gravity = Gravity.CENTER;
        viewKSign.setLayoutParams(params);
        viewKSign.setPivotX(DEF_BACKGROUND_SIZE / 2);
        viewKSign.setPivotY(DEF_BACKGROUND_SIZE / 2);
        viewKSign.setKSignViewSize(DEF_KSIGN_SIZE, DEF_KSIGN_SIZE);
    }

    @Override
    public void scaleInside(float scale) {
        super.scaleInside(scale);
        viewKSign.setScaleX(scale);
        viewKSign.setScaleY(scale);
        viewKSign.scaleInside(scale);
    }

    @Override
    public void updateSkin() {
        resetColor();
    }

    public void resetColor() {
        int[] colors = null;
        if (mFixedColorGetter != null) {
            colors = mFixedColorGetter.getColors(isDelayState());
        }

        if (colors == null) {
            colors = isDelayState() ? LoadingColorHelper.getDelayColor(true) : LoadingColorHelper.getRegularColor(true);
        }

        setColorArcAndCircle(colors[2]);
        viewKSign.setColor(colors[1], colors[0]);
    }

    public boolean isDelayState() {
        return isDelayState;
    }

    //
    @Override
    public void startLoading() {
        super.startLoading();
        startTimer();
    }

    @Override
    public void endLoading() {
        super.endLoading();
        endTimer();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!autoLoadingWhileVisible) {
            super.onVisibilityChanged(changedView, visibility);
            return;
        }
        boolean isVisible = ((visibility == View.VISIBLE) && (getVisibility() == View.VISIBLE));
        if (isVisible) {
            isVisible = isShown();
        }
        super.onVisibilityChanged(changedView, visibility);
        if (isVisible) {
            resetColor();
            startLoading();
        } else {
            endLoading();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!autoLoadingWhileAttaching) {
            return;
        }
        if (getVisibility() == View.VISIBLE) {
            resetColor();
            startLoading();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!autoLoadingWhileAttaching) {
            return;
        }
        endLoading();
    }



    private void startTimer() {
        startTimer(timeDelay);
    }

    private void startTimer(long time) {
        if (timer == null) {
            timer = new CountDownTimer(time, ONE_SECOND) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    isDelayState = true;
                    resetColor();
                    if (onDelayListener != null) {
                        onDelayListener.onDelay();
                    }
                }
            };
        }
        timer.cancel();
        timer.start();
    }

    private void endTimer() {
        if (timer == null) {
            isDelayState = false;
            return;
        }
        timer.cancel();
        timer = null;
        isDelayState = false;
        resetColor();
    }

    public void setOnDelayListener(OnDelayListener onDelayListener) {
        this.onDelayListener = onDelayListener;
    }

    public interface OnDelayListener {
        void onDelay();
    }

    public void restartLoading() {
        restartLoading(false);
    }

    public void restartLoading(boolean resetApm) {
        endLoading();
        startLoading();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

    //
    public void setFixedColorGetter(FixedColorsGetter mFixedColorGetter) {
        this.mFixedColorGetter = mFixedColorGetter;
        if (this.mFixedColorGetter != null) {
            resetColor();
        }
    }

    //
    public void setAutoLoadingWhileVisible(boolean autoLoadingWhileVisible) {
        this.autoLoadingWhileVisible = autoLoadingWhileVisible;
    }

    public boolean isAutoLoadingWhileAttaching(){
        return autoLoadingWhileAttaching;
    }

    public void setAutoLoadingWhileAttaching(boolean autoLoadingWhileAttaching) {
        this.autoLoadingWhileAttaching = autoLoadingWhileAttaching;
    }

    public boolean isAutoLoadingWhileVisible() {
        return autoLoadingWhileVisible;
    }

    public FixedColorsGetter getFixedColorGetter() {
        return mFixedColorGetter;
    }

    public interface FixedColorsGetter {
        int[] getColors(boolean isDelayState);
    }

}
