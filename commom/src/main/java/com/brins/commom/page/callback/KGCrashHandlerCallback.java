package com.brins.commom.page.callback;

import com.kugou.page.FrameworkCallbacks;

public class KGCrashHandlerCallback implements FrameworkCallbacks.CrashHandlerCallback {
    @Override
    public void reflectCrashHandlerSaveAnException(Throwable ex) {
    }

    @Override
    public void reflectCrashHandlerSaveAnException(Throwable ex, String append) {
    }

    @Override
    public void reflectCrashHandlerSaveAnException(Throwable ex, String append, boolean isOnlyTree) {
    }

    @Override
    public void sendCrashToBugTree(Throwable t) {

    }

    @Override
    public void sendCrashToBugTree(Throwable t, String msg) {

    }
}
