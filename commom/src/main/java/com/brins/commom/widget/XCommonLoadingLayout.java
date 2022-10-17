package com.brins.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.brins.commom.R;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.SystemUtils;
import com.kugou.common.skinpro.widget.ISkinViewUpdate;

public class XCommonLoadingLayout extends LinearLayout implements ISkinViewUpdate {

    public final static int STATE_DRAG = 1;// 正常
    public final static int STATE_RELEASE_REFRESH = 2;// 释放刷新
    public final static int STATE_REFRESHING = 3;// 正在刷新

    public static final int MODE_NONE = 0;
    public static final int MODE_LEFT = 1;
    public static final int MODE_RIGHT = 2;
    public static final int MODE_BOTTOM = 3;
    public static final int MODE_TOP = 4;

    public static final int COLOR_THEME_NORMAL = 0;
    public static final int COLOR_THEME_WHITE = 1;
    protected int textMode = MODE_NONE;

    private int refreshState = STATE_DRAG;

    private TextView mTipView;
    public XCommonLoadingView mLoadingView;
    private LayoutParams txtParams, loadingParams;
    protected int iconNormalColor, arcNormalColor, changeColor;

    protected int viewSize;

    protected boolean isCircleStype;
    protected Drawable bgDrawable = null;
    protected Drawable mDefaultDrawable = null;

    protected String mDragStr, mReleaseStr, mLoadingStr, mLoadingSecondStr;

    protected int mTextColor;

    private boolean isChangeColor;
    private int mTextSize;
    private int mColorTheme;
    private boolean mSkinEnable = true;


    public XCommonLoadingLayout(Context context) {
        this(context, null);
    }

    public XCommonLoadingLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public XCommonLoadingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XCommonLoadingLayout);
        if (typedArray != null) {
            try {
                isCircleStype = typedArray.getInt(R.styleable.XCommonLoadingLayout_loading_type, XCommonLoadingView.LOADING_TYPE_NORMAL) == XCommonLoadingView.LOADING_TYPE_X_MODE;
                iconNormalColor = typedArray.getColor(R.styleable.XCommonLoadingLayout_iconNormalColor, Color.parseColor("#409EFF"));
                arcNormalColor = typedArray.getColor(R.styleable.XCommonLoadingLayout_arcNormalColor, Color.parseColor("#409EFF"));
                changeColor = typedArray.getColor(R.styleable.XCommonLoadingLayout_changeColor, Color.parseColor("#FF6C00"));
                bgDrawable = typedArray.getDrawable(R.styleable.XCommonLoadingLayout_loadingBgDrawable);
                mDefaultDrawable = typedArray.getDrawable(R.styleable.XCommonLoadingLayout_defaultSrcDrawable);
                textMode = typedArray.getInt(R.styleable.XCommonLoadingLayout_textMode, MODE_NONE);
                mColorTheme = typedArray.getInt(R.styleable.XCommonLoadingLayout_colorTheme, COLOR_THEME_NORMAL);
                mDragStr = typedArray.getString(R.styleable.XCommonLoadingLayout_dragStr);
                mReleaseStr = typedArray.getString(R.styleable.XCommonLoadingLayout_releaseStr);
                mLoadingStr = typedArray.getString(R.styleable.XCommonLoadingLayout_loadingStr);
                mLoadingSecondStr = typedArray.getString(R.styleable.XCommonLoadingLayout_loadingSecondStr);
                viewSize = typedArray.getInt(R.styleable.XCommonLoadingLayout_viewSize, XCommonLoadingView.SIZE_NORMAL);
                if (bgDrawable == null) {
                    bgDrawable = getResources().getDrawable(R.drawable.common_loading_view_bg_gray);
                }
                if (TextUtils.isEmpty(mDragStr)) {
                    mDragStr = getResources().getString(R.string.pull_to_refresh_pull);
                }
                if (TextUtils.isEmpty(mReleaseStr)) {
                    mReleaseStr = getResources().getString(R.string.pull_to_refresh_release);
                }
                if (TextUtils.isEmpty(mLoadingStr)) {
                    mLoadingStr = getResources().getString(R.string.pull_to_refresh_refreshing);
                }
                if (TextUtils.isEmpty(mLoadingSecondStr)) {
                    mLoadingSecondStr = getResources().getString(R.string.loading_tips_secondary);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }
        setLoadingParams();
        initView();
        setupView();
    }

    public void setSkinEnable(boolean enable){
        this.mSkinEnable = enable;
    }

    protected void setLoadingParams() {

    }

    public XCommonLoadingView getLoadingView() {
        return mLoadingView;
    }

    public void setLoadingViewVisible(boolean visible) {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(visible ? VISIBLE : GONE);
        }
    }


    public void setLoadingStr(String loadingStr) {
        mLoadingStr = loadingStr;
    }

    public void setFo(String fo) {
        if (mLoadingView != null) {
            mLoadingView.setFo(fo);
        }
    }

    public void setLoadingSecondStr(String loadingSecondStr) {
        mLoadingSecondStr = loadingSecondStr;
    }

    public XCommonLoadingView createLoadingVew() {
        return new XCommonLoadingView(getContext());
    }

    private void initView() {
//                <TextView
//        android:id="@+id/comm_loading_tip_view"
//        android:layout_width="wrap_content"
//        android:layout_height="wrap_content"
//        android:textSize="12dip"
//        android:layout_marginLeft="8dip"
//        android:includeFontPadding="false"
//        android:textColor="@color/skin_secondary_text"
//        android:text="@string/loading_tips_primary"
//        app:skin_enable="true"/>
        mTipView = new TextView(getContext());
        mTextSize = SystemUtils.dip2px(12f);
        mTipView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTipView.setIncludeFontPadding(false);
        mTipView.setTextColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.SECONDARY_TEXT));
        mTipView.setText(getResources().getString(R.string.loading_tips_primary));
        txtParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTipView.setLayoutParams(txtParams);

        mLoadingView = new XCommonLoadingView(getContext());
        mLoadingView.setColorMode(mColorTheme);
        mLoadingView.setCircleStype(isCircleStype);
        mLoadingView.setViewSize(viewSize);
        mLoadingView.setColorStyle(iconNormalColor, arcNormalColor, changeColor, bgDrawable);
        mLoadingView.setOnLoadingListener(new OnLoadingListener() {
            @Override
            public void onChangeColor() {
                mTipView.setText(mLoadingSecondStr);
            }
        });
        loadingParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLoadingView.setLayoutParams(loadingParams);
        if (mDefaultDrawable != null) {
            mLoadingView.setImageDrawable(mDefaultDrawable);
        }
        if (mTextColor != 0) {
            mTipView.setTextColor(getResources().getColor(mTextColor));
        }
    }

    public void setImageSrc(Drawable drawable) {
        mLoadingView.setImageDrawable(drawable);
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTipView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    }


    //请误在加载中使用
    public void setTextColor(int color) {
        mTipView.setTextColor(color);
    }

    private void setupView() {
        setGravity(Gravity.CENTER);
        removeAllViews();
        switch (textMode) {
            case MODE_NONE:
                setOrientation(LinearLayout.HORIZONTAL);
                addView(mLoadingView);
                break;
            case MODE_LEFT:
                setOrientation(LinearLayout.HORIZONTAL);
                txtParams.rightMargin = SystemUtils.dip2px(8f);
                addView(mTipView);
                addView(mLoadingView);
                break;
            case MODE_RIGHT:
                setOrientation(LinearLayout.HORIZONTAL);
                txtParams.leftMargin = SystemUtils.dip2px(8f);
                addView(mLoadingView);
                addView(mTipView);
                break;
            case MODE_BOTTOM:
                setOrientation(LinearLayout.VERTICAL);
                txtParams.topMargin = SystemUtils.dip2px(8f);
                addView(mLoadingView);
                addView(mTipView);
                break;
            case MODE_TOP:
                setOrientation(LinearLayout.VERTICAL);
                txtParams.bottomMargin = SystemUtils.dip2px(8f);
                addView(mTipView);
                addView(mLoadingView);
                break;
        }
    }

    public void setPullScale(float scale) {
        mLoadingView.setPullScale(scale);
    }

    public void restartRefresh() {
        restartRefresh(false);
    }
    public void restartRefresh(boolean resetApm) {
        mTipView.setText(mLoadingStr);
        mLoadingView.restartRefresh(resetApm);
    }

    public void setCircleStype(boolean circleStype) {
        mLoadingView.setCircleStype(circleStype);
    }

    @Override
    public void updateSkin() {
        if (!mSkinEnable) return;
        mLoadingView.updateSkin();
        mTipView.setTextColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.SECONDARY_TEXT));

        if (SkinProfileUtil.isDefaultLocalSimpleSkin() || SkinProfileUtil.isDefaultSkin() || SkinProfileUtil.isSolidSkin()) {
            setColorStyle(getResources().getColor(R.color.x_common_loadiing_icon_normal_color), getResources().getColor(R.color.x_common_loadiing_icon_normal_color), getResources().getColor(R.color.x_common_loadiing_change_color), getResources().getDrawable(R.drawable.common_loading_view_bg_gray));
        } else if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
            setColorStyle(getResources().getColor(R.color.x_common_loadiing_icon_normal_color), Color.WHITE, getResources().getColor(R.color.x_common_loadiing_change_color), getResources().getDrawable(R.drawable.common_loading_view_bg_white));
        } else {
            setColorStyle(getResources().getColor(R.color.x_common_loadiing_icon_normal_color), Color.WHITE, getResources().getColor(R.color.x_common_loadiing_change_color), getResources().getDrawable(R.drawable.common_loading_view_bg_white));
        }
    }

    public void setIconImg(int resId) {
        mLoadingView.setImageResource(resId);
    }

    public void setColorFilter(int color) {
        mLoadingView.setColorFilter(color);
    }

    public void setUseLoadingApm(boolean useApm) {
        mLoadingView.setUseLoadingApm(useApm);
    }

    public void startRefresh() {
        startRefresh(-1L);
    }

    public void startRefresh(long changeColorTimeMillis) {
        startRefresh(changeColorTimeMillis, true);
    }

    public void startRefresh(long changeColorTimeMillis, boolean resumeRefreshByAttach) {
        if (mLoadingView != null) {
            mLoadingView.setResumeRefreshByAttach(resumeRefreshByAttach);
            if (mTipView != null)
                mTipView.setText(mLoadingStr);
            if (changeColorTimeMillis < 0L) {
                mLoadingView.startRefresh();
            } else {
                mLoadingView.startRefresh(changeColorTimeMillis, true);
            }
        }
    }

    public boolean isAnimating() {
        return mLoadingView != null && mLoadingView.isAnimating();
    }

    public void endRefresh() {
        endRefresh(false);
    }

    public void endRefresh(boolean endApm) {
        if (mLoadingView != null) {
            if (mTipView != null)
                mTipView.setText(mDragStr);
            mLoadingView.endRefresh(endApm);
        }
    }

    public void setColorStyle(int iconNorColor, int arcNorColor, int changedColor, Drawable bgDrawable) {
        mLoadingView.setColorStyle(iconNorColor, arcNorColor, changedColor, bgDrawable);
    }

    public void setViewType(int txtMode) {
        this.textMode = txtMode;
        setupView();
    }

    public void setDragStr(String mDragStr) {
        this.mDragStr = mDragStr;
    }

    public void pullToRefreshImpl() {
        if (mTipView != null)
        mTipView.setText(mDragStr);
    }

    public void releaseToRefreshImpl() {
        if (mTipView != null)
        mTipView.setText(mReleaseStr);
    }

    public void resetImpl() {
        mLoadingView.endRefresh(false);
        if (mTipView != null)
        mTipView.setText(mDragStr);
    }

    public void setTipText(String msg) {
        if (mTipView != null)
        mTipView.setText(msg);
    }

    public void setViewSize(int size) {
        mLoadingView.setViewSize(size);
    }

    public void setRefreshState(int state) {
        this.refreshState = state;
        if (mTipView == null){
            return;
        }
        switch (refreshState) {
            case STATE_DRAG:
            default:
                mTipView.setText(mDragStr);
                break;
            case STATE_RELEASE_REFRESH:
                mTipView.setText(mReleaseStr);
                break;
            case STATE_REFRESHING:
                mTipView.setText(mLoadingStr);
                break;
        }
    }

    public void setLoadingType(int type) {
        if (mLoadingView != null) mLoadingView.setLoadingType(type);
    }

    public TextView getTipView() {
        return mTipView;
    }

    public void setChangeTime(int seconds) {
        if (mLoadingView != null) mLoadingView.setChangeTime(seconds);
    }

    public void resetStartTime() {
        if (mLoadingView != null) mLoadingView.resetStartTime();
    }

    public static interface OnLoadingListener {
        void onChangeColor();
    }

}
