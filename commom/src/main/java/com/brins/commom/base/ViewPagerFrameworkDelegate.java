package com.brins.commom.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.brins.commom.toast.ToastCompat;
import com.brins.commom.toast.ToastUtils;
import com.brins.commom.utils.KGAssert;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.common.base.FragmentPersistable;
import com.kugou.common.base.FragmentRemovePersistable;
import com.kugou.common.base.MenuCard;
import com.kugou.common.base.ViewPager;
import com.kugou.common.base.uiframe.FragmentViewBase;
import com.kugou.page.core.KGFrameworkFragment;
import com.kugou.page.framework.KGFragmentActivity;
import com.kugou.page.framework.delegate.FrameworkDelegateBase;
import com.kugou.page.framework.delegate.KGEmptyLifecycleCallback;
import com.kugou.page.framework.delegate.PendingAction;
import com.kugou.page.utils.LogUtil;

public class ViewPagerFrameworkDelegate extends FrameworkDelegateBase<AbsFrameworkFragment> {

    private static final String TAG = "ViewPagerFrameworkDelegate";

    private static final String RESTORE_PLAYER_FRAGMENT_STATE = TAG + ":restore_player_fragment-state";

    public static final String KEY_OPEN_TWO_FRAGMENT = "viewpager_framework_delegate_open_two_fragment";

    public static boolean sFragmentTransactionTraceEnable;

    private final DelegateListener mListener;

    private long mLastTime;

    public ViewPagerFrameworkDelegate(KGFragmentActivity activity, DelegateListener listener) {
        super(activity);
        mListener = listener;
        FrameworkUtil.setDelegate(this);
        registerKGFrameworkCallback(new KGEmptyLifecycleCallback() {
            @Override
            public void dispatchFragmentResume(KGFrameworkFragment f) {
                if (f != null && f.isActivityCreated() && f.isFragmentFirstStartInvoked()) {
                    sCurrentResumeFragment = f.getClass().getName();
                }
            }
        });
    }

    public void addNormalIngoreView(View view) {
        if (mMenuCard != null) {
            mMenuCard.addNormalIngoreView(view);
        }
    }

    /**
     *
     * @return ?????????????????????
     */
    public MainFragmentContainer getMainFragmentContainer() {
        AbsFrameworkFragment fragment = getMainFragmentContainerInternal();
        return fragment instanceof MainFragmentContainer ?  (MainFragmentContainer) fragment : null;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param enabled ????????????????????????
     * @deprecated ????????????:
     * {@link ViewPagerFrameworkDelegate#setSlidingEnabled(AbsFrameworkFragment, boolean)}
     */
    public void setSlidingEnabled(boolean enabled) {
        setSlidingEnabled(getCurrentFragment(), enabled);
    }

    /**
     * ????????????Fragment???????????????????????????????????????
     *
     * @param fragment ??????Fragment
     * @param enabled  ????????????
     */
    public void setSlidingEnabled(final AbsFrameworkFragment fragment, final boolean enabled) {
        if (fragment == null) return;
        try {
            FragmentViewBase viewBase = (FragmentViewBase) mViewPager.findViewById(fragment.getContainerId());
            if (viewBase != null) {
                viewBase.setSlidingEnabled(enabled);
            }
        } catch (ClassCastException e) {
            
        }
    }

    /**
     * ????????????Fragment???????????????????????????????????????
     * ?????????????????????????????????
     *
     * @param fragment ??????Fragment
     * @param enabled  ????????????
     */
    public void setVerticalSlidingEnabled(final AbsFrameworkFragment fragment, final boolean enabled) {
        if (fragment == null) return;
        View view = mViewPager.findViewById(fragment.getContainerId());
        if (view instanceof FragmentViewBase) {
            FragmentViewBase viewBase = (FragmentViewBase) view;
            viewBase.setVerticalSlidingEnabled(enabled);
        }
    }

    public static int restoreMainTab = 0;
    public static boolean isFirstInit = true;

    public void onCreate(MenuCard menuCard, Bundle savedInstanceState) {
        super.onCreate(menuCard, savedInstanceState);
        /*if (AutoSelectedKanUtil.isAutoSelectKanStatus() && DefaultPrefs.getInstance().isShowLiveTab()) {
            restoreMainTab = MainFragmentContainer.TAB_KAN;
        } else if ((SystemUtils.isAvalidNetSetting() && EnvManager.isOnline())) {//???????????????????????????????????????
            restoreMainTab = MainFragmentContainer.TAB_TING; //???
        } else {
            restoreMainTab = MainFragmentContainer.TAB_MINE; //???
        }
        if (savedInstanceState != null) {
            playerFragmentState = savedInstanceState.getBoolean(RESTORE_PLAYER_FRAGMENT_STATE);
            restoreMainTab = savedInstanceState.getInt(MainFragmentContainer.KEY_CURRENT_TAB_INDEX, 0);
            isFirstInit = savedInstanceState.getBoolean(MainFragmentContainer.KEY_ISFIRSTINIT, true);
//            if (restoreMainTab == MainFragmentContainer.TAB_KAN && !DefaultPrefs.getInstance().isShowKanTab()) {
//                restoreMainTab = MainFragmentContainer.TAB_TING;
//                KGThreadPool.postToMainThread(() -> {
//                    MainFragmentContainer mainFragmentContainer = getMainFragmentContainer();
//                    if (mainFragmentContainer != null) {
//                        mainFragmentContainer.switchToRadioMode();
//                    }
//                }, 100);
//            }
        }
        initViewPagerDelegate(menuCard);*/

    }

    boolean playerFragmentState;



    @Override
    protected void onMenuCardPageSelectedAfterAnimation(MenuCard v, int prevPosition, int curPosition) {
        super.onMenuCardPageSelectedAfterAnimation(v, prevPosition, curPosition);
    }

    @Override
    protected void onMenuCardPageSelected(MenuCard v, int prevPosition, int curPosition) {
        super.onMenuCardPageSelected(v, prevPosition, curPosition);
    }

    @Override
    public void showLeftMenu(boolean anim) {
        super.showLeftMenu(anim);
    }

    @Override
    public void showRightMenu(boolean anim) {
        super.showRightMenu(anim);

    }

    @Override
    public void hideMenu(boolean anim) {
        super.hideMenu(anim);
    }

    private void log(String str) {
        if (DrLog.DEBUG) DrLog.e("playerFramework", "ViewPagerFrameworkDelegate-->log," + str);
    }


    @Override
    protected void restoreFragments(@NonNull Bundle savedInstanceState) {
        super.restoreFragments(savedInstanceState);
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                mListener.onFragmentFirstStart();
            }
        });
    }

    public void startAndRestoreFragments(AbsFrameworkFragment mainFragmentContainer, Bundle savedInstanceState) {
        try {
            startHomeFragment(mainFragmentContainer, new Bundle());
        } catch (Exception e) {
            if (DrLog.DEBUG) DrLog.e(Log.getStackTraceString(e));
        }
        if (savedInstanceState != null) {
            restoreFragments(savedInstanceState);
        } else {
            mListener.onFragmentFirstStart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        }


    private IPlayingBar mPlayingBar;

    /**
     * ??????????????????????????????????????????
     */
    public void forceDestroyPage(FragmentPersistable persistFrag) {
        if (persistFrag instanceof AbsFrameworkFragment) {
            AbsFrameworkFragment fragment = ((AbsFrameworkFragment) persistFrag).getTopParentFragment();
            FragmentViewBase fragView = (FragmentViewBase) mViewPager.findViewById(fragment.getContainerId());
            removeFragmentFromManager(fragment);
            if (fragView != null) {
                mViewPager.removeView(fragView);
            }
        }
    }

    private void forceDestoryPersistFragment(FragmentViewBase viewBase) {
        if (viewBase != null) {
            AbsFrameworkFragment removeFrag = (AbsFrameworkFragment) getActivity().getSupportFragmentManager().findFragmentByTag(String.valueOf(viewBase.getId()));
            if (removeFrag != null) {
                removeFragmentFromManager(removeFrag, true);
            }
            mViewPager.removeView(viewBase);
            if (DrLog.isDebug()) {
                DrLog.d(TAG, "forceDestroyPersistFrag --- ?????????????????????:" + removeFrag + viewBase);
            }
        }
    }
    /**
     * ???????????????????????????FragmentPersistable?????????
     * @param persisClass ???????????????FragmentPersistable
     */
    public void forceDestroyPersistFrag(Class<? extends Fragment> persisClass) {
        FragmentViewBase viewBase = getPersistentContainer(persisClass);
        forceDestoryPersistFragment(viewBase);
    }
    
    

    public void attachPlayingBar(IPlayingBar bar) {
        mPlayingBar = bar;
        if (mPlayingBar != null) {
            AbsFrameworkFragment f = getCurrentFragment();
            if (f != null) {
                mPlayingBar.makeSureVisibility(f.hasPlayingBar(), f.hasMainBottomView());
            }
        }
    }

    private boolean shoudShowFMPlayerFragment = false;

    public void setShowFMPlayerFragment(boolean showFMPlayerFragment) {
        shoudShowFMPlayerFragment = showFMPlayerFragment;
    }

    private void startPlayerFragment(boolean anim) {
        startPlayerFragment(anim, null);
    }

    private void startPlayerFragment(boolean anim, Bundle bundle) {
        startPlayerFragment(anim, shoudShowFMPlayerFragment, bundle);
    }

    Class mPlayerFragmentCls;
    Class mFMPlayerFragmentCls;
    Class mChildPlayerFragmentCls;

    private void startPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment) {
        startPlayerFragment(anim, isShoudShowFMPlayerFragment, null);
    }


    private void startPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment, Bundle bundle) {
        try {
            if (mPlayerFragmentCls == null) {
                mPlayerFragmentCls = Class.forName("com.kugou.android.app.player.PlayerFragment");
            }

            startFragment(getCurrentFragment(), mPlayerFragmentCls, bundle, anim, false, false);
        } catch (Exception e) {
        }

    }

    @Override
    public AbsFrameworkFragment onCreateMenuFragmentTing() {
        return null;
    }

    public FragmentStackInfo genFragmentStackInfo() {
        final int SIZE = getContainerSize();
        AbsFrameworkFragment[] fragments = new AbsFrameworkFragment[SIZE];
        View[] views = new View[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            FragmentViewBase view = mViewPager.getFragmentViewAt(i);
            views[i] = view;
            if (view == null) {
                fragments[i] = null;
            } else {
                AbsFrameworkFragment f = findFragmentByContainerId(view.getId());
                fragments[i] = f;
            }
        }
        FragmentStackInfo stackInfo = new FragmentStackInfo();
        stackInfo.mViews = views;
        stackInfo.mFragments = fragments;
        return stackInfo;
    }


    public AbsFrameworkFragment getHomePageFragment() {
        return getMainFragmentContainerInternal();
    }

    /**
     * @param target
     * @param cls
     * @param args
     * @param anim
     * @param replaceMode replaceMode??????????????????fragment
     * @param clearTop    replaceMode ??? clearTop???????????????true
     */
    public void startFragment(AbsFrameworkFragment target, Class<? extends Fragment> cls,
                              Bundle args, boolean anim, boolean replaceMode, boolean clearTop) {
        startFragment(target, cls, args, anim, replaceMode, clearTop, false);
    }

    public void showPlayerFragment(boolean anim) {
        startPlayerFragment(anim);
    }

    public void showPlayerFragment(boolean anim, Bundle bundle) {
        startPlayerFragment(anim, bundle);
    }

    public void showPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment) {
        startPlayerFragment(anim, isShoudShowFMPlayerFragment);
    }

    public void showPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment, Bundle bundle) {
        startPlayerFragment(anim, isShoudShowFMPlayerFragment, bundle);
    }

    public boolean isPlayerFragmentShowing() {
        if (getCurrentFragment() != null && getCurrentFragment().getClass() != null) {
            String name = getCurrentFragment().getClass().getSimpleName();
            return isPlayerFragment(name);
        }
        return false;

    }

    public boolean isPlayerFragmentScrolling() {
        return mViewPager != null && isPlayerFragmentShowing() && mViewPager.getScrollState() != ViewPager.SCROLL_STATE_IDLE;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AbsFrameworkFragment f = getCurrentFragment();
        if (f != null && f.isActivityCreated()) {
            if (f.onKeyDown(keyCode, event))
                return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onKeyBackFinishFragment()) {
                mListener.onKeyBackSlideCallback();
                return true;
            } else {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE && getContainerSize() == 1) {
                    long tmp = System.currentTimeMillis();
                    if (tmp - mLastTime < 2000 && event.getRepeatCount() == 0) {
                        try {
                            getActivity().moveTaskToBack(true);
                        } catch (NullPointerException e) {
                            if (DrLog.DEBUG) {
                                KGAssert.fail("MediaActivity moveTaskToBack failed");
                            } else {

                            }
                        }
                    } else {
                        String hmosVersion;
                        if (SystemUtils.isHMOS() && !TextUtils.isEmpty(hmosVersion = SystemUtils.getHMOSVersion()) && hmosVersion.startsWith("3")) {
                            ToastUtils.showToastShortWithGravity(getActivity(), "????????????????????????", Gravity.NO_GRAVITY);
                        } else {
                            ToastCompat.makeText(getActivity(), "????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                        mLastTime = tmp;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getScrollState() {
        return mScrollState;
    }


    public void showMainFragment() {
        showMainFragment(true);
    }

    public void showMainFragment(boolean anim) {
        if (isMenuOpen()) {
            hideMenu(true);
        }
        AbsFrameworkFragment current = getCurrentFragment();
        /*MainFragmentContainer mainContainer = getMainFragmentContainer();
        if (current != null && mainContainer != null) {
            mainContainer.setSelectTab(current.getFragmentSourceType());
        }
        showFragment(getMainFragmentContainer(), anim);*/
    }

    public void setPlayingbarVisibility(boolean visible) {
        setPlayingbarVisibility(visible, true);
    }

    public void setPlayingbarVisibility(boolean visible, boolean withAnim) {
        if (mPlayingBar != null) {
            if (withAnim) {
                mPlayingBar.animVisibility(visible);
            } else {
                AbsFrameworkFragment fragment = getCurrentFragment();
                mPlayingBar.makeSureVisibility(visible, fragment != null && fragment.hasMainBottomView());
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     * @param show
     * @param hasMainBottomView
     */
    public void controlPlayingBarVisibility(boolean show, boolean hasMainBottomView) {
        if (mPlayingBar != null) {
            mPlayingBar.controlVisibility(show, hasMainBottomView);
        }
    }

    public boolean getPlayingbarVisibility() {
        AbsFrameworkFragment current = getCurrentFragment();
        if (mPlayingBar != null && mPlayingBar.isGlobalVisibility()
                && current != null && current.hasPlayingBar() && !current.isPlayerFragmentShowing()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPlayerFragment(String fragmentName) {
        return ("PlayerFragment".equals(fragmentName)) || "FmPlayFragment".equals(fragmentName);
    }

    /**
     * ????????? ???AB????????????A???B???A???B???A????????????????????????A?????????????????????B->A
     * ?????????????????????Fragment??????????????????
     * @param fragment
     */
    public void removeFragment4Stack(AbsFrameworkFragment fragment) {
        if (fragment == null) return;
        if (mMenuCard.getScrollState() != MenuCard.SCROLL_STATE_IDLE
                || mViewPager.getScrollState() != ViewPager.SCROLL_STATE_IDLE) {
            return;
        }
        final int containerId = fragment.getContainerId();
        removeFragmentFromManager(fragment);
        View targetView = mViewPager.findViewById(containerId);
        if (targetView instanceof FragmentViewBase) {
            mViewPager.removeView((FragmentViewBase) targetView);
        }
    }

    public static void setFragmentTransactionTraceEnable(final boolean enable) {
        sFragmentTransactionTraceEnable = enable;
    }
    @Override
    protected void controlPlayingBarVisibility(AbsFrameworkFragment currentF) {
        super.controlPlayingBarVisibility(currentF);
        if (currentF != null) {
            if (mPlayingBar != null) {
                mPlayingBar.controlVisibility(currentF.hasPlayingBar(),currentF.hasMainBottomView());
            }
        }
    }

    /**
     * ??????????????? Fragment????????????????????????????????????
     *
     * @return ??????????????????
     */
    public boolean removeFragment(final AbsFrameworkFragment targetFragment) {
        if (targetFragment == null) return false;
        FragmentViewBase targetContainerView = (FragmentViewBase) mViewPager.findViewById(targetFragment.getContainerId());
        if (targetContainerView == null) return false;
        PendingAction action = new PendingAction();
        action.finishFragment = targetFragment;
        enqueueAction(action);
        execPendingActions();
        final int containerId = targetFragment.getId();
        mViewPager.removeView((FragmentViewBase) mViewPager.findViewById(containerId));

        // ??? FragmentManager ?????????
        if (!getActivity().isFinishing()) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(targetFragment);
            try {
                ft.commitAllowingStateLoss();
                manager.executePendingTransactions();
            } catch (Exception var5) {
                if (LogUtil.isDebug()) {
                    LogUtil.throwExceptionIfDebug(var5);
                }
            }
            removeSaveFragmentBundle(targetFragment);
        }

        return true;
    }

    private boolean realPersistable(AbsFrameworkFragment f) {
        return true;
    }
    @Override
    protected void onAddFragmentToManager(AbsFrameworkFragment f) {

    }

    @Override
    protected boolean enablePersistableFragmentOnNewBundle(AbsFrameworkFragment f) {
        //?????????????????????fragment????????????????????????onNewBundle???
        //??????????????????FragmentPersistableControl??????????????????????????????????????????????????????
        return true;
    }

    @Override
    protected void handlePersistableFragmentRemoveFromManager(AbsFrameworkFragment f, FragmentTransaction ft, boolean removeAnyWay) {
        if (realPersistable(f) && !removeAnyWay) {
            ((FragmentPersistable) f).onFragmentRemoveToHide();
        } else {
            ft.remove(f);
            removePersistentFragment(f.getClass());
        }
        if (f instanceof FragmentRemovePersistable) {
            removePersistentFragment(f.getClass());
            ft.remove(f);
        }
    }

    public interface DelegateListener {

        void onKeyBackSlideCallback();

        /**
         * ????????????????????????
         */
        void onSlideToLeftCallback();

        /**
         * ????????????????????????
         */
        void onSlideToRightCallback();

        /**
         * ??????????????????????????????
         * @param isFMPlayer
         * @param isShowing
         */
        void onPlayerSlideCallback(boolean isFMPlayer, boolean isShowing);

        void onFragmentFirstStart();

    }

    /* ==================  ????????????LifeCycle???????????????????????? Start ================*/
    @Override
    public void startFragment(AbsFrameworkFragment target, Class<? extends Fragment> cls, Bundle args, boolean anim, boolean replaceMode, boolean clearTop, boolean fromRestore) {
        super.startFragment(target, cls, args, anim, replaceMode, clearTop, fromRestore);

    }

    @Override
    @Deprecated
    protected void legacyOnSlideToLeftCallback() {
        if (mListener != null) {
            mListener.onSlideToLeftCallback();
        }
    }

    @Override
    @Deprecated
    protected void legacyOnPlayerSlideCallback(AbsFrameworkFragment fragment) {
        /*if (mListener != null && (fragment.hasPlayingBar() || fragment instanceof MainFragmentContainer || fragment.hasReadNovelBar())) {
            mListener.onPlayerSlideCallback(shoudShowFMPlayerFragment, false);
        }*/
    }

    @Override
    @Deprecated
    protected void legacyOnSlideToRightCallback() {
        if (mListener != null) {
            mListener.onSlideToRightCallback();
        }
    }

    @Override
    @Deprecated
    protected void legacyUpdateBackTime() {

    }

    @Override
    @Deprecated
    protected void legacyControlPlayingBarVisibility(int playBarState) {
        if (playBarState != FragmentViewBase.PLAYING_BAR_UNKNOWN && mPlayingBar != null) {
            ;
            mPlayingBar.controlVisibility(playBarState == FragmentViewBase.PLAYING_BAR_HAS, getCurrentFragment() != null && getCurrentFragment().hasMainBottomView());
        }
    }

    @Override
    @Deprecated
    protected void legacyMakeSurePlayingBarVisibility(AbsFrameworkFragment f) {
        if (mPlayingBar != null) {
            mPlayingBar.makeSureVisibility(f.hasPlayingBar(),getCurrentFragment() != null && getCurrentFragment().hasMainBottomView());
        }
    }

    @Override
    public void finishCurrentFragmentOnMainThread() {
        super.finishCurrentFragmentOnMainThread();

    }

    /* ==================  ????????????LifeCycle???????????????????????? End ================*/
}