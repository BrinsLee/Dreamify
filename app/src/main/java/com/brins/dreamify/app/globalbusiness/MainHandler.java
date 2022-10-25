package com.brins.dreamify.app.globalbusiness;

import android.os.Message;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.brins.dreamify.app.MainActivity;
import com.kugou.android.app.MainController;

public class MainHandler extends StackTraceHandler {
    private final String TAG = MainActivity.class.getName();
    public static final int MSG_UI_FM_UPDATE_STATE = -1;//防止冲突，使用负数
    public static final int MSG_UI_JUMP_TO_KUQUN_FROM_LOCKSCREEN = -2; //防止冲突，使用负数,从锁屏跳转酷群
    private MainActivity mMediaActivity;
    private MainController mMainController;

    public MainHandler(MainController mainController) {
        this.mMediaActivity = mainController.getAttachActivity();
        this.mMainController = mainController;
    }


    @Override
    public void handleMessage(Message msg) {

    }

}
