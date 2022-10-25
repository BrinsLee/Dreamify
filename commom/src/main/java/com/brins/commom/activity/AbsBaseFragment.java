/**
 * Copyright (C) 2004 KuGou-Inc.All Rights Reserved
 */

package com.brins.commom.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import com.brins.commom.base.AbsFrameworkFragment;
import com.brins.commom.base.lifecycle.KGLifeCycleObserver;
import com.brins.commom.base.lifecycle.KGLifeCycleOwner;
import com.brins.commom.base.pagenorm.IEasyFunc;
import com.brins.commom.base.pagenorm.IKugouPageBaseFeature;
import com.brins.commom.base.pagenorm.impl.EasyFuncImpl;
import com.brins.commom.base.pagenorm.impl.KugouPageBaseFeatureImpl;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.toast.LoadingTypes;
import com.brins.commom.utils.log.DrLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * 抽象基础Fragment
 *
 * @author luo.qiang
 * @time Jun 21, 2013 6:05:50 PM
 */
public class AbsBaseFragment extends AbsFrameworkFragment
    implements IKugouPageBaseFeature, IEasyFunc,
         IEasyFunc.ProgressDialoginterface {

    private static final String TAG = AbsBaseFragment.class.getName();

    private KugouPageBaseFeatureImpl mBaseFeature = new KugouPageBaseFeatureImpl();

    private EasyFuncImpl mEasyFunc = new EasyFuncImpl();

    private AbsBaseActivity mActivity;

    private View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBaseFeature.onAttach(activity, this);
        mEasyFunc.onAttach(activity);
        mActivity = (AbsBaseActivity) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBaseFeature.onViewCreated(view, savedInstanceState);
        mView = view;
        if (DrLog.DEBUG) DrLog.d("Fragment_Name",getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBaseFeature.onDestroyView();
        // 停止后台线程
        if (mWorker != null)
            mWorker.quit();

        if (mDownloadWorker != null)
            mDownloadWorker.quit();

        if (DrLog.DEBUG) DrLog.d("TEST", this.getClass().getName() + ":onDestroyView");
    }

    @Override
    public Context getApplicationContext() {
        return mBaseFeature.getApplicationContext();
    }

    @Override
    public final View findViewById(int id) {
        return mBaseFeature.findViewById(id);
    }

    @Override
    public void hideSoftInput() {
        mBaseFeature.hideSoftInput();
    }

    @Override
    public void showSoftInput() {
        mBaseFeature.showSoftInput();
    }

    @Override
    public void runOnUITread(Runnable action) {
        mBaseFeature.runOnUITread(action);
    }

    @Override
    public void setFixInputManagerLeakEnable(boolean enable) {
        mBaseFeature.setFixInputManagerLeakEnable(enable);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return getLayoutInflater(null);
    }


    // Implements for IEasyFunc

    @Override
    public void showToastLong(int strResId) {
        mEasyFunc.showToastLong(strResId);
    }

    @Override
    public void showToastLong(String msg) {
        mEasyFunc.showToastLong(msg);
    }

    @Override
    public void showToastLong(String msg, int gravity, int xOffset, int yOffset, int duration) {
        mEasyFunc.showToastLong(msg, gravity, xOffset, yOffset, duration);
    }

    @Override
    public void showSuccessedToast(String msg) {
        mEasyFunc.showSuccessedToast(msg);
    }

    @Override
    public void showFailToast(String msg) {
        mEasyFunc.showFailToast(msg);
    }

    @Override
    public void showToastWithIcon(int drawableId, int msgId, int duration) {
        mEasyFunc.showToastWithIcon(drawableId, msgId, duration);
    }

    @Override
    public void showToastWithIcon(Drawable drawable, String msg, int duration) {
        mEasyFunc.showToastWithIcon(drawable, msg, duration);
    }

    @Override
    public void showToast(int strResId) {
        mEasyFunc.showToast(strResId);
    }

    @Override
    public void showToast(Drawable drawable, int strResId) {
        mEasyFunc.showToast(drawable, strResId);
    }

    @Override
    public void showToast(Drawable drawable, String msg) {
        mEasyFunc.showToast(drawable, msg);
    }

    @Override
    public void showToast(CharSequence str) {
        mEasyFunc.showToast(str);
    }

    @Override
    public void showToastLeft(CharSequence str) {
        mEasyFunc.showToastLeft(str);
    }

    public void showToast(Drawable drawable,CharSequence str) {
        mEasyFunc.showToast(drawable, str);
    }

    @Override
    public void showToast(int strResId, int gravity, int xOffset, int yOffset) {
        mEasyFunc.showToast(strResId, gravity, xOffset, yOffset);
    }

    @Override
    public void showToast(CharSequence str, int gravity, int xOffset, int yOffset) {
        mEasyFunc.showToast(str, gravity, xOffset, yOffset);
    }

    @Override
    public void showToastCenter(int strResId) {
        mEasyFunc.showToastCenter(strResId);
    }

    @Override
    public void showToastCenter(CharSequence str) {
        mEasyFunc.showToastCenter(str);
    }

    @Override
    public void showProgressDialog() {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING);
    }

    @Override
    public void showProgressDialog(boolean canCancel) {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING, canCancel);
    }

    @Override
    public void showProgressDialog(DialogInterface.OnDismissListener listener) {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING, listener);
    }

    @Override
    public void showProgressDialog(boolean canCancel, String text) {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING, canCancel, text);
    }

    @Override
    public void showProgressDialog(boolean canCancel, boolean canCancelOutside) {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING, canCancel, canCancelOutside);
    }
    @Override
    public void showProgressDialog(String text,boolean canCancel, boolean canCancelOutside) {
        mEasyFunc.showProgressDialog(0, LoadingTypes.FLOATING, canCancel, canCancelOutside,text);
    }
    @Override
    public void dismissProgressDialog() {
        mEasyFunc.dismissProgressDialog();
    }

    //解决ktv页面重新关不掉的问题
    public void dismissProgressDialogNew() {
        mEasyFunc.dismissProgressDialog();
    }

    @Override
    public void showProgressDialog(int loadingType) {
        mEasyFunc.showProgressDialog(0, loadingType);
    }

    @Override
    public void showProgressDialog(int loadingType, boolean canCancel) {
        mEasyFunc.showProgressDialog(0, loadingType, canCancel);
    }

    @Override
    public void showProgressDialog(int loadingType, DialogInterface.OnDismissListener listener) {
        mEasyFunc.showProgressDialog(0, loadingType, listener);
    }

    @Override
    public void showProgressDialog(int loadingType, boolean canCancel, String text) {
        mEasyFunc.showProgressDialog(0, loadingType, canCancel, text);
    }

    @Override
    public void showProgressDialog(int loadingType, boolean canCancel, String text, String secondStr) {
        mEasyFunc.showProgressDialog(0, loadingType, canCancel, text, secondStr);
    }

    @Override
    public void showProgressDialog(int loadingType, boolean canCancel, boolean canCancelOutside) {
        mEasyFunc.showProgressDialog(0, loadingType, canCancel, canCancelOutside);
    }

    @Override
    public boolean isProgressDialogShowing() {
        return mEasyFunc.isProgressDialogShowing();
    }




    @Override
    public View getView() {
        if (super.getView() == null) {
            return mView;
        }
        return super.getView();
    }

    @Override
    public FragmentActivity getActivity() {
        return mActivity == null ? super.getActivity() : mActivity;
    }

    @Override
    public Resources getResources() {
        return mActivity.getResources();
    }

    public AbsBaseActivity getContext() {
        return mActivity;
    }

    public void sendBroadcast(Intent intent) {
        BroadcastUtil.sendBroadcast(intent);
    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        BroadcastUtil.registerMultiReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        BroadcastUtil.unregisterMultiReceiver(receiver);
    }

    protected void notifyDataSetChanged(BaseAdapter adapter) {
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * findViewById
     * @param resId id of the view
     * */
    protected <T extends View> T $(int resId) {
        if(mView!=null) {
            return (T) mView.findViewById(resId);
        }else{
            return null;
        }
    }
    /**
     * set onCLick listener to these views
     * @param listener
     * @param views views to be setted
     * */
    protected void $K(View.OnClickListener listener, View... views) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setOnClickListener(listener);
                }
            }
        }
    }


    protected HandlerThread mWorker;

    public Looper getWorkLooper() {
        if (mWorker == null || !mWorker.isAlive()) {
            mWorker = new HandlerThread(this.getClass().getName(), getWorkLooperThreadPriority());
            mWorker.start();
        }
        return mWorker.getLooper();
    }

    protected int getWorkLooperThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }













/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////                                    ////////////////////////
//////////////////// 下面这坨代码啊，你就不能封成个Loader吗?  ////////////////////////
////////////////////                                    /////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////



    // start--------------歌曲下载控制代码----------------
    /**
     * 音质下载选择
     */
    public static final int REQUESTCODE_DOWNLOAD = 0x100;

    /**
     * 待下载的歌曲集合
     */
   /* private static KGSong[] mDownloadSongs;

    private DownloadTraceModel mDownloadTraceModel;

    private static SongQuality mSelectQuality;

    private static String mSaveDir;

    private static List<KGMusic> mNoNeedFixSongs;*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (resultCode == Activity.RESULT_OK) { // 点击下载按钮
            if (requestCode == REQUESTCODE_DOWNLOAD) {
                onMusicQualitySelected(data);
            }
        } else if (resultCode == CloudLoginFragment.RESULTCODE_SUCCESS) {
            // change to relative path 根本没有使用到该变量
            // String saveDir = GlobalEnv.CACHE_DEFAULT_FOLDER;
            String saveDir = KGSDcardMgrDelegate
                    .sGetCurrentSDcardPath(GlobalRelativePath.CACHE_DEFAULT_FOLDER);
            if (mDownloadSongs != null) {
                if (mDownloadSongs.length > 1) {
                    downloadMusicWithSelector(mDownloadSongs, saveDir,mDownloadTraceModel);
                } else {
                    downloadMusicWithSelector(mDownloadSongs[0], saveDir,mDownloadTraceModel);
                }
                mSelectQuality = null;
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 工作线程
     */
    private Handler workhandler;

    private Handler getWorkHandler() {
        if (workhandler == null) {
            workhandler = new Handler(getDownloadLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    // super.handleMessage(msg);

                }
            };// end handler
        }// end if
        return workhandler;
    }

    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
        }

        ;
    };


    private HandlerThread mDownloadWorker;

    private Looper getDownloadLooper() {
        if (mDownloadWorker == null) {
            mDownloadWorker = new HandlerThread(TAG, getWorkLooperThreadPriority());
            mDownloadWorker.start();
        }
        return mDownloadWorker.getLooper();
    }

    public boolean isExistUnstage() {
        return existUnstage;
    }

    public void setExistUnstage(boolean existUnstage) {
        this.existUnstage = existUnstage;
    }

    private boolean existUnstage = false;

    public void cancleHandler(Handler handler) {
        if (handler == null)
            return;

        handler.removeCallbacksAndMessages(null);
        if(handler.getLooper() != null && handler.getLooper() != Looper.getMainLooper()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                handler.getLooper().quitSafely();
            } else {
                try {
                    handler.getLooper().quit();
                } catch (IllegalStateException ignore) {}
            }
        }
    }


    public String getCloudIdentifySourceName(){
        return "";
    }

    public long getCloudPlaylistId(){
        return -1;
    }
    public void setCloudPlaylistId(long id){

    }

    public void safeRegistEventBus(){
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {

        }
        registerKGLifeCycleObserver(new KGLifeCycleObserver() {
            @Override
            public void onStateChanged(KGLifeCycleOwner owner, int event) {
                switch (event){
                    case Event.ON_DESTROY:
                        EventBus.getDefault().unregister(AbsBaseFragment.this);
                        break;
                }
            }
        });
    }

    public void safeRegistBroadcast(BroadcastReceiver broadcastReceiver, IntentFilter filter){
        BroadcastUtil.registerReceiver(broadcastReceiver, filter);
        registerKGLifeCycleObserver(new KGLifeCycleObserver() {
            @Override
            public void onStateChanged(KGLifeCycleOwner owner, int event) {
                switch (event){
                    case Event.ON_DESTROY:
                        BroadcastUtil.unregisterMultiReceiver(broadcastReceiver);
                        break;
                }
            }
        });
    }

    @Override public void removeLifeCycleObserver(KGLifeCycleObserver observer) {

    }
}
