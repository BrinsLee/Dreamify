package com.brins.commom.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.brins.commom.R;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.BitmapUtil;
import com.brins.commom.utils.SystemUtils;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;
import java.util.List;

/**
 * Created by zkzhou on 2016/2/29.
 */
public class CommonAlphaBgImageView extends ImageView implements ISkinViewUpdate {
    private Boolean mIsAlphaSuppressed = null;
    private int alpha = 255;
    private List<Drawable> mDrawableLists;
    private Drawable fore,next;
    private int mDrawableListSize;

    public CommonAlphaBgImageView(Context context) {
        super(context);
    }

    public CommonAlphaBgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonAlphaBgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawableLists(List<Drawable> mDrawableLists) {
        this.mDrawableLists = mDrawableLists;
        if (mDrawableLists != null)
            mDrawableListSize = mDrawableLists.size();
    }

    public void setBgAlpha(float alpha){
        if (mIsAlphaSuppressed == null) {
            mIsAlphaSuppressed = SkinProfileUtil.isDefaultSkin();
        }
        this.alpha = mIsAlphaSuppressed ? 255 : (int) alpha;
        invalidate();
    }

    //慎重使用
    public void setBgAlphaWithDefaultSkin(float alpha) {
        this.alpha = (int) alpha;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        try {
            width = getResources().getDimensionPixelSize(R.dimen.v8_comm_main_top_height);
        } catch (Resources.NotFoundException e) {
            width = SystemUtils.dip2px(getContext(), 45);
        }
        if(SystemUtils.getSdkInt() >= Build.VERSION_CODES.KITKAT){
            width += SystemUtils.getStatusBarHeight(getContext());
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mDrawableLists || SkinProfileUtil.isCustomSkin()) {
            return;
        }
        int foreAlpha = alpha;
        int nextAlpha = 255 - alpha;
        fore = mDrawableLists.get(0);
        mDrawableListSize = mDrawableLists.size();
        if(mDrawableListSize > 1) {
            next = mDrawableLists.get(1);
        }
        if(next != null) {
            next.setBounds(0, 0, getWidth(), getHeight());
            next.setAlpha(nextAlpha);
            next.draw(canvas);
        }
        if(fore != null) {
            fore.setBounds(0, 0, getWidth(), getHeight());
            fore.setAlpha(foreAlpha);
            fore.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    public void updateSkin() {
        if (mDrawableLists != null) {
            mDrawableLists.clear();
            if (mDrawableListSize > 1) {
                mDrawableLists.add(0, SkinResourcesUtils.getInstance().getDrawable("skin_kg_navigation_comm_top_bg",
                        R.drawable.skin_kg_navigation_comm_top_bg));
                if (SkinProfileUtil.isDefaultSkin()) {
                    mDrawableLists.add(0, new BitmapDrawable(
                        BitmapUtil.createColorBitmap(SkinResourcesUtils.getInstance().getColor(
                            SkinColorType.TITLE))));
                } else {
                    mDrawableLists.add(0, new BitmapDrawable(BitmapUtil.createColorBitmap(SkinResourcesUtils.getInstance().getColor(SkinColorType.DATE_PRESSED_TEXT))));
                }
            } else {
                if (SkinProfileUtil.isDefaultSkin()) {
                    mDrawableLists.add(0, new BitmapDrawable(BitmapUtil.createColorBitmap(SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE))));
                } else {
                    mDrawableLists.add(0, new BitmapDrawable(BitmapUtil.createColorBitmap(SkinResourcesUtils.getInstance().getColor(SkinColorType.DATE_PRESSED_TEXT))));
                }
            }
            setDrawableLists(mDrawableLists);
            setBgAlpha(255);
        }

        // 使用默认皮肤时，左右滑动Tab不需要改变顶端颜色透明度
        mIsAlphaSuppressed = SkinProfileUtil.isDefaultSkin();
    }
}
