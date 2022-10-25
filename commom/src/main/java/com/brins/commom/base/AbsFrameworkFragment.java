/**
 * Copyright (C) 2004 KuGou-Inc.All Rights Reserved
 */

package com.brins.commom.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.base.lifecycle.KGLifeCycleObserver;
import com.brins.commom.base.lifecycle.KGLifeCycleOwner;
import com.brins.commom.base.lifecycle.DRLifecycleRegistry;
import com.brins.commom.base.ui.IFramePage;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.statusbar.StatusBarCompat;
import com.brins.commom.utils.KGBitmapUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.kugou.common.base.ViewPager;
import com.kugou.common.base.uiframe.FragmentStackView;
import com.kugou.common.utils.RenderThreadUtil;
import com.kugou.page.face.BackgroundLayer;
import com.kugou.page.face.TitleBar;
import com.kugou.page.framework.KGFragmentActivity;
import com.kugou.page.framework.delegate.SaveInstanceDelegate;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 使用ViewPager控件作为UI框架界面的Fragment
 *
 * @author luo.qiang
 * @time Sep 26, 2013 4:59:14 PM
 */
public abstract class AbsFrameworkFragment extends AbsSkinFragment implements
    IFramePage, KGLifeCycleOwner {

    public static final String FLAG_NEW_INSTANCE = "flag_new_instance";

    private static final String TAG = "AbsFrameworkFragment";

    private static long DEFAULT_VALUE = -1L;

    private AbsFrameworkActivity mAbsFrameworkActivity;

    public boolean permitDataCollect=true;

    private long mOnResumeTime = DEFAULT_VALUE;

    private boolean hasPaused = false;

    protected boolean onFragmentSaving = false;

    private boolean onFragmentResumeNotifyed = false;

    public boolean isOnFragmentResumeNotifyed() {
        return onFragmentResumeNotifyed;
    }


    @NonNull
    private DRLifecycleRegistry drLifecycleRegistry;

    public AbsFrameworkFragment() {
        super();
        drLifecycleRegistry = new DRLifecycleRegistry();
    }

    int oldSize = 0;
    int oldWidth = 0;
    int oldHeight = 0;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!SystemUtils.isFoldMode()) {
            return;
        }
        try{
            int[] sizes = SystemUtils.getScreenSize(DRCommonApplication.getContext());
            if (oldSize == sizes[0] * sizes[1]) {
                if (oldWidth == sizes[0] && oldHeight == sizes[1]) {

                } else {
                    //折叠屏横竖屏切换
                    onMeasure(sizes);
                }
                return;
            }
            onMeasure(sizes);
            DrLog.d("siganid", "onConfigurationChanged hide menu");
            hideMenu(false);
        }catch (Exception e){
        }

    }

    private void onMeasure(int[] sizes) {
        oldWidth = sizes[0];
        oldHeight = sizes[1];
        oldSize = sizes[0]* sizes[1];
        /*for (FragmentStackView.MeasureCallBack measureCallBack : UpdateViewSizeModel.init(). callBackList) {
            measureCallBack.onMeasure(sizes[0], sizes[1]);
        }*/
    }

    protected static final String KEY_SOURCE_PATH = "KEY_SOURCE_PATH";

    public static boolean judgeFragmentAlive(AbsFrameworkFragment fragment) {
        return fragment != null && fragment.isAlive();
    }

    public void setActivity(KGFragmentActivity act){
        mAbsFrameworkActivity = (AbsFrameworkActivity) act;
    }

    @Override
    public FragmentActivity getActivity() {
        if(mAbsFrameworkActivity == null){
            return super.getActivity();
        }
        return mAbsFrameworkActivity;
    }

    /**
     * 增加一个通用方法，方便在common调用kugou的实现
     * @param activity
     * @param doWhat
     */
    public void doSomething(FragmentActivity activity, int doWhat) {

    }

    @Override
    public void onAttach(Activity activity) {
        log("onAttach,activity="+activity.getClass().getSimpleName());
        super.onAttach(activity);
        try {
            mAbsFrameworkActivity = (AbsFrameworkActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must be AbsFrameworkActivity");
        }
        drLifecycleRegistry.onAttach(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            view.setTag(0x4FFFFFFF, "fragment:" + getClass().getName());
        }
        Log.d(TAG, "onResume: " + view.getTag(0x4FFFFFFF));
        
        try {
            int[] sizes = SystemUtils.getScreenSize(DRCommonApplication.getContext());
            oldSize = sizes[0] * sizes[1];
        } catch (Exception e) {
        }

    }
    

    /**
     * 更新当前页面绑定的UI页栈信息
     */
/*    protected void updatePageStack() {
        View view = getView();
        if (view != null) {
            String stack = PageInfoUtil.buildCurrentFragmentStack(getClass(), view);
            view.setTag(PageInfoTag.TAG_PAGE_STACK, stack);
        }
    }*/


    @Override
    public void onDestroyView() {
        if(getActivity() instanceof AbsBaseActivity){
            AbsBaseActivity baseActivity = (AbsBaseActivity)getActivity();
        }
        super.onDestroyView();
    }

    /**
     * 是否显示播放bar条,默认显示bar条
     *
     * @return true显示，false不显示
     */
    public boolean hasPlayingBar() {
        return true;
    }

    public boolean enableBottomBg() {
        return true;
    }

    public boolean hasMainBottomView() {
        return false;
    }

    public boolean hasResume() {
        return mOnResumeTime != DEFAULT_VALUE || !hasPaused;
    }

    public boolean hasPaused() {
        return hasPaused;
    }

    /**
     * 在使用RenderThread做动画的时候, 是不是要启用waitForFragmentFirstStart
     */
    protected boolean disableWaitFirstStartInRTMode() {
        return false;
    }

    ArrayList<View> mIgnoredViews = new ArrayList<View>();

    public ArrayList<View> getIgnoredViews() {
        AbsFrameworkFragment topParentFragment = getTopParentFragment();
        if (topParentFragment == null) {
            return null;
        }
        return topParentFragment.mIgnoredViews;
    }

    /**
     * 添加一个view使得这个view区域不受框架滑动影响
     *
     * @param v
     */
    public void addIgnoredView(View v) {
        AbsFrameworkFragment topParentFragment = getTopParentFragment();
        if (topParentFragment != null) {
            ArrayList<View> mIgnoredViews = topParentFragment.mIgnoredViews;
            if (!mIgnoredViews.contains(v)) {
                mIgnoredViews.add(v);
            }
        }
    }

    /**
     * 清除所有不受框架滑动影响的view
     */
    public void clearIgnoredViews() {
        AbsFrameworkFragment topParentFragment = getTopParentFragment();
        if (topParentFragment != null) {
            topParentFragment.mIgnoredViews.clear();
        }
    }

    /**
     * 移除一个view使得这个view区域接受框架滑动影响
     *
     * @param v
     */
    public void removeIgnoredView(View v) {
        AbsFrameworkFragment topParentFragment = getTopParentFragment();
        if (topParentFragment != null) {
            topParentFragment.mIgnoredViews.remove(v);
        }
    }

    private final byte[] firstStartLock = new byte[0];

    /**
     * 在界面栈中，当前fragment第一次可见（在滑过来的动画停止之后） ！！！请最先super.onFragmentFirstStart();
     * 一般不重写该方法.
     */
    public void onFragmentFirstStart() {
        super.onFragmentFirstStart();
        synchronized (firstStartLock) {
            firstStartLock.notifyAll();
        }

        excuteMessageAfterFirstStart();
    }

    /**
     * 第一次刷新界面的线程等待onFragmentFirstStart回调后继续往下执行 ！！！该方法只能在非主线程执行.
     */
    public void waitForFragmentFirstStart() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("不能在主线程上执行");
        }
        if (disableWaitFirstStartInRTMode()
                && RenderThreadUtil.isRenderThreadEnable()) {
            return;
        }
        if (!isFragmentFirstStartInvoked()) {
            synchronized (firstStartLock) {
                while (!isFragmentFirstStartInvoked()) {
                    try {
                        firstStartLock.wait();
                    } catch (InterruptedException e) {
                        
                    }
                }
            }
        }
    }

    public int DELAY_SHOW_MENU = 1000;
    /**
     * 在界面栈中，当前fragment每次从不可见变成可见时的回调。
     */
    public void onFragmentResume() {
        super.onFragmentResume();
        hasPaused = false;
        onFragmentResumeNotifyed = true;
        mOnResumeTime = System.currentTimeMillis();
        if (DrLog.DEBUG) log("onFragmentResume");
        if(mAbsFrameworkActivity != null) {//针对子fragment 返回null
            String activityName = mAbsFrameworkActivity.getClass().getSimpleName();
            if (activityName.equals("MediaActivity")) { //只针对MediaActivity框架中使用的Fragment起作用
                onInitSoftInputMode();
            }
        }

        drLifecycleRegistry.onFragmentResume();//通知生命周期监听
        if (this == getFrameworkFragment()) {
            updateSystemStatusBar();
        }
    }


    @Override
    public boolean hasMenu() {
        return false;
    }


    /**
     * 在界面栈中，当前fragment每次从不可见变成可见时的回调。跟onFragmentResume 相似，在onFragmentPause 之后调用
     */
    public void onFragmentResumeAfterPause() {
        super.onFragmentResumeAfterPause();
    }


    /**
     * 在界面栈中，当前fragment每次从可见变成不可见时的回调。
     * 如果如果已经是不可见状态（在第二个或更底下的界面栈位置时），它上面又有新的fragment被打开，这个方法是不会回调的。 。
     */
    public void onFragmentPause() {
        super.onFragmentPause();
        hasPaused = true;
        boolean tempResumeNotifyd = onFragmentResumeNotifyed;
        onFragmentResumeNotifyed = false;
        if (DrLog.DEBUG) log("onFragmentPause");
        if (mOnResumeTime != DEFAULT_VALUE && getActivity() != null) {
            long showingTime = System.currentTimeMillis() - mOnResumeTime;
            mOnResumeTime = DEFAULT_VALUE;
        }
          if (tempResumeNotifyd) {
            //只有在resume之后，才回调这个状态
            drLifecycleRegistry.onFragmentPause();
        }
    }

    /**
     * 唤醒onFragmentResume。该方法防治重复调用
     */
    public void notifyOnfragmentResume() {
        if (!onFragmentResumeNotifyed) {
            onFragmentResume();
        }
    }

    public void notifyOnFragmentPause() {
        if (onFragmentResumeNotifyed) {
            onFragmentPause();
        }
    }

    /**
     * 屏幕停止滑动
     */
    public static final int STATE_IDLE = ViewPager.SCROLL_STATE_IDLE;

    /**
     * 屏幕被用户抓住滑动
     */
    public static final int STATE_DRAGGING = ViewPager.SCROLL_STATE_DRAGGING;

    /**
     * 屏幕被用户松手后的归位滑动
     */
    public static final int STATE_SETTLING = ViewPager.SCROLL_STATE_SETTLING;

    /**
     * 屏幕滑动状态发生改变的回调
     *
     * @param state {@link #STATE_IDLE} {@link #STATE_DRAGGING}
     *            {@link #STATE_SETTLING}
     */
    public void onScreenStateChanged(int state) {
        if (DrLog.isDebug()) {
            String stateName = null;
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    stateName = "idle";
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    stateName = "dragging";
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    stateName = "settling";
                    break;
            }
            log("onScreenStateChanged: " + stateName);
        }
    }

    private boolean invokeFragmentFirstStartBySelf = false;

    public void setInvokeFragmentFirstStartBySelf() {
        invokeFragmentFirstStartBySelf = true;
    }

    public boolean isInvokeFragmentFirstStartBySelf() {
        return invokeFragmentFirstStartBySelf;
    }

    @Override
    public void onResume() {
        log("onResume");
        super.onResume();
        if (invokeFragmentFirstStartBySelf) {
            invokeFragmentFirstStartBySelf = false;
            onFragmentFirstStart();
        }
    }

    protected void startFragmentOnUIThread(final Class<? extends Fragment> cls, Bundle args,
                                           final boolean anim, final boolean replaceMode, boolean clearTop) {
        startFragmentOnUIThread(null, cls, args, anim, replaceMode, clearTop);
    }

    protected void startFragmentOnUIThread(AbsFrameworkFragment target, final Class<? extends Fragment> cls, Bundle args,
                                           final boolean anim, final boolean replaceMode, boolean clearTop) {
        boolean isContinuous = args!= null && args.getBoolean("iscontious");
        startFragmentOnUIThread(target,cls,args,anim,replaceMode,clearTop,isContinuous);
    }

    protected void startFragmentOnUIThread(AbsFrameworkFragment target, final Class<? extends Fragment> cls, Bundle args,
                                           final boolean anim, final boolean replaceMode, boolean clearTop,boolean isContinuousOpen) {
        startFragmentOnUIThread(target,cls,args,anim,replaceMode,clearTop,isContinuousOpen,false);
    }

    /** @hide */
    protected void startFragmentOnUIThread(AbsFrameworkFragment target, final Class<? extends Fragment> cls, Bundle args,
            final boolean anim, final boolean replaceMode, boolean clearTop,boolean isContinuousOpen,boolean isQuickAnim) {
        /*ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();

        if (delegate != null) {
            if (args == null) {
                args = new Bundle();
            }

            String path = mPagePath;
            int subPage = getSubPage();
            if (subPage >= 0) {
                if (TextUtils.isEmpty(path)) {
                    path = "" + PagePath.PAGE_UNDEFINED + PagePath.PATH_SUB_SEP + subPage;
                } else {
                    path = path + PagePath.PATH_SUB_SEP + subPage;
                }
            }
            args.putString(KEY_SOURCE_PATH, path);
            delegate.startFragmentOnMainThread(target != null ? target : getTopParentFragment(), cls, args, anim, replaceMode, clearTop,isContinuousOpen,isQuickAnim);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                DialogManager.getSingleton().hideAllDialogs();
            }
        }*/
    }

    /**
     * 打开一个的新的界面
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     */
    @Override
    public void  startFragment(Class<? extends Fragment> cls, Bundle args) {
        startFragmentOnUIThread(cls, args, true, false, false);
    }

    /**
     * 打开一个的新的界面
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     * @param anim - 是否动画
     */
    @Override
    public void startFragment(Class<? extends Fragment> cls, Bundle args, boolean anim) {
        startFragmentOnUIThread(cls, args, anim, false, false);
    }

    /**
     * 打开一个的新的界面
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     * @param anim - 是否动画
     * @param isQuickAnim - 加速动画
     */
    public void startFragment(Class<? extends Fragment> cls, Bundle args, boolean anim,boolean isQuickAnim) {
        startFragmentOnUIThread(null, cls, args, anim, false, false, false, isQuickAnim);
    }

    /**
     * 打开一个的新的界面，使用clearTop方式
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     */
    public void startFragmentFromRecent(Class<? extends Fragment> cls, Bundle args) {
        startFragmentOnUIThread(cls, args, false, false, true);
    }

    /**
     * 打开一个的新的界面，使用clearTop方式
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     * @param anim - 是否动画
     */
    public void startFragmentFromRecent(Class<? extends Fragment> cls, Bundle args, boolean anim) {
        startFragmentOnUIThread(cls, args, anim, false, true);
    }

    /**
     * 替换当前界面来打开一个界面
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     */
    public void replaceFragment(Class<? extends Fragment> cls, Bundle args) {
        startFragmentOnUIThread(cls, args, true, true, false);
    }

    /**
     * 替换当前界面来打开一个界面
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     * @param anim - 是否有动画
     */
    public void replaceFragment(Class<? extends Fragment> cls, Bundle args,boolean anim) {
        startFragmentOnUIThread(cls, args, anim, true, false);
    }

    public void startFragmentWithTarget(AbsFrameworkFragment target, Class<? extends Fragment> cls, Bundle args) {
        startFragmentOnUIThread(target, cls, args, false, false, false);
    }

    public void startFragmentWithTarget(AbsFrameworkFragment target, Class<? extends Fragment> cls, Bundle args, boolean isReplaceMode) {
        startFragmentOnUIThread(target, cls, args, false, isReplaceMode, false);
    }

    public void startFragmentWithTarget(AbsFrameworkFragment target, Class<? extends Fragment> cls, Bundle args, boolean isReplaceMode,boolean anim) {
        startFragmentOnUIThread(target, cls, args, anim, isReplaceMode, false);
    }


    /**
     * 连续打开多个fragemnet
     *
     * @param cls - 界面fragment的类名
     * @param args - 界面fragment的arguments参数
     */
    public void startFragmentWithContinuous(Class<? extends Fragment> cls, Bundle args) {
        startFragmentOnUIThread(null,cls, args, true, false, false,true);
    }

    public void startFragmentWithContinuous(Class<? extends Fragment> cls, Bundle args, boolean anim) {
        startFragmentOnUIThread(null,cls, args, anim, false, false,true);
    }

    /**
     * 获取当前正在显示的Fragment
     *
     * @return - 当前显示的Fragment
     */
    public AbsFrameworkFragment getCurrentFragment() {
        if (mAbsFrameworkActivity == null) {
            return null;
        }
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.getCurrentFragment();
        }
        return null;
    }

    public AbsFrameworkFragment getLastFragment() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.getLastFragment();
        }
        return null;
    }

    /**
     * 获取当前主页容器的Fragment
     * @return - 主页容器的Fragment
     */
    public MainFragmentContainer getMainFragmentContainer() {
        if (mAbsFrameworkActivity == null)
            mAbsFrameworkActivity = (AbsFrameworkActivity) getActivity();
        if (mAbsFrameworkActivity != null) {
            ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
            if (delegate != null) {
                return delegate.getMainFragmentContainer();
            }
        }
        return null;
    }

    /**
     * 获取首页当前选中的tab fragment
     * @return
     */
    public AbsFrameworkFragment getMainCurrentFragment() {
        MainFragmentContainer mainContainer = getMainFragmentContainer();
        if(mainContainer != null) {
            return mainContainer.getSelectedFragment();
        }
        return null;
    }
    /**
     * 获取首页当前听首页 fragment
     * @return
     */
    public AbsFrameworkFragment getMainTingFragment() {
        MainFragmentContainer mainContainer = getMainFragmentContainer();
        if (mainContainer != null) {
            return mainContainer.getFragmentTing();
        }
        return null;
    }


    /**
     * 获取首页当前我首页 fragment
     * @return
     */
    public AbsFrameworkFragment getMainMineFragment() {
        MainFragmentContainer mainContainer = getMainFragmentContainer();
        if (mainContainer != null) {
            return mainContainer.getFragmentMine();
        }
        return null;
    }
    protected AbsFrameworkFragment getTopParentFragment() {

        if(this instanceof onMainTabChangedListener){
            return null;
        }
        Fragment topParent = getParentFragment();
        if (topParent == null) {
            return this;
        } else {
            Fragment temp = topParent;
            while (temp != null) {
                temp = temp.getParentFragment();
                if (temp != null) {
                    topParent = temp;
                }
            }
            return (AbsFrameworkFragment) topParent;
        }
    }

    /**
     * 获取UI框架中直接引用的Fragment
     * @return UI框架中直接引用的Fragment
     */
    public AbsFrameworkFragment getFrameworkFragment() {
        Fragment topParent = this, curr;
        do {
            curr = topParent;
            topParent = curr.getParentFragment();
        } while (topParent != null);
        return curr instanceof AbsFrameworkFragment ? (AbsFrameworkFragment) curr : null;
    }

    /**
     * 关掉当前界面
     */
    public void finish() {
        finish(false);
    }

    /**
     * 关掉当前界面
     *
     * @param fakeFinish - 是否假的关闭页面（只是滑动）
     */
    public void finish(final boolean fakeFinish) {

        mAbsFrameworkActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
                if (delegate != null) {
                    log("finish");
                    delegate.finishFragment(getTopParentFragment(), true);
                }else{
                }
            }
        });
    }

    public void finishWithoutAnimation() {

        mAbsFrameworkActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
                if (delegate != null) {
                    log("finish");
                    delegate.finishFragment(getTopParentFragment(), false);
                }
            }
        });
    }

    public void finishWithAnimation() {

        mAbsFrameworkActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
                if (delegate != null) {
                    log("finish");
                    delegate.finishFragment(getTopParentFragment(), true);
                }
            }
        });
    }

    /**
     * 将当前页面弹出栈
     */
    public void popSelf() {
        mAbsFrameworkActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
                if (delegate != null) {
                    log("finish");
                    delegate.removeFragment(AbsFrameworkFragment.this);
                }
            }
        });
    }

    /**
     * 从栈中移除，没有动画，不会切换页面
     */
    public void finishDefinite() {
        mAbsFrameworkActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
                if (delegate != null) {
                    log("finish");
                    delegate.removeFragment(getTopParentFragment());
                }
            }
        });
    }

    /**
     * 显示播放页
     *
     * @param anim
     */
    @Override
    public void showPlayerFragment(boolean anim) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showPlayerFragment(anim);
        }
    }

    @Override
    public void showPlayerFragment(boolean anim,Bundle bundle) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showPlayerFragment(anim,bundle);
        }
    }

    public void showPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showPlayerFragment(anim, isShoudShowFMPlayerFragment);
        }
    }

    /**
     * 当前是否显示播放页
     *
     * @return
     */
    @Override
    public boolean isPlayerFragmentShowing() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.isPlayerFragmentShowing();
        }
        return false;
    }

    /**
     * 播放页是否正在滚动
     *
     * @return
     */
    public boolean isPlayerFragmentScrolling() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.isPlayerFragmentScrolling();
        }
        return false;
    }

    /**
     * 显示菜单抽屉
     * @param anim
     */
    public void showMenu(boolean anim) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showRightMenu(anim);
        }
    }

    /**
     * 显示左菜单抽屉
     * @param anim
     */
    public void showLeftMenu(boolean anim) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showLeftMenu(anim);
        }
    }

    /**
     * 隐藏菜单抽屉
     * @param anim
     */
    public void hideMenu(boolean anim) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.hideMenu(anim);
        }
    }

    /**
     * 菜单抽屉是否打开
     * @return
     */
    public boolean isMenuOpen() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.isMenuOpen();
        }
        return false;
    }

    /**
     * 左菜单抽屉是否打开
     * @return
     */
    public boolean isLeftMenuOpen() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.isLeftMenuOpen();
        }
        return false;
    }

    /**
     * 菜单抽屉是否正在滑动
     * @return
     */
    public boolean isMenuScrolling() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            return delegate.isMenuScrolling();
        }
        return false;
    }

    /**
     * 显示主页导航
     */
    public void showMainFragment() {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showMainFragment();
        }
    }

    /**
     * 显示主页导航
     */
    public void showMainFragment(boolean anim) {
        ViewPagerFrameworkDelegate delegate = mAbsFrameworkActivity.getDelegate();
        if (delegate != null) {
            delegate.showMainFragment(anim);
        }
    }

    /**
     * 框架内部的fragment
     */
    public static final int TYPE_INNER = 0x01;

    /**
     * 框架的菜单fragment
     */
    public static final int TYPE_MENU = 0x02;

    /**
     * 框架的播放页fragment
     */
    public static final int TYPE_PLAYER = 0x03;

    /**
     * 框架外部的fragment
     */
    public static final int TYPE_OUTER = 0x04;

    /**
     * 默认返回类型是框架内部的fragment
     * @return 指定给框架该fragment的类型
     */
    public int getFragmentType() {
        return TYPE_INNER;
    }

    /**
     * 来源未知
     */
    public static final int SOURCETYPE_UNKNOW = 0x00;

    /**
     * 来源是听（酷狗）
     */
    public static final int SOURCETYPE_TING = 0x01;

    /**
     * 来源是看（繁星）
     */
    public static final int SOURCETYPE_KAN = 0x02;

    /**
     * 来源是唱（KTV）
     */
    public static final int SOURCETYPE_CHANG = 0x03;

    /**
     * 来源是玩（游戏）
     */
    public static final int SOURCETYPE_WAN = 0x04;

    /**
     * 来源是我的（酷狗）
     */
    public static final int SOURCETYPE_MINE = 0x05;

    /**
     * 来源是关注（酷狗）
     */
    public static final int SOURCETYPE_FOLLOW = 0x06;

    /**
     * 来源是消息（酷狗）
     */
    public static final int SOURCETYPE_MSG = 0x07;

    /**
     * 默认返回来源类型是听（酷狗）
     * @return 指定给框架该fragment的来源类型
     */
    public int getFragmentSourceType() {
        return SOURCETYPE_TING;
    }

    @Override
    public Resources getResources() {
        return mAbsFrameworkActivity.getResources();
    }

    /**
     * 类似Activity的onNewIntent,此时getArguments获得的已经是参数传递的arguments这个了
     *
     * @param arguments
     */
    public void onNewBundle(Bundle arguments) {
        if (DrLog.DEBUG) DrLog.d(TAG, "arguments:" + arguments);
    }



    /**
     * 获得UI框架的delegate
     * @return
     */
    public ViewPagerFrameworkDelegate getDelegate() {
        return mAbsFrameworkActivity == null ? null : mAbsFrameworkActivity.getDelegate();
    }

    private void log(String str) {
        if (DrLog.DEBUG) DrLog.i("FragmentLifeCycle", getClass().getSimpleName() + "-->" + str);
    }


    /**
     *设置windowSoftInputMode属性 控制软键盘的弹出效果
     * 此为默认实现,一般不需要更改，若更改则子类重写此方法
     */
    public void onInitSoftInputMode() {
        // 默认保持宿主Activity在AndroidManifest.xml中的配置
        Activity activity = getActivity();
        if(activity!=null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    public CopyOnWriteArrayList<Message> mWaitMessages = new CopyOnWriteArrayList<Message>();

    /**
     * 发送需要在waitForFragmentFirstStart后执行的消息，先存起来，待onFragmentFirstStart中再发出
     * @param handler
     * @param msg
     */
    public void sendMessageExcuteAfterFirstStart(Handler handler, Message msg) {

        if (handler != null) {
            msg.setTarget(handler);
        }

        if (isFragmentFirstStartInvoked()) {
            msg.sendToTarget();
            return;
        }
        mWaitMessages.add(msg);
        if (DrLog.DEBUG) DrLog.d(TAG, "sendMessageExcuteAfterFirstStart:" + msg.what);
    }

    public void excuteMessageAfterFirstStart() {
        for (Message msg : mWaitMessages) {
            msg.sendToTarget();
            if (DrLog.DEBUG) DrLog.d(TAG, "excuteMessageAfterFirstStart:" + msg.what);
        }
        mWaitMessages.clear();
    }

    /**
     * 支持手动处理
     * @param what
     * @return
     */
    public Message getAndRemoveWaitingMessage(int what) {
        Message result = null;
        for (Message msg : mWaitMessages) {
            if (msg.what == what) {
                result = msg;
                break;
            }
        }

        if (result != null) {
            mWaitMessages.remove(result);
        }

        if (DrLog.DEBUG) DrLog.d(TAG, "getAndRemoveWaitingMessage:" + ((result == null) ? null : result.what));

        return result;
    }


    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        if (getDelegate() != null) {
            onRestoreFragmentBundle();
        }
        onFragmentSaving = false;
    }

    private final void onSaveFragmentBundle(Bundle outState) {
        int item =0;
        try {
            item = getTab();
        }catch (NullPointerException e){
            
        }
        Bundle bundle = new Bundle();
        if(outState!=null){
            bundle.putAll(outState);
        }
        bundle.putInt("SkinTab_Indicator", item);
        if (DrLog.DEBUG) DrLog.i("ocean", getClass().getSimpleName() + "--getContainerId--" + getContainerId());
        MainFragmentContainer container = getMainFragmentContainer();
        if (container != null && container.getDelegate() != null) {
            getMainFragmentContainer().getDelegate().putBundleToSaveFragment(bundle, getContainerId()); // parasoft-suppress BD.EXCEPT.NP-1
        }
    }

    private final void onRestoreFragmentBundle() {
        if(getArguments()==null)
            return;
        if (DrLog.isDebug()) {
            DrLog.e(TAG, "-->,onRestoreFragmentBundle");
        }

        Bundle bundle=null;
        try {
            bundle = getArguments().getBundle(SaveInstanceDelegate.FRAGMENT_SAVE_STATE);
        }catch (NullPointerException e){
            
        }
        if (bundle != null) {
            int item = bundle.getInt("SkinTab_Indicator");
            if (item > 0) {
                bundle.putInt("SkinTab_Indicator",0);//用过即弃
                setTab(item);
            }
        }
    }

    public void setTab(int item) {
    }

    public int getTab() {
        return 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DrLog.isDebug()) {
            DrLog.e(TAG, "-->,onSaveInstanceState");
        }
        onFragmentSaving = true;
        super.onSaveInstanceState(outState);
        if (getDelegate() != null) {
            onSaveFragmentBundle(outState);
            getDelegate().putBundleToSaveFragment(outState,getContainerId(), getClass().getName()); // parasoft-suppress BD.EXCEPT.NP-1
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        log("onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        log("onDetach");
        super.onDetach();
        drLifecycleRegistry.onDetach();//通知生命周期监听
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // 这里有小概率会因为Fragment对象中的mFragmentManager字段为空，报空指针异常，所以加个判断在这里
        if (!getUserVisibleHint() && isVisibleToUser && getState() < 4 && getFragmentManager() == null) {
            return;
        }
        super.setUserVisibleHint(isVisibleToUser);
        log("setUserVisibleHint(" + isVisibleToUser + ")");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log("onActivityCreated");
    }

    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        if (isOnStackTop()){
            updateSystemStatusBar();
        }
        BackgroundLayer backgroundLayer = getBackgroundLayer();
        if (backgroundLayer != null) {
            backgroundLayer.onSkinAllChanged();
        }
        TitleBar titleBar = getTitleBar();
        if (titleBar != null) {
            //todo zhpu 崩溃，临时修改
            if (titleBar.getMenu() != null) {
                titleBar.getMenu().onSkinAllChanged();
            }
        }
    }

    public View getSkeletonView(Context context) { return null; }

    /**
     * 判断当前fragmnet是不是在栈的最顶层
     * @return 如果在栈的最顶层返回true; 否则，返回false
     */
    public boolean isOnStackTop() {
        ViewPagerFrameworkDelegate delegate = getDelegate();
        if (delegate == null) {
            return false;
        } else {
            return delegate.isFragmentOnStackTop(getFrameworkFragment());
        }
    }

    public void updateSystemStatusBar() {
        switch (getStatusBarActionType()) {
            case IStatusBarActionType.TYPE_CHANGE_BY_SKIN:
                StatusBarCompat.setLightStatusBar(getActivity(), SkinProfileUtil.isDarkTxtStatusBarSkin() || SkinProfileUtil.isDefaultLocalSimpleSkin());
                break;
            case IStatusBarActionType.TYPE_KEEP_LIGHT:
                StatusBarCompat.setLightStatusBar(getActivity(), true);
                break;
            case IStatusBarActionType.TYPE_KEEP_DARK:
                StatusBarCompat.setLightStatusBar(getActivity(), false);
                break;
            case IStatusBarActionType.TYPE_NONE:
            default:
                break;
        }
    }

    public int getStatusBarActionType() {
        return IStatusBarActionType.TYPE_CHANGE_BY_SKIN;
    }

    /**
     * 注册生命周期监听。主线程使用
     * fragment结束的时候，会自动清理掉observer
     *
     * @param observer 同一个对象第二次被add会被忽略。
     *                 要注意，如果你fragment复用但是observer不复用(比如每次都new)，会泄露observer
     */
    public void addLifeCycleObserver(KGLifeCycleObserver observer) {
        drLifecycleRegistry.addLifeCycleObserver(observer);
    }

    /**
     * 在这里移除
     * 使用场景：你fragment复用但是observer不复用(比如每次都new)，会泄露observer。
     * @param observer
     */
    public void removeLifeCycleObserver(KGLifeCycleObserver observer) {
        drLifecycleRegistry.remove(observer);
    }

    /**
     * 注册生命周期监听
     * fragment的onDetach的时候，会自动清理掉observer
     *
     * @param observer 同一个对象第二次被add会被忽略。
     *                 要注意，如果你fragment复用但是observer不复用(比如每次都new)，会泄露observer
     */
    public void registerKGLifeCycleObserver(KGLifeCycleObserver observer) {
        addLifeCycleObserver(observer);
    }

    /**
     * 在沉浸式导航栏的情况下，添加假的导航栏白色背景
     *
     * @return
     */
    public boolean hasFakeNavigationBar() {
        return true;
    }

    /**
     * 注意，这个没有Event.ON_FRAGMENT_RESUME这种状态
     * 故推荐使用getLifecycle().getCurrentState().isAtLeast(x)
     * 如果要判断onFragmentResume,使用hasResume()
     * @deprecated
     * @return
     */
    public @KGLifeCycleObserver.Event int getKgLifeCycleEvent() {
        return drLifecycleRegistry.getKgLifeCycleEvent();
    }

    @Override
    public void onSlideCallback(boolean toLeft) {
        super.onSlideCallback(toLeft);
    }

    public void autoInjectLifeCycle(View view) {
        if (view != null) {
            if (view instanceof KGLifeCycleObserver) {
                registerKGLifeCycleObserver((KGLifeCycleObserver) view);
                //补一个OnCreate
                ((KGLifeCycleObserver) view).onStateChanged(this, KGLifeCycleObserver.Event.ON_CREATE);
            }
            if (view instanceof ViewGroup) {
                injectLifeCycleChile((ViewGroup) view);
            }
        }
    }

    private void injectLifeCycleChile(ViewGroup viewGroup) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof KGLifeCycleObserver) {
                    registerKGLifeCycleObserver((KGLifeCycleObserver) view);
                    //补一个OnCreate
                    ((KGLifeCycleObserver) view).onStateChanged(this, KGLifeCycleObserver.Event.ON_CREATE);
                }
                if (view instanceof ViewGroup) {
                    injectLifeCycleChile((ViewGroup) view);
                }
            }
        }
    }

    public Pair<Bitmap, String> processScreenShotBitmap(String imgPath) {
        return new Pair<>(KGBitmapUtil.decodeFile(imgPath), imgPath);
    }

    public boolean hasReadNovelBar() {
        return false;
    }

    public boolean hasAINovelBar() {
        return false;
    }
}
