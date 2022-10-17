package com.brins.commom.utils;

import android.os.Handler;
import android.os.SystemClock;

/**
 * Created by huangmingzhi on 2018/11/18.
 */


public class BlockingRunnable implements Runnable {
    private final Runnable mTask;
    private boolean mDone;

    public BlockingRunnable(Runnable task) {
        mTask = task;
    }

    @Override
    public void run() {
        try {
            mTask.run();
        } finally {
            synchronized (this) {
                mDone = true;
                notifyAll();
            }
        }
    }

    public boolean justWait(long timeout) {
        synchronized (this) {
            if (timeout > 0) {
                final long expirationTime = SystemClock.uptimeMillis() + timeout;
                while (!mDone) {
                    long delay = expirationTime - SystemClock.uptimeMillis();
                    if (delay <= 0) {
                        return false; // timeout
                    }
                    try {
                        wait(delay);
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                while (!mDone) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
        return true;
    }

    public boolean postAndWait(Handler handler, long timeout) {
        if (handler == null) {
            new Thread(this).start(); // kg-suppress REGULAR.THREAD tool method, for some situation
        } else if (!handler.post(this)) {
            return false;
        }
        return justWait(timeout);
    }

}