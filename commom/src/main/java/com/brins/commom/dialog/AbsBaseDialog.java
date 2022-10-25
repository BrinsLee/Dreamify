/**
 * Copyright (C) 2004 KuGou-Inc.All Rights Reserved
 */

package com.brins.commom.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.brins.commom.R;
import com.brins.commom.activity.AbsBaseFragment;
import com.brins.commom.broadcast.BroadcastUtil;

/**
 * 抽象的基础弹框（Fragment形式）
 * 
 * @author luo.qiang
 * @time Jun 26, 2013 11:21:18 AM
 */
public abstract class AbsBaseDialog extends AbsBaseFragment implements
        DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";

    private static final String SAVED_THEME = "android:theme";

    private static final String SAVED_CANCELABLE = "android:cancelable";

    private static final String SAVED_BACK_STACK_ID = "android:backStackId";

    private static final int DEFAULT_THEME = R.style.Kugou_Theme_Dialog;

    int mTheme = DEFAULT_THEME;

    boolean mCancelable = true;

    int mBackStackId = -1;

    Dialog mDialog;

    boolean mViewDestroyed;

    boolean mDismissed;

    boolean mShownByMe;

    public void show(FragmentManager manager, String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    public void setTheme(int theme) {
        mTheme = theme;
    }

    public int getTheme() {
        return mTheme;
    }

    public void dismiss() {
        dismissInternal(true);
    }

    void dismissInternal(boolean allowStateLoss) {
        if (mDismissed) {
            return;
        }
        mDismissed = true;
        mShownByMe = false;
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mViewDestroyed = true;
        if (mBackStackId >= 0) {
            getFragmentManager().popBackStack(mBackStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mBackStackId = -1;
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        if (mDialog != null)
            mDialog.setCancelable(cancelable);
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!mShownByMe) {
            // If not explicitly shown through our API, take this as an
            // indication that the dialog is no longer dismissed.
            mDismissed = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!mShownByMe && !mDismissed) {
            // The fragment was not shown by a direct call here, it is not
            // dismissed, and now it is being detached... well, okay, thou
            // art now dismissed. Have fun.
            mDismissed = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTheme = savedInstanceState.getInt(SAVED_THEME, DEFAULT_THEME);
            mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
            mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }
    }

    /** @hide */
    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {

        mDialog = onCreateDialog();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.getLayoutInflater(savedInstanceState);
    }

    Dialog onCreateDialog() {
        return new Dialog(getActivity(), mTheme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "AbsBaseDialog can not be attached to a container view");
            }
            mDialog.setContentView(view);
        }
        mDialog.setOwnerActivity(getActivity());
        mDialog.setCancelable(mCancelable);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(this);
        mDialog.setOnDismissListener(this);
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
            if (dialogState != null) {
                mDialog.onRestoreInstanceState(dialogState);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null) {
            mViewDestroyed = false;
            mDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mDialog != null) {
            Bundle dialogState = mDialog.onSaveInstanceState();
            if (dialogState != null) {
                outState.putBundle(SAVED_DIALOG_STATE_TAG, dialogState);
            }
        }
        if (mTheme != DEFAULT_THEME) {
            outState.putInt(SAVED_THEME, mTheme);
        }
        if (!mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, mCancelable);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.hide();
        }
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null) {
            // Set removed here because this dismissal is just to hide
            // the dialog -- we don't want this to cause the fragment to
            // actually be removed.
            mViewDestroyed = true;
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 子类dialog可以重写该方法来监听cancel事件
     */
    @Override
    public void onCancel(DialogInterface dialog) {
    }

    /**
     * 子类dialog可以重写该方法来监听dismiss事件 ！！！但是需要调用super.onDismiss(dialog)
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!mViewDestroyed) {
            // Note: we need to use allowStateLoss, because the dialog
            // dispatches this asynchronously so we can receive the call
            // after the activity is paused. Worst case, when the user comes
            // back to the activity they see the dialog again.
            dismissInternal(true);
        }
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public void sendBroadcast(Intent intent) {
        if (!mViewDestroyed) {
            BroadcastUtil.sendBroadcast(intent);
        }
    }
}
