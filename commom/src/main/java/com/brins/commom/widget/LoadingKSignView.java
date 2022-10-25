package com.brins.commom.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.brins.commom.R;

public class LoadingKSignView extends FrameLayout {

    private ImageView ivKSign;
    private int colorKSign = 0xff5ecef7;
    private int colorBackground = 0x205ecef7;

    public LoadingKSignView(@NonNull Context context) {
        super(context);
        init();
    }

    public LoadingKSignView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingKSignView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ivKSign = new ImageView(getContext());
        ivKSign.setImageResource(R.drawable.ic_loading_k);
        ivKSign.setColorFilter(colorKSign);
        addView(ivKSign);
    }

    public void setKSignViewSize(int width, int height) {
        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        ivKSign.setLayoutParams(params);
        ivKSign.setPivotX(width / 2);
        ivKSign.setPivotY(height / 2);
    }

    public void setColor(int bgColor, int kSignColor) {
        ivKSign.setColorFilter(kSignColor);
        setBackground(bgColor);
        this.colorBackground = bgColor;
        this.colorKSign = kSignColor;
    }

    private void setBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(getMeasuredHeight() / 2);
        setBackgroundDrawable(drawable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackground(colorBackground);
    }

    public void scaleInside(float scale) {}

}
