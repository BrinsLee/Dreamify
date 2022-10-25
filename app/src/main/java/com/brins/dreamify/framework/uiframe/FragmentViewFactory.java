package com.brins.dreamify.framework.uiframe;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.kugou.common.base.uiframe.AnimationAccelerate;
import com.kugou.common.base.uiframe.FragmentViewBase;
import com.kugou.common.base.uiframe.FragmentViewHome;
import com.kugou.common.base.uiframe.FragmentViewNormal;
import com.kugou.page.framework.CreateViewInterface;

/**
 * @author 于晓飞
 * @date 2017/9/25.
 */
public class FragmentViewFactory implements CreateViewInterface {
    private static class Holder {
        private final static FragmentViewFactory INSTANCE = new FragmentViewFactory();
    }

    public static FragmentViewFactory getInstance() {
        return Holder.INSTANCE;
    }

    public interface AnimationAccelerateExtend {
        int KUX_WEB_PAGE = 18;
        int KTV_MINIMIZABLE = 19;
        int AI_SING_PLAYER = 20;
    }

    @Override
    public FragmentViewBase create(@NonNull Activity context, AnimationAccelerate animationAccelerate, @NonNull Bundle bundle) {
        FragmentViewBase fragmentView = null;
        if (animationAccelerate != null) {
            switch (animationAccelerate.viewType()) {
                /*case AnimationAccelerate.PLAYER: {
                    fragmentView = new FragmentViewPlayer3(context);
                    break;
                }*/
                case AnimationAccelerate.HOME: {
                    fragmentView = new FragmentViewHome(context);
                    break;
                }
                /*case AnimationAccelerate.NORMAL_RT: {
                    fragmentView = new FragmentViewNormalAnimationFirst(context, animationAccelerate);
                    break;
                }
                case AnimationAccelerate.VIDEO: {
                    fragmentView = new FragmentViewVideo(context);
                    break;
                }
                case AnimationAccelerate.MINI_APP_PAGE: {
                    fragmentView = new FragmentMiniAppMainPage(context);
                    break;
                }
                case AnimationAccelerate.MINI_APP_SUB_PAGE:
                    fragmentView = new FragmentMiniAppSubPage(context);
                    break;
                case AnimationAccelerate.CIRCLE_SET_PAGE:
                    fragmentView = new FragmentCircleSetPage(context);
                    break;
                case AnimationAccelerate.MINI_APP_STACK_MAIN:
                    fragmentView = new FragmentMiniAppStackPage(context, bundle.getInt("task_type"), bundle.getInt("start_type"));
                    break;
                case AnimationAccelerate.MV_COMMENT_LIST:
                    fragmentView = new FragmentViewMVComment(context);
                    break;
                case AnimationAccelerate.FLEX_WEB_PAGE:
                    fragmentView = new FragmentViewFlexWeb(context, animationAccelerate, bundle);
                    break;
                case 16:
                    fragmentView = new FragmentFollowListenRoomPage(context);
                    break;
                case 17:
                    fragmentView = new FragmentViewImmerseMV(context);
                    break;
                case AnimationAccelerateExtend.KUX_WEB_PAGE:
                    fragmentView = new FragmentViewKuxWeb(context, animationAccelerate, bundle);
                    break;
                case AnimationAccelerateExtend.KTV_MINIMIZABLE:
                    fragmentView = new FragmentViewKtvMinimizable(context);
                    break;
                case AnimationAccelerateExtend.AI_SING_PLAYER:
                    fragmentView = new FragmentViewAISingPlayer(context);
                    break;
                case AnimationAccelerate.LOCAL_AUDIO_PAGE:
                    fragmentView = new FragmentViewLocalAudioPage(context);
                    break;*/
                default: {
                    fragmentView = new FragmentViewNormal(context);
                    break;
                }
            }
        }
        if (fragmentView == null) {
            fragmentView = new FragmentViewNormal(context);
        }
        return fragmentView;
    }
}
