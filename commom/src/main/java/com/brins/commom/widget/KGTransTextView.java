package com.brins.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.brins.commom.R;
import com.kugou.uilib.widget.textview.KGUITextView;

public class KGTransTextView extends KGUITextView {

    private float normalAlpha = 1.0f;
    // kugou9.0ui要求按下效果不透明度为60%
    private float pressedAlpha = 0.6f;

    private boolean mEnableTrans = true;

    private boolean drawableChangeEnable = false;

    private boolean drawableCenter = false;

    public void setNormalAlpha(float normalAlpha) {
        this.normalAlpha = normalAlpha;
    }

    public void setPressedAlpha(float pressedAlpha) {
        this.pressedAlpha = pressedAlpha;
    }

    public KGTransTextView(Context context) {
        this(context,null);
    }

    public KGTransTextView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public KGTransTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getResources().obtainAttributes(attrs, R.styleable.KGTransTextView);
            if (array != null) {
                mEnableTrans = array.getBoolean(R.styleable.KGTransTextView_enable_click_alpha,true);
                normalAlpha = array.getFloat(R.styleable.KGTransTextView_normal_status_alpha,1.0f);
                pressedAlpha = array.getFloat(R.styleable.KGTransTextView_pressed_status_alpha,0.6f);
                drawableChangeEnable = array.getBoolean(R.styleable.KGTransTextView_drawable_change_enable, false);
                drawableCenter = array.getBoolean(R.styleable.KGTransTextView_drawable_gravity_center, false);
                array.recycle();
            }
        }
    }

    public void setEnableTrans(boolean enableTrans) {
        mEnableTrans = enableTrans;
    }

    public void setDrawableChangeEnable(boolean drawableChangeEnable) {
        this.drawableChangeEnable = drawableChangeEnable;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mEnableTrans) {
            setAlpha((isPressed() || isFocused() || isSelected()) ? pressedAlpha : normalAlpha);
        }

        if (drawableChangeEnable) {
            setAlpha(!isEnabled() ? pressedAlpha : normalAlpha);
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        if (drawableCenter) {
            Drawable[] drawables = getCompoundDrawables();
            if (drawables != null) {
                Drawable drawableLeft = drawables[0];
                if (drawableLeft != null) {
                    float textWidth = getPaint().measureText(getText().toString());
                    int drawablePadding = getCompoundDrawablePadding();
                    int drawableWidth = 0;
                    drawableWidth = drawableLeft.getIntrinsicWidth();
                    float bodyWidth = textWidth + drawableWidth + drawablePadding;
                    canvas.translate((getWidth() - bodyWidth) / 2, 0);
                }
            }
        }
        super.onDraw(canvas);
    }
}
