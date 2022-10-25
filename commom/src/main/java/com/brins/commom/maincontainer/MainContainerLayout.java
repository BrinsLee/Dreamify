package com.brins.commom.maincontainer;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.brins.commom.R;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.swipetap.MainFragmentViewPage;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import com.kugou.common.widget.base.NavigationBarCompat;

/**
 * Created by burone on 2017/4/20.
 */

public class MainContainerLayout extends FrameLayout implements ISkinViewUpdate {

    private MainFragmentViewPage mPagerContainer;
    ViewState viewState;

    public void setViewState(ViewState viewState) {
        this.viewState = viewState;
    }

    public MainContainerLayout(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        buildHierarchy(context);
        updateSkin();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (viewState != null) {
            viewState.onAttachedToWindow();
        }
    }

    boolean shouldRunFirstLayout = false;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!shouldRunFirstLayout) {
            if (viewState != null) {
                viewState.onFirstLayout();
            }
            shouldRunFirstLayout = true;
        }
    }

    private void buildHierarchy(Context context) {
        mPagerContainer = new MainFragmentViewPage(context);
        mPagerContainer.setId(R.id.comm_main_container_viewpager); // must give an id.
        mPagerContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPagerContainer.setPadding(0,0,0,NavigationBarCompat.isTranslucentNavigationBar() ? NavigationBarCompat.windowBottomInset() : 0);
        addView(mPagerContainer);
    }

    public void adjustStatusBar() {
//        SystemUtils.adjustStatusBar(mTopBarContainer, getContext(), TopBarHeight, 0, 0, 0, 0);
    }

    public MainFragmentViewPage getPagerContainer() {
        return mPagerContainer;
    }

    @Override
    public void updateSkin() {
        if (SkinProfileUtil.isBlurOrSolidOrSimpleSkin() ||
                SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
            setBackgroundColor(SkinProfileUtil.getLocalMainBgColor());
        } else {
            setBackgroundDrawable(SkinResourcesUtils.getInstance().getDrawableBg(SkinBgType.MAIN));
        }
    }

    public interface ViewState {
        void onAttachedToWindow();

        void onFirstLayout();
    }

}
