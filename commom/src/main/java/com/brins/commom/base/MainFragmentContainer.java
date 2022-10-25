package com.brins.commom.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.brins.commom.R;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.constant.DRIntent;
import com.brins.commom.event.BottomTabChangeEvent;
import com.brins.commom.event.TingTabEvent;
import com.brins.commom.maincontainer.MainContainerLayout;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.swipetap.MainFragmentViewPage;
import com.brins.commom.swipetap.SwipeViewPage;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.kugou.common.base.MenuCard;
import com.kugou.common.base.ViewPager;
import com.kugou.common.base.uiframe.AnimationAccelerate;
import com.kugou.page.core.KGFrameworkFragment;
import com.kugou.page.sub.ViewPagerLifecycleManager;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@AnimationAccelerate(viewType = AnimationAccelerate.HOME)
public class MainFragmentContainer extends AbsFrameworkFragment implements MainContainerLayout.ViewState,
        SwipeViewPage.DisallowInterceptCallback {

    private AbsFrameworkFragment mFragmentTing, mFragmentMine;
    private AbsFrameworkFragment[] allFragments = new AbsFrameworkFragment[2];

    private int mCurrentTabIndex = TAB_TING;

    public static final int TAB_TING = 0;
    public static final int TAB_KAN = 3; // 可以通过设置页配置，显示是看Tab还是听书Tab
    public static final int TAB_CHANG = 2;
    public static final int TAB_MINE = 1;

    public static final int TAB_VIDEO = 4;

    public static int firstTabIndex = TAB_TING; // 用于记录第一次初始化MainFragmentContainer的第一个Tab的索引

    private MainContainerLayout mContainerLayout;
    private MainFragmentViewPage mCommViewPager;
    private BaseMainFragmentPagerAdapter mAdapter;
    
    public static int TAB_COUNT = 2;

    private int mTingCurVPPos;//听页面viewpager当前位置 用于控制首页滑动等

    private boolean isResumed = false;
    private boolean mNeedScrolle=true;
    private boolean mIsPageSelected=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return onCreateViewImpl();
        } catch (Throwable t) {
            throw t;
        }
    }

    public View onCreateViewImpl() {
        if (DrLog.DEBUG) DrLog.d("MainFragmentContainer", "onCreateView");
        MainContainerLayout view = new MainContainerLayout(getActivity());
        mContainerLayout=view;
        mContainerLayout.setViewState(this);
        return mContainerLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            onViewCreatedImpl();
        } catch (Throwable t) {
            throw t;
        }
    }

    public void onViewCreatedImpl() {
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
        if(isFromAdJump){
            mCurrentTabIndex = tabIndexFromAd;
        }else {
            mCurrentTabIndex = getDelegate().restoreMainTab;
        }
        firstTabIndex = mCurrentTabIndex;
        mAdapter = new BaseMainFragmentPagerAdapter(getChildFragmentManager(), getActivity(),mCurrentTabIndex);
        mFragmentMine = (AbsFrameworkFragment) mAdapter.getFragmentItem(TAB_MINE);//
        mFragmentTing = (AbsFrameworkFragment) mAdapter.getFragmentItem(TAB_TING);
        allFragments[TAB_TING] = mFragmentTing;
        allFragments[TAB_MINE] = mFragmentMine;
        mAdapter.setCurrentPosition(mCurrentTabIndex);
        mCommViewPager = mContainerLayout.getPagerContainer();
        setChildFragmentLifecycleManager(new ViewPagerLifecycleManager(this, mCommViewPager, mAdapter, 0));
        mCommViewPager.setAdapter(mAdapter);
        mCommViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mCommViewPager.setCanSlide(false);
        mCommViewPager.setOffscreenPageLimit(TAB_COUNT - 1);
        changeTab(mCurrentTabIndex, true);
        mContainerLayout.adjustStatusBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
/*        filter.addAction(KGIntent.ACTION_USER_LOGOUT);
        filter.addAction(KGIntent.USER_LOGIN_SUCCESS_ACTION);
        filter.addAction(KtvIntent.ACTION_MESSAGE);
        filter.addAction(KugouPlaybackService.PLAYSTATE_CHANGED);*/
        BroadcastUtil.registerReceiver(receiver, filter);
    }

    public void setHeaderGroupAlpha(float alpha) {
//        mTopTabBar.updateLeftAreaAlpha(alpha);
    }

    @Override
    public void onAttachedToWindow() {
       // DataCollector.getInstance().addonPageShowEvent(this);
    }

    @Override
    public void onFirstLayout() {
    }

    @Override
    public void onResume() {
        super.onResume();
        initTabStatus();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mContainerLayout != null && newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin() ||
                    SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
                mContainerLayout.setBackgroundColor(SkinProfileUtil.getLocalMainBgColor());
            } else {
                //mContainerLayout.setBackgroundDrawable(IBootMagicBox.get().getMainBackground(true));
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        BroadcastUtil.unregisterReceiver(receiver);
    }

    private void initTabStatus() {
        switch (mCurrentTabIndex) {
            case TAB_MINE:
                onMineTabClick(null);
                break;

            case TAB_TING:
                onTingTabClick(null);
                break;
            default:
                break;
        }
    }


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int state) {
            //DrLog.i("xfeng","onPageScrollStateChanged   state:"+state);
            onMainScrollStateChangedCallBack(state);
            if (state == 1) {
                try {
                    turnToloading();
                } catch (Exception e) {

                }
            } else if (state == 0) {
                try {
                    turnToContent();
                } catch (Exception e) {

                }
            }

        }

        @Override
        public void onPageScrolled(int position, float v, int i1) {
            //DrLog.i("xfeng","onPageScrolled   position:"+position+"  v"+v+"  i1"+i1);
            //这里在点击事件的时候,只调用一次
            if(mIsPageSelected){
                mIsPageSelected=false;
                onPageSelectAnim(position);
            }
            onMainScrolled(position, v);
        }

        @Override
        public void onPageSelected(int position, boolean smoothScroll) {
            //DrLog.i("xfeng","onPageSelected   position:"+position);
            EventBus.getDefault().post(new MainTabChangeEvent(mCurrentTabIndex,position));
//            if (allFragments != null && allFragments.length > mCurrentTabIndex - 1 && allFragments[mCurrentTabIndex] != null && allFragments[mCurrentTabIndex].isAlive()) {
//                allFragments[mCurrentTabIndex].onFragmentPause();
//            }
            setSelectedTabUI(position);
            mIsPageSelected = true;
        }

        @Override
        public void onPageSelectedAfterAnimation(int position) {
            //DrLog.i("xfeng","onPageSelectedAfterAnimation   position:"+position);
            onPageSelectAnim(position);
        }
    };

    //这里处理加载逻辑
    private void onPageSelectAnim(int position) {
        //DrLog.i("xfeng","onPageSelectAnim   position:"+position);
        changeTab(position, false);
        mCurrentTabIndex = position; // 要放在setSelectedTabUI后面，否则无法进入updateMenuCardByTab方法修改菜单拦截
        initTabFragment(position);

        onMainTabChangedCallBack(position);
//        if (allFragments != null && allFragments.length > mCurrentTabIndex - 1 && allFragments[mCurrentTabIndex] != null && allFragments[mCurrentTabIndex].isAlive()) {
//            allFragments[mCurrentTabIndex].onFragmentResume();
//        }
    }

    private void turnToloading() {
        View mineview = mAdapter.getFrameLayout(TAB_MINE);
        if (mineview != null) {
            mineview.findViewById(R.id.progress_info).setVisibility(View.VISIBLE);
        }
        View tingview = mAdapter.getFrameLayout(TAB_TING);
        if (tingview != null) {
            tingview.findViewById(R.id.progress_info).setVisibility(View.VISIBLE);
        }
        View kanview = mAdapter.getFrameLayout(TAB_KAN);
        if (kanview != null) {
            kanview.findViewById(R.id.progress_info).setVisibility(View.VISIBLE);
        }
        View changeView = mAdapter.getFrameLayout(TAB_CHANG);
        if (changeView != null) {
            changeView.findViewById(R.id.progress_info).setVisibility(View.VISIBLE);
        }
        View videoView = mAdapter.getFrameLayout(TAB_VIDEO);
        if (videoView != null) {
            videoView.findViewById(R.id.progress_info).setVisibility(View.VISIBLE);
        }
    }

    private void turnToContent() {
        View mineview = mAdapter.getFrameLayout(TAB_MINE);
        if (mineview != null) {
            mineview.findViewById(R.id.progress_info).setVisibility(View.GONE);
        }
        View tingview = mAdapter.getFrameLayout(TAB_TING);
        if (tingview != null) {
            tingview.findViewById(R.id.progress_info).setVisibility(View.GONE);
        }
        View kanview = mAdapter.getFrameLayout(TAB_KAN);
        if (kanview != null) {
            kanview.findViewById(R.id.progress_info).setVisibility(View.GONE);
        }
        View changeView = mAdapter.getFrameLayout(TAB_CHANG);
        if (changeView != null) {
            changeView.findViewById(R.id.progress_info).setVisibility(View.GONE);
        }
        View videoView = mAdapter.getFrameLayout(TAB_VIDEO);
        if (videoView != null) {
            videoView.findViewById(R.id.progress_info).setVisibility(View.GONE);
        }
    }

    private AbsFrameworkFragment getSubFragment(int tab) {
        if (getDelegate() != null && getMainFragmentCallbacks() != null) {
            switch (tab) {
                case TAB_MINE:
                    if (mFragmentMine == null) {
                        mFragmentMine = getMainFragmentCallbacks().onCreateMainFragmentMine();
                        allFragments[TAB_MINE] = mFragmentMine;
                        if (mFragmentMine != null) {
                            mFragmentMine.setArguments(new Bundle());
                            if (isInvokeFragmentFirstStartBySelf()) {
                                mFragmentMine.setInvokeFragmentFirstStartBySelf();
                            }
                            if (isFragmentFirstStartInvoked()) {
                                mFragmentMine.setFragmentFirstStartInvoked();
                            }
                        }
                    }
                    return mFragmentMine;

                case TAB_TING:
                    if (mFragmentTing == null) {
                        mFragmentTing = getMainFragmentCallbacks().onCreateMainFragmentTing();
                        allFragments[TAB_TING] = mFragmentTing;
                        if (mFragmentTing != null) {
                            mFragmentTing.setArguments(new Bundle());
                            if (isInvokeFragmentFirstStartBySelf()) {
                                mFragmentTing.setInvokeFragmentFirstStartBySelf();
                            }
                            if (isFragmentFirstStartInvoked()) {
                                mFragmentTing.setFragmentFirstStartInvoked();
                            }
                        }
                    }
                    return mFragmentTing;
                default:
                    break;
            }

        }
        return null;
    }

    /**
     * 设置tab的选中效果
     *
     * @param tab
     */
    private void setSelectedTabUI(int tab) {
        changeTab(tab, false);
        switch (tab) {
            case TAB_MINE:
                break;

            case TAB_TING:
                break;
            case TAB_KAN:
                break;
            case TAB_CHANG:
                break;
            default:
                break;
        }
    }

    public void setSelectFragment(int tab) {
        if (mCommViewPager != null) {
            //-----------注意:如果需要滚动效果,请修改上面逻辑之后,再置为true:注意-----------
            mCommViewPager.setCurrentItem(tab, false);
            SystemUtils.hideSoftInput(getActivity());
        }
    }

    public AbsFrameworkFragment getSelectedFragment() {
        if (getMainFragmentCallbacks() != null) {
            final int tab = mCurrentTabIndex;
            switch (tab) {
                case TAB_MINE:
                    return mFragmentMine;
                case TAB_TING:
                    return mFragmentTing;
                default:
                    break;
            }
        }
        return null;
    }

    public AbsFrameworkFragment getFragmentTing() {
        return mFragmentTing;
    }

    public AbsFrameworkFragment getFragmentMine() {
        return mFragmentMine;
    }

    public void onMineTabClick(View view) {
        setSelectFragment(TAB_MINE);
    }

    public void onTingTabClick(View view) {
        setSelectFragment(TAB_TING);
        
    }


    public boolean isBottomTabBarInit(){
        //mAdapter 不为空说明已经初始化好了
        return mAdapter != null;
    }

    private boolean isFromAdJump = false;
    private int tabIndexFromAd = -1;

    @Override
    public int getFragmentSourceType() {
        final int tabIndex = mCurrentTabIndex;
        if (tabIndex == TAB_MINE) {
            return SOURCETYPE_MINE;
        } else {
            return SOURCETYPE_TING;
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        isResumed = true;
        DrLog.i("lusonTest","MainFragmentContainer onFragmentResume");
    }


    @Override
    public void onFragmentPause() {
        super.onFragmentPause();

        isResumed = false;
        DrLog.i("lusonTest","MainFragmentContainer onFragmentPause");

    }


    @Override
    public void onPersistentFragmentRestart() {
        super.onPersistentFragmentRestart();
        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null && fragment.isActivityCreated()) {
                fragment.onPersistentFragmentRestart();
            }
        }
    }

    @Override
    public void onScreenStateChanged(int state) {
        super.onScreenStateChanged(state);
        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null && fragment.isActivityCreated()) {
                fragment.onScreenStateChanged(state);
            }
        }
    }

    @Override
    protected void onSkinColorChanged() {
        super.onSkinColorChanged();
        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null && fragment.isActivityCreated()) {
                fragment.onSkinColorChanged();
            }
        }
    }

    @Override
    protected void onNaviBGAlphaChanged() {
        super.onNaviBGAlphaChanged();
        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null && fragment.isActivityCreated()) {
                fragment.onNaviBGAlphaChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AbsFrameworkFragment f = getSelectedFragment();
        if (f != null && f.isActivityCreated() && f.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        AbsFrameworkFragment f = getSelectedFragment();
        if (f != null && f.isActivityCreated() && f.onKeyLongPress(keyCode, event)) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        AbsFrameworkFragment f = getSelectedFragment();
        if (f != null && f.isActivityCreated() && f.onKeyMultiple(keyCode, count, event)) {
            return true;
        }
        return super.onKeyMultiple(keyCode, count, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        AbsFrameworkFragment f = getSelectedFragment();
        if (f != null && f.isActivityCreated() && f.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean canSlide() {
        return false;
    }

    private boolean mHasMenu = false;
    @Override
    public boolean hasMenu() {
        if(DrLog.DEBUG) DrLog.d("zhpu_tt",  "hasmenu " + mHasMenu);
        return mHasMenu;
    }


    @Override
    public boolean hasKtvMiniBar() {
//        if (mCurrentTabIndex == TAB_TING) {
//            //唱放在听的子teb，通过听的子tab返回
//            if (mFragmentTing != null) {
//                return mFragmentTing.hasKtvMiniBar();
//            }
//        }
//        if (mCurrentTabIndex == TAB_FOLLOW) {
//            if (mFragmentFollow != null) {
//                return mFragmentFollow.hasKtvMiniBar();
//            }
//        }
        if (mCurrentTabIndex == TAB_CHANG){
            return true;
        }
        if (mCurrentTabIndex == TAB_MINE) {
            if (mFragmentMine != null) {
                return mFragmentMine.hasKtvMiniBar();
            }
        }
        return false;
    }

    private Object getRealSelectedObject() {
        if (mAdapter != null) {
            return mAdapter.mCurrentPrimaryItem;
        }
        return null;
    }

    public boolean isTingFragmentSelected() {
        return (getRealSelectedObject() == mFragmentTing);
    }

    @Override
    public boolean hasAIMiniBar() {
        if (isTingFragmentSelected()) {
            if (mFragmentTing != null) {
                return mFragmentTing.hasAIMiniBar();
            }
        }
        return false;
    }

    @Override
    public int getTypeMenu() {
        return MenuCard.SLIDINGMODE_LEFT_RIGHT;
    }

    //是否允许MenuCard，拦截touch event
    private boolean mDisallowMenuCardIntercept;

    @Override
    public boolean getDisallowMenuCardIntercept() {
        return mDisallowMenuCardIntercept;
    }
    

    public void changeTabFromOuter(int tab){
        changeTab(tab,false);

        mAdapter.loadTabFragment(tab);
        onMainTabChangedCallBack(tab);
    }

    private void changeTab(int tab, boolean isInit) {
        if (DrLog.DEBUG) DrLog.d("jiese1990-tab", "changeTab1 tab "
                + tab + ", mCurrentTabIndex " + mCurrentTabIndex
                + ", isInit " + isInit
        );

        if (mCurrentTabIndex != tab || isInit) {
            mCurrentTabIndex = tab;
            notifyTabChanged(tab,2,false);
        }
    }

    public static void notifyTabChanged(int tab,int toWho,boolean isClick) {
        EventBus.getDefault().post(new BottomTabChangeEvent().setTab(tab).setToWho(toWho).setClick(isClick));
    }

    private void onMainTabChangedCallBack(int tab) {
        DrLog.d("gehu_frame", "onMainTabChangedCallBack: " + tab +
                "; mFragmentTing = " + mFragmentTing
        );
        long startTime = SystemClock.elapsedRealtime();

        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null) {
                if (fragment instanceof onMainTabChangedListener) {
                    ((onMainTabChangedListener) fragment).onMainTabChanged(tab);
                }
            }
        }
        if (DrLog.DEBUG) DrLog.d("gehu_frame", "onMainTabChangedCallBack time: " + (SystemClock.elapsedRealtime() - startTime));
    }

    private int mScrollState = 0;

    private void onMainScrollStateChangedCallBack(int state) {
        if (DrLog.DEBUG) DrLog.d("gehu_frame", "onMainScrollStateChangedCallBack: " + state);
        long startTime = SystemClock.elapsedRealtime();
        mScrollState = state;

        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null) {
                if (fragment instanceof onMainTabChangedListener) {
                    ((onMainTabChangedListener) fragment).onMainScrollStateChanged(state);
                }
            }
        }
        if (DrLog.DEBUG) DrLog.d("gehu_frame", "onMainScrollStateChangedCallBack time: " + (SystemClock.elapsedRealtime() - startTime));
    }

    private void onMainScrolled(int position, float v) {
        float alphaMine = 255F, alphaTing = 255f, alphaKan = 255f, alphaChang = 255f;
        if (position == 0) {
            if (v <= 0.3) {
                alphaTing = 255f - 850 * v;
                alphaKan = 127.5f + 425 * v;
            } else {
                alphaTing = 0f;
                alphaKan = 255f;
            }
        } else {
            alphaKan = 255f;
        }
        DrLog.i("zkzhou", "onMainScrolled..." + "alphaTing: " + alphaTing + "; alphaKan: " +
                alphaKan + "; position: " + position + "; offset:" + v);
        if (mFragmentMine != null) {
            if (mFragmentMine instanceof onMainTabChangedListener) {
                ((onMainTabChangedListener) mFragmentMine).onMainScrolled(alphaMine, alphaTing, alphaKan, alphaChang, position, v);
            }
        } else {
            mAdapter.setTabAlphaWhenLoading(TAB_MINE, alphaMine);
        }

        if (mFragmentTing != null) {
            if (mFragmentTing instanceof onMainTabChangedListener) {
                ((onMainTabChangedListener) mFragmentTing).onMainScrolled(alphaMine, alphaTing, alphaKan, alphaChang, position, v);
            }
        }else{
            mAdapter.setTabAlphaWhenLoading(TAB_TING, alphaMine);
        }
    }

    class BaseMainFragmentPagerAdapter extends MainFragmentPagerAdapter {

        public BaseMainFragmentPagerAdapter(FragmentManager fm, Context context,int curPosition) {
            super(fm, context,curPosition);
        }

        @Override
        public KGFrameworkFragment getItem(int position) {
            return getSubFragment(position);
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    /**
     * 判断一个tab对应的fragment 是否已经初始化
     *
     * @param tab
     * @return
     */
    private boolean isTabFragmentExist(int tab) {
        AbsFrameworkFragment fragment = null;
        switch (tab) {
            case TAB_MINE:
                fragment = mFragmentMine;
                break;

            case TAB_TING:
                fragment = mFragmentTing;
                break;
        }

        return (fragment != null);
    }

    private synchronized void initTabFragment(int tab) {
        if (DrLog.DEBUG) DrLog.d("gehu_frame", "initTabFragment: " + tab);
        if (mAdapter != null) {
            mAdapter.loadTabFragment(tab);
        }

        //sendLoadMessageDelay();
    }


    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        if (mContainerLayout != null) {
            mContainerLayout.updateSkin();
        }
        for (AbsFrameworkFragment fragment : allFragments) {
            if (fragment != null) {
                fragment.onSkinAllChanged();
            }
        }

        if (mAdapter != null) {
            mAdapter.onSkinAllChanged();
        }
    }

    public int getmCurrentTabIndex() {
        return mCurrentTabIndex;
    }

    @Override
    public int getTab() {
        return mCurrentTabIndex;
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(BottomTabChangeEvent event) {
        if (event == null) {
            return;
        }
        int toWho = event.getToWho();
        if (toWho == 1) {
            int tab = event.getTab();
            if (!event.isClick()) {

            }
            if (tab == TAB_MINE) {
                onMineTabClick(null);
            } else if (tab == TAB_TING) {
                onTingTabClick(null);
            }
            mCurrentTabIndex = tab;
        } else if (toWho == 3) {
            notifyTabChanged(mCurrentTabIndex, 2,false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TingTabEvent event){

        setSelectFragment(TAB_TING);
        
    }
    

    private BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
        }
    };


    public int getStatusBarActionType(){
        return IStatusBarActionType.TYPE_CHANGE_BY_SKIN;
    }
    @Override
    public boolean hasPlayingBar() {
        return true;
    }

    @Override
    public boolean hasMainBottomView() {
        return true;
    }

    @Override
    public void requestDisallowInterceptTouchEvent() {
        if (mCommViewPager != null && mCommViewPager.getParent() != null){
            mCommViewPager.getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    public boolean hasFakeNavigationBar() {
        return false;
    }

    @Override
    public boolean hasKuqunMiniBar() {
        return false;
    }
    

    private MainFragmentCallbacks getMainFragmentCallbacks() {
        Activity activity = getActivity();
        if (activity instanceof MainFragmentCallbacks) {
            return (MainFragmentCallbacks) activity;
        }
        return null;
    }
}