package com.brins.commom.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.utils.log.DrLog;

/**
 * Created by buronehuang on 2018/8/23.
 */

public class BroadcastSenderUtil {

    public static void send(Intent intent) {
        Context context = DRCommonApplication.getContext();
        intent.setExtrasClassLoader(context.getClassLoader());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        if (DrLog.DEBUG) DrLog.d("BroadcastSender", "send local broadcast: action = " + intent.getAction());

    }

    public static void sendLocal(Intent intent) {

    }

}
