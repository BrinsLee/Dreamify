package com.brins.commom.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by oceanou on 2016/3/4.
 */
public class ThreadUtils {

    static {
        sUIHandler = new Handler(Looper.getMainLooper());
    }

    private static Handler sUIHandler;

    private static ExecutorService fixedThreadExecutor = Executors.newFixedThreadPool(5);

    private static ScheduledExecutorService singleThreadExecutor = Executors.newScheduledThreadPool(1);

    // 单线程确保串行任务
    public static void executeOnSingleThread(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }

    public static void executeOnSingleThreadDelay(Runnable runnable, long delay, TimeUnit unit) {
        singleThreadExecutor.schedule(runnable, delay, unit);
    }

    public static void execute(Runnable runnable) {
        fixedThreadExecutor.execute(runnable);
    }

    public static boolean isInMainThread() {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    public static void runOnUIThreadNextLoop(Runnable runnable) {
        sUIHandler.post(runnable);
    }

    public static void runOnUIThread(Runnable runnable) {
        if (isInMainThread()) {
            runnable.run();
        } else {
            sUIHandler.post(runnable);
        }
    }

    public static void runDelayOnUIThread(Runnable runnable, long delayMillis) {
        sUIHandler.postDelayed(runnable, delayMillis);
    }

    public static void removeRunnable(Runnable runnable) {
        sUIHandler.removeCallbacks(runnable);
    }

    public static void removeRunnableOnSingleThread(Runnable runnable) {
        if (singleThreadExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadExecutor = (ThreadPoolExecutor) singleThreadExecutor;
            threadExecutor.remove(runnable);
        }
    }

}
