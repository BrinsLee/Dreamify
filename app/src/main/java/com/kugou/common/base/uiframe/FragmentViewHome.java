package com.kugou.common.base.uiframe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kugou.common.base.uiframe.FragmentViewBase;

/**
 * @author 于晓飞
 * @date 2017/9/25.
 */
public class FragmentViewHome extends FragmentViewBase {

    public FragmentViewHome(@NonNull Context context) {
        super(context);
    }

    @Override
    public void prepareEnterAnimation(int clientWidth, int clientHeight, Bundle args) {
        // nothing
    }

    @Override
    public void enterAnimation(int width) {
        throw new IllegalStateException("MainContainer doesn't have a enter animation");
    }

    @Override
    public void leaveAnimation(final FragmentViewBase targetContainer) {
        throw new IllegalStateException("MainContainer doesn't have a leave animation.");
    }

    @Override
    public boolean isAnimationFirst() {
        return false;
    }
}
