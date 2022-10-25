
package com.brins.commom.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.brins.commom.broadcast.BroadcastUtil;

public abstract class AbstractDelegate {

    protected final String TAG=this.getClass().getSimpleName();

    protected DelegatePage mPage;

    protected DelegateFragment mFragment;

    protected DelegateActivity mActivity;

    public AbstractDelegate(DelegatePage page) {
        mPage = page;
    }

    public AbstractDelegate(DelegateFragment fragment) {
        mFragment = fragment;
    }

    public AbstractDelegate(DelegateActivity activity) {
        mActivity = activity;
    }

    public abstract void init();

    public void showToast(String msg) {
        if (mFragment != null) {
            mFragment.showToast(msg);
        } else if (mActivity != null) {
            mActivity.showToast(msg);
        } else if (mPage != null) {
            mPage.showToast(msg);
        }
    }

    public void showToast(int strResId) {
        if (mFragment != null) {
            mFragment.showToast(strResId);
        } else if (mActivity != null) {
            mActivity.showToast(strResId);
        } else if (mPage != null) {
            mPage.showToast(strResId);
        }
    }

    public Bundle getArguments() {
        if (mFragment != null) {
            return mFragment.getArguments();
        } else if (mPage != null) {
            return mPage.getArguments();
        }
        return null;
    }

    public Intent getIntent() {
        if (mActivity != null) {
            mActivity.getIntent();
        }
        return null;
    }

    public void sendBroadcast(Intent intent) {
        if (mFragment != null || mActivity != null || mPage != null) {
            BroadcastUtil.sendBroadcast(intent);
        }
    }

    public void startActivity(Intent intent) {
        if (mFragment != null) {
            mFragment.startActivity(intent);
        } else if (mActivity != null) {
            mActivity.startActivity(intent);
        } else if (mPage != null) {
            mPage.startActivity(intent);
        }
    }

    public View findViewById(int id) {
        if (mFragment != null && mFragment.getView() != null) {
            return mFragment.getView().findViewById(id);
        } else if (mActivity != null) {
            return mActivity.findViewById(id);
        } else if (mPage != null) {
            return mPage.findViewById(id);
        }
        return null;
    }

    public Activity getActivity() {
        if (mFragment != null && mFragment.getContext() != null) {
            return mFragment.getContext();
        } else if (mActivity != null) {
            return mActivity;
        } else if (mPage != null) {
            return mPage.getActivity();
        }
        return null;
    }

    public Context getContext() {
        if (mFragment != null && mFragment.getContext() != null) {
            return mFragment.getContext();
        } else if (mActivity != null) {
            return (Context) mActivity;
        } else if (mPage != null) {
            return mPage.getContext();
        }
        return null;
    }

    public Context getApplicationContext() {
        if (mFragment != null && mFragment.getContext() != null) {
            return mFragment.getContext().getApplicationContext();
        } else if (mActivity != null) {
            mActivity.getApplicationContext();
        } else if (mPage != null) {
            return mPage.getApplicationContext();
        }
        return null;
    }

    public void finish() {
        if (mFragment != null) {
            mFragment.finish();
        } else if (mActivity != null) {
            mActivity.finish();
        } else if (mPage != null) {
            mPage.finish();
        }
    }

    /**
     * called when activity or fragment visiable changed
     * */
    public void setUserVisibleHint(boolean isVisibleToUser){

    }
    /**
     * this method is called when fragment is loaded ok,not implement for activity yet,
     * for now ,used for delegate to notify onFragmentFirstStart
     * */
    public void onViewShowFinish(){

    }
}
