package com.brins.commom.skin;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.kugou.skinlib.ISkinResource;

public class KGSkinResourceImpl implements ISkinResource {

    @Override
    public int getColor(String attrValueName, int attrValueId) {
        return SkinResourcesUtils.getInstance().getColor(attrValueName, attrValueId);
    }

    @Override
    public Drawable getDrawable(String attrValueName, int attrValueId) {
        return SkinResourcesUtils.getInstance().getDrawable(attrValueName,attrValueId);
    }

    @Override
    public ColorStateList getColorStateList(String attrValueName, int attrValueId) {
        return SkinResourcesUtils.getInstance().getColorStateList(attrValueName,attrValueId);
    }

    @Override
    public Drawable getDrawableBG(String attrValueName, int attrValueId) {
        return SkinResourcesUtils.getInstance().getDrawable(attrValueName,attrValueId);
    }
}
