package com.brins.commom.utils;

import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.entity.NetworkType;
import com.brins.commom.utils.log.DrLog;

/**
 * Created by zhaomingzhang on 2016/10/13.
 */
public class LoadingTimeoutUtil {
    public static final int DEFALUT_LOADING_MAX_DURATION=15*1000;
    public static final int DEFALUT_NET2G_LOADING_MAX_DURATION=20*1000;

    public static final int IGNORE_TIME=0;//无限制

    public static final int WALLET_QUERY_MAX_DURATION = 4 * 1000;

    public static int getMaxDurationTime(){
       int netType= SystemUtils.getNetWorkType(DRCommonApplication.getContext());
        if(NetworkType.NET_2G_INT==netType){
            if (DrLog.DEBUG) DrLog.i("zzm-loading","maxTime:"+DEFALUT_NET2G_LOADING_MAX_DURATION);
            return DEFALUT_NET2G_LOADING_MAX_DURATION;
        }
        if (DrLog.DEBUG) DrLog.i("zzm-loading","netType:"+netType+"--maxTime:"+DEFALUT_LOADING_MAX_DURATION);
        return DEFALUT_LOADING_MAX_DURATION;
    }

    public static int getRemainingTime(int totalTime,long startTime){
        long currentTime=System.currentTimeMillis();
        int remaingTime=totalTime  -(int)((currentTime-startTime));
        if (DrLog.DEBUG) DrLog.i("zzm-loading","remaingTime:"+remaingTime);
        return remaingTime;
    }

    private static final int MAX_OUT_TIME = 3 * 60 *1000;

    public static int getMaxDurationTime(int songNum){
        int MIN_OUT_TIME=getMaxDurationTime();
        int time = ((songNum / 20) + 1 )* MIN_OUT_TIME;
        int minTime = MIN_OUT_TIME * 2;
        time = time >= minTime ? time : minTime;
        time = time <= MAX_OUT_TIME ? time : MAX_OUT_TIME;
        if (DrLog.DEBUG) DrLog.i("zzm-loading","songNum:"+songNum+"getMaxDurationTime:"+time);
        return time;
    }
}
