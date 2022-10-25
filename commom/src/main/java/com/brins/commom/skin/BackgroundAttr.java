package com.brins.commom.skin;

import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import com.brins.commom.entity.SkinBgType;
import com.brins.commom.profile.SkinProfileUtil;
import com.kugou.skinlib.attrs.AndroidViewAttr;
import com.kugou.skinlib.attrs.base.ISkinType;

/**
 * Created by liyun on 15/10/18.
 */
public class BackgroundAttr extends AndroidViewAttr {

    public void apply(View view) {
        apply(view, ISkinType.SKIN_PACKAGE_TYPE);
    }

    @Override
    public void apply(View view, @ISkinType int skinType) {
        if ("color".equals(attrValueTypeName)) {
            if ("skin_title".equals(attrValueName) && SkinProfileUtil.isCustomSkin()) {
                view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTitle());
            } else {
                view.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(this));
            }
        } else if ("drawable".equals(attrValueTypeName)) {
            Drawable origin = null;
            switch (attrValueName) {
                case "skin_main_bg":
                    view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getDrawableBg(
                        SkinBgType.MAIN));
                    break;
                case "skin_menu_bg":
                    view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getDrawableBg(SkinBgType.MENU));
                    break;
                case "skin_tab":
                    if (SkinProfileUtil.isCustomSkin()) {
                        view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTab());
                    } else {
                        origin = SkinResourcesUtils.getInstance().getDrawable(this);
                        view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getRippleDrawable(origin));
                    }
                    break;
                case "skin_list_selector":
                    origin = SkinResourcesUtils.getInstance().getDrawable(this);
                    view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getRippleDrawable(origin));
                    break;
                case "skin_background_borderless_ripple":
                    view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getRippleDrawable(null, true));
                    break;
                case "skin_title":
                    if (SkinProfileUtil.isCustomSkin()) {
                        view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTitle());
                    } else {
                        view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getDrawable(this));
                    }
                    break;
                default:
                    try {
                        view.setBackgroundDrawable(SkinResourcesUtils.getInstance().getDrawable(this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }

}
