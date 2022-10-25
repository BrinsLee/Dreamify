package com.brins.commom.utils;


import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.base.IPlayingBar;
import com.kugou.common.widget.base.NavigationBarCompat;
import java.lang.ref.WeakReference;

/**
 * Created by thomaschen on 2016/1/14.
 */
public class KGPlayingBarUtil {
    private static int mPlayingBarHeight;

    private static int mBottomTabHeight;

    private static int mMainPlayingBarHeight;

    private static int mListenPartViewHeight;

    private static int mListenPanelViewHeight;

    private static boolean mIsPlayingBarShowing = true;

    private static boolean mIsListenPanelShowing;

    private static int mPlayingBarAvatarHeight;

    private static int mPlayingBarMarginBottom;

    /**
     * 包含播放栏和底部间距的高度
     */
    public static int getPlayingBarHeight() {
        if (mPlayingBarHeight <= 0) {
            mPlayingBarHeight = DRCommonApplication.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.kg_main_playing_bar_large_height);
        }
        return mPlayingBarHeight;
    }

    public static void setPlayingBarHeight(int height) {
        mPlayingBarHeight = height;
    }

    public static void setmMainPlayingBarHeight(int mMainPlayingBarHeight) {
        KGPlayingBarUtil.mMainPlayingBarHeight = mMainPlayingBarHeight;
    }

    private static WeakReference<IPlayingBar> sRef = null;

    public static void setCurrentPlayingBar(IPlayingBar playingBar) {
        sRef = new WeakReference<>(playingBar);
    }

    public static void startInsertSongAnimation() {
        if (sRef == null) return;
        IPlayingBar playingBar = sRef.get();
        if (playingBar == null) return;
        playingBar.startInsertSongAnimation();
    }

    public static int[] getPlayingBarLocationOnScreen() {
        int[] location = new int[]{0, 0};
        if (sRef == null) return location;
        IPlayingBar playingBar = sRef.get();
        if (playingBar == null) return location;
        return playingBar.getPlayingBarLocationOnScreen();
    }

    /**
     * 获得首页底部导航bar高度
     * @return
     */
    public static int getmBottomTabHeight() {
        if (mBottomTabHeight <= 0) {
            mBottomTabHeight = DRCommonApplication.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.common_main_bottom_bar_debug_height);  //随时会改
            if (NavigationBarCompat.isTranslucentNavigationBar()) {
                mBottomTabHeight += NavigationBarCompat.windowBottomInset();
            }
        }
        return mBottomTabHeight;
    }

    public static int getListenPartHeight() {
        if (mListenPartViewHeight <= 0) {
            mListenPartViewHeight = DRCommonApplication.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.kg_playing_bar_online_horn_max_height);  //随时会改
        }
        return mListenPartViewHeight;
    }

    public static int getListenPanelHeight(){
        if(mListenPanelViewHeight <= 0){
            mListenPanelViewHeight = SystemUtils.dip2px(36);
        }
        return mListenPanelViewHeight;
    }

    /**
     * 获得首页播放bar高度
     * @return
     */
    public static int getMainPlayingbarHeight() {
        if (mMainPlayingBarHeight <= 0) {
            mMainPlayingBarHeight = DRCommonApplication.getContext().getResources()
                    .getDimensionPixelOffset(R.dimen.kg_main_playing_bar_height);
        }
        return mMainPlayingBarHeight;
    }
    public static void setmBottomTabHeight(int mBottomTabHeight) {
        KGPlayingBarUtil.mBottomTabHeight = mBottomTabHeight;
    }

    public static boolean isPlayingBarShowing() {
        return mIsPlayingBarShowing;
    }

    public static void setPlayingBarShowing(boolean showing) {
        mIsPlayingBarShowing = showing;
    }

    public static boolean isListenPanelShowing() {
        return mIsListenPanelShowing;
    }

    public static void setListenPanelShowing(boolean showing) {
        mIsListenPanelShowing = showing;
    }

    public static int getPlayingBarAvatarHeight() {
        if (mPlayingBarAvatarHeight <= 0) {
            mPlayingBarAvatarHeight = SystemUtils.dip2px(50f);
        }
        return mPlayingBarAvatarHeight;
    }

    public static void setPlayingBarAvatarHeight(int height) {
        KGPlayingBarUtil.mPlayingBarAvatarHeight = height;
    }

    public static int getPlayingBarMarginBottom() {
        if (mPlayingBarMarginBottom <= 0) {
            mPlayingBarMarginBottom = SystemUtils.dip2px(15f);
        }
        return mPlayingBarMarginBottom;
    }

    public static void setPlayingBarMarginBottom(int height) {
        KGPlayingBarUtil.mPlayingBarMarginBottom = height;
    }
}
