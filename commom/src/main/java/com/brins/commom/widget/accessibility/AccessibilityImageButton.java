package com.brins.commom.widget.accessibility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageButton;
import com.brins.commom.utils.log.DrLog;

/**
 * @author jamywang
 * @since 2018/6/12 21:39
 */

public class AccessibilityImageButton extends ImageButton{
    public static final String TAG = "AccessibilityImageButton";

    AccessibilityAttrProvider mProvider = new AccessibilityAttrProvider();

    private ViewShadowDelegate delegate;

    public AccessibilityImageButton(Context context) {
        this(context, null);
    }

    public AccessibilityImageButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccessibilityImageButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mProvider.init(context, attrs);
        if (mProvider.needSpeak() && TextUtils.isEmpty(getContentDescription())){
            int srcId = mProvider.getSrcId();
            String remark = mProvider.getRemark();
            String contentDescBySrcName = AccessibilityHelper.getInstance().getContentDescBySrcName(srcId, remark, this);
            if (DrLog.DEBUG) {
                DrLog.d(TAG, "AccessibilityImageButton: srcId="+srcId+" remark="+remark+" contentDescBySrcName="+contentDescBySrcName+" getContentDescription="+mProvider.getContentDescription());
            }
            setContentDescription(contentDescBySrcName);
        }
    }

    public void setAccessibilityRemark(String remark){
        mProvider.setRemark(remark);
        setContentDescription(AccessibilityHelper.getInstance().getContentDescBySrcName(mProvider.getSrcId(), mProvider.getRemark()));
    }

    private void initViewShadowDelegate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (delegate != null) {
                return;
            }

            delegate = new ViewShadowDelegate(new ViewShadowDelegate.ViewShadowDelegateCall() {
                @Override
                public void init() {
                    if (getBackground() == null) {
                        setBackgroundColor(Color.TRANSPARENT);
                    }
                    setWillNotDraw(false);
                }

                @Override
                public void invalidateView() {
                    invalidate();
                }

                @Override
                public int getViewMeasuredWidth() {
                    return getMeasuredWidth();
                }

                @Override
                public int getViewMeasuredHeight() {
                    return getMeasuredHeight();
                }

                @Override
                public void superDraw(Canvas canvas) {
                    AccessibilityImageButton.super.draw(canvas);
                }

                @Override
                public Context getViewContext() {
                    return getContext();
                }
            });
        }
    }

    public void setShadowView(boolean showShadow) {
        if (showShadow) {
            initViewShadowDelegate();
        }

        if(delegate != null) {
            delegate.setShadowView(showShadow);
        }
    }


    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);

        if(delegate != null) {
            delegate.refreshShadowBitmap();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        if(delegate != null) {
            delegate.refreshShadowBitmap();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        if(delegate != null) {
            delegate.refreshShadowBitmap();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        if (delegate != null) {
            delegate.draw(canvas);
        }
        super.draw(canvas);
    }


}
