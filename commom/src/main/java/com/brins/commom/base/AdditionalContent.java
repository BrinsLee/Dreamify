package com.brins.commom.base;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.brins.commom.activity.AbsBaseActivity;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.base.bar.BottomTabView;
import com.brins.commom.base.bar.MediaBottomTabHelperController;
import com.brins.commom.boot.BootMagicBox;
import com.brins.commom.utils.KGPlayingBarUtil;
import com.brins.commom.utils.SystemUtils;

/**
 * Created by burone on 2017/4/6.
 */

public class AdditionalContent {
    private AbsBaseActivity mActivity;

    public AdditionalLayout mContentRoot;
    //private PlayingBar mPlayingBar;
    public BottomTabView mBottomTabView;


    private MediaBottomTabHelperController mMediaBottomTabHelperController;
    private String mCurAvator;

    public AdditionalContent(Context context) {
        mContentRoot = new AdditionalLayout(context,this);
        if (FrameworkUtil.isMainCurrentStack()) {
            //fix
            View bottomBgView = BootMagicBox.get().getBottomBackgroundView(context);
            ViewParent bgParent = bottomBgView.getParent();
            if (bgParent instanceof ViewGroup) {
                ((ViewGroup) bgParent).removeView(bottomBgView);
            }
            attachBottomBackground(bottomBgView);
        }

        BottomTabView bottomTabView = new BottomTabView(context);
        bottomTabView.init(context);
        attachBottomTab(bottomTabView);
        if (mMediaBottomTabHelperController == null && context instanceof AbsBaseActivity) {
            mMediaBottomTabHelperController = new MediaBottomTabHelperController(
                (AbsBaseActivity) context);
            mMediaBottomTabHelperController.setMainContentView(mContentRoot);
            bottomTabView.setMediaBottomTabHelperController(mMediaBottomTabHelperController);
        }

    }


    private void attachBottomTab(BottomTabView bottomTabView) {
        mBottomTabView = bottomTabView;
        mContentRoot.setMainBottomBarRoot(mBottomTabView);
    }

    private void attachBottomBackground(View bottomBg) {
        mContentRoot.setMainBottomBackground(bottomBg);
    }

    private void attachTipTab(View tipView) {
        mContentRoot.setTipRoot(tipView);
    }

    public void moveAttachPlayingBarWithoutAnim(boolean hasPlayingBar, boolean isMainPage) {
        mContentRoot.movePlayingBarRoot(hasPlayingBar, isMainPage, false);
    }

    public void moveAttachPlayingBar(boolean hasPlayingBar, boolean isMainPage) {
        mContentRoot.movePlayingBarRoot(hasPlayingBar, isMainPage);
        //        if (isMainPage) {
        //            mBottomTabView.getPlayerListenPanel().pageResume();
        //        } else {
        //            mBottomTabView.refreshOnlineHornView(null);
        //        }
    }


    public View getContentRoot() {
        return mContentRoot;
    }

    public void attachActivity(final AbsBaseActivity activity, ViewGroup container) {
        mActivity = activity;
        container.addView(getContentRoot(), new ViewGroup.LayoutParams(-1, -1));

        //注意这里有个BottomTabView.sTotalHeight高度  可能会影响滑动
        mActivity.getDelegate().addNormalIngoreRect(new Rect(0, SystemUtils.getScreenHeight(
            DRCommonApplication.getContext()) - KGPlayingBarUtil.getmBottomTabHeight(), SystemUtils.getScreenWidth(DRCommonApplication.getContext()), SystemUtils.getScreenHeight(DRCommonApplication.getContext())));
/*        mActivity.getDelegate().attachPlayingBar(mPlayingBar);
        mActivity.getDelegate().getmMenuCard().addNormalIngoreView(mPlayingBar.getBarRoot());

        mActivity.getDelegate().setReadNovelBar(readNovelPlayingBar);*/
    }

    public void release() {
/*        mPlayingBar.release();
        mQueuePanel.release();*/
        mBottomTabView.release();
        /*if(mMenuPanel!=null)
            mMenuPanel.release();
        if (readNovelPlayingBar != null) {
            readNovelPlayingBar.release();
        }
        if (mAINovelMiniBar != null) {
            mAINovelMiniBar.release();
        }*/
    }


    public void onSkinAllChanged() {
        mContentRoot.onSkinAllChanged();
        /*mPlayingBar.onSkinAllChanged();
        mQueuePanel.onSkinAllChanged();
        if (readNovelPlayingBar != null) {
            readNovelPlayingBar.onSkinAllChanged();
        }*/
        mBottomTabView.updateSkin();
        /*if (mAIMiniBar != null) {
            mAIMiniBar.onSkinAllChanged();
        }
        if (mAINovelMiniBar != null) {
            mAINovelMiniBar.onSkinAllChanged();
        }*/
    }


    public String getCurAvatorPath(){
        return mCurAvator;
    }


    public void controlBarVisibility(boolean show, boolean hasMainBottomView) {
        if (mContentRoot != null) {
            mContentRoot.controlBarVisibility(show, hasMainBottomView);
        }
    }

    public boolean isHasShowBottomTab() {
        return mContentRoot != null && mContentRoot.isHasShowBottomTab();
    }

    public boolean isHasInitMove() {
        return mContentRoot != null && mContentRoot.isHasInitMove();
    }
}
