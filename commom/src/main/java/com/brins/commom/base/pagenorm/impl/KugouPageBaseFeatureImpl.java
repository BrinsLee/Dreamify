package com.brins.commom.base.pagenorm.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.base.pagenorm.IKugouPageBaseFeature;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import java.lang.reflect.Field;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by burone on 2017/7/28.
 */

public class KugouPageBaseFeatureImpl implements IKugouPageBaseFeature {

    private static final String TAG = "KugouPageBaseFeatureImpl";

    private AbsBaseActivity mActivity;

    private Fragment mFragment;

    private Context mApplicationContext;

    private boolean mFixIMLeakEnable = true;

    private UnsupportedOperationException newUnsupportedOperationException() {
        return new UnsupportedOperationException("No default implement on KugouPageBaseFeatureImpl");
    }

    public void onAttach(Activity activity, Fragment fragment) {
        try {
            mActivity = (AbsBaseActivity) activity;
            mFragment = fragment;
            mApplicationContext = mActivity.getApplicationContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be AbsBaseActivity");
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setClickable(true);
    }

    public void onDestroyView() {
        // 修复InputMethodManager内存泄漏问题，暂时只对魅族5x系统做处理
        if (mFixIMLeakEnable) {
            fixInputMethodManagerLeak();
        }
    }

    @Override
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    @Override
    public View findViewById(int id) {
        if (mFragment != null && mFragment.getView() != null) {
            return mFragment.getView().findViewById(id);
        }
        return null;
    }

    @Override
    public void hideSoftInput() {
        if (mActivity != null) {
            mActivity.hideSoftInput();
        }
    }

    @Override
    public void showSoftInput() {
        if (mActivity != null) {
            mActivity.showSoftInput();
        }
    }

    @Override
    public void runOnUITread(Runnable action) {
        if (mActivity != null) {
            mActivity.runOnUiThread(action);
        }
    }

    @Override
    public void setFixInputManagerLeakEnable(boolean enable) {
        mFixIMLeakEnable = enable;
    }


    /**
     * 不支持提供默认实现
     */
    @Override
    public LayoutInflater getLayoutInflater() {
        throw newUnsupportedOperationException();
    }


    /**
     * 修复魅族Flyme OS 5X系统因为InputMethodManager内存泄漏导致的输入法无法输入的问题
     */
    private void fixInputMethodManagerLeak(){
        if (mActivity == null){
            return;
        }
        if (SystemUtils.flymeVersionName == null){
            if (DrLog.DEBUG) DrLog.d(TAG,"SystemUtils.flymeVersionName is empty");
            Observable.just(null)
                    .map(new Func1<Object, String>() {
                        @Override
                        public String call(Object o) {
                            return SystemUtils.getFlymeUIVerionName();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            fixInputMethodManagerLeak(s);
                        }
                    });
        } else {
            if (DrLog.DEBUG) DrLog.d(TAG,"SystemUtils.flymeVersionName is getted");
            fixInputMethodManagerLeak(SystemUtils.flymeVersionName);
        }
    }

    private void fixInputMethodManagerLeak(String versionName){
        InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null){
            return;
        }
        String flymeName = "Flyme OS 5";
        String flymeName2 = "Flyme 6";
        String flymeName3 = "Flyme 5";
        if (versionName != null && (versionName.contains(flymeName) || versionName.contains(flymeName2) || versionName.contains(flymeName3))){
            if (DrLog.DEBUG) DrLog.d(TAG,"Flyme OS 5X version try to fix the InputMethodManagerLeak");
            try {
                Object obj = null;
                Field mServedView = manager.getClass().getDeclaredField("mServedView");
                Field mNextServedView = manager.getClass().getDeclaredField("mNextServedView");

                if (!mServedView.isAccessible()) {
                    mServedView.setAccessible(true);
                }
                obj = mServedView.get(manager);
                if (obj != null){
                    mServedView.set(manager, null);
                    if (DrLog.DEBUG) DrLog.d(TAG,"Flyme OS 5X version set mServedView null");
                }
                if (!mNextServedView.isAccessible()) {
                    mNextServedView.setAccessible(true);
                }
                obj = mNextServedView.get(manager);
                if (obj != null){
                    mNextServedView.set(manager, null);
                    if (DrLog.DEBUG) DrLog.d(TAG,"Flyme OS 5X version set mNextServedView null");
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void fixAllInputMethodManagerLeak() {
        if (mActivity == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null) {
            return;
        }
        if (DrLog.DEBUG) DrLog.d(TAG, "OS try to fix the InputMethodManagerLeak");
        String[] arr = new String[]{"mServedView", "mNextServedView"};
        Object obj = null;
        Field field = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                field = manager.getClass().getDeclaredField(param);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                obj = field.get(manager);
                if (obj != null && obj instanceof View) {
                    View targetView = (View) obj;
                    boolean canNull = targetView.getContext() == mActivity;
                    if (canNull) {
                        field.set(manager, null);
                    } else {
                        if (DrLog.DEBUG) {
                            DrLog.e(TAG, "fixInputMethodManagerLeak break");
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
