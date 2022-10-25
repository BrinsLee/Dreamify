package com.brins.commom.base.bar;

import android.graphics.drawable.Drawable;

public interface ItemTabViewInterface {

    BottomTabItemView getRootView();

    String getDescribeText();

    int getDefaultIconRes();

    Drawable getSelectedDrawable();

    Drawable getUnselectedDrawable();

    boolean isSelected();

    String getAnimationResPath();
}
