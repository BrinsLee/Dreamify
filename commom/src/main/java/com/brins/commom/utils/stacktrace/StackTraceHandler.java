package com.brins.commom.utils.stacktrace;

import android.os.Looper;
import android.os.Message;
import com.brins.commom.utils.log.DrLog;
import java.lang.ref.WeakReference;

public class StackTraceHandler extends android.os.Handler {

    public StackTraceHandler() {
        super();
    }

    public StackTraceHandler(Callback callback) {
        super(callback);
    }

    public StackTraceHandler(Looper looper) {
        super(looper);
    }

    public StackTraceHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    private static final int INVALID_WHAT = -12345;

    private int mLastWhat = INVALID_WHAT;
    private WeakReference<Runnable> mLastRunnable;

    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        // 注意：postAtFrontOfQueue和sendMessageAtFrontOfQueue不会走到该方法
        if (msg != null) {
            msg.obj = new ObjWrapper(msg.obj, isSameMsg(msg));
        }
        return super.sendMessageAtTime(msg, uptimeMillis);
    }

    @Override
    public void dispatchMessage(Message msg) {
        if (msg != null && msg.obj instanceof ObjWrapper) {
            ObjWrapper wrapper = (ObjWrapper) msg.obj;
            msg.obj = wrapper.wrapped;
        }
        recordLastMsg(msg);
        try {
            super.dispatchMessage(msg);
        } finally {
            clearLastMsg();
        }
    }

    private void recordLastMsg(Message msg) {
        if (msg == null) {
            return;
        }

        if (msg.getCallback() != null) {
            mLastRunnable = new WeakReference<>(msg.getCallback());
        } else {
            mLastWhat = msg.what;
        }
    }

    private void clearLastMsg() {
        mLastRunnable = null;
        mLastWhat = INVALID_WHAT;
    }

    /**
     * 是否在执行某消息时又发出同一个消息
     */
    private boolean isSameMsg(Message msg) {
        if (msg == null
                || getLooper().getThread() != Thread.currentThread()) { // 其他线程发出的消息不属于循环消息
            return false;
        }

        if (msg.getCallback() != null) {
            WeakReference<Runnable> lastRunnable = mLastRunnable;
            return lastRunnable != null && lastRunnable.get() == msg.getCallback();
        } else {
            return msg.what == mLastWhat;
        }
    }

    private static class ObjWrapper {
        Object wrapped;

        ObjWrapper(Object wrapped, boolean sameMsg) {
            this.wrapped = wrapped;
        }
    }
}
