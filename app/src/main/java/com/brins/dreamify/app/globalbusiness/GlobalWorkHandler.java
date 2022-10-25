package com.brins.dreamify.app.globalbusiness;


import android.os.Looper;
import android.os.Message;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.dreamify.app.MainActivity;
import com.kugou.android.app.MainController;

public class GlobalWorkHandler extends StackTraceHandler {
    private MainActivity mMediaActivity;
    private MainController mMainController;
    private MainHandler mHandler;

    public GlobalWorkHandler(MainController mainController, Looper looper) {
        super(looper);
        this.mMediaActivity = mainController.getAttachActivity();
        this.mMainController = mainController;
        mHandler = mainController.mHandler;
    }

    //用于标记第一次启动时也会受到网络变化
    private boolean isFirstFromBoot=true;
    @Override
    public void handleMessage(Message msg) {

    }

}