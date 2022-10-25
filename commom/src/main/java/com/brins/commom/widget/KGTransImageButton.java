package com.brins.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.brins.commom.R;
import com.brins.commom.widget.accessibility.AccessibilityImageButton;

public class KGTransImageButton extends AccessibilityImageButton {

    public boolean isCanAlpha = true;

    public Float defAlpha = null;
    private float pressedAlpha = 0.3f;
    private CornerClipDelegate cornerClipDelegate;

    public KGTransImageButton(Context context) {
        super(context);
        init(null);
    }

    public KGTransImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public KGTransImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getResources().obtainAttributes(attrs, R.styleable.KGTransImageButton);
            if (array != null) {
                float defAlpha = array.getFloat(R.styleable.KGTransImageButton_kg_tib_alpha_def, -1);
                isCanAlpha = array.getBoolean(R.styleable.KGTransImageButton_can_alpha,true);
                if (defAlpha > 0) {
                    this.defAlpha = defAlpha;
                }
                array.recycle();
            }
        }
        cornerClipDelegate = new CornerClipDelegate(this);
    }

    public void setCanAlpha(boolean canAlpha) {
        this.isCanAlpha = canAlpha;
    }

    public void setPressedAlpha(float pressedAlpha) {
        this.pressedAlpha = pressedAlpha;
    }

    public void setDefAlpha(float defAlpha){
        this.defAlpha = defAlpha;
        if(isCanAlpha){
            setAlpha(defAlpha);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(isCanAlpha)
            setAlpha((isPressed() || isFocused() || isSelected() || !isEnabled()) ? pressedAlpha : defAlpha == null ? 1.0f : defAlpha);
    }

    public void setShouldClip(boolean shouldClip) {
        setShouldClip(shouldClip, false);
    }

    public void setShouldClip(boolean shouldClip, boolean isRect) {
        if (cornerClipDelegate != null) {
            cornerClipDelegate.setShouldClip(shouldClip, isRect);
        }
    }
}
