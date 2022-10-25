package com.brins.dreamify.app.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import com.brins.commom.base.AbsFrameworkFragment;
import com.brins.commom.base.MainFragmentContainer;
import com.brins.commom.base.onMainTabChangedListener;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.delegate.DelegateFragment;
import com.brins.commom.swipetap.SwipeViewPage;
import com.brins.commom.utils.CollectionUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.ToastUtil;
import com.brins.dreamify.R;
import com.kugou.page.framework.KGFragmentActivity;
import com.kugou.page.sub.ViewPagerLifecycleManager;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.greenrobot.eventbus.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;



public class TingMainFragment extends DelegateFragment
        implements onMainTabChangedListener, SwipeViewPage.DisallowInterceptCallback {

    public static final String BOTTOM_SPACE = "bottom_space";

    private int mCurrentTab = 0;
    public static int mFirstTabIndex = 0; // 记录第一次显示的时候，是第几个tab
    public static int mParentCurTab = MainFragmentContainer.TAB_TING;

    public static boolean isOnResume, isOnFragmentResume;
    public static boolean hasResumed, hasFragmentResumed;
    public static String mRadioTabName = "";
    public static String mSearchConfig = "";
    private boolean needUpdateTabs;
    private boolean needFavTabGuide;
    private long exposeTime;    // 当前页面曝光时的时间戳
    private boolean isGrayMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ting_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFromXTingMainFragment();
        initEventBus();
        requestTingTab();
        registerReceive();
    }


    /**
     * 请求获取首页tab
     */
    private void requestTingTab() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        if (mReceiver != null) {
            BroadcastUtil.unregisterReceiver(mReceiver);
        }
    }

    private void initEventBus() {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    //****************************广告 start****************************
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    private void registerReceive(){
        IntentFilter filter = new IntentFilter();
        BroadcastUtil.registerReceiver(mReceiver, filter);
    }


    /**
     * 重设导航栏颜色,适配沉浸式
     */
    private void resetImmerse(int position) {

        updateImmerseColor(1f);
        setTopBarBgAlpha(0f);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onMainTabChanged(int tab) {
        mParentCurTab = tab;
    }


    @Override
    public void requestParentDisableScroll(int value, boolean isBeingDragged) {
        super.requestParentDisableScroll(value, isBeingDragged);
    }

    @Override
    public void onMainScrollStateChanged(int state) {

    }

    @Override
    public void onMainScrolled(float alphaMine, float alphaTing, float alphaKan, float alphaChang, int position, float v) {

    }

    //不需要越界滑动,不需要这个回调了  需要的话请实现:SwipeViewPage.DisallowInterceptCallback
    @Override
    public void requestDisallowInterceptTouchEvent() {
        /*if (mViewPager != null && mViewPager.getParent() != null) {
            mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
        }*/
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        isOnFragmentResume = false;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        isOnFragmentResume = true;
        hasFragmentResumed = true;
    }



    @Override
    public void onResume() {
        super.onResume();
        isOnResume = true;
        hasResumed = true;
    }


    @Override
    public void onPause() {
        super.onPause();
        isOnResume = false;
    }

    public boolean isPageResume() {
        return isOnFragmentResume && isOnResume;
    }

    public boolean checkNetwork(boolean toast) {
        if (!SystemUtils.isAvalidNetSetting(getContext())) {
            if (toast) {
                ToastUtil.showToastShort(getContext(), R.string.no_network);
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        resetImmerse(mCurrentTab);
    }

    @Override
    public boolean hasAIMiniBar() {
        return false;
    }


    public interface ViewPagerActiveListener {

        void onParentViewPagerActive(boolean isSameTab);
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }


    /**
     * 更新导航栏颜色
     */
    public void updateImmerseColor(float alpha) {

    }

    /**
     * 更新导航栏背景透明度
     */
    public void setTopBarBgAlpha(float alpha) {

    }

    @Override
    public boolean hasMenu() {
        return false;
    }

}
