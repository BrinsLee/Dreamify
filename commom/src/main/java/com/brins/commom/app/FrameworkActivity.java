
package com.brins.commom.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.brins.arouter_annotation.ARouter;
import com.brins.arouter_annotation.Parameter;
import com.brins.arouter_api.ParameterManager;
import com.brins.commom.R;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.base.AbsFrameworkFragment;
import com.brins.commom.base.FrameworkActivityInterface;
import com.brins.commom.base.FrameworkUtil;
import com.brins.commom.base.MainFragmentCallbacks;
import com.brins.commom.base.ViewPagerFrameworkDelegate;
import com.brins.commom.base.service.MainFragmentService;
import com.brins.commom.base.service.MineFragmentService;
import com.brins.commom.base.ui.hostframe.IFrameFace;
import com.brins.commom.boot.BootMagicBox;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.playingbar.BottomBackgroundView;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.res.NResources;
import com.brins.commom.utils.KGBitmapUtil;
import com.brins.commom.utils.KGPlayingBarUtil;
import com.brins.commom.utils.SystemBarUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.utils.stacktrace.StackTraceHandler;
import com.kugou.android.app.boot.FrameworkContentView;
import com.kugou.common.base.MaskView;
import com.kugou.common.base.MenuCard;
import com.kugou.common.base.uiframe.FragmentViewBase;
import com.kugou.common.widget.base.NavigationBarCompat;
import com.kugou.common.widget.base.WaterFallCompat;

import static com.brins.commom.constant.PathKt.APP_MAIN_FRAGMENT;
import static com.brins.commom.constant.PathKt.APP_MINE_FRAGMENT;

public abstract class FrameworkActivity extends AbsBaseActivity
    implements IFrameFace, ViewPagerFrameworkDelegate.DelegateListener,
        FrameworkContentView.FirstFaceListener, BootMagicBox.PlayBarInflatedListener,
    FrameworkActivityInterface,
    MainFragmentCallbacks {

    /**
     * 外部启动fragment内页界面需要带fragment类名全称（带包名）
     */
    public static final String KEY_FRAGMENT_FULL_CLASS_NAME = "key_fragment_class_full_name";

    /**
     * 外部启动fragment内页界面需要带fragment的bundle参数
     */
    public static final String KEY_FRAGMENT_ARGS = "key_fragment_args";

    public static long IDLE_DELAY = 4000L;

    private FrameworkContentView mContentView = null;

    public MineFragmentService mMineFragmentService;

    public MainFragmentService mMainFragmentService;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NavigationBarCompat.adjustWindowNavigationBar(getWindow());
        WaterFallCompat.updateWaterFallInsets(getWindow());
    }

    public FrameworkContentView getContentView() {
        if (mContentView == null) {
            mContentView = new FrameworkContentView(this);
            mContentView.addFirstFaceListener(this);
            mContentView.addFirstFaceListener(BootMagicBox.get());
            mContentView.getMenuCard().setSlidingEnabled(false);
            if (mContentView.getMenuCard().getContentClickView() != null) {
                mContentView.getMenuCard().getContentClickView().setContentDescription("关闭侧边栏");
                mContentView.getMenuCard().getContentClickView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            updateContentShadow();
        }
        return mContentView;
    }

    protected MenuCard getMenuCard() {
        return getContentView().getMenuCard();
    }

    public ViewGroup getAdditionalContainer() {
        return (ViewGroup) getMenuCard().getAdditionalContent();
    }


    @Override
    public void onPlayBarPreInflated(View playBar, View bottomBar, View bgView) {
        if (playBar == null || bottomBar == null || bgView == null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout container = (FrameLayout) getAdditionalContainer();
                if (container.getChildCount() > 0 ||
                        playBar.getParent() != null ||
                        bottomBar.getParent() != null ||
                        bgView.getParent() != null) {
                    return;
                }
                // 背景
                FrameLayout.LayoutParams bgLp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, 0);
                bgLp.gravity = Gravity.BOTTOM;
                container.addView(bgView, bgLp);
                if (bgView instanceof BottomBackgroundView) {
                    ((BottomBackgroundView) bgView).resetBackground();
                }
                // 底栏
                LinearLayout bottomLayout = new LinearLayout(mContext);
                bottomLayout.setOrientation(LinearLayout.VERTICAL);
                bottomLayout.addView(playBar);
                bottomLayout.addView(bottomBar);
                int height = KGPlayingBarUtil.getMainPlayingbarHeight() +
                        KGPlayingBarUtil.getmBottomTabHeight();
                if (NavigationBarCompat.isTranslucentNavigationBar()) {
                    height += NavigationBarCompat.windowBottomInset();
                }
                FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, height);
                barLp.gravity = Gravity.BOTTOM;
                container.addView(bottomLayout, barLp);
            }
        });
    }

    @Override
    public void onFirstFace() {
        if (DrLog.DEBUG) DrLog.i("burone-", "onFirstFace ------> yeah !!!");

        final Handler handler = new StackTraceHandler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initAdditionalContent();
                onAdditionContentPrepared();
                onPostAdditionContentInit();
            }
        }, FrameworkUtil.isMainCurrentStack()?0:FragmentViewBase.ANIMATION_DURATION);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onIdle();
            }
        }, IDLE_DELAY);
    }

    protected void onFirstFaceAsync() {
        addServiceListener();
    }

    protected void onIdle() {

    }

    protected void onIdleAsync() {
    }


    @CallSuper
    protected void initAdditionalContent() {
        if (getAdditionalContainer().getChildCount() > 0) {
            getAdditionalContainer().removeAllViews();
        }
    }

    protected void onAdditionContentPrepared() {
    }

    private void addServiceListener() {
        /*ServiceManager.runOnceRemoteAttach(new Runnable() {
            @Override
            public void run() {
                onRemoteAttached();
            }
        });*/
    }

    protected void onRemoteAttached() {

    }
    private View menuCard;

    private ViewPagerFrameworkDelegate mDelegate;

    @Override
    public ViewPagerFrameworkDelegate getDelegate() {
        return mDelegate;
    }

    @Override
    protected FragmentContainer getContainer() {
        return mDelegate.getContainer();
    }

    public static class CycleState {
        public static final int CREATE = 0x00;
        public static final int START =  0x01;
        public static final int RESUME = 0x02;
        public static final int PAUSE =  0x03;
        public static final int STOP =   0x04;
        public static final int DESTROY = 0x05;
    }

    private int mCycleState = CycleState.CREATE;

    public int currentCycleState() {
        return mCycleState;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        BootMagicBox.get().setPlayBarInflatedListener(this);
        BootMagicBox.get().attachGodActivityContext(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDelegate = new ViewPagerFrameworkDelegate(this, this);
        super.onCreate(savedInstanceState);
        mCycleState = CycleState.CREATE;
        final Bundle state = savedInstanceState;

        /**
         * BootMagicBox中会使用新的Inflater预解析bar条布局，这里将该Inflater
         * 的SkinFactory置于SkinEngine的管理中，以实现换肤
         */
        if (getSkinEngine() != null) {
            getSkinEngine().addCollateralFactory(BootMagicBox.get()
                    .getLayoutInflater(this).getSkinFactory());
        }

        setTheme(R.style.Kugou_Theme_KGActivityRuntime);

        setContentView(getContentView());
        /**
         * 一个神奇的现象：Android7.0上，若已登陆账号，则退出、断网并重启kugou，
         * 会发现界面不刷新，侧边栏和Bar条无法显示（BugID = 75935）。疑与沉浸式
         * 是否设置有关。夜深要睡觉，具体原因不察。添加以下代码可解决。
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SystemBarUtil.setSystemUiVisibility(getWindow().getDecorView(), View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        mDelegate.onCreate(getMenuCard(), savedInstanceState);
    }

    protected void onCreateAsync(Bundle state) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCycleState = CycleState.START;

        if (!BootMagicBox.get().isBootCompleted()) {
            onFirstStart();
        } else {
            onNormalStart();
        }
    }

    protected void onFirstStart() {

    }

    protected void onNormalStart() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCycleState = CycleState.RESUME;
        if (!BootMagicBox.get().isBootCompleted()) {
            onFirstResume();
        } else {
            onNormalResume();
        }

    }

    @Override
    protected boolean careBootSpeed() {
        return true;
    }

    protected void onResumeAsync() {
        doOnResume();
    }

    protected void onFirstResume() {

    }

    protected void onNormalResume() {
        //mDelegate.onNormalResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCycleState = CycleState.PAUSE;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCycleState = CycleState.STOP;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCycleState = CycleState.DESTROY;
    }

    public Pair<Bitmap, String> processScreenShotBitmap(String imagePath) {
        AbsFrameworkFragment fragment = null;
        if (mDelegate != null) {
            fragment = mDelegate.getCurrentFragment();
        }
        if (fragment != null) {
            Pair<Bitmap, String> pair = null;
            try {
                pair = fragment.processScreenShotBitmap(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
            }
            return pair != null ? pair : new Pair<>(KGBitmapUtil.decodeFile(imagePath), imagePath);
        }
        return new Pair<>(KGBitmapUtil.decodeFile(imagePath), imagePath);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            outState.clear();
            mDelegate.onSaveInstanceState(outState);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String className = intent.getStringExtra(KEY_FRAGMENT_FULL_CLASS_NAME);
            if (className != null) {

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mDelegate.isInit()) {
            if (mDelegate.onKeyDown(keyCode, event))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mDelegate.onKeyUp(keyCode, event))
            return true;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (mDelegate.onKeyLongPress(keyCode, event))
            return true;
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        if (mDelegate.onKeyMultiple(keyCode, count, event))
            return true;
        return super.onKeyMultiple(keyCode, count, event);
    }

    @Override
    public void onBackPressed() {
        // do nothing, just return.
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDelegate.getWhichFragment()==0){
            hideMenu(true);
        }
    }


    protected void onPostAdditionContentInit() {

    }



    //----------- com.kugou.common.base.ViewPagerFrameworkDelegate.DelegateListener --------------//


    @Override
    public AbsFrameworkFragment onCreateMainFragmentMine() {
        AbsFrameworkFragment f = mMineFragmentService.provideMineFragment();
        f.setActivity(this);
        return f;

    }

    @Override
    public AbsFrameworkFragment onCreateMainFragmentTing() {
        AbsFrameworkFragment tingTabFragment = mMainFragmentService.provideMainFragment();
        tingTabFragment.setActivity(this);
        return tingTabFragment;
    }


    @Override
    public void onKeyBackSlideCallback() {

    }

    @Override
    public void onNewBundle(Bundle args) {

    }

    @Override
    public void onFragmentFirstStart() {

    }

    @Override
    public void onSlideToLeftCallback() {

    }

    @Override
    public void onSlideToRightCallback() {

    }

    @Override
    public void onPlayerSlideCallback(boolean isFMPlayer, boolean isShowing) {

    }

    public void showPlayerFragment(boolean anim, boolean isShoudShowFMPlayerFragment, Bundle bundle) {
        if (isShoudShowFMPlayerFragment) {
            mDelegate.showPlayerFragment(anim, bundle);
        } else {
            mDelegate.showPlayerFragment(anim, isShoudShowFMPlayerFragment, bundle);
        }
    }

    public boolean isPlayerFragmentShowing() {
        return mDelegate.isPlayerFragmentShowing();
    }

    public boolean isPlayerFragmentScrolling() {
        return mDelegate.isPlayerFragmentScrolling();
    }

    public void showMenu(boolean anim) {
        mDelegate.showRightMenu(anim);
    }

    public void hideMenu(boolean anim) {
        mDelegate.hideMenu(anim);
    }

    public boolean isMenuOpen() {
        return mDelegate.isMenuOpen();
    }

    public boolean isMenuScrolling() {
        return mDelegate.isMenuScrolling();
    }

    public void showMainFragment() {
        mDelegate.showMainFragment();
    }

    public AbsFrameworkFragment getCurrentFragment() {
        return mDelegate.getCurrentFragment();
    }

    public AbsFrameworkFragment getCurrentMenuFragment() {
        return mDelegate.getCurrentMenuFragment();
    }

    public void setShowFMPlayerFragment(boolean showFMPlayerFragment) {
        mDelegate.setShowFMPlayerFragment(showFMPlayerFragment);
    }

    public void startFragment(AbsFrameworkFragment target, Class<? extends Fragment> cls,
            Bundle args, boolean anim, boolean replaceMode, boolean clearTop,boolean isCloseDialog) {
        /*if(isCloseDialog){
            DialogManager.getSingleton().hideAllDialogs();
        }*/
        mDelegate.startFragment(target, cls, args, anim, replaceMode, clearTop);
    }

    public void startFragment(AbsFrameworkFragment target, Class<? extends Fragment> cls,
                              Bundle args, boolean anim, boolean replaceMode, boolean clearTop) {
        startFragment(target,cls,args,anim,replaceMode,clearTop,true);
    }

    private boolean isClassExist(String className) {
        boolean exist = false;
        try {
            Class.forName(className);
            exist = true;
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    public void updateContentShadow() {
        FrameworkContentView contentView = getContentView();
        if (contentView == null || contentView.getMenuCard() == null) {
            return;
        }
        if (!(contentView.getMenuCard().getContentShadow() instanceof MaskView)) {
            return;
        }
        MaskView maskView = (MaskView) contentView.getMenuCard().getContentShadow();
        int radius = SystemUtils.dip2px(20f);
        if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin()) {
            maskView.setMaskColor(Color.parseColor("#266277C0"), radius);
        } else {
            maskView.setMaskColor(Color.TRANSPARENT, radius);
        }
    }
}
