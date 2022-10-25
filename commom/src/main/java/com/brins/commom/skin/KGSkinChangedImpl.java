package com.brins.commom.skin;

import com.kugou.skinlib.listener.IKGSkinChangedListener;
import com.kugou.skinlib.manager.ISkinChangedListener;

public class KGSkinChangedImpl implements ISkinChangedListener {
    @Override
    public void addSkinChangedListener(IKGSkinChangedListener listener) {
        SkinManager.getInstance().addSkinChangedListener(listener);
    }

    @Override
    public void removeSkinChangedListener(IKGSkinChangedListener listener) {
        SkinManager.getInstance().removeSkinChangedListener(listener);
    }

    @Override
    public void clearSkinChangedListeners() {
        SkinManager.getInstance().clearSkinChangedListeners();
    }

    @Override
    public void updateSkinChanged() {
        SkinManager.getInstance().updateSkinChanged();
    }
}
