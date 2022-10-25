/**
 * Copyright (C) 2004 KuGou-Inc.All Rights Reserved
 */

package com.brins.commom.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.brins.commom.utils.CollectionUtil;
import com.brins.commom.utils.KGAssert;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import com.kugou.page.core.KGFrameworkFragment;
import java.util.ArrayList;

/**
 * 抽象的皮肤Fragment
 * 
 * @author luo.qiang
 * @time Jun 20, 2013 4:42:51 PM
 */
public abstract class AbsSkinFragment extends KGFrameworkFragment implements ISkinUpdateManager {

    private ArrayList<ISkinViewUpdate> mSkinnableArray;
    /**
     * 皮肤Activity
     */
    private AbsSkinActivity mActivity;

    /**
     * 系统默认的LayoutInflater
     */
    private LayoutInflater mSystemInflater;

    /**
     * 辅助的开关用于判断当前皮肤功能是否启用了,默认是启用
     */
    protected boolean isChangedSkin = true;

    public AbsSkinFragment() {
        super();
        if (isChangedSkin) mSkinnableArray = new ArrayList<>(10);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (AbsSkinActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be AbsSkinActivity");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeAllSkinUpdate();
    }

    protected void removeViewFromSkinEngine(View view) {
        if (mActivity == null)
            return;

        mActivity.removeViewFromSkinEngine(view);
    }

    @SuppressLint("RestrictedApi") @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {

        if (!isChangedSkin) {
            if (mSystemInflater == null) {
                mSystemInflater = mActivity.getLayoutInflater().cloneInContext(mActivity);
                mSystemInflater.setFactory(mActivity);
            }
            return mSystemInflater;
        } else {
            return mActivity.getLayoutInflater();
        }
    }

    /**
     * 当前fragment是否使用了皮肤功能
     * 
     * @return true启用了，false没有启用
     */
    public boolean isChangedSkin() {
        return isChangedSkin;
    }

    // --------------子类需要实现修改皮肤的方法
    /**
     * 全部换肤，默认是finish
     */
    protected void onSkinAllChanged() {
        applySkinUpdate();
    }

    /**
     * 换背景，子类实现
     */
    @Deprecated
    protected void onSkinBgChanged() {

    }

    /**
     * 换颜色，子类实现
     */
    @Deprecated
    protected void onSkinColorChanged() {
    }

    /**
     * 换主界面透明度，子类实现
     */
    @Deprecated
    protected void onNaviBGAlphaChanged() {
    }

    @Override
    public boolean addSkinUpdate(ISkinViewUpdate... updates) {
        KGAssert.assertMainThread();
        int size = updates == null ? 0 : updates.length;
        int addedCount = 0;

        for (int i = 0; i < size; i++) {
            ISkinViewUpdate update = updates[i];
            if (update == null || mSkinnableArray.contains(update)) continue;
            if (mSkinnableArray.add(updates[i])) addedCount += 1;
        }

        return addedCount > 0;
    }

    @Override
    public boolean removeSkinUpdate(ISkinViewUpdate update) {
        KGAssert.assertMainThread();
        return !CollectionUtil.isEmpty(mSkinnableArray) && mSkinnableArray.remove(update);
    }

    private void applySkinUpdate() {
        KGAssert.assertMainThread();
        if (CollectionUtil.isEmpty(mSkinnableArray)) return;

        for (ISkinViewUpdate update : mSkinnableArray) update.updateSkin();
    }

    public void applySkinChange() {
        applySkinUpdate();
    }

    private void removeAllSkinUpdate() {
        KGAssert.assertMainThread();
        if (!CollectionUtil.isEmpty(mSkinnableArray)) mSkinnableArray.clear();
    }
}
