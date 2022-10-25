package com.brins.commom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.brins.commom.R;
import com.brins.commom.loading.ILoadingPresenter;
import com.brins.commom.loading.LoadingPresenter;
import com.brins.commom.toast.LoadingTypes;
import com.brins.commom.utils.log.DrLog;

/**
 * Loading组件(已经废弃) :请直接使用 XCommonLoadingLayout
 *
 * 文字位置有目前支持三种模式：无文字#MODE_NORMAL、无文字#MODE_TOP_CENTER、靠右#MODE_RIGHT、底部#MODE_BOTTOM
 * 动画控制、计时逻辑控制由{@link LoadingPresenter}控制，提供{@link ILoadingPresenter}接口调用，通过{@link #getLoadingPresenter()}获取。
 */
public class CommonLoadingView extends XCommonLoadingLayout {

    private final static boolean DEBUG = DrLog.isDebug();
    private final static String TAG = "CommonLoadingView";
    public static final int MODE_NONE = 0;
    private final int MODE_NORMAL = 1;
    private final int MODE_RIGHT = 2;
    private final int MODE_BOTTOM = 3;
    private final int MODE_TOP_CENTER = 4;
    private int textMode = MODE_BOTTOM;

    /**
     * public static final int MODE_NONE = 0;
     *     public static final int MODE_LEFT = 1;
     *     public static final int MODE_RIGHT = 2;
     *     public static final int MODE_BOTTOM = 3;
     *     public static final int MODE_TOP = 4;
     *     protected int textMode = MODE_NONE;
     */

    private ILoadingPresenter loadingPresenter;

    private LayerDrawable mDrawable;
    private AnimationDrawable mAnim;

    // color for icon
    private int mIconColor;
    private int mPrimaryColor;
    private int mSecondaryColor;

    private final int DEFAULT_DRAW_PADDING = 20;
    private int drawPadding = DEFAULT_DRAW_PADDING;

    private final String DEFAULT_TEXT = "加载中，请稍候";
    private String mPrimaryText;
    private String mSecondaryText;
    private String mText = DEFAULT_TEXT;
    String textColorStr;
    String primaryColorText = null;
    String secondaryColorText = null;

    private int mType = LoadingTypes.DEFAULT;


    public CommonLoadingView(Context context) {
        super(context);
        init(null);
    }

    public CommonLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context.obtainStyledAttributes(attrs, R.styleable.LoadingView));
    }

    public CommonLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context.obtainStyledAttributes(attrs, R.styleable.LoadingView));
    }




    private void init(TypedArray typedArray) {
        setGravity(Gravity.CENTER);
        loadingPresenter = new LoadingPresenter();
        loadingPresenter.attachView(this);

//        int defaultSize = getResources().getDimensionPixelSize(R.dimen.loadingView_textSize_default);
//        Drawable bgDrawable = null;

        if (typedArray != null) {
            try {
                textMode = typedArray.getInt(R.styleable.LoadingView_loadingTextMode, MODE_BOTTOM);

//                //==============================动画框架已经换了,以下设置都是无效的=========================
//
//
//                //图片背景
//                bgDrawable = typedArray.getDrawable(R.styleable.LoadingView_iconBg);
//                //加载中的text颜色
                textColorStr = typedArray.getString(R.styleable.LoadingView_loadingTextColor);
//                //加载中的textSize 默认12
//                defaultSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_loadingTextSize, defaultSize);
//                //text模式  有上下左右  默认是下

//                //绘制icon的padding
//                drawPadding = typedArray.getDimensionPixelSize(R.styleable.LoadingView_drawPadding, DEFAULT_DRAW_PADDING);
//                //主要的icon颜色
                primaryColorText = typedArray.getString(R.styleable.LoadingView_iconPrimaryColor);
//                //次要的icon颜色
                secondaryColorText = typedArray.getString(R.styleable.LoadingView_iconSecondaryColor);
//                //主要的文字内容
                mPrimaryText = typedArray.getString(R.styleable.LoadingView_loadingPrimaryText);
//                //次要的文字内容
                mSecondaryText = typedArray.getString(R.styleable.LoadingView_loadingSecondaryText);
//                //用在哪里
//                /*// name="loadingPosition" format="enum">
//               name="normal" value="1"/> 屏幕中间的加载
//                name="header" value="2"/>  下拉刷新
//                name="footer" value="3"/>    加载更多
//                name="floating" value="4"/>*/   //悬浮窗
                mType = typedArray.getInt(R.styleable.LoadingView_loadingPosition, LoadingTypes.DEFAULT);
            } catch (Exception e) {

            } finally {
                typedArray.recycle();
            }
        }
//
//       // if (bgDrawable == null){
//            bgDrawable = getResources().getDrawable(R.drawable.common_loading_view_bg_gray);
//       // }
//        if (!TextUtils.isEmpty(textColorStr)) {
//            //setColorStyle(Color.parseColor("#409EFF"),Color.parseColor("#409EFF"),Color.parseColor("#FF6C00"),bgDrawable);
//
//            //setTextColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.SECONDARY_TEXT));
//            //} else {
//            //setTextColor(Color.parseColor(textColorStr));
//            setColorStyle(Color.parseColor(textColorStr), Color.parseColor(textColorStr), Color.parseColor("#FF6C00"), bgDrawable);
//            //}
//        }
        if (TextUtils.isEmpty(mPrimaryText)) {
            mPrimaryText = getResources().getString(R.string.loading_tips_primary);
            mLoadingStr=mPrimaryText;
        }
        if (TextUtils.isEmpty(mSecondaryText)) {
            mSecondaryText = getResources().getString(R.string.loading_tips_secondary);
            mLoadingSecondStr=mSecondaryText;
        }
//        setTextSize(defaultSize);
//        mPrimaryColor = Color.parseColor(TextUtils.isEmpty(primaryColorText) ? "#0090FF" : primaryColorText);
//        mSecondaryColor = Color.parseColor(TextUtils.isEmpty(primaryColorText) ? "#FF6C00" : secondaryColorText);
//        mText = mPrimaryText;
//
//        //int width = bgDrawable.getIntrinsicWidth();
//        //int height = bgDrawable.getIntrinsicHeight();
////        mDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.common_loading);
////        //mDrawable.setBounds(0, 0, width <= 0 ? mDrawable.getMinimumWidth() : width, height <= 0 ? mDrawable.getIntrinsicHeight() : height);
////        mAnim = (AnimationDrawable) mDrawable.findDrawableByLayerId(R.id.common_loading_anim_id);
////        mAnim.setColorFilter(mPrimaryColor, PorterDuff.Mode.SRC_IN);
//        //setCompoundDrawablePadding(drawPadding);
        setText(mText);
        setViewType(textMode);
        if(mType==3){
            mDefaultDrawable=getResources().getDrawable(R.drawable.x_refresh_loading_pic_small_31);
            setViewSize(XCommonLoadingView.SIZE_SMALL);
            setImageSrc(getResources().getDrawable(R.drawable.x_refresh_loading_pic_small_31));
        }else {
            mDefaultDrawable=getResources().getDrawable(R.drawable.x_refresh_loading_pic31);
        }
    }


    @Override
    protected void setLoadingParams() {
        isCircleStype=true;
        bgDrawable = getResources().getDrawable(R.drawable.common_loading_view_bg_gray);
        iconNormalColor=getResources().getColor(R.color.x_common_loadiing_icon_normal_color);
        arcNormalColor = getResources().getColor(R.color.x_common_loadiing_icon_normal_color);
        changeColor=getResources().getColor(R.color.x_common_loadiing_change_color);
        if(mType==3){
            mDefaultDrawable=getResources().getDrawable(R.drawable.x_refresh_loading_pic_small_31);
            setViewSize(XCommonLoadingView.SIZE_SMALL);
            setImageSrc(getResources().getDrawable(R.drawable.x_refresh_loading_pic_small_31));
        }else {
            mDefaultDrawable=getResources().getDrawable(R.drawable.x_refresh_loading_pic31);
        }
        if(textColorStr!=null){
            mTextColor= Color.parseColor(textColorStr);
        }
        //textMode=MODE_NONE;
    }


    public void setText(String text) {
        if (MODE_NORMAL == textMode || MODE_TOP_CENTER == textMode) return;

        //super.setText(text);
    }

    public ILoadingPresenter getLoadingPresenter() {
        return loadingPresenter;
    }

    @Nullable
    public AnimationDrawable getAnim() {
        return mAnim;
    }

    public int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(@ColorInt int color) {
        this.mIconColor = color;
        if (mAnim != null) {
            mAnim.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            mAnim.invalidateSelf();
        }
    }

    /**
     * This method deprecated, see {@link ILoadingPresenter}.
     */
    @Deprecated
    public void startAnimationDrawable() {
       startRefresh();
    }

    /**
     * This method deprecated, see {@link ILoadingPresenter}.
     */
    @Deprecated
    public void stopAnimationDrawable() {
        endRefresh();
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public int getSecondaryColor() {
        return mSecondaryColor;
    }

    public String getPrimaryText() {
        return mPrimaryText;
    }

    public void setPrimaryText(String mPrimaryText) {
        this.mPrimaryText = mPrimaryText;
    }

    public String getSecondaryText() {
        return mSecondaryText;
    }

    public void setSecondaryText(String mSecondaryText) {
        this.mSecondaryText = mSecondaryText;
    }


    @Deprecated
    public void updateViews(boolean isDeepSkin) {

    }

    @Override
    public void draw(Canvas canvas) {
        // skip draw when out of screen.
        if (loadingPresenter.checkLocation()) return;

        //if (DEBUG) DrLog.i(TAG, "CommonLoadingView draw!!!");
        super.draw(canvas);
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        boolean isVisible = false;
        isVisible = ((visibility == View.VISIBLE) && (getVisibility() == View.VISIBLE));
        if (isVisible) {
            //todo  可能会遇到Loading动画不动
            /**
             * !!! isShown方法会误判 !!!(当子View的Parent为null时)
             * 如果遇到Loading动画不动时，通过{@link #getLoadingPresenter()} 获取{@link ILoadingPresenter}控制动画.
             */
            isVisible = isShown();
        }
        super.onVisibilityChanged(changedView, visibility);
        if (!isVisible) {
            stopAnim();
            return;
        }
        startAnim();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (DrLog.isDebug()) {
            DrLog.e("wwh-" + TAG, "onAttachedToWindow :" + (getVisibility() == VISIBLE) + " ** name :" + getClass().getName());
        }
        if (getVisibility() == VISIBLE){
            startAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (DrLog.isDebug()) {
            DrLog.e("wwh-" + TAG, "onDetachedFromWindow :" + (getVisibility() == VISIBLE) + " ** name :" + getClass().getName());
        }
        stopAnim();
    }

    protected void startAnim() {
        //if (getLoadingPresenter() == null) return;

        startRefresh();
    }

    protected void stopAnim() {
        //if (getLoadingPresenter() == null) return;
        endRefresh();
    }

    public void setType(int type) {
        if (mLoadingView != null) mLoadingView.setLoadingType(type);
    }

    public void setAllowStatistics(boolean allowStatistics) {
        if (mLoadingView != null) mLoadingView.setAllowStatistics(allowStatistics);
    }
}