package com.brins.commom.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.reflect.Reflect;

/**
 * Created by BuroneHuang on 2016/1/27.
 */
public class BroadcastUtil {

    /**
     * 需要接收应用外广播（如系统广播），使用该方法注册
     */
    public static void registerSysReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            DRCommonApplication.getContext().registerReceiver(receiver, filter);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        ;
    }

    /**
     * 需要接收应用外广播（如系统广播），使用该方法注册
     */
    public static void registerSysReceiver(BroadcastReceiver receiver, IntentFilter filter,
        String broadcastPermission, Handler handler) {
        try {
            DRCommonApplication.getContext()
                .registerReceiver(receiver, filter, broadcastPermission, handler);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void unregisterSysReceiver(BroadcastReceiver receiver) {
        try {
            DRCommonApplication.getContext().unregisterReceiver(receiver);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 需要接收应用内广播（含跨进程），使用该方法注册
     */
    public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            LocalBroadcastManager.getInstance(DRCommonApplication.getContext())
                .registerReceiver(receiver, filter);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            LocalBroadcastManager.getInstance(DRCommonApplication.getContext())
                .unregisterReceiver(receiver);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 若receiver同时接收应用内外的广播，使用该方法注册
     */
    public static void registerMultiReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registerReceiver(receiver, filter);
        registerSysReceiver(receiver, filter);
    }

    public static void unregisterMultiReceiver(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
        unregisterSysReceiver(receiver);
    }

    /**
     * 发送应用内广播，默认双进程
     */
    public static void sendBroadcast(Intent intent) {
        sendBroadcast(intent, false);
    }

    /**
     * 发送应用内广播，可选是否跨进程
     *
     * @param isLocalOnly true=仅当前进程内传播；false=双进程传播
     */
    public static void sendBroadcast(Intent intent, boolean isLocalOnly) {

        BroadcastSenderUtil.send(intent);
    }

    /**
     * 发送系统级广播
     */
    public static void sendSysBroadcast(Intent intent) {
        DRCommonApplication.getContext().sendBroadcast(intent);
    }

    @Deprecated
    public static void sendStickyBroadcast(Intent intent) {
        try {
            DRCommonApplication.getContext().sendStickyBroadcast(intent);
        } catch (SecurityException e) {
            DRCommonApplication.getContext().sendBroadcast(intent);
        }
    }

    @Deprecated
    public static void removeStickyBroadcast(Intent intent) {
        try {
            DRCommonApplication.getContext().removeStickyBroadcast(intent);
        } catch (SecurityException e) {
        }
    }

    @Nullable
    public static Intent registerStickyIntent(Context context, IntentFilter intentFilter) {
        Intent intent = null;
        try {
            intent = Reflect.on(context).call("registerReceiver", null, intentFilter).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }
}
