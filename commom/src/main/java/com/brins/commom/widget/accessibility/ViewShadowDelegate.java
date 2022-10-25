package com.brins.commom.widget.accessibility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.blur.FastBlurUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ViewShadowDelegate {

    private static final int ALPHA = 40;
    private static final int OFFSET = 3;
    private static final int BLUR_RADIUS = 9;
    private static final int BITMAP_RANGE = 100;
    private static final int SHADOW_BORDER = 15;

    private ViewShadowDelegateCall mDelegateCall;

    private Bitmap mShadowBitmap;
    private RectF mRect;
    private Paint mPaint;

    private Matrix mMatrix;

    private boolean needCreateBitmap = true;

    private boolean mShowShadow = false;

    private long mLastExceptionTime;

    private Subscription subscription;

    private boolean mLimitLowVersion = true;

    public ViewShadowDelegate(ViewShadowDelegateCall delegateCall) {
        mDelegateCall = delegateCall;

        mMatrix = new Matrix();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColorFilter(SkinResourcesUtils.color2ColorFilter(Color.BLACK));

        mRect = new RectF();

        delegateCall.init();
    }

    public void setLimitLowVersion(boolean limitLowVersion) {
        this.mLimitLowVersion = limitLowVersion;
    }

    public void setShadowView(boolean showShadow) {
        if (showShadow != mShowShadow) {
            if (!showShadow) {
                recycleShadowBitmap();
            }
            mShowShadow = showShadow;

            needCreateBitmap = true;
            mDelegateCall.invalidateView();
        }
    }

    public void refreshShadowBitmap() {
        needCreateBitmap = true;

        mDelegateCall.invalidateView();
    }

    private void recycleShadowBitmap() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (mShadowBitmap != null && !mShadowBitmap.isRecycled()) {
            mShadowBitmap.recycle();
            mShadowBitmap = null;
        }
    }

    private void createBitmap() {
        recycleShadowBitmap();

        subscription = Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Bitmap>() {
                    @Override
                    public Bitmap call(Object o) {
                        Bitmap inputBitmap = null;
                        try {
                            float sca1 = BITMAP_RANGE / (float) mDelegateCall.getViewMeasuredWidth();
                            float sca2 = BITMAP_RANGE / (float) mDelegateCall.getViewMeasuredHeight();

                            mRect.set(0, 0, mDelegateCall.getViewMeasuredWidth(), mDelegateCall.getViewMeasuredHeight());

                            mMatrix.reset();
                            mMatrix.postScale(1 / sca1, 1 / sca2);
                            mMatrix.postTranslate(-SHADOW_BORDER / sca1, -SHADOW_BORDER / sca2 + OFFSET);

                            int width = BITMAP_RANGE + SHADOW_BORDER * 2;

                            inputBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_4444);
                            Canvas canvas = new Canvas(inputBitmap);
                            canvas.translate(SHADOW_BORDER - (1 - sca1) * width / 2, SHADOW_BORDER - (1 - sca2) * width / 2);
                            canvas.scale(sca1, sca2, width / 2f, width / 2f);

                            mDelegateCall.superDraw(canvas);
                            canvas.save();
                        } catch (Throwable throwable) {
                            mLastExceptionTime = System.currentTimeMillis();

                        }
                        return inputBitmap;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(new Func1<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap call(Bitmap inputBitmap) {
                        if (inputBitmap == null) {
                            return null;
                        }
                        Bitmap outputBitmap = null;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            return FastBlurUtils.geneFastBlur(inputBitmap, BLUR_RADIUS);
                        } else {
                            try {
                                //创建将在ondraw中使用到的经过模糊处理后的bitmap
                                outputBitmap = Bitmap.createBitmap(inputBitmap);

                                //创建RenderScript，ScriptIntrinsicBlur固定写法
                                RenderScript rs = RenderScript.create(mDelegateCall.getViewContext());
                                ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

                                //根据inputBitmap，outputBitmap分别分配内存
                                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
                                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

                                //设置模糊半径取值0-25之间，不同半径得到的模糊效果不同
                                blurScript.setRadius(BLUR_RADIUS);
                                blurScript.setInput(tmpIn);
                                blurScript.forEach(tmpOut);

                                //得到最终的模糊bitmap
                                tmpOut.copyTo(outputBitmap);
                            } catch (Throwable throwable) {
                                mLastExceptionTime = System.currentTimeMillis();
                            }
                            return outputBitmap;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        mShadowBitmap = bitmap;

                        mDelegateCall.invalidateView();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mLastExceptionTime = System.currentTimeMillis();
                    }
                });

    }

    private boolean frequentlyException() {
        return Math.abs(System.currentTimeMillis() - mLastExceptionTime) < 1000 * 60 * 5;
    }

    public void draw(Canvas canvas) {
        if (mLimitLowVersion && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            return;
        }

        if (mShowShadow) {
            if (needCreateBitmap && !frequentlyException()) {
                createBitmap();
                needCreateBitmap = false;
            }

            if (mShadowBitmap != null && !mShadowBitmap.isRecycled()) {
                canvas.saveLayerAlpha(mRect, ALPHA, Canvas.ALL_SAVE_FLAG);
                canvas.drawBitmap(mShadowBitmap, mMatrix, mPaint);
                canvas.restore();
            }
        }
    }

    public interface ViewShadowDelegateCall {
        void init();

        void invalidateView();

        int getViewMeasuredWidth();

        int getViewMeasuredHeight();

        void superDraw(Canvas canvas);

        Context getViewContext();
    }

}
