package com.brins.commom.base.bar;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Build;
import android.view.View;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.value.LottieValueCallback;
import com.brins.commom.R;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.skin.SkinResourcesUtils;

public class SkinModeDefaultLight extends SkinModeDefault {

    private final int selectedColor;
    private final int unselectedColor;

    public SkinModeDefaultLight(ItemTabViewInterface rootView) {
        super(rootView);
        selectedColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.DATE_TEXT);
        unselectedColor = SkinResourcesUtils.getInstance().getColor(SkinColorType.DATE_UNSELECTED_TEXT);
    }

    @Override
    public void setupElement() {
        super.setupElement();
        if (mIcon != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // placeholder
                    mIcon.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                    mIcon.setImageResource(rootView.getDefaultIconRes());
                }
                mIcon.setAnimation(rootView.getAnimationResPath());
                LottieValueCallback<ColorFilter> lottieIconCallback = getLottieFilterCallback(Color.BLACK);
                mIcon.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, lottieIconCallback);
            } catch (NoSuchMethodError error) {

            }
        }
        if (selectIndicator != null) {
            try {
                selectIndicator.setAlpha(0.34f);
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
        if (describeText == null || selectIndicator == null) {
            return;
        }
        describeText.setTextColor(selectedColor);
        selectIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelSelect() {
        super.onCancelSelect();
        if (describeText == null || selectIndicator == null) {
            return;
        }
        describeText.setTextColor(unselectedColor);
        selectIndicator.setVisibility(View.INVISIBLE);
    }
}
