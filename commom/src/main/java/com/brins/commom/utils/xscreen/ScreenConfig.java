package com.brins.commom.utils.xscreen;

/**
 *
 * 特殊屏幕配置
 * Created by jasonzuo.
 */

class ScreenConfig {

    int screenConfigSpec = 0;
    final int statusBarHeight;

    /**
     * VIVO的屏下指纹需要调整锁屏页
     */
    private int lockControlBMargin;
    private int lockSlideBMargin;

    /**
     * 凹口宽度
     */
    private int concaveWidth = 0;

    ScreenConfig(int screenConfigSpec, int statusBarHeight) {
        this.screenConfigSpec = screenConfigSpec;
        this.statusBarHeight = statusBarHeight;
    }

    int getLockControlBMargin() {
        return lockControlBMargin;
    }

    void addScreenConfig(int configFlag) {
        screenConfigSpec |= configFlag;
    }

    void setLockControlBMargin(int lockControlBMargin) {
        this.lockControlBMargin = lockControlBMargin;
    }

    int getLockSlideBMargin() {
        return lockSlideBMargin;
    }

    void setLockSlideBMargin(int lockSlideBMargin) {
        this.lockSlideBMargin = lockSlideBMargin;
    }

    int getConcaveWidth() {
        return concaveWidth;
    }

    void setConcaveWidth(int concaveWidth) {
        this.concaveWidth = concaveWidth;
    }

    @Override
    public String toString() {
        return "ScreenConfig{" +
                "screenConfigSpec=" + screenConfigSpec +
                ", statusBarHeight=" + statusBarHeight +
                ", lockControlBMargin=" + lockControlBMargin +
                ", lockSlideBMargin=" + lockSlideBMargin +
                ", concaveWidth=" + concaveWidth +
                '}';
    }
}
