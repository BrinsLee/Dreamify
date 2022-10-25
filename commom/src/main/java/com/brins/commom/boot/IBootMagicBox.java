package com.brins.commom.boot;

import android.content.Context;
import android.view.View;
import com.kugou.android.app.boot.FrameworkContentView;

/**
 * @author lipeilin
 * @date 2022/10/22
 * @desc
 */
public abstract class IBootMagicBox implements FrameworkContentView.FirstFaceListener{

    private static final String CHILD = "com.brins.commom.boot.BootMagicBox";
    private static final class InstanceHolder {
        public static IBootMagicBox sMagicBox;
        static {
            try {
                sMagicBox = (IBootMagicBox) Class.forName(CHILD).newInstance();
            } catch (Exception e) {
            }
        }
    }

    public interface BootCompleteListener {
        void onBootCompleted();
    }

    public interface PlayBarInflatedListener {
        void onPlayBarPreInflated(View playBar, View bottomBar, View bgView);
    }

    public abstract boolean isFaced();

    public abstract boolean isBootCompleted();

    public abstract void prepareOnColdBoot();

    public abstract void preloadResources();

    public abstract long getBootCompleteTime();

    public abstract void addBootCompleteListener(BootCompleteListener listener);

    public abstract void removeBootCompleteListener(BootCompleteListener listener);

    public abstract void setPlayBarInflatedListener(PlayBarInflatedListener listener);

    public abstract void attachGodActivityContext(Context context);

    public abstract CollateralLayoutInflater getLayoutInflater(Context context);

    public static IBootMagicBox get() {
        return InstanceHolder.sMagicBox;
    }

    public abstract View getBottomBarView(Context context);

    public abstract View getBottomBackgroundView(Context context);

}
