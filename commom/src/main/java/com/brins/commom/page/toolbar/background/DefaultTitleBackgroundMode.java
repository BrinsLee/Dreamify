package com.brins.commom.page.toolbar.background;

import android.view.View;
import com.brins.commom.R;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.kugou.page.core.TitleBackgroundMode;

public class DefaultTitleBackgroundMode implements TitleBackgroundMode {
    @Override
    public void onInit(View backgroundView) {
        if (backgroundView != null) {
            if (SkinProfileUtil.isCustomSkin()) {
                backgroundView.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTitle());
            } else if (SkinProfileUtil.isDefaultSkin()) {
                backgroundView.setBackgroundDrawable(backgroundView.getResources().getDrawable(R.drawable.skin_title));
            } else {
                backgroundView.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(
                    SkinColorType.TITLE));
            }
        }
    }

    @Override
    public void onSkinChanged(View backgroundView) {
        if (backgroundView != null) {
            if (SkinProfileUtil.isCustomSkin()) {
                backgroundView.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTitle());
            } else if (SkinProfileUtil.isDefaultSkin()) {
                backgroundView.setBackgroundDrawable(backgroundView.getResources().getDrawable(R.drawable.skin_title));
            } else {
                backgroundView.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE));
            }
        }
    }
}
