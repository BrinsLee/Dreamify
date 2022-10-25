package com.brins.commom.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;

/**
 * Created by taicixu on 2015/11/11.
 *
 * @author taicixu
 */
public class SkinBasicTransText extends TextView implements ISkinViewUpdate {

    private boolean pressTrans;

    private float pressAlpha = 0.3f;

    public SkinBasicTransText(Context context) {
        super(context);
    }

    public SkinBasicTransText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkinBasicTransText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void changeDrawableState() {
        setAlpha((isPressed() || isSelected() || isFocused() || (enableTrans && !isEnabled())) ? pressAlpha : 1.0f);
    }

    public void setPressAlpha(float pressAlpha) {
        this.pressAlpha = pressAlpha;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (pressTrans) {
            changeDrawableState();
        }
    }

    @Override
    public void updateSkin() {
    }

    private boolean isPressTrans() {
        return pressTrans;
    }

    public void setPressTrans(boolean pressTrans) {
        this.pressTrans = pressTrans;
    }

    private boolean enableTrans; //enable状态为不的时候，也显示透明度

    public void setEnableTrans(boolean enableTrans) {
        this.enableTrans = enableTrans;
    }
}
