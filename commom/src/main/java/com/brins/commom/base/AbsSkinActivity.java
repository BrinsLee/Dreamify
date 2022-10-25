package com.brins.commom.base;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import com.brins.commom.R;
import com.brins.commom.broadcast.BroadcastUtil;
import com.brins.commom.constant.DRIntent;
import com.brins.commom.page.framework.StateFragmentActivity;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinChangedReceiver;
import com.brins.commom.skin.SkinConfig;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.statusbar.StatusBarCompat;
import com.brins.commom.utils.SystemBarUtil;
import com.kugou.skinlib.engine.ISkinListener;
import com.kugou.skinlib.engine.KGSkinEngine;
import java.util.ArrayList;

/**
 * @author lipeilin
 * @date 2022/10/17
 * @desc
 */
public abstract class AbsSkinActivity extends StateFragmentActivity implements ISkinListener {

    /**
     * 皮肤引擎
     */
    private KGSkinEngine mSkinEngine;

    protected boolean isChangedSkin = true;

    private SkinChangedReceiver mSkinBroadcastReceiver;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (isChangedSkin) {
            mSkinEngine = new KGSkinEngine(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(SkinConfig.ACTION_SKIN_CHANGED);
            mSkinBroadcastReceiver = new SkinChangedReceiver(this);
            BroadcastUtil.registerReceiver(mSkinBroadcastReceiver, filter);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        updateNavigationBg();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        updateNavigationBg();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        updateNavigationBg();
    }

    private void updateNavigationBg() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int color = 0;
                if (SkinProfileUtil.isDefaultLocalSimpleSkin()
                    || SkinProfileUtil.isBlurOrSolidSkin()) {
                    color = Color.WHITE;
                } else if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
                    color = getResources().getColor(R.color.kg11_main_bg_color_dark);
                } else {
                    Drawable drawable = SkinResourcesUtils.getInstance()
                        .getDrawable("skin_kg_playing_bar_right_bg",
                            R.drawable.skin_kg_playing_bar_right_bg);

                    if (drawable != null) {
                        int oriDrawableWidth = drawable.getIntrinsicWidth();
                        int oriDrawableHeight = drawable.getIntrinsicHeight();
                        drawable.setBounds(0, 0, oriDrawableWidth, oriDrawableHeight);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        Matrix matrix = new Matrix();
                        matrix.preTranslate(-oriDrawableWidth / 2, -(oriDrawableHeight - 1));
                        canvas.setMatrix(matrix);
                        drawable.draw(canvas);
                        color = bitmap.getPixel(0, 0);
                        //                    KGLog.d("AbsSkinActivity", "AbsSkinActivity::updateNavigationBg() called paletteColor=" + paletteColor);
                    }
                }
                getWindow().setNavigationBarColor(color);
                setNavigationBarTheme();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNavigationBarTheme() {
        int vis = getWindow().getDecorView().getSystemUiVisibility();
        //纯色跟默认
        //状态栏是否要显示黑色字体
        boolean isLightMode = SkinResourcesUtils.getInstance().getBoolean("skin_is_dark_status_bar", R.bool.skin_is_dark_status_bar);
        if (SkinProfileUtil.isDefaultLocalSimpleSkin() || SkinProfileUtil.isBlurOrSolidSkin()) {
            //按照com.kugou.common.base.AbsSkinActivity.updateNavigationBg逻辑 这时候导航栏时白色的 所以是亮模式 所以我们字体要显示黑色
            isLightMode = true;
        }
        if (!isLightMode) {
            //dark模式 导航栏按钮显示白色
            //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //                getWindow().getInsetsController().setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            //            }
            vis &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            //light模式 导航栏按钮显示黑色
            //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //                getWindow().getInsetsController().setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
            //            }
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        SystemBarUtil.setSystemUiVisibility(getWindow().getDecorView(), vis, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSkinBroadcastReceiver != null) {
            BroadcastUtil.unregisterReceiver(mSkinBroadcastReceiver);
        }
        if (mSkinEngine != null) {
            mSkinEngine.destroy();
        }
    }

    public KGSkinEngine getSkinEngine() {
        return mSkinEngine;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onChangedSkinNotifer() {
        runOnUiThread(this::onSkinAllChanged);
    }

    public void removeViewFromSkinEngine(View view) {
        if (mSkinEngine == null)
            return;

        mSkinEngine.removeView(view);
    }

    protected void onSkinAllChanged() {
        updateNavigationBg();

        ArrayList<Fragment> fragments = getAddedFragments();
        for (Fragment f : fragments) {
            if (f instanceof AbsSkinFragment && ((AbsSkinFragment) f).isChangedSkin()) {
                if (f instanceof AbsFrameworkFragment) {
                    if (((AbsFrameworkFragment) f).isAlive()) {
                        ((AbsSkinFragment) f).onSkinAllChanged();
                    }
                }
            }
        }
    }

    @Deprecated
    protected void onSkinBgChanged() {
        ArrayList<Fragment> fragments = getAddedFragments();
        for (Fragment f : fragments) {
            if (f instanceof AbsSkinFragment && ((AbsSkinFragment) f).isChangedSkin()) {
                ((AbsSkinFragment) f).onSkinBgChanged();
            }
        }
    }

    @Deprecated
    protected void onSkinColorChanged() {
        ArrayList<Fragment> fragments = getAddedFragments();
        for (Fragment f : fragments) {
            if (f instanceof AbsSkinFragment && ((AbsSkinFragment) f).isChangedSkin()) {
                ((AbsSkinFragment) f).onSkinColorChanged();
            }
        }
    }

    /**
     * 更好主界面背景透明度，子类实现 !!!子类必须调用super.onNaviBGAlphaChanged();
     */
    @Deprecated
    protected void onNaviBGAlphaChanged() {
        ArrayList<Fragment> fragments = getAddedFragments();
        for (Fragment f : fragments) {
            if (f instanceof AbsSkinFragment && ((AbsSkinFragment) f).isChangedSkin()) {
                ((AbsSkinFragment) f).onNaviBGAlphaChanged();
            }
        }
    }

    /**
     * 皮肤变更广播
     *
     * @param type 换肤类型
     *            <ul>
     *            <li>0,全换{@link KGIntent.SKIN_CHANGED_TYPE_ALL}</li>
     *            <li>1,换背景图 {@link KGIntent.SKIN_CHANGED_TYPE_BG}</li>
     *            <li>2,换颜色 {@link KGIntent.SKIN_CHANGED_TYPE_COLOR}</li>
     *            <li>3,换主界面透明度 {@link KGIntent.SKIN_CHANGED_TYPE_NAVI_BG_ALPHA}</li>
     *            <ul>
     */
    protected void sendSkinChangedBroadcast(int type) {
        Intent intent = new DRIntent();
        intent.setAction(DRIntent.ACTION_SKIN_CHANGED);
        intent.putExtra("change_type", type);
        BroadcastUtil.sendBroadcast(intent);
    }

    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    protected void $K(View.OnClickListener listener,View... views){
        if(views!=null){
            for (View view:views){
                if (view == null) {
                    continue;
                }
                view.setOnClickListener(listener);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSystemStatusBar();
    }


    public void updateSystemStatusBar() {
        switch (getStatusBarActionType()) {
            case IStatusBarActionType.TYPE_CHANGE_BY_SKIN:
                StatusBarCompat.setLightStatusBar(getActivity(), SkinProfileUtil.isDarkTxtStatusBarSkin() || SkinProfileUtil.isDefaultLocalSimpleSkin());
                break;
            case IStatusBarActionType.TYPE_KEEP_LIGHT:
                StatusBarCompat.setLightStatusBar(getActivity(), true);
                break;
            case IStatusBarActionType.TYPE_KEEP_DARK:
                StatusBarCompat.setLightStatusBar(getActivity(), false);
                break;
            case IStatusBarActionType.TYPE_NONE:
            default:
                break;
        }
    }

    public int getStatusBarActionType(){
        return IStatusBarActionType.TYPE_CHANGE_BY_SKIN;
    }

}
