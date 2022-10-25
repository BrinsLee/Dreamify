package com.kugou.common.base.uiframe;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewPropertyAnimator;
import com.kugou.renderthread.RenderThread;

/**
 * @author 于晓飞
 * @date 2017/9/11.
 */
public class FragmentViewNormal extends FragmentViewSwipeBase {

    private static boolean isFirstUseRt = true;

    public FragmentViewNormal(@NonNull Context context) {
        super(context);
    }

    /**
     * 在动画开始前执行，此时Fragment还没有准备完毕
     * @param clientWidth 容器宽度
     * @param clientHeight 容器高度
     * @param args 打开Fragment使用的参数，可以用来传递自定义参数
     */
    @Override
    public void prepareEnterAnimation(int clientWidth, int clientHeight, Bundle args) {
        setTranslationX(clientWidth);
        setAlpha(0);
    }

    @Override
    public void enterAnimation(int width) {
        ViewPropertyAnimator animatorIn = AnimatorCompact.getInstance().generateAnimator(this);
        animatorIn.translationX(0F);
        animatorIn.alpha(1.0f);
        animatorIn.setDuration(ANIMATION_DURATION);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                setViewState(STATE_IDLE, false);
            }
        }, ANIMATION_DURATION);

        animatorIn.setListener(new FragmentViewBase.EnterAnimatorListener());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && isEnableRt()) {
            RenderThread.animate(this, animatorIn);
        }
        setViewState(STATE_SETTLING, false);
    }

    private boolean isEnableRt() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && isFirstUseRt) {
            isFirstUseRt = false;
            return false;
        }
        return true;
    }

    @Override
    public void leaveAnimation(final FragmentViewBase targetContainer) {
        animate().setDuration(ANIMATION_DURATION)
                .translationX(getMeasuredWidth())
                .setListener(new FragmentViewBase.LeaveAnimatorListener(this, targetContainer));
    }



    @Override
    public boolean isAnimationFirst() {
        return false;
    }
}
