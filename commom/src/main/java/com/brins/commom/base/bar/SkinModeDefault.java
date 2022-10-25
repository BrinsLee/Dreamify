package com.brins.commom.base.bar;

import android.widget.RelativeLayout;
import com.brins.commom.R;
import com.brins.commom.utils.SystemUtils;

public class SkinModeDefault extends SkinModeBase {

    protected SkinModeDefault(ItemTabViewInterface itemTabView) {
        super(itemTabView);
    }

    @Override
    public void setupElement() {
        super.setupElement();
        if (mLayout != null && mLayout.getLayoutParams() != null) {
            mLayout.getLayoutParams().height = rootView.getRootView().getResources().getDimensionPixelSize(R.dimen.kg11_bottom_bar_tab_height);
        }
        if (mDotLayout != null) {
            try {
                RelativeLayout.LayoutParams dotLP = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                dotLP.addRule(RelativeLayout.RIGHT_OF, R.id.bottom_tab_ly);
                mDotLayout.setLayoutParams(dotLP);
                dotLP.topMargin = SystemUtils.dip2px(3.5f);
            } catch (NoSuchMethodError error) { // kg-suppress REGULAR.ERROR-1

            }
        }
    }

    @Override
    public void playAnimation() {
        if (mIcon != null) {
            mIcon.playAnimation();
        }
    }

    @Override
    public void onSelect(boolean playAnimation) {
        super.onSelect(playAnimation);
        if (selectIndicator != null) {
            selectIndicator.playAnimation();
        }
    }
}
