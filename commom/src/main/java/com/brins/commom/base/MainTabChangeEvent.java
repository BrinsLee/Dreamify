package com.brins.commom.base;

public class MainTabChangeEvent {
    private int lastPosition;
    private int currentPosition;

    public MainTabChangeEvent(int lastPosition, int currentPosition) {
        this.lastPosition = lastPosition;
        this.currentPosition = currentPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
