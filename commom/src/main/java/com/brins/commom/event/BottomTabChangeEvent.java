package com.brins.commom.event;

/**
 * @author zhouzhankun
 * @time 2019-06-27 10:27
 **/

public class BottomTabChangeEvent {

    private int tab;
    //1:手动点击底部tab通知主页；2：主页滑动通知底部tab更新；3：底部tab初始化完成显示选中下标；
    private int toWho;

    private boolean isClick;

    public BottomTabChangeEvent setTab(int tab) {
        this.tab = tab;
        return this;
    }

    public int getTab() {
        return tab;
    }

    public BottomTabChangeEvent setToWho(int toWho) {
        this.toWho = toWho;
        return this;
    }

    public boolean isClick() {
        return isClick;
    }

    public BottomTabChangeEvent setClick(boolean click) {
        isClick = click;
        return this;
    }

    public int getToWho() {
        return toWho;
    }
}
