package com.brins.commom.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sunzheng on 15/11/22.
 */
public class KGThreadPool {

    private volatile HandlerThread statisticsThread;
    private volatile HandlerThread mainPageThread;
    private static volatile KGThreadPool sPool = null;
    private static volatile Handler sHandler = null;
    private static volatile Handler statisticsHandler = null;

    private ThreadPoolExecutor mPoolService;

    private KGThreadPool() {
        int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int POOL_SIZE = Math.min(CPU_COUNT * 50, 200);
        mPoolService = new ThreadPoolExecutor(CPU_COUNT, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        mPoolService.allowCoreThreadTimeOut(true);
    }

    public  static KGThreadPool getInstance() {
        if (sPool == null) {
            init();
        }
        return sPool;
    }

    /**
     * 仅用于优化线程池数量，平常使用异步线程执行异步任务的话禁止使用这个方法
     * @return
     */
    public ThreadPoolExecutor getPoolService() {
        return mPoolService;
    }

    private static synchronized void init(){
        sPool = new KGThreadPool();
    }

    public static void schedule(Runnable task) {
        getInstance().execute(task);
    }

    public static void scheduleStatistics(Runnable statisticsTask) {
        if (statisticsHandler == null) {
            synchronized (KGThreadPool.class) {
                if (statisticsHandler == null) {
                    statisticsHandler = new Handler(getInstance().getStatisticsLooper());
                }
            }
        }
        statisticsHandler.post(statisticsTask);
    }

    public static void scheduleAndWait(Runnable task, long timeout) {
        BlockingRunnable blockTask = new BlockingRunnable(task);
        schedule(blockTask);
        blockTask.justWait(timeout);
    }

    public void execute(Runnable task) {
        if (task != null && !mPoolService.isShutdown()) {
            mPoolService.execute(task);
        }
    }

    public void shutdown() {
        mPoolService.shutdown();
        sPool = null;
        mPoolService = null;
    }

    public Looper getStatisticsLooper() {
        if (statisticsThread == null) {
            synchronized (KGThreadPool.class) {
                if (statisticsThread == null) {
                    statisticsThread = new HandlerThread("Statistics", Process.THREAD_PRIORITY_BACKGROUND);
                    statisticsThread.start();
                }
            }
        }
        return statisticsThread.getLooper();
    }

    public Looper getMainWorkLooper() {
        if (mainPageThread == null) {
            synchronized (KGThreadPool.class) {
                if (mainPageThread == null) {
                    mainPageThread = new HandlerThread("mainPage", Process.THREAD_PRIORITY_BACKGROUND);
                    mainPageThread.start();
                }
            }
        }
        return mainPageThread.getLooper();
    }


    private static Handler getMainHandler() {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        return sHandler;
    }

    public static void postToMainThread(Runnable task){
        if (task!=null){
            getMainHandler().post(task);
        }
    }

    public static void postToMainThread(Runnable task, long delay) {
        if (task != null) {
            getMainHandler().postDelayed(task, delay);
        }
    }

    public static void removeMainThread(Runnable task) {
        if (task != null) {
            getMainHandler().removeCallbacks(task);
        }
    }

    public void executeSwitchToMainThread(Callable<?> callable) {
        execute(callable);
    }

    public abstract static class Callable<T> implements Runnable {
        @Override
        public void run() {
            final T t = onBackground();
            postToMainThread(new Runnable() {
                @Override
                public void run() {
                    onCompleted(t);
                }
            });
        }

        public abstract T onBackground();

        public abstract void onCompleted(T t);
    }

}
