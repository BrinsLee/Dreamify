package com.brins.commom.boot;

import android.content.Context;
import android.view.View;
import com.brins.commom.base.bar.BottomTabView;
import com.brins.commom.widget.BottomBackgroundView;
import java.util.ArrayList;

/**
 * @author lipeilin
 * @date 2022/10/22
 * @desc
 */
public class BootMagicBox extends IBootMagicBox{

    private boolean mFaced = false;
    private boolean mBootCompleted = false;
    private PlayBarInflatedListener mPlayBarInflatedListener;
    private ArrayList<BootCompleteListener> mBootCompleteListeners;
    private BottomTabView bottomTabView;
    private BottomBackgroundView bottomBgView;
    @Override public void onFirstFace() {

    }

    @Override
    public boolean isFaced() {
        return mFaced;
    }

    @Override
    public boolean isBootCompleted() {
        return mBootCompleted;
    }

    @Override public void prepareOnColdBoot() {

    }

    @Override public void preloadResources() {

    }

    @Override public long getBootCompleteTime() {
        return 0;
    }

    @Override public void addBootCompleteListener(BootCompleteListener listener) {

    }

    @Override public void removeBootCompleteListener(BootCompleteListener listener) {

    }

    @Override public void setPlayBarInflatedListener(PlayBarInflatedListener listener) {

    }

    @Override public void attachGodActivityContext(Context context) {

    }

    @Override
    public CollateralLayoutInflater getLayoutInflater(Context context) {
        return CollateralLayoutInflater.from(context);
    }

    @Override public View getBottomBarView(Context context) {
        if (bottomTabView == null) {
            bottomTabView = new BottomTabView(context);
            bottomTabView.init(context);
        }
        return bottomTabView;
    }

    @Override public View getBottomBackgroundView(Context context) {
        if (bottomBgView == null) {
            bottomBgView = new BottomBackgroundView(context);
        }
        return bottomBgView;
    }
}
