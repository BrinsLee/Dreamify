package com.brins.commom.base.lifecycle;

public interface KGLifeCycleOwner {
    /**
     * 第一次刷新界面的线程等待onFragmentFirstStart回调后继续往下执行 ！！！该方法只能在非主线程执行.
     */
    void waitForFragmentFirstStart();

    /**
     * 移除observer
     * @param observer
     */
    void removeLifeCycleObserver(KGLifeCycleObserver observer);
}
