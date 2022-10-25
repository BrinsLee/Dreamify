package com.brins.commom.base;

/**
 * Created by burone on 2017/4/6.
 */

public interface IPlayingBar {

    void controlVisibility(boolean show, boolean hasMainBottomView);

    void makeSureVisibility(boolean show, boolean hasMainBottomView);

    void animVisibility(boolean visible);

    boolean isGlobalVisibility();

    void rejectTouchMotion(boolean reject);

    void startInsertSongAnimation();

    int[] getPlayingBarLocationOnScreen();
}
