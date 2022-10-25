package com.kugou.android.app;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import com.brins.commom.base.AdditionalContent;
import com.brins.commom.utils.log.DrLog;
import com.brins.dreamify.app.MainActivity;
import com.brins.dreamify.app.globalbusiness.GlobalWorkHandler;
import com.brins.dreamify.app.globalbusiness.MainHandler;

/**
 * @author lipeilin
 * @date 2022/10/24
 * @desc
 */
public class MainController implements LifecycleObserver {

    private static final String TAG = MainActivity.class.getName();
    private static final String LOG_TAG = "MainActivityLifecycle";
    private static final int PLAYBAR_HEIGHT = 0;
    private MainActivity mainActivity;
    public GlobalWorkHandler mWorkHandler = null;
    //public GlobalEventBusHandler mGlobalEventBusHandler;
    public MainHandler mHandler;
    private boolean currentFront;
    public boolean isFrist;

    private AdditionalContent mAdditionalContent;

    public MainController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public MainActivity getAttachActivity() {
        return mainActivity;
    }


    public MainHandler getMainHandler() {
        return mHandler;
    }

    public GlobalWorkHandler getGlobalWorkHandler() {
        return mWorkHandler;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected void onCreate() {
        DrLog.i(LOG_TAG, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart() {
        if (DrLog.DEBUG) DrLog.i(LOG_TAG, "onStart");
        if (currentFront) {
            return;
        }
        currentFront = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        if (DrLog.DEBUG) DrLog.i(LOG_TAG, "onStop");
        currentFront = false;
    }

    public void initAdditionalContent() {
        AdditionalContent additionalContent = new AdditionalContent(
            mainActivity);
        additionalContent.attachActivity(mainActivity, mainActivity.getAdditionalContainer());
        mAdditionalContent = additionalContent;
        mAdditionalContent.onSkinAllChanged();
    }
}
