package com.brins.commom.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.brins.commom.R;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.base.bar.BottomTabView;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.utils.KGPlayingBarUtil;
import com.brins.commom.utils.SystemUtils;
import com.brins.commom.utils.log.DrLog;
import com.brins.commom.widget.BottomBackgroundView;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by burone on 2017/4/6.
 */

public class AdditionalLayout extends FrameLayout {
    private View mQueuePanelView;
    private View mShareBarView;
    private View mPlayingBarView;
    private View mKtvMiniBarView;
    private View mKtvKRoomBarView;
    private View mKuqunMiniBarView;
    private View mAIMiniBarView;
    private View mReadNovelBarView;
    private View mAINovelMiniBarView;
    public View mMainBottomBarView;
    private View mMenuPanelView;
    private FrameLayout mPlayRingGuideRootView;
    private FrameLayout mMineTabGuideRootView;

    private ImageView mPlayMusicNote;
    private ImageView mInsertMusicNote;

    private View mBottomBgView;
    private LinearLayout mMainBarLayout;

    private AdditionalContent mAdditionalContent;

    private AnimatorSet bottomTabAnim;
    /**
     * 记录是否已经展示首页导航条，防止重复执行消失动画
     */
    private boolean hasShowBottomTab;

    private boolean hasPlayingBar;

    private boolean hasInitMove;

    private int mPlayingBarHeight;

    public AdditionalLayout(Context context, AdditionalContent additionalContent) {
        super(context);
        mPlayingBarHeight = context.getResources().getDimensionPixelSize(R.dimen.kg_playing_bar_album_background_size_with_shadow);
        this.mAdditionalContent = additionalContent;
        /*addView(mShareBarView = new View(context));
        addView(mAIMiniBarView = new View(context));
        addView(mReadNovelBarView = new View(context));
        addView(mAINovelMiniBarView = new View(context));
        addView(mPlayRingGuideRootView = new FrameLayout(context));*/
        addView(mBottomBgView = new View(context));
        addView(mMainBarLayout = new LinearLayout(context));
        mMainBarLayout.setOrientation(LinearLayout.VERTICAL);
        mMainBarLayout.addView(mPlayingBarView = new View(context));
        mMainBarLayout.addView(mMainBottomBarView = new View(context));
//        mMainBarLayout.setClickable(true);
        /*addView(mQueuePanelView = new View(context));
        addView(mKtvMiniBarView = new View(context));
        addView(mKtvKRoomBarView = new View(context));
        addView(mMenuPanelView = new View(context));
        addView(mMineTabGuideRootView = new FrameLayout(context));*/
        setId(R.id.additional_layout);
        /*addNoteViews(context);
        setTag(PageInfoTag.TAG_PAGE_INFO, PageIds.HOME_ADDITIONAL_LAYOUT);*/
    }

    private void addNoteViews(Context context) {
        addView(mPlayMusicNote = new ImageView(context),
                new ViewGroup.LayoutParams(-2, -2));
        addView(mInsertMusicNote = new ImageView(context),
                new ViewGroup.LayoutParams(-2, -2));
        mPlayMusicNote.setVisibility(GONE);
        mInsertMusicNote.setVisibility(GONE);
    }

    public void setQueuePanelRoot(View root) {
        int index = indexOfChild(mQueuePanelView);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT);
//        lp.bottomMargin = getResources().getDimensionPixelOffset(
//                R.dimen.kg_playing_bar_min_height);
        lp.gravity = Gravity.BOTTOM;

        addView(root, index, lp);
        removeView(mQueuePanelView);
        mQueuePanelView = root;
    }

    public void setTipRoot(View root) {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        int naviHeight = SystemUtils.getNavigationBarHeight(DRCommonApplication.getContext());
        //lp.bottomMargin = PlayerUtils.getMarginBottomPlayBar(getContext(), true, false, false) + (naviHeight > 60 ?  5 : naviHeight);
        addView(root, lp);
    }


    public void setSharePlayingBarRoot(View root) {
        int index = indexOfChild(mShareBarView);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;

        addView(root, index, lp);
        removeView(mShareBarView);
        mShareBarView = root;
    }

    public void setMainBottomBarRoot(View root) {
        int index = mMainBarLayout.indexOfChild(mMainBottomBarView);
        LayoutParams lp = (LayoutParams) mMainBarLayout.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        mMainBarLayout.setLayoutParams(lp);

        if (root.getParent() == null) {
            mMainBarLayout.addView(root, index);
            mMainBarLayout.removeView(mMainBottomBarView);
        }
        mMainBottomBarView = root;
    }

    public void setMainBottomBackground(View view) {
        int index = indexOfChild(mBottomBgView);
        if (view.getParent() == null) {
            addView(view, index);
            removeView(mBottomBgView);
        }
        mBottomBgView = view;
        resetBottomBg();
    }

    private void resetBottomBg() {
        if (mBottomBgView == null) {
            return;
        }
        // 设置高度
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        lp.gravity = Gravity.BOTTOM;
        mBottomBgView.setLayoutParams(lp);
        // 设置背景
        if (mBottomBgView instanceof BottomBackgroundView) {
            ((BottomBackgroundView) mBottomBgView).resetBackground();
        }
    }

    public void setMenuPanelView(View root) {
        int index = indexOfChild(mMenuPanelView);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.BOTTOM;

        addView(root, index, lp);
        removeView(mMenuPanelView);
        mMenuPanelView = root;
    }

    public void addToPlayRingGuideRootView(View view, int bottomMargin) {
        mPlayRingGuideRootView.removeAllViews();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        //bar总高度-阴影高度
        lp.bottomMargin = bottomMargin;
        view.setVisibility(VISIBLE);
        mPlayRingGuideRootView.addView(view, lp);
    }

    public void removeFromPlayRingGuideRootView(View view) {
        if (view != null) {
            mPlayRingGuideRootView.removeView(view);
        }
    }

    public void addToMineTabGuideRootView(View view, int bottomMargin) {
        if (mMineTabGuideRootView != null) {
            mMineTabGuideRootView.removeAllViews();
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            //bar总高度-阴影高度
            lp.bottomMargin = bottomMargin;
            view.setVisibility(VISIBLE);
            mMineTabGuideRootView.addView(view, lp);
        }

    }

    public void removeMineTabGuideRootView(View view) {
        if (view != null && mMineTabGuideRootView!=null) {
            mMineTabGuideRootView.removeView(view);
        }
    }

    public void setPlayingBarRoot(View root) {
        int index = mMainBarLayout.indexOfChild(mPlayingBarView);
        LayoutParams lp = (LayoutParams) mMainBarLayout.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        mMainBarLayout.setLayoutParams(lp);

        mMainBarLayout.addView(root, index);
        mMainBarLayout.removeView(mPlayingBarView);
        mPlayingBarView = root;
    }

    public void movePlayingBarRoot(boolean hasPlayingBar, boolean isMainPage) {
        movePlayingBarRoot(hasPlayingBar, isMainPage, true);
    }

    public void movePlayingBarRoot(boolean hasPlayingBar, boolean isMainPage, boolean withAnim) {
        if (mMainBottomBarView == null || !(mMainBottomBarView instanceof BottomTabView)) {
            return;
        }
        ((BottomTabView) mMainBottomBarView).setIsOnResume(isMainPage);
        if (!hasInitMove || hasShowBottomTab != isMainPage || this.hasPlayingBar != hasPlayingBar) {
            //防止重复执行消失动画
            int curBarHeight = isMainPage ? KGPlayingBarUtil.getMainPlayingbarHeight() : KGPlayingBarUtil.getPlayingBarHeight();
            if (!hasPlayingBar) {
                curBarHeight += KGPlayingBarUtil.getListenPartHeight();
            }
            int bottomHeight = KGPlayingBarUtil.getmBottomTabHeight();
            int maxHeight = (isMainPage ? 0 : bottomHeight) + (!hasPlayingBar ? curBarHeight : 0);
            int minHeight = 0;
            float translationY;
            if (isMainPage) {
                translationY = minHeight;
            } else {
                translationY = maxHeight;
            }
            if (withAnim) {
                animBottomTab(translationY, isMainPage);
            } else {
                mMainBarLayout.setTranslationY(translationY);
                mBottomBgView.setTranslationY(translationY);
                if (isMainPage) {
                    mMainBottomBarView.setVisibility(View.VISIBLE);
                    mBottomBgView.setVisibility(View.VISIBLE);
                } else {
                    mMainBottomBarView.setVisibility(View.INVISIBLE);
                    mBottomBgView.setVisibility(View.GONE);
                    //EventBus.getDefault().post(new BottomTabGoneEvent());
                }
            }
        }
        /*PatchAdControlDisplayMgr.getInstance().onNavBottomTabViewVisible(hasShowBottomTab != isMainPage, isMainPage);
        this.hasShowBottomTab = isMainPage;
        this.hasPlayingBar = hasPlayingBar;
        KGPlayingBarUtil.setPlayingBarShowing(hasPlayingBar);
        this.hasInitMove = true;
        if (!isMainPage) {
            //通知我的tab引导view消失
            EventBus.getDefault().post(new BottomTabGuideEvent(BottomTabGuideEvent.MINE_TAB_HIDE));
        }*/
    }

    private void animPlayingBar(final View bottom, final View playbar, int min, int max, boolean up){
        DrLog.d("wuhqal", "animPlayingBar");
        ValueAnimator va;
        if (up) {
            va = ValueAnimator.ofInt(min, max);
        } else {
            va = ValueAnimator.ofInt(max, min);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int h =(Integer)valueAnimator.getAnimatedValue();
//				((LayoutParams)playbar.getLayoutParams()).bottomMargin = h;
                ((LayoutParams)bottom.getLayoutParams()).height = h;
//				playbar.requestLayout();
                bottom.requestLayout();
            }
        });
        va.setDuration(200);
        va.start();
    }

    private void animBottomTab(float translationY, final boolean show) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMainBarLayout, "translationY", translationY);
        ObjectAnimator bgAnimator = ObjectAnimator.ofFloat(mBottomBgView, "translationY", translationY);
        if (bottomTabAnim != null) {
            bottomTabAnim.cancel();
        }
        bottomTabAnim = new AnimatorSet();
        bottomTabAnim.playTogether(animator, bgAnimator);
        bottomTabAnim.setInterpolator(new LinearInterpolator());
        bottomTabAnim.setDuration(200);

        bottomTabAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mMainBottomBarView.setVisibility(View.VISIBLE);
                mBottomBgView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!show) {
                    mMainBottomBarView.setVisibility(View.INVISIBLE);
                    mBottomBgView.setVisibility(View.GONE);
                    //EventBus.getDefault().post(new BottomTabGoneEvent());
                } else {
                    mMainBottomBarView.setVisibility(View.VISIBLE);
                    mBottomBgView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        bottomTabAnim.start();
    }
    
    
    
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    private boolean applySkin(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        if (d != null) {
            d.setColorFilter(SkinResourcesUtils.getInstance().getColor("skin_common_widget",
                    R.color.skin_common_widget), PorterDuff.Mode.SRC_ATOP);
            return true;
        }
        return false;
    }

    public void onSkinAllChanged() {
        resetBottomBg();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        /**
         * While a window lost its window-focus, a view who is focused within this window
         * (if exist) will *NOT* lost its focus. What will happen? Think about such a
         * situation: A focused view "FV" is on the bottom of the screen, a minute later
         * it covered with a dialog (which contains an EditText). Then an Input-Keyboard
         * rises up. "FV"'s window (ViewRootImpl#draw()->scrollToRectOrFocus()) will scroll
         * its canvas to make sure the focus "FV" to be visible. But in this situation we
         * don't care about what has been covered with Keyboard on the low-layer window,
         * we just care about things within the hight-layer window: the Dialog's window,
         * and those are the actually focused things to user. Scroll the "FV"'s window
         * is "怪怪的", so we clear the focused view while window lost its window-focus.
         */
        if (!hasWindowFocus && hasFocus()) {
            View focus = findFocus();
            if (focus != null) {
                if (DrLog.DEBUG) {
                    DrLog.i("burone-focus", "如果你在AdditionalLayout里有什么关于焦点的异常，查看下这里。" +
                            "这个焦点View即将被清除焦点：" + " focus = " + focus.toString());
                }
                focus.setFocusable(false);
                focus.setFocusableInTouchMode(false);
                /**
                 * must after {@link #setFocusable} and {@link #setFocusableInTouchMode},
                 * for the method {@link #clearFocus()} will clear the target's focus
                 * and then find the new focus at the same time. If the target is enable
                 * to receive the focus ,it will take it again before this method return.
                 * So, make it be disable.
                 */
                focus.clearFocus();
                if (DrLog.DEBUG) {
                    DrLog.i("burone-focus", "isFocused = " + focus.isFocused()
                            + ", onWindowFocusChanged. focusable = " + focus.isFocusable()
                            + ", focusableintouchmode = " + focus.isFocusableInTouchMode());
                }
            }
        }
    }

    public View getPlayingBarView() {
        return mPlayingBarView;
    }

    public View getMainBottomBarView() {
        return mMainBottomBarView;
    }

    public View getBottomBgView() {
        return mBottomBgView;
    }

    public void controlBarVisibility(boolean show, boolean hasMainBottomView) {
        movePlayingBarRoot(show, hasMainBottomView);
    }

    public boolean isHasShowBottomTab() {
        return hasShowBottomTab;
    }

    public boolean isHasInitMove() {
        return hasInitMove;
    }
}
