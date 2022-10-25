package com.brins.commom.base.pagenorm.impl;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.Toast;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.base.pagenorm.IEasyFunc;

/**
 * Created by burone on 2017/7/28.
 */

public class EasyFuncImpl implements IEasyFunc, IEasyFunc.ProgressDialoginterface2 {

    private AbsBaseActivity mActivity;

    public void onAttach(Activity activity) {
        mActivity = (AbsBaseActivity) activity;
    }

    @Override
    public void showToastLong(int strResId) {
        mActivity.showToastLong(strResId);
    }

    @Override
    public void showToastLong(String msg) {
        mActivity.showToastLong(msg);
    }

    @Override
    public void showToastLong(String msg, int gravity, int xOffset, int yOffset, int duration) {
        mActivity.showToastLong(msg, gravity, xOffset, yOffset, duration);
    }

    @Override
    public void showSuccessedToast(String msg) {
        if (mActivity != null) {
            mActivity.showSuccessedToast(msg);
        }
    }

    @Override
    public void showFailToast(String msg) {
        if (mActivity != null) {
            mActivity.showFailToast(msg);
        }
    }

    @Override
    public void showToastWithIcon(int drawableId, int msgId, int duration) {
        mActivity.showToastWithIcon(drawableId, msgId, duration);
    }

    @Override
    public void showToastWithIcon(Drawable drawable, String msg, int duration) {
        mActivity.showToastWithIcon(drawable, msg, duration);
    }

    @Override
    public void showToast(int strResId) {
        mActivity.showToast(strResId);
    }

    @Override
    public void showToast(Drawable drawable, int strResId) {
        mActivity.showToastWithIcon(drawable, mActivity.getString(strResId), Toast.LENGTH_SHORT);
    }

    @Override
    public void showToast(Drawable drawable, String msg) {
        mActivity.showToastWithIcon(drawable, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showToast(CharSequence str) {
        if (mActivity != null) {
            mActivity.showToast(str);
        }
    }

    @Override
    public void showToastLeft(CharSequence str) {
        if (mActivity != null) {
            mActivity.showToast(null, str, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
    }

    public void showToast(Drawable drawable, CharSequence str) {
        if (mActivity != null) {
            mActivity.showToast(drawable, str);
        }
    }

    @Override
    public void showToast(int strResId, int gravity, int xOffset, int yOffset) {
        mActivity.showToast(strResId, gravity, xOffset, yOffset);
    }

    @Override
    public void showToast(CharSequence str, int gravity, int xOffset, int yOffset) {
        mActivity.showToast(str, gravity, xOffset, yOffset);
    }

    @Override
    public void showToastCenter(int strResId) {
        mActivity.showToast(strResId, Gravity.CENTER, 0, 0);
    }

    @Override
    public void showToastCenter(CharSequence str) {
        mActivity.showToast(str, Gravity.CENTER, 0, 0);
    }


    @Override
    public void showProgressDialog(int pageId, int loadingType) {
        if (mActivity != null)
            mActivity.showProgressDialog(pageId, loadingType);
    }

    @Override
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel) {
        if (mActivity != null)
            mActivity.showProgressDialog(pageId, loadingType, canCancel);
    }

    @Override
    public void showProgressDialog(int pageId, int loadingType, DialogInterface.OnDismissListener listener) {
        if (mActivity != null)
            mActivity.showProgressDialog(pageId, loadingType, listener);
    }

    @Override
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text) {
        if (mActivity != null)
            mActivity.showProgressDialog(pageId, loadingType, canCancel,text);
    }

    @Override
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, String text, String secondStr) {
        if (mActivity != null)
            mActivity.showProgressDialog(pageId, loadingType, canCancel,text, secondStr);
    }

    @Override
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, boolean canCancelOutside) {
        if (mActivity != null) {
            mActivity.showProgressDialog(pageId, loadingType, canCancel, canCancelOutside);
        }
    }
    @Override
    public void showProgressDialog(int pageId, int loadingType, boolean canCancel, boolean canCancelOutside,String text) {
        if (mActivity != null) {
            mActivity.showProgressDialog(pageId, loadingType, canCancel, canCancelOutside,text);
        }
    }
    @Override
    public void dismissProgressDialog() {
        if (mActivity != null)
            mActivity.dismissProgressDialog();
    }

    @Override
    public boolean isProgressDialogShowing() {
        if (mActivity != null)
            return mActivity.isProgressDialogShowing();
        return false;
    }

}
