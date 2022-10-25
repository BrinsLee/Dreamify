package com.brins.commom.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 优先展示某种颜色的TextView。用来拦截外界调用setTextColor()
 * Created by beniozhang on 2021/6/11
 */
public class PreferredColorTextView extends TextView {
    private ColorStateList mPreferredTextColor;
    private ColorStateList mOriginTextColor;

    public PreferredColorTextView(Context context) {
        super(context);
    }

    public PreferredColorTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferredColorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPreferredTextColor(ColorStateList colors) {
        mPreferredTextColor = colors;
        if (colors != null) {//优先设置此颜色
            super.setTextColor(colors);
        } else if (mOriginTextColor != null) {//恢复之前设置的颜色
            super.setTextColor(mOriginTextColor);
        }
    }

    @Override
    public void setTextColor(int color) {
        if (mPreferredTextColor != null) {// 此时不允许设置其他颜色
            mOriginTextColor = ColorStateList.valueOf(color);
            return;
        }
        super.setTextColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        if (mPreferredTextColor != null) {// 此时不允许设置其他颜色
            mOriginTextColor = colors;
            return;
        }
        super.setTextColor(colors);
    }
}
