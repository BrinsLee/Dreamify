
package com.brins.commom.skin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.kugou.skinlib.engine.ISkinListener;
import com.kugou.skinlib.engine.WeakSkinListener;

/**
 * 换肤广播
 * 
 * @author HalZhang
 * @version 2011-10-8上午11:03:22
 */
public class SkinChangedReceiver extends BroadcastReceiver {

    private final WeakSkinListener mSkinChangedListener;

    public SkinChangedReceiver(ISkinListener skinListener) {
        mSkinChangedListener = new WeakSkinListener(skinListener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (SkinConfig.ACTION_SKIN_CHANGED.equals(action)) {
            SkinResourcesUtils.getInstance().releaseBg();
            mSkinChangedListener.onChangedSkinNotifer();
        }
    }

}
