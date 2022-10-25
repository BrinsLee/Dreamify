package com.brins.commom.base.bar;

import android.graphics.ColorFilter;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.brins.commom.R;

public class SkinModeBase implements SkinMode {

    protected LottieAnimationView mIcon;
    protected ImageView mDot;
    protected TextView describeText;
    protected RelativeLayout mLayout;
    protected LottieAnimationView selectIndicator;
    protected View mDotLayout;

    protected boolean isLoadDotRes = false;

    protected int mDefaultIcon;

    protected final KeyPath keyPath;

    @NonNull
    protected final ItemTabViewInterface rootView;

    protected SkinModeBase(ItemTabViewInterface itemTabView) {
        this.rootView = itemTabView;
        mLayout = itemTabView.getRootView().findViewById(R.id.bottom_tab_ly);
        describeText = itemTabView.getRootView().findViewById(R.id.bottom_tab_tv);
        mIcon = itemTabView.getRootView().findViewById(R.id.bottom_tab_icon);
        mDotLayout = itemTabView.getRootView().findViewById(R.id.tab_short_video_red_dot_fly);
        selectIndicator = itemTabView.getRootView().findViewById(R.id.bottom_tab_icon_selected);
        keyPath = new KeyPath("**");
    }

    @Override
    public void setupElement() {
        describeText.setText(rootView.getDescribeText());
    }

    @Override
    public void onSelect(boolean playAnimation) {
        if (playAnimation) playAnimation();
    }

    @Override
    public void onCancelSelect() {
    }

    @Override
    public void playAnimation() {

    }

    public LottieValueCallback<ColorFilter> getLottieFilterCallback(int color) {
        SimpleColorFilter selectedFilter = new SimpleColorFilter(color);
        return new LottieValueCallback<>(selectedFilter);
    }
}
