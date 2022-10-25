package com.brins.commom.page.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.brins.commom.base.BaseMvpViewLifecycleDelegate;
import com.brins.commom.exit.ActivityHolder;
import com.brins.commom.utils.xscreen.ScreenHandler;
import com.kugou.page.framework.KGFragmentActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc 可以获取旗下fragments的activity
 */
public abstract class StateFragmentActivity extends KGFragmentActivity {

    private ArrayList<WeakReference<Fragment>> fragmentList = new ArrayList<WeakReference<Fragment>>();
    private boolean appExiting = false;
    protected Bundle savedInstanceState = null;
    protected boolean restoreCheckHasPermission = true;
    private static boolean hadCheckedBasicPermission = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenHandler.setHuaweiWindowInCutout(getWindow());
        super.onCreate(savedInstanceState);
        ActivityHolder.getInstance().add(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseMvpViewLifecycleDelegate.onDestroy(getWindow().getDecorView());
        ActivityHolder.getInstance().remove(this);
    }

    public void notifyAppExiting() {
        appExiting = true;
    }

    public boolean isAppExiting() {
        return appExiting;
    }

    public Bundle getSavedInstanceState(){
        return savedInstanceState;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragmentList.add(new WeakReference<Fragment>(fragment));
        clearNullFragments();
    }

    void clearNullFragments() {
        if (fragmentList.size() > 10) {
            ArrayList<WeakReference<Fragment>> dirty = new ArrayList<WeakReference<Fragment>>();

            for (WeakReference<Fragment> fragRef : fragmentList) {
                if (fragRef.get() == null) {
                    dirty.add(fragRef);
                }
            }

            for (WeakReference<Fragment> frag : dirty) {
                fragmentList.remove(frag);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        BaseMvpViewLifecycleDelegate.onStart(getWindow().getDecorView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseMvpViewLifecycleDelegate.onResume(getWindow().getDecorView());
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseMvpViewLifecycleDelegate.onStop(getWindow().getDecorView());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseMvpViewLifecycleDelegate.onPause(getWindow().getDecorView());
    }

    /**
     * 获得activity中所有的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getAllFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                ret.add(f);
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isAdded的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getAddedFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isAdded()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isDetached的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getDetachedFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isDetached()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isHidden的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getHiddenFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isHidden()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isInLayout的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getInLayoutFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isInLayout()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isRemoving的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getRemovingFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isRemoving()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isResumed的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getResumedFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isResumed()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    /**
     * 获得activity中状态为isVisible的fragments
     *
     * @return fragments集合
     */
    public ArrayList<Fragment> getVisibleFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragmentList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    protected boolean enablePermissionCheck() {
        return true;
    }

}
