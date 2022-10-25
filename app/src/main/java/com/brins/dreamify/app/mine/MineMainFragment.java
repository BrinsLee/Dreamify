package com.brins.dreamify.app.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.brins.commom.base.MainFragmentContainer;
import com.brins.commom.base.onMainTabChangedListener;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.delegate.DelegateFragment;
import com.brins.commom.glide.GlideBlurTransform2;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.swipetap.SwipeViewPage;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.ThreadUtils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;

import com.brins.dreamify.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SquaringDrawable;
import com.bumptech.glide.request.target.Target;
import com.kugou.common.base.ViewPager;
import com.kugou.common.base.uiframe.FragmentStackView;
import com.kugou.page.face.BackgroundLayer;
import com.kugou.page.sub.ViewPagerLifecycleManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;

/**
 * @author zhouzhankun
 * @time 2019-10-09 10:32
 **/

public class MineMainFragment extends DelegateFragment
    implements onMainTabChangedListener, SwipeViewPage.DisallowInterceptCallback {

    private static final String TAG = "MineMainFragment";
    
    public static final int TAB_COUNTS = 4;

    public static final int X_TAB_MUSIC = 0;//音乐
    public static final int X_TAB_ARTIST = 1;//动态 - 》 艺人,调整为第二个tab
    public static final int X_TAB_DYNAMIC = 2;//动态
    public static final int X_TAB_MSG = 3;//消息

    public static int getTabMsg() {
        return X_TAB_MSG;
    }

    public static int getTabDynamic() {
        return X_TAB_DYNAMIC;
    }
    
/*    private XMineMainHeaderLayout mHeaderSwipeTabView;
    private MainFragmentViewPage mViewPager;
    private SwipeDelegate.ViewPageAdapter mAdapter;*/

    private Subscription subscription;


    private boolean isFirstInit = true;
    private boolean isOnResume = false;
    private boolean isFragmentResume = false;

    public int mCurrentParentTab = MainFragmentContainer.TAB_TING;
    private int mCurrentSubTab = X_TAB_MUSIC;

    private int mOnScreenStateChanged = ViewPager.SCROLL_STATE_IDLE;

    private boolean mIsVisibleUser = false;
    public boolean mFirstReportRedDot = true;

    Subscription mJumpSubscription;
    

    public static boolean hasSelectMineTab;

    private boolean needBlurBg;
    private ImageView guessLikeBgMask;
    private ImageView guessLikeBg;
    private ImageView guessLikeBgFront;
    private View guessLikecontentBg;

    private ValueAnimator guessLikeBgAnimator;
    private static final int GUESS_LIKE_BG_BLUR_ALPHA = 90; //与255计算
    private static final int GUESS_LIKE_BG_BLUR_RADIUS = 12;
    private static final int GUESS_LIKE_BG_ANIMATION_DURATION_MS = 500;
    private static final int HANDLER_WHAT_UPDATE_GUESS_LIKE_BG = 1;
    private static final int GUESS_LIKE_BG_CHANGE_INTERVAL_MS = 0;
    private Handler guessLikeBgHandler;
    private Subscription guessLikeBgSubscription;
    private String currentBgUrl = "";
    
    private static final String DEFAULT_HEAD_PIC = "http://imge.kugou.com/commendpic/20210913/20210913114134755894.png";
    // tab子页面停留时长统计
    private Pair<String, Long> pageDurationInfo = null;
    private boolean isFirstResume = true; // 第一次切到看tab也会走onResume,避免两种方式重复上报
    private int guessBgHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mine_main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHandler();
        initViews();
        initObservers();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        
        registerBroadcast();
        updateBgLayoutParams();
    }

    @Override
    public void onResume() {
        super.onResume();
        isOnResume = true;
    }
    

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        isFragmentResume = true;
//        if (checkFragmentByIndex(mCurrentSubTab)) {
//            mFragments[mCurrentSubTab].onFragmentResume();
//        }
/*        if (mHeaderSwipeTabView != null) {
            mHeaderSwipeTabView.setIsMineFragmentResume(true);
            if (mCurrentSubTab != getTabMsg() && mHeaderSwipeTabView.getRedDotLayoutVisibleByPos(getTabMsg())) {
                if (DrLog.DEBUG)
                    DrLog.i("xkr", "traceMsgBadgeExpose onFragmentResume mCurrentSubTab " + mCurrentSubTab);
                mHeaderSwipeTabView.traceMsgBadgeExpose();
            }
        }*/

        //mHeaderSwipeTabView.fragmentResume();
    }

    @Override
    public boolean hasKtvMiniBar() {
        return super.hasKtvMiniBar();
    }
    

    private void initViews() {

        /*mHeaderSwipeTabView = (XMineMainHeaderLayout) findViewById(R.id.swipe_tabview);
        mViewPager = (MainFragmentViewPage) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(TAB_COUNTS - 1);

        mAdapter = new SwipeDelegate.ViewPageAdapter(getActivity(), mFragmentManager);
        setChildFragmentLifecycleManager(new ViewPagerLifecycleManager(this, mViewPager, mAdapter, 0));
        mAdapter.setLazyLoadEnable(true);
        mAdapter.setEnableRemoveItem(true);
        mAdapter.setFragments(new ArrayList<>(Arrays.asList(mFragments)),new ArrayList<>(Arrays.asList(mTabTitles)), mCurrentSubTab);
        mViewPager.setAdapter(mAdapter);
        mViewPager.registerSwipeCallback(new MainFragmentViewPage.SwipeCallback() {
            @Override
            public boolean canLeftSwipe() {
                return mCurrentSubTab > 0;
            }

            @Override
            public boolean canRightSwipe() {
                return mCurrentSubTab < TAB_COUNTS - 1;
            }

            @Override
            public void onViewPagerTouch() {

            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mHeaderSwipeTabView.updateIndicatorByCoordinate(position, positionOffset, positionOffsetPixels);
                fixFragmentOnResume(position);
            }

            @Override
            public void onPageSelected(int position, boolean smoothScroll) {
                // 同步子tab的生命周期

                for (int i = 0; i < mFragments.length; i++) {
                    if (checkFragmentByIndex(i) && mFragments[i] instanceof OnMainTabChangedListener) {
                        ((OnMainTabChangedListener) mFragments[i]).onSubTabChanged(position);
                    }
                }

                if (checkFragmentByIndex(mCurrentSubTab)) {
                    if (isCurDynamicPage()) {
                        getDynamicFragmentCycleMonitor().onPause();
                    }
                    mFragments[mCurrentSubTab].onFragmentPause();
                }
                mCurrentSubTab = position;
                if (checkFragmentByIndex(mCurrentSubTab)) {
                    if (isCurDynamicPage()) {
                        getDynamicFragmentCycleMonitor().onResume();
                    }
                }

                if (position == MineMainFragment.getTabDynamic() && smoothScroll) {
                    reportEnterDynamicTabHasRedPoint(position, smoothScroll);
                }

                if (position == MineMainFragment.getTabMsg()) {
                    boolean isRedPoint = mHeaderSwipeTabView.getRedDotLayoutVisibleByPos(position);
                    if (checkFragmentByIndex(getTabMsg())) {
                        ((OnMainTabChangedListener) mFragments[getTabMsg()]).onRedPointStatus(isRedPoint);
                    }
                }

                handleRedDot(position);

                mHeaderSwipeTabView.setCurrentItem(position);

                checkShowSecondaryTabIfNeeded();
                IPendantManager iPendantManager = getIPendantManager();
                if (iPendantManager != null) {
                    iPendantManager.setVisibility(position == MineMainFragment.X_TAB_MUSIC);
                }
                reportSubTabPageExposure();
                reportPageDurationOnSubTabChanged();
            }

            @Override
            public void onPageSelectedAfterAnimation(int position) {
                mAdapter.setCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    checkShowSecondaryTabIfNeeded();
                }
            }
        });
*/
        /*mHeaderSwipeTabView.setOnTabSelectedListener(new SwipeTabView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                boolean isSameTab = position == mCurrentSubTab;
                if (position != mCurrentSubTab) {
                    mHeaderSwipeTabView.setSwipeTabViewTag("isSelectedByUser");
                }else {
                    //消息和音乐tab，实现了onBottomTabClickAgain 其他的自己监听了EventBus
                    if (position == X_TAB_MUSIC || position == X_TAB_MSG) {
                        if (mCurrentParentTab < mFragments.length) {
                            AbsFrameworkFragment currentFragment = mFragments[mCurrentSubTab];
                            if (currentFragment instanceof OnBottomTabClickListener) {
                                ((OnBottomTabClickListener) currentFragment).onBottomTabClickAgain();
                            }
                        }
                    } else {
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.PAGE_MINE_TAB));
                    }

                }



                mAdapter.setCurrentPosition(position);

                if (position == MineMainFragment.getTabDynamic()) {
                    reportEnterDynamicTabHasRedPoint(position, false);
                }
                mViewPager.setCurrentItem(position, false);
                if (mFragments == null) {
                    return;
                }
                for (AbsFrameworkFragment mFragment : mFragments) {
                    if (mFragment != null && mFragment
                            .isAlive() && mFragment instanceof ViewPagerActiveListener) {
                        ((ViewPagerActiveListener) mFragment).onParentViewPagerActive(isSameTab);
                    }
                }
                if (position != getTabMsg()) {
                    EventBus.getDefault().post(new MsgCenterSwipeEvent());
                }
            }
        });

        mHeaderSwipeTabView.setOperateClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubClick();
            }
        });

        mHeaderSwipeTabView.setSearchClickListener(view -> {
            String[] desc = getCurrentTabDesc();
            if (getCurrentSubTab() >= 0) {
                Bundle bundle = new Bundle();
                bundle.putString(SearchMainFragment.SEARCH_ENTRANCE, desc[1]);
                bundle.putBoolean(SearchMainFragment.KEY_IS_RUN_SEARCH_ANIMATION, true);
                KGRouter.getInstance()
                        .build(PageIds.SEARCH_MAIN)
                        .withBundle(bundle)
                        .animation(false)
                        .navigateFragment();
                traceSearchClick(desc[0]);
            }
        });

        mHeaderSwipeTabView.setMsgSettingClickListener(view -> {
            RecentWeekSwitchManager manager = new RecentWeekSwitchManager(getContext());
            manager.enterMsgSettingFragment();
        });

        mHeaderSwipeTabView.setMsgCleanClickListener(view -> {
            EventBus.getDefault().post(new HeadViewCleanAllEvent());
        });

        mHeaderSwipeTabView.setIdentifyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckChinaIpDialogUtil.canUseNetService()) {
                    CheckChinaIpDialogUtil.showCheckChinaIpDialog(ForbiddenDelegate.TYPE_MUSIC_IDENTIFY);
                    return;
                }
                String[] desc = getCurrentTabDesc();
                Hound.trace(new ClickTask(KGApplication.getContext(), Function.CLICK_MUSIC_HUNTER_ENTRY).setFo(desc[0]));
                Bundle bundle = new Bundle();
                bundle.putString(NewAudioIdentifyFragment.FROM_SOURCE, desc[1]);
                NavigationMoreUtils.startMusicHunterActivity(MineMainFragment.this, bundle);
            }
        });

        mHeaderSwipeTabView.setCurrentItem(0);
        mViewPager.setCurrentItem(0, false);

        MusicZoneUtils.updateMusicZoneDynamic(true);*/

    }

    private void initObservers() {
        /*DynamicRedDotManager.INSTANCE.getDynamicTabRedDot().observe(this, show -> {
            //如果当前在动态tab，则切换到其他tab后显示红点
            boolean needShow = show != null && show && mCurrentSubTab != X_TAB_DYNAMIC;
            mHeaderSwipeTabView.updateRedDotLayout(X_TAB_DYNAMIC, needShow, 0);
            if (needShow) {
                Hound.trace(new AbsFunctionTask(KGApplication.getContext(), Function2.SINGER_RED_DOT_SHOW).setSvar1("动态"));
            }
        });
        DynamicRedDotManager.INSTANCE.getArtistTabRedDot().observe(this, show -> {
            //如果当前在艺人tab，则切换到其他tab后显示红点
            boolean needShow = show != null && show && mCurrentSubTab != X_TAB_ARTIST;
            mHeaderSwipeTabView.updateRedDotLayout(X_TAB_ARTIST, needShow, 0);
            if (needShow) {
                Hound.trace(new AbsFunctionTask(KGApplication.getContext(), Function2.SINGER_RED_DOT_SHOW).setSvar1("艺人"));
            }
        });*/
    }


    @Override
    public void onScreenStateChanged(int state) {
        super.onScreenStateChanged(state);
        mOnScreenStateChanged = state;
        
    }

    private boolean checkShowConditions() {
        /*if (!getUserVisibleHint() || mCurrentParentTab != MainFragmentContainer.TAB_MINE || !isOnResume || !isFragmentResume) {
            return false;
        }
        if (!CommonEnvManager.isLogin()) {
            return false;
        }
        if (getDelegate().isMenuOpen()) {
            return false;
        }
        if (SettingPrefs.getInstance().getMineListenSongTotalGuideHasShow()) {
            return false;
        }

        if (mViewPager.getScrollState() != ViewPager.SCROLL_STATE_IDLE) {
            return false;
        }

        if (mCurrentSubTab != X_TAB_MUSIC) {
            return false;
        }

        if (mOnScreenStateChanged != ViewPager.SCROLL_STATE_IDLE) {
            return false;
        }*/

        return true;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleUser = isVisibleToUser;

        //EventBus.getDefault().post(new MineFragmentVisibleEvent(isVisibleToUser));

    }

/*    private void fixFragmentOnResume(int position) {
        //第一次加载子Fragment父Fragment走onFragmentResume时子Fragment还未初始化完成
        if (isFirstInit) {
            isFirstInit = false;
            mCurrentSubTab = position;
            if (checkFragmentByIndex(mCurrentSubTab)) {
                if (mFragments[mCurrentSubTab] instanceof OnMainTabChangedListener) {
                    ((OnMainTabChangedListener) mFragments[mCurrentSubTab]).onParentTabChanged(mCurrentParentTab);
                }
            }
        }
    }*/
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
        }

        return super.onKeyDown(keyCode, event);
    }

    
    private boolean isCurDynamicPage() {
        return mCurrentSubTab == getTabDynamic();
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();

        isFragmentResume = false;
        
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnResume = false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //从其他tab切换到我tab
    @Override
    public void onMainTabChanged(int tab) {
        mCurrentParentTab = tab;
    }

    @Override
    public void onMainScrolled(float alphaMine, float alphaTing, float alphaKan, float alphaChang, int position, float v) {

    }

    @Override
    public void onMainScrollStateChanged(int state) {

    }

    @Override
    public void requestDisallowInterceptTouchEvent() {
        
    }

    private boolean isNeedBlurBg() {
        return //!TextUtils.isEmpty(CommonSettingPrefs.getInstance().getUserImageUrl()) &&
                !isMiniSDK() && (SkinProfileUtil.isDefaultLocalSimpleSkin() || SkinProfileUtil.isSolidSkin());
    }

    private void setBlur(String url) {
        if (guessLikeBg == null || guessLikeBgFront == null || guessLikeBgMask == null) {
            return;
        }
        needBlurBg = isNeedBlurBg();
        if (needBlurBg) {
            guessLikeBg.setVisibility(View.VISIBLE);
            guessLikeBgFront.setVisibility(View.VISIBLE);
            guessLikeBgMask.setVisibility(View.VISIBLE);
            updateGuessLikeBg(url);
        } else {
            guessLikeBg.setVisibility(View.INVISIBLE);
            guessLikeBgFront.setVisibility(View.INVISIBLE);
            guessLikeBgMask.setVisibility(View.GONE);
        }
        updateGuessLikeContentBg();
        getBackgroundLayer().setTitleBackgroundTransparent();
    }

    @Override
    protected void onSkinAllChanged() {
        super.onSkinAllChanged();
        updateBg();
        
    }

    private void updateBg() {
        needBlurBg = isNeedBlurBg();
        /*if (CommonEnvManager.isLogin()) {
            setBlur(currentBgUrl);
        } else {
            if (getView() != null) {
                initGuessLikeBg(getView(), false);
            }
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mJumpSubscription != null) {
            mJumpSubscription.unsubscribe();
        }
//        guessLikeBgHandler.removeMessages(HANDLER_WHAT_UPDATE_GUESS_LIKE_BG);

        if (guessLikeBgAnimator != null) {
            guessLikeBgAnimator.cancel();
            guessLikeBgAnimator.removeAllUpdateListeners();
            guessLikeBgAnimator.removeAllListeners();
            guessLikeBgAnimator = null;
        }

        if (guessLikeBgSubscription != null) {
            guessLikeBgSubscription.unsubscribe();
        }
        unregisterBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    

    public int getCurrentSubTab() {
        return mCurrentSubTab;
    }

    public interface ViewPagerActiveListener {

        void onParentViewPagerActive(boolean isSameTab);
    }


    //

    // Broadcast
    private BroadcastReceiver broadcastReceiver = null;

    private void registerBroadcast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    
                }
            };
        }

        IntentFilter filter = new IntentFilter();
        BroadcastUtil.registerMultiReceiver(broadcastReceiver, filter);
    }

    private void unregisterBroadcast() {
        if (broadcastReceiver != null) {
            BroadcastUtil.unregisterMultiReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onInitBackgroundLayer(BackgroundLayer backgroundLayer) {
        super.onInitBackgroundLayer(backgroundLayer);
        needBlurBg = isNeedBlurBg();
        if (backgroundLayer != null) {
            backgroundLayer.setTitleBackgroundTransparent();
            backgroundLayer.getTitleBackgroundLayout().setVisibility(View.GONE);
            backgroundLayer.setContentBackgroundDrawable(null);
            //backgroundLayer.setContentBackground(R.layout.kg_main_mine_background);
        }
    }

    private void updateGuessLikeBg(String url) {
        if (guessLikeBg == null) return;
        if (guessLikeBg.getVisibility() != View.VISIBLE || guessLikeBgFront.getVisibility() != View.VISIBLE) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            updateBgFail();
            return;
        }
        url = url.replace("{size}", "480");
        Glide.with(this).load(url)
                .bitmapTransform(new GlideBlurTransform2(getContext(), GUESS_LIKE_BG_BLUR_RADIUS, 0)) // 这里用java做高斯模糊，用RenderScript效果有点不一致
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        updateBgFail();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (isAlive() && resource != null) {
                            Bitmap bitmap = null;
                            if (resource instanceof GlideBitmapDrawable) {
                                bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                            } else if (resource instanceof SquaringDrawable) {
                                SquaringDrawable squaringDrawable = (SquaringDrawable) resource;
                                bitmap = ((GlideBitmapDrawable) squaringDrawable.getCurrent())
                                        .getBitmap();
                            }
                            if (bitmap != null) {
                                loadGuessLikeBgCropBitmap(model, bitmap);
                            } else {
                                updateBgFail();
                            }
                        }else {
                            updateBgFail();
                        }
                        // 自己来加载
                        return true;
                    }
                }).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    private void loadGuessLikeBgCropBitmap(String url, Bitmap bitmap) {
        /*if (TextUtils.isEmpty(url)) {
            updateBgFail();
            return;
        }
        if (guessLikeBgSubscription != null) {
            guessLikeBgSubscription.unsubscribe();
        }
        Bitmap cache = urlBitmapCache.get(url);
        if (cache != null && !cache.isRecycled()) {
            loadGuessLikeBgCropBitmapWithAnimation(url, cache);
            return;
        }
        if (bitmap != null) {
            guessLikeBgSubscription = Single.just(new Pair<>(url, bitmap))
                    .map(new Func1<Pair<String, Bitmap>, Pair<String, Bitmap>>() {
                        @Override
                        public Pair<String, Bitmap> call(Pair<String, Bitmap> pair) {
                            Bitmap bitmap = pair.second;
                            int targetWidth = SystemUtil.getDisplayWidth(getContext());
                            int targetHeight = guessLikeBg.getLayoutParams().height;
                            Bitmap scaleBitmap = BitmapUtil.zoomImage(bitmap, targetWidth, targetHeight);

                            *//*int cropHeight = guessLikeBg.getLayoutParams().height;
                            int startY =0;
                            Bitmap guessLikeBg = null;
                            try {
                                guessLikeBg = Bitmap
                                        .createBitmap(scaleBitmap, 0, startY, scaleBitmap
                                                .getWidth(), startY + cropHeight);
                            } catch (OutOfMemoryError e) {
                                DrLog.uploadException(e);
                            }*//*
                            return new Pair<>(pair.first, scaleBitmap);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleSubscriber<Pair<String, Bitmap>>() {
                        @Override
                        public void onSuccess(Pair<String, Bitmap> pair) {
                            if (!TextUtils.isEmpty(pair.first) && pair.second != null) {
                                loadGuessLikeBgCropBitmapWithAnimation(pair.first, pair.second);
                                urlBitmapCache.put(pair.first, pair.second);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            updateBgFail();
                        }
                    });
        }*/
    }

    private void loadGuessLikeBgCropBitmapWithAnimation(String url, final Bitmap bitmap) {
        if (TextUtils.equals(currentBgUrl, url)) {
            updateBgFail();
            return;
        }
        currentBgUrl = url;
        if (guessLikeBgAnimator != null) {
            guessLikeBgAnimator.cancel();
            guessLikeBgAnimator.removeAllUpdateListeners();
            guessLikeBgAnimator.removeAllListeners();
            guessLikeBgAnimator = null;
        }
        guessLikeBgAnimator = ValueAnimator.ofFloat(0, 1);
        guessLikeBgAnimator.setDuration(GUESS_LIKE_BG_ANIMATION_DURATION_MS);
        guessLikeBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                guessLikeBgFront.setAlpha(alpha);
                guessLikeBg.setAlpha(1 - alpha);
            }
        });
        guessLikeBgAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                guessLikeBgFront.setAlpha(0f);
                guessLikeBg.setAlpha(1f);
                guessLikeBg.setImageBitmap(bitmap);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                guessLikeBgFront.setAlpha(0f);
                guessLikeBg.setAlpha(1f);
                guessLikeBgFront.setImageBitmap(bitmap);
            }
        });
        guessLikeBgAnimator.start();
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        guessLikeBgHandler = new StackTraceHandler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_WHAT_UPDATE_GUESS_LIKE_BG && msg.obj instanceof String) {
                    updateGuessLikeBg((String) msg.obj);
                }
            }
        };
    }

    public void scheduleUpdateGuessLikeBg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        guessLikeBgHandler.removeMessages(HANDLER_WHAT_UPDATE_GUESS_LIKE_BG);
        Message message = Message.obtain();
        message.what = HANDLER_WHAT_UPDATE_GUESS_LIKE_BG;
        message.obj = url;
        guessLikeBgHandler.sendMessageDelayed(message, GUESS_LIKE_BG_CHANGE_INTERVAL_MS);
    }

    @Override
    public boolean useV2Interface() {
        return true;
    }


    @Override
    public boolean hasMenu() {
        return false;
    }

    private void updateGuessLikeContentBg() {
        BackgroundLayer backgroundLayer = getBackgroundLayer();
        if (backgroundLayer != null) {
            backgroundLayer.setContentBackgroundDrawable(null);
        }
        if (guessLikecontentBg != null) {
            if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin()) {
                guessLikecontentBg.setBackgroundColor(SkinProfileUtil.getLocalMainBgColor());
            } else {
                guessLikecontentBg.setBackground(null);
            }
        }
    }

    private boolean isMiniSDK() {
        //7.0以下
        return Build.VERSION.SDK_INT <= 24;
    }

    private void updateBgFail(){
        ThreadUtils.runOnUIThread(() -> {
            if (guessLikeBgMask != null) guessLikeBgMask.setVisibility(View.GONE);
            if (guessLikeBg != null) guessLikeBg.setVisibility(View.GONE);
            if (guessLikeBgFront != null) guessLikeBgFront.setVisibility(View.GONE);
        });
    }

    private void updateBgLayoutParams(){
        if (guessLikeBg == null || guessLikeBgFront == null || guessLikeBgMask == null) {
            return;
        }
        RelativeLayout.LayoutParams bgParams = (RelativeLayout.LayoutParams) guessLikeBg.getLayoutParams();
        bgParams.height = guessBgHeight;
        guessLikeBg.requestLayout();

        RelativeLayout.LayoutParams bgFrontParams = (RelativeLayout.LayoutParams) guessLikeBgFront.getLayoutParams();
        bgFrontParams.height = guessBgHeight;
        guessLikeBgFront.requestLayout();

        RelativeLayout.LayoutParams bgmaskParams = (RelativeLayout.LayoutParams) guessLikeBgMask.getLayoutParams();
        bgmaskParams.height = guessBgHeight;
        guessLikeBgMask.requestLayout();
    }
}
