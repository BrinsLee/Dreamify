package com.brins.commom.base.bar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import com.brins.commom.R;
import com.brins.commom.base.MainFragmentContainer;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.event.BottomTabChangeEvent;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.ClickWatcher;
import com.brins.commom.utils.KGPlayingBarUtil;
import com.brins.commom.utils.SystemUtils;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import com.kugou.common.widget.base.NavigationBarCompat;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author zhouzhankun
 * @time 2019-06-26 17:51
 **/

public class BottomTabView extends RelativeLayout implements ISkinViewUpdate {

    private static final String TAG = "BottomTabView";

    public static final int TING = 0;
    public static final int KAN = 1;
    public static final int CHANG = 2;
    public static final int MINE = 3;
    public static final int VIDEO = 4;

    //ignore
//    public static final int FOLLOW = 4;
//    public static final int MSG = 5;

    private int mCurrentTab;

    private LinearLayout tabLayout, tabSpaceLayout;
    private View lineView;
//    private View mShadowView;

    private Context context;

    private BottomTabItemView mMine, mTing;
    private Space mMidSpace;

    private boolean isPlayRingInNormal = true;

    private HashMap<Integer, String> mTabContentMap = new HashMap<>();

    private BottomTabBroadCast mBottomTabBroadCast;

    private boolean isOnResume = false;
    private int mBaseHeight;
    private int mLineHeight;
    private int mTabHeight;

    private int mKanIndex = 1;
    private int mChangIndex = 2;
    private MediaBottomTabHelperController mMediaBottomTabHelperController;

    public BottomTabView(Context context) {
        this(context, null);
    }

    public BottomTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    /**
     * 必须调用这个方法才能初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        initHeight();
        initTabViews(context);
        configViewIdForAutoTest();
        mTabContentMap.put(KAN, "直播");
        mTabContentMap.put(CHANG, "K歌");
//        mTabContentMap.put(FOLLOW,"动态");
        mTabContentMap.put(MINE, "我首页");
        addViews();
        initBroadCast();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new BottomTabChangeEvent().setTab(TING).setToWho(3).setClick(false));

    }

    /**
     * 添加ID只为自动化测试
     */
    private void configViewIdForAutoTest() {
        mMine.setId(R.id.comm_main_top_mine);
        mTing.setId(R.id.comm_main_top_ting);
    }

    private void initHeight() {
        mLineHeight = 1;
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.common_main_bottom_bar_debug_height);
        mBaseHeight = mTabHeight;
        if (NavigationBarCompat.isTranslucentNavigationBar()) {
            mBaseHeight += NavigationBarCompat.windowBottomInset();
        }
        KGPlayingBarUtil.setmBottomTabHeight(mBaseHeight);
    }

    public boolean isPlayRingInNormal() {
        return this.isPlayRingInNormal;
    }


    public void setIsOnResume(boolean isOnResume) {
        this.isOnResume = isOnResume;
    }

    public void resetTotalHeight() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
        if (lp == null) {
            lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mBaseHeight);
        }
        if (SkinProfileUtil.isOnlineSkin()) {
            int onlineSpace = getResources().getDimensionPixelSize(R.dimen.common_main_bottom_bar_online_skin_space);
            lp.height = mBaseHeight + onlineSpace;
            lp.topMargin = -onlineSpace;
        } else {
            lp.height = mBaseHeight;
            lp.topMargin = 0;
        }
        setLayoutParams(lp);
        KGPlayingBarUtil.setmBottomTabHeight(mBaseHeight);
    }

    private void initTabViews(Context context) {

        resetTotalHeight();

        tabLayout = new LinearLayout(context);
        tabLayout.setId(R.id.bottom_tab_layout);
        tabLayout.setClickable(true); // 防止点击穿透
        LayoutParams tabParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabLayout.setLayoutParams(tabParams);
        tabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabSpaceLayout = new LinearLayout(context);
        tabSpaceLayout.setId(R.id.bottom_tab_space_layout);
        LayoutParams tabSpaceParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBaseHeight);
        tabSpaceLayout.setLayoutParams(tabSpaceParams);
        tabSpaceParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tabSpaceLayout.setOrientation(LinearLayout.HORIZONTAL);


        mMine = new BottomTabItemView(context,
                context.getResources().getString(R.string.accessibility_main_top_mine),
                R.drawable.comm_bottom_bar_mine_def,
                "comm_bottom_bar_mine_selected",
                "comm_bottom_bar_mine_unselected",
                "svgfile/bottom_tab_item_mine.json"
        );

        mTing = new BottomTabItemView(context,
                context.getResources().getString(R.string.accessibility_main_top_ting),
                R.drawable.comm_bottom_bar_home_def,
                "comm_bottom_bar_ting_selected",
                "comm_bottom_bar_ting_unselected",
                "svgfile/bottom_tab_item_home.json"
        );

        //bar上面分割线
        lineView = new View(context);
        lineView.setId(R.id.bottom_tab_line);
        LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, mLineHeight);
        lineParams.addRule(RelativeLayout.ABOVE, R.id.bottom_tab_layout);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.LINE));

        if (NavigationBarCompat.isTranslucentNavigationBar()) {
            int navigationBarHeight = NavigationBarCompat.windowBottomInset();

            ViewGroup.LayoutParams lp = tabSpaceLayout.getLayoutParams();
            lp.height = mTabHeight + navigationBarHeight;
            tabLayout.setPadding(SystemUtils.dip2px(context, 10), 0,
                    SystemUtils.dip2px(context, 10), navigationBarHeight / 2);
        } else {
            tabLayout.setPadding(SystemUtils.dip2px(context, 10), 0,
                    SystemUtils.dip2px(context, 10), 0);
        }
    }

    private void initBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        mBottomTabBroadCast = new BottomTabBroadCast(this);
        BroadcastUtil.registerReceiver(mBottomTabBroadCast, intentFilter);

    }


    private void addViews() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = 0;
        params.weight = 1;
        tabLayout.addView(mTing, params);
        tabLayout.addView(mMine, params);

        mMine.setOnClickListener(mClick);
        mTing.setOnClickListener(mClick);

        addView(lineView);
        addView(tabSpaceLayout);


        addView(tabLayout);

    }


    public void turnToArcMode() {
        isPlayRingInNormal = false;
//        if (mAvatarWidget != null) {
//            mAvatarWidget.large();
//        }
    }

    public void turnToNormalMode() {
        isPlayRingInNormal = true;
//        if (mAvatarWidget != null) {
//            mAvatarWidget.normal();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BottomTabChangeEvent event) {
        if (event == null) {
            return;
        }
        int beforeTab = mCurrentTab;
        if (event.getToWho() == 2) {
            int who = event.getTab();
            boolean isClick = event.isClick();

            if (mMine != null) mMine.updateSelectState(MINE == who, isClick);
            if (mTing != null) mTing.updateSelectState(TING == who, isClick);

            mCurrentTab = who;
        } else if (event.getToWho() == 1) {
            //会先收到这个广播
            int who = event.getTab();
            boolean isClick = event.isClick();
            mMine.updateSelectState(MINE == who, isClick);
            mTing.updateSelectState(TING == who, isClick);
            mCurrentTab = who;
        }
    }


    private BottomTabClickChangeListener tabClickChangeListener;
    public void setTabClickChangeListener(BottomTabClickChangeListener tabClickChangeListener) {
        this.tabClickChangeListener = tabClickChangeListener;
    }

    private ClickWatcher clickWatcher = null;

    private OnClickListener mClick = new OnClickListener() {
        @Override
        public void onClick(View v) {

            int oldTab = mCurrentTab;
            if (v == mMine) {
                if (mCurrentTab != MINE) {
                    MainFragmentContainer.notifyTabChanged(MINE, 1, true);
                    mCurrentTab = MINE;
                } else {
                    //EventBus.getDefault().post(new RefreshEvent(RefreshEvent.PAGE_MINE_TAB));
                }
            } else if (v == mTing) {
                if (mCurrentTab != TING) {
                    MainFragmentContainer.notifyTabChanged(TING, 1, true);
                    mCurrentTab = TING;
                } else {
                    //EventBus.getDefault().post(new BottomTabClickAgainEvent(mCurrentTab));
                }
            }
        }
    };

    @Override
    public void updateSkin() {
        resetTotalHeight();
        mMine.updateSkin();
        mTing.updateSkin();
        lineView.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.LINE));
    }

    public void release() {
        EventBus.getDefault().unregister(this);
        BroadcastUtil.unregisterReceiver(mBottomTabBroadCast);
    }

    public void setMediaBottomTabHelperController(MediaBottomTabHelperController mediaBottomTabHelperController) {
        mMediaBottomTabHelperController = mediaBottomTabHelperController;
    }


    public static class BottomTabBroadCast extends BroadcastReceiver {
        private WeakReference<BottomTabView> weakReference;

        public BottomTabBroadCast(BottomTabView manager) {
            weakReference = new WeakReference<>(manager);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        }
    }


    public int getCurrentTab() {
        return mCurrentTab;
    }

    public int getTabCount() {
        if (tabLayout != null) {
            return tabLayout.getChildCount();
        }
        return 0;
    }

    /**
     * 处理底部bar选中状态，例如从我的tab进入老年模式，选中状态要切换到发现tab，离开老年模式要恢复到我的
     */
    public int lastSelectTab = -1;

}
