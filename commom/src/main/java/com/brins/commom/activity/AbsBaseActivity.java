/**
 * Copyright (C) 2004 KuGou-Inc.All Rights Reserved
 */

package com.brins.commom.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.brins.commom.base.AbsPromptActivity;
import com.brins.commom.dialog.AbsBaseDialog;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象基础Activity
 *
 * @author luo.qiang
 * @time Jun 21, 2013 3:22:49 PM
 */
public abstract class AbsBaseActivity extends AbsPromptActivity {

//    private static final String TAG = AbsBaseActivity.class.getName();

/*    private FactoryManager mFactoryManager;

    public FactoryManager getFactoryManager() {
        if (mFactoryManager == null) {
            mFactoryManager = new FactoryManager();
            mFactoryManager.addFactory(new SongItemFactory(this), FactoryManager.SONG_ITEM_VIEW);
            mFactoryManager.addFactory(new SongContentFactory(this), FactoryManager.SONG_CONTENT_VIEW);
            mFactoryManager.setMaxRecycledViews(FactoryManager.SONG_ITEM_VIEW, 20);
            mFactoryManager.setMaxRecycledViews(FactoryManager.SONG_CONTENT_VIEW, 20);
        }
        return mFactoryManager;
    }*/

    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        /*if(mFactoryManager != null) {
            mFactoryManager.updateSkin();
        }*/
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // manager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        if (getCurrentFocus() != null && manager != null) {
            try{
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    /**
     * 控制软键盘的显示隐藏
     */
    public void showSoftInput() {
        try{
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // end----------------软键盘控制代码----------------

    // start--------------显示fragment形式的dialog代码----------------
    public void showFragmentDialog(Class<?> cls, Bundle args) {
        try {
            AbsBaseDialog dialog = (AbsBaseDialog) cls.newInstance();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "dialog");
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }
    }

    private static String mSaveDir;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 在这里必须调用super，因为fragment响应Fragment.onActivityResult在FragmentActivity中有处理
        switch (resultCode) {
            case RESULT_OK:
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    };


    private Handler getWorkHandler() {
        if (workhandler == null) {
            workhandler = new WorkHandler(getWorkLooper());
        }
        return workhandler;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化音量控制, 可异步设置
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("wufuqin", "common onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();

        // 停止后台线程
        if (mWorker != null)
            mWorker.quit();
    }

    private final int DOWNLOAD_MSG = 0;

    private final int DOWNLOAD_MSG_DOWNLOAD = 1;

    private final int FIX_DOWNLOAD_MUSIC = 2;

    /**
     * 工作线程
     */
    private Handler workhandler;

    class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    }

    ;

    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
        }

        ;
    };

    public AbsBaseActivity getActivity() {
        return this;
    }

}
