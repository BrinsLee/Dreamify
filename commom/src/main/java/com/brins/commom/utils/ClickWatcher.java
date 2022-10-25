package com.brins.commom.utils;

public class ClickWatcher {

    private long lastClickTimeStamp = 0;

    private long time = 500;

    public ClickWatcher(long time) {
        this.time = time;
    }

    public ClickWatcher() {
    }

    public boolean isNextClickAllowed() {
        long currentTime = System.currentTimeMillis();
        boolean clickable = currentTime - lastClickTimeStamp > time;
        if (clickable) {
            lastClickTimeStamp = currentTime;
        }
        return clickable;
    }
}
