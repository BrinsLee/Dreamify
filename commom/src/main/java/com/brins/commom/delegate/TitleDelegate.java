
package com.brins.commom.delegate;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brins.commom.R;
import com.brins.commom.delegate.AbstractDelegate;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.ColorUtil;
import com.brins.commom.skin.SkinBasicTransText;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.AnimUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.widget.KGTransImageButton;
import com.brins.commom.widget.KGTransTextView;
import java.util.List;

public class TitleDelegate extends AbstractDelegate implements OnClickListener {

	private boolean isSkinEnable = true;
	private boolean disableSkinCall;
	private ImageButton mBtnBack,mBtnSearch;
	private KGTransImageButton mBtnCustom;
	private View mBackground;
	/**
	 * 自定义文字
	 */
	private KGTransTextView mTextCustom;
	/**
	 * 标题栏文字标题
	 */
	private TextView mTextTitle;

	public TitleDelegate(DelegatePage page) {
		super(page);
	}

	public TitleDelegate(DelegateFragment fragment) {
		super(fragment);
	}

	public TitleDelegate(DelegateActivity activity) {
		super(activity);
	}

	public static interface OnTopDoubleClickCallback {
		public void onTopDoubleClick(View v);
	}

	/**
	 * 设置双击顶部标题栏的回调
	 *
	 * @param callback
	 */
	public void setOnTopDoubleClickCallback(OnTopDoubleClickCallback callback) {
		mOnTopDoubleClickCallback = callback;
	}

	private OnTopDoubleClickCallback mOnTopDoubleClickCallback;

	public static interface OnBackClickCallback {
		public void onBackClick(View v);
	}

	/**
	 * 设置返回Back按钮的回调
	 *
	 * @param callback
	 */
	public void setOnBackClickCallback(OnBackClickCallback callback) {
		mOnBackClickCallback = callback;
	}

	public OnBackClickCallback getmOnBackClickCallback() {
		return mOnBackClickCallback;
	}

	private OnBackClickCallback mOnBackClickCallback;

	public static interface OnSearchClickCallback {
		public void onSearchClick(View v);
	}

	/**
	 * 设置搜索Search按钮的回调
	 *
	 * @param callback
	 */
	public void setOnSearchClickCallback(OnSearchClickCallback callback) {
		setOnSearchClickCallback(callback, true);
	}

	/**
	 * 设置搜索Search按钮的回调
	 *
	 * @param callback
	 */
	public void setOnSearchClickCallback(OnSearchClickCallback callback, boolean setBtnVisible) {
		mOnSearchClickCallback = callback;
		if (setBtnVisible) {
			setSearchButtonVisible(true);
		}
	}

	private boolean isSearchButtonVisible;
	/**
	 * 设置搜索按钮可见性
	 *
	 * @param visible
	 */
	public void setSearchButtonVisible(boolean visible) {
		isSearchButtonVisible = visible;
		if (mBtnSearch != null) {
			mBtnSearch.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	private OnSearchClickCallback mOnSearchClickCallback;

	public static interface OnCustomClickCallback {
		public void onCustomClick(View v);
	}

	/**
	 * 设置custom按钮的回调
	 *
	 * @param callback
	 */
	public void setOnCustomClickCallback(OnCustomClickCallback callback) {
		mOnCustomClickCallback = callback;
		setCustomButtonVisible(true);
	}

	private OnCustomClickCallback mOnCustomClickCallback;

	private boolean isCustomButtonVisible;

	/**
	 * 设置custom按钮可见性
	 *
	 * @param visible
	 */
	public void setCustomButtonVisible(boolean visible) {
		isCustomButtonVisible = visible;
		if (mBtnCustom != null) {
			mBtnCustom.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * custom按钮是否可见
	 *
	 * @return
	 */
	public boolean isCustomButtonVisible() {
		if (mBtnCustom != null && mBtnCustom.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}

	/**
	 * 设置custom按钮图标
	 *
	 * @param d
	 */
	public void setCustomButtonImage(Drawable d) {
		if (mBtnCustom != null) {
			mBtnCustom.setImageDrawable(d);
		}
	}

	/**
	 * 设置返回按钮图标
	 *
	 * @param d
	 */
	public void setBackButtonImage(Drawable d) {
		if (mBtnBack != null) {
			mBtnBack.setImageDrawable(d);
		}
	}

	public void setBackButtonColorFilter(int color){
		if (mBtnBack != null && mBtnBack.getDrawable()!=null) {
			mBtnBack.getDrawable().setColorFilter(color,PorterDuff.Mode.SRC_IN);
		}
	}

	/**
	 * 设置返回按钮图标
	 *
	 * @param drawableId
	 */
	public void setBackButtonImage(int drawableId) {
		if (mBtnBack != null) {
			mBtnBack.setImageResource(drawableId);
		}
	}

	public void setBackButtonImageWithSkin(int drawableId){
		if (mBtnBack != null) {
			mBtnBack.setImageResource(drawableId);
			mBtnBack.setColorFilter(SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE_PRIMARY_COLOR), PorterDuff.Mode.SRC_IN);
		}
	}

	/**
	 * 设置返回按钮背景图片
	 *
	 * @param drawable
	 */
	public void setBackButtonBackImage(Drawable drawable) {
		if (mBtnBack != null) {
			mBtnBack.setBackgroundDrawable(drawable);
		}
	}

	/**
	 * 设置custom按钮图标资源
	 *
	 * @param resId
	 */
	public void setCustomButtonImage(int resId) {
		if (mBtnCustom != null) {
			mBtnCustom.setImageResource(resId);
		}
	}

	public View getCustomButton() {
		return mBtnCustom;
	}


	/**
	 * 设置返回按钮可见性
	 *
	 * @param visible
	 */
	public void setBackButtonVisible(int visible) {
		if (mBtnBack != null) {
			mBtnBack.setVisibility(visible);
		}
	}

	/**
	 * 设置返回按钮可见性
	 *
	 * @param visible
	 */
	public void setTitleVisible(int visible) {
		if (mTextTitle != null) {
			mTextTitle.setVisibility(visible);
		}
	}

	public void setCommonTitleBarVisible(boolean visible) {
		if (mBackground != null) {
			mBackground.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	@Override public void onClick(View v) {

	}

	@Override public void init() {

	}

	public void onResume() {

	}

	public void onDestroy() {
		mOnTopDoubleClickCallback = null;
		mOnCustomClickCallback = null;
		mOnSearchClickCallback = null;
	}

	public static final int STYLE_SAMPLE = 0;
	public static final int STYLE_TRANSPARENT = 1;

	public void changeTitlebarStyle(int style) {

		switch (style) {
			case STYLE_SAMPLE:
				resetTitleBackground();
				setTitleVisible(View.VISIBLE);
				break;
			case STYLE_TRANSPARENT:
				setTitleBackgroundColor(Color.TRANSPARENT);
				setTitleVisible(View.INVISIBLE);
				break;
		}
		mTextTitle.invalidate();

	}

	private boolean isTittleBackgroundBeSeted = false;

	private boolean isUseCustomBg = false;  //是否自定义标题背景

	public void setUseCustomBg(boolean useCustomBg) {
		isUseCustomBg = useCustomBg;
	}

	/**
	 * 恢复标题栏默认背景
	 */
	public void resetTitleBackground() {
		if (!isUseCustomBg) {
			setTitleBackground();
		}
		isTittleBackgroundBeSeted = false;
	}


	private void setTitleBackground() {
		if (mBackground != null) {
			if (SkinProfileUtil.isCustomSkin()) {
				mBackground.setBackgroundDrawable(SkinResourcesUtils.getInstance().getCustomTitle());
			}else if (SkinProfileUtil.isDefaultSkin()){
				mBackground.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.skin_title));
			} else {
				mBackground.setBackgroundColor(SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE));
			}
		}
	}

	/**
	 * 设置标题栏背景
	 *
	 * @param resId
	 */
	public void setTitleBackground(int resId) {
		if (mBackground != null) {
			mBackground.setBackgroundResource(resId);
		}
		isTittleBackgroundBeSeted = true;
	}

	/**
	 * 设置标题栏背景 颜色
	 *
	 * @param color
	 */
	public void setTitleBackgroundColor(int color) {
		if (mBackground != null) {
			mBackground.setBackgroundColor(color);
		}
		isTittleBackgroundBeSeted = true;
	}

	/**
	 * 设置标题栏背景 颜色
	 *
	 * @param drawable
	 */
	public void setTitleBackgroundDrawable(Drawable drawable) {
		if (mBackground != null) {
			mBackground.setBackgroundDrawable(drawable);
		}
		isTittleBackgroundBeSeted = true;
	}

	public void onSkinColorChanged() {
		if (disableSkinCall) {
			return;
		}
		if (!isTittleBackgroundBeSeted && !isUseCustomBg) {
			setTitleBackground();
		}
		if (mTextCustom != null) {
			Drawable bgDrawable = mTextCustom.getBackground();
			if (bgDrawable != null && bgDrawable instanceof GradientDrawable) {
				GradientDrawable gradientDrawable = (GradientDrawable) bgDrawable;
				gradientDrawable.setStroke(SystemUtils.dip2px(1f), ColorUtil.getHybridAlphaColor(
					SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE_PRIMARY_COLOR),
					0.6f));
				mTextCustom.setBackgroundDrawable(gradientDrawable);
			}
		}
		setTitleSkinMode(isSkinEnable);
	}
	//是否支持换肤
	public void setTitleSkinMode(boolean isEnableSkin) {}

	public String getCommonTitile() {
		if (mTextTitle != null)
			return mTextTitle.getText().toString();
		return "";
	}
}
