package com.brins.commom.base.bar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.brins.commom.R;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.SystemUtils;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;


public class BottomTabItemView extends RelativeLayout implements ISkinViewUpdate, ItemTabViewInterface {
    private SkinMode skinMode = null;

    private final String describeText;
    private final int defaultIconRes;
    private final String themeSelectedKey;
    private final String themeUnselectedKey;
    private boolean selected;
    private final String animationResPath;
    private int mIconSize;
    private TextView tabText;
    private float tabTextSize;

    public BottomTabItemView(Context context, String describeText, int defaultIconRes, String themeSelectedKey, String themeUnselectedKey, String animationResPath) {
        super(context);
        this.describeText = describeText;
        this.defaultIconRes = defaultIconRes;
        this.animationResPath = animationResPath;
        this.themeSelectedKey = themeSelectedKey;
        this.themeUnselectedKey = themeUnselectedKey;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.kg_x_bottom_animation_tab_item, this, true);
        setContentDescription(describeText);
        resetSize();
        ImageView mIcon = findViewById(R.id.bottom_tab_icon);
        if (mIcon != null) {
            mIcon.setImageDrawable(getUnselectedDrawable());
        }
        updateSkin();
    }

    /**
     * 按屏幕比例设置大小
     */
    private void resetSize() {
        int screenWidth = SystemUtils.getScreenWidth();
        // 最大尺寸，避免极端情况尺寸过大
        float tabHeight = SystemUtils.dip2px(getContext(), 48);
        float maxIconSize = tabHeight * 0.52f;
        float maxTextSize = tabHeight * 0.28f;
        float iconSize = screenWidth * 0.0666f; // 24/360
        if (iconSize > maxIconSize) {
            iconSize = maxIconSize;
        }
        float textSize = screenWidth * 0.0299f; // 10/360
        if (textSize > maxTextSize) {
            textSize = maxTextSize;
        }
        mIconSize = (int) iconSize;
        // 设置 icon 大小
        FrameLayout iconLayout = findViewById(R.id.bottom_tab_icon_ly);
        if (iconLayout != null) {
            LayoutParams iconLP = (LayoutParams) iconLayout.getLayoutParams();
            if (SkinProfileUtil.isOnlineSkin()) {
                iconLayout.setScaleX(1f);
                iconLayout.setScaleY(1f);
                iconLP.width = SystemUtils.dip2px(55f);
                iconLP.height = SystemUtils.dip2px(47f);
            } else {
                int defaultSize = SystemUtils.dip2px(22);
                float scale = iconSize / defaultSize;
                iconLayout.setScaleX(scale);
                iconLayout.setScaleY(scale);
                iconLP.width = defaultSize;
                iconLP.height = defaultSize;
            }
            iconLayout.setLayoutParams(iconLP);
        }
        // 设置文字大小
        tabText = findViewById(R.id.bottom_tab_tv);
        if (tabText != null) {
            tabTextSize = textSize;
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        // 设置最小宽度
        View tabLayout = findViewById(R.id.bottom_tab_ly);
        if (tabLayout != null) {
            tabLayout.setMinimumWidth((int) iconSize);
        }
    }

    public void updateSelectState(boolean select, boolean playAnimation) {
        this.selected = select;

        if (tabText != null) {
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
        }
        if (select) skinMode.onSelect(playAnimation);
        else skinMode.onCancelSelect();
    }

    private Drawable getElderSelectedDrawable(){
        return this.getResources().getDrawable(Integer.parseInt(themeSelectedKey));
    }

    private Drawable getElderUnselectedDrawable(){
        return this.getResources().getDrawable(Integer.parseInt(themeUnselectedKey));
    }

    public int getIconSize() {
        return mIconSize;
    }

    @Override
    public int getDefaultIconRes() {
        return defaultIconRes;
    }

    @Override
    public Drawable getSelectedDrawable() {
        return SkinResourcesUtils.getInstance().getDrawable(themeSelectedKey, defaultIconRes);
    }

    @Override
    public Drawable getUnselectedDrawable() {
        return SkinResourcesUtils.getInstance().getDrawable(themeUnselectedKey, defaultIconRes);
    }

    @Override
    public String getDescribeText() {
        return describeText;
    }

    @Override
    public void updateSkin() {
        resetSize();
        if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
            skinMode = new SkinModeDefaultDark(this);
        } else {
            skinMode = new SkinModeDefaultLight(this);
        }
        skinMode.setupElement();
    }

    public BottomTabItemView getRootView() {
        return this;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String getAnimationResPath() {
        return animationResPath;
    }
}
