package com.brins.commom.base.bar;

import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.os.Build;
import android.view.View;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.value.LottieValueCallback;
import com.brins.commom.R;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.skin.ColorUtil;
import com.brins.commom.skin.SkinResourcesUtils;

public class SkinModeDefaultDark extends SkinModeDefault {

    private final int selectedColor;
    private final int unselectedColor;

    private final LottieValueCallback<ColorFilter> lottieSelectedCallback;
    private final LottieValueCallback<ColorFilter> lottieUnselectedCallback;

    public SkinModeDefaultDark(ItemTabViewInterface rootView) {
        super(rootView);
        selectedColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.COMMON_WIDGET);
        unselectedColor = ColorUtil.getHybridAlphaColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.BASIC_WIDGET), 0.3f);
        lottieSelectedCallback = getLottieFilterCallback(selectedColor);
        lottieUnselectedCallback = getLottieFilterCallback(unselectedColor);
    }

    @Override
    public void setupElement() {
        super.setupElement();
        if (mIcon != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // placeholder
                    int iconColor = rootView.isSelected() ? selectedColor : unselectedColor;
                    mIcon.setImageTintList(ColorStateList.valueOf(iconColor));
                    mIcon.setImageResource(rootView.getDefaultIconRes());
                }
                mIcon.setAnimation(rootView.getAnimationResPath());
                mIcon.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, rootView.isSelected() ? lottieSelectedCallback : lottieUnselectedCallback);
            } catch (NoSuchMethodError error) {

            }
        }
        if (describeText != null) {
            describeText.setTextColor(rootView.isSelected() ? selectedColor : unselectedColor);
        }
        if (selectIndicator != null) {
            try {
                selectIndicator.setAlpha(0.1f);
                int indicatorColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.GRADIENT_COLOR);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // placeholder
                    selectIndicator.setImageTintList(ColorStateList.valueOf(indicatorColor));
                    selectIndicator.setImageResource(R.drawable.bottom_tab_item_indicator);
                }
                selectIndicator.setAnimation("svgfile/bottom_tab_item_indicator_light.json");
                LottieValueCallback<ColorFilter> lottieIndicatorCallback = getLottieFilterCallback(indicatorColor);
                selectIndicator.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, lottieIndicatorCallback);
                if (rootView.isSelected()) {
                    selectIndicator.setVisibility(View.VISIBLE);
                } else {
                    selectIndicator.setVisibility(View.INVISIBLE);
                }
            } catch (NoSuchMethodError error) {

            }
        }
    }

    @Override
    public void onSelect(boolean playAnimation) {
        super.onSelect(playAnimation);
        if (mIcon == null || describeText == null || selectIndicator == null) {
            return;
        }
        mIcon.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, lottieSelectedCallback);
        describeText.setTextColor(selectedColor);
        selectIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelSelect() {
        super.onCancelSelect();
        if (mIcon == null || describeText == null || selectIndicator == null) {
            return;
        }
        mIcon.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, lottieUnselectedCallback);
        describeText.setTextColor(unselectedColor);
        selectIndicator.setVisibility(View.INVISIBLE);
    }
}
