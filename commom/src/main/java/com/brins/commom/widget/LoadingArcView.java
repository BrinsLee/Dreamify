package com.brins.commom.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.brins.commom.utils.SystemUtils;

/***
 * LoadingView扇形进度条，换肤由继承者或者引用者决定
 */

public class LoadingArcView extends FrameLayout {

    private static final long DURATION_LOADING_PER = 1000;

    private RectF rectArc = null;
    private RectF rectCircle = null;
    private Paint paintArc = null;
    private Paint paintCircle = null;

    private float WIDTH_ARC = SystemUtils.dip2px(4f);
    private int MAX_ANGLE = 100;
    private int ANGLE_ONE_CIRCLE = 360;
    private int ANGLE_HALF_CIRCLE = 180;

    private ValueAnimator valueAnimator = null;

    // 从0到1
    // 左边扇形的边缘开始的位置
    private float leftStartPoint;
    private float leftEndPoint;
    // 左边扇形边缘需要到达的位置
    private float leftFinalStartPoint;
    private float leftFinalEndPoint;

    // 从1到0
    // 左边扇形的边缘开始的位置
    private float leftStartPoint2;
    private float leftEndPoint2;
    // 左边扇形边缘需要到达的位置
    private float leftFinalStartPoint2;
    private float leftFinalEndPoint2;

    // 左边扇形当前位置
    private float leftCurrentStartPoint;
    // 左右的角度是相同的
    private float currentAngle;

    // 是否在Loading状态
    private boolean isLoading = false;

    //
    private float scaleInside; // 0f - 1f

    //
    private int colorArcAndCircle = Color.TRANSPARENT;

    public LoadingArcView(@NonNull Context context) {
        super(context);
        initViews();
    }

    public LoadingArcView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public LoadingArcView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initArcFrameRect(canvas);
        initCircleFrameRect(canvas);
        initPaintCircle(colorArcAndCircle);
        initPaintArc(colorArcAndCircle);
        float centerX = rectCircle.width() / 2;
        float centerY = rectCircle.height() / 2;
        float radius = WIDTH_ARC / 2;
        if (isLoading) {
            if (currentAngle <= 1) {
                float leftCenterXFinal = rectCircle.left + radius;
                float rightCenterXFinal = rectCircle.right - radius;
                canvas.drawCircle(leftCenterXFinal, centerY, radius, paintCircle);
                canvas.drawCircle(rightCenterXFinal, centerY, radius, paintCircle);
            } else {
                canvas.drawArc(rectArc, leftCurrentStartPoint, currentAngle, false, paintArc);
                canvas.drawArc(rectArc, leftCurrentStartPoint + ANGLE_HALF_CIRCLE, currentAngle, false, paintArc);
            }
        } else {
            if (scaleInside <= 0) {
                canvas.drawCircle(centerX, centerY, radius, paintCircle);
            } else {
                float leftCenterXFinal = rectCircle.left + radius;
                float leftCenterXCurrent = centerX - ((centerX - leftCenterXFinal) * scaleInside);
                float rightCenterXFinal = rectCircle.right - radius;
                float rightCenterXCurrent = centerX + ((rightCenterXFinal - centerX) * scaleInside);
                canvas.drawCircle(leftCenterXCurrent, centerY, radius, paintCircle);
                canvas.drawCircle(rightCenterXCurrent, centerY, radius, paintCircle);
            }
        }
    }

    private void initViews() {
        setWillNotDraw(false);

        int origin = -ANGLE_HALF_CIRCLE;

        // 左边扇形边缘
        leftStartPoint = origin; // 从0开始
        leftEndPoint = origin; // 从0开始

        // 0-1时左边扇形边缘需要到达的位置
        leftFinalStartPoint = origin + (ANGLE_HALF_CIRCLE / 2) + (ANGLE_HALF_CIRCLE - MAX_ANGLE) / 2;
        leftFinalEndPoint = leftFinalStartPoint + MAX_ANGLE;
        // 1-0时左边扇形边缘需要到达的位置
        leftFinalStartPoint2 = origin + ANGLE_ONE_CIRCLE;
        leftFinalEndPoint2 = origin + ANGLE_ONE_CIRCLE;
    }

    private void initArcFrameRect(Canvas canvas) {
        if (rectArc == null) {
            float interval = WIDTH_ARC / 2;
            rectArc = new RectF(interval, interval, canvas.getWidth() - interval, canvas.getHeight() - interval);
        }
    }

    private void initCircleFrameRect(Canvas canvas) {
        if (rectCircle == null) {
            rectCircle = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    private void initPaintArc(int color) {
        if (paintArc == null) {
            paintArc = new Paint();
            paintArc.setStrokeWidth(WIDTH_ARC);
            paintArc.setStyle(Paint.Style.STROKE);
            paintArc.setStrokeCap(Paint.Cap.ROUND);
            paintArc.setAntiAlias(true);
        }
        paintArc.setColor(color);
    }

    private void initPaintCircle(int color) {
        if (paintCircle == null) {
            paintCircle = new Paint();
            paintCircle.setStyle(Paint.Style.FILL);
            paintCircle.setAntiAlias(true);
        }
        paintCircle.setColor(color);
    }

    private void initAnimator() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 2f);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setDuration(DURATION_LOADING_PER);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value <= 1) {
                        leftCurrentStartPoint = leftStartPoint + (value * (leftFinalStartPoint - leftStartPoint));
                        leftEndPoint2 = leftEndPoint + (value * (leftFinalEndPoint - leftEndPoint));
                        currentAngle = leftEndPoint2 - leftCurrentStartPoint;
                        leftStartPoint2 = leftCurrentStartPoint;
                    } else {
                        value = value - 1;
                        leftCurrentStartPoint = leftStartPoint2 + (value * (leftFinalStartPoint2 - leftStartPoint2));
                        currentAngle = leftEndPoint2 + (value * (leftFinalEndPoint2 - leftEndPoint2)) - leftCurrentStartPoint;
                    }
                    invalidate();
                }
            });
        }
    }

    public void startLoading() {
        if (isLoading) {
            return;
        }
        initAnimator();
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        isLoading = true;
        valueAnimator.start();
    }

    public void endLoading() {
        endLoading(false);
    }

    public void endLoading(boolean lockState) {
        if (!isLoading) {
            return;
        }
        if (valueAnimator == null) {
            isLoading = false;
            return;
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        isLoading = false;
        if (!lockState) {
            invalidate();
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void scaleInside(float scale) {
        endLoading();
        this.scaleInside = scale;
        if (scale <= 0) {
            scaleInside = 0;
        } else if (scaleInside > 1) {
            scaleInside = 1;
        } else {
            scaleInside = scale;
        }
        invalidate();
    }

    public float getCurrentScaleInside() {
        return scaleInside;
    }

    public void setColorArcAndCircle(int colorArcAndCircle) {
        this.colorArcAndCircle = colorArcAndCircle;
        if (!isLoading) {
            invalidate();
        }
    }

    public void setRadius(float radius) {
        this.WIDTH_ARC = radius;
        if (!isLoading) {
            invalidate();
        }
    }

}
