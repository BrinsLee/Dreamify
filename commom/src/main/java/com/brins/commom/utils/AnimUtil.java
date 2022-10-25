package com.brins.commom.utils;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimUtil {
    public static void clearAnim(View view) {
        try {
            Animation animation = view.getAnimation();
            if (animation != null && animation.hasEnded()) {
                animation.cancel();
                view.clearAnimation();
            }
        } catch (Exception e) {
        }
    }

    public static Animation makeShowEmoticonAnim() {
        // 输入框高度：80dp，表情高度：180dp
        float fromYValue = 180.0f / (80 + 180);
        // 使用Animation.RELATIVE_TO_SELF时，参数是百分比
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                fromYValue, Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());
        return anim;
    }

    public static Animation makeHideEmoticonAnim() {
        // 输入框高度：80dp，表情高度：180dp
        float toYValue = 180.0f / (80 + 180);
        // 使用Animation.RELATIVE_TO_SELF时，参数是百分比
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, toYValue);
        anim.setDuration(300);
        anim.setInterpolator(new DecelerateInterpolator());
        return anim;
    }


    public interface AnimationCallback {
        void onAnimationEnd();
    }

    public static class SimpleAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public static class SimpleAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
