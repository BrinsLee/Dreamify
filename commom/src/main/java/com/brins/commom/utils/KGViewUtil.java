package com.brins.commom.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;
import com.brins.commom.widget.CommonAlphaBgImageView;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunzheng on 15/11/24.
 */
public class KGViewUtil {

    private static final float ALPHA_PRESSED = 0.3f;

    private static final float ALPHA_NORMAL = 1f;

    public static void updateVisibility(View view, boolean visiable) {
        if (view != null) {
            view.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
    }

    public static void updateVisibilities(View v1, View v2, View v3, boolean visible) {
        updateVisibility(v1, visible);
        updateVisibility(v2, visible);
        updateVisibility(v3, visible);
    }

    public static void updateVisibility(View parent, int childId, boolean visiable) {
        updateVisibility(parent.findViewById(childId), visiable);
    }

    public static void updateVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 硬件加速的开关
     */
    public static void setLayerType(View view, int type, Paint paint) {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            view.setLayerType(type, paint);
        }
    }

    /**
     * 判断一个View当前是否可见
     *
     * @param view
     */
    public static boolean isVisiable(View view) {
        if (view == null) {
            return false;
        }
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * 延后使View可用
     */
    public static void enableDelay(final View view, final long time) {
        if (view.isEnabled()) {
            view.setEnabled(false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, time);
        }
    }

    public static void enableViewDelay(View v, int time) {
        final View view = v;
        if (view != null) {
            view.setEnabled(false);
            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, time);
        }
    }

    private static final HashMap<String, Long> tag_Tims = new HashMap<>();

    /**
     * 一个操作的延时响应时间
     *
     * @param tag       操作标示
     * @param delaytime 该操作执行后，需要保护的时间
     * @return
     */
    public static boolean canTagAction(String tag, int delaytime) {
        synchronized (tag_Tims) {
            if (tag != null) {
                Long usabletime = tag_Tims.get(tag);
                if (usabletime == null || System.currentTimeMillis() > usabletime) {//当前时间已经超过可用时间
                    tag_Tims.put(tag, System.currentTimeMillis() + delaytime);//把更新后的时间
                    return true;
                } else {//当前时间未超过可用时间
                    return false;
                }
            }
            return true;
        }
    }

    private static final Map<String, Boolean> tag_Boolean = Collections.synchronizedMap(new HashMap<String, Boolean>());

    /**
     * 保存一个操作的相应时间
     *
     * @param tag
     * @return
     */
    public static boolean getTagBoolean(String tag) {
        synchronized (tag_Boolean) {
            if (tag != null) {
                Boolean action = tag_Boolean.get(tag);
                if (action == null || action) {
                    return true;
                } else {//当前时间未超过可用时间
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 该方法是临时的打补丁方案
     *
     * @param tag
     * @param action
     * @param maxResetTime
     * @return
     */
    public static boolean setTagBoolean(final String tag, final Boolean action, final long maxResetTime) {
        synchronized (tag_Boolean) {
            if (tag != null) {
                if (!action) {//立即关闭 action=false
                    tag_Boolean.put(tag, action);
                    if (maxResetTime > 0) {//在关闭的时候，可以设置延时还原时间，防止异常导致再也无法开启
                        KGThreadPool.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(maxResetTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tag_Boolean.put(tag, true);

                            }
                        });
                    }
                } else {//延迟开启 action=false
                    KGThreadPool.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(maxResetTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            tag_Boolean.put(tag, true);
                        }
                    });
                }
            }

            return true;
        }
    }

    public static void setPaddingTop(View view, int top) {
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingBottom();
        view.setPadding(left, top, right, bottom);
    }

    public static boolean isPressedOrFocusedOrSelected(View view) {
        if (view == null)
            return false;

        return (view.isPressed() || view.isFocused() || view.isSelected());
    }


    public static void updateDrawableStateWithTextAlpha(View view, float textAlpha) {
        float alpha = KGViewUtil.isPressedOrFocusedOrSelected(view) ? ALPHA_PRESSED : ALPHA_NORMAL;
        view.setAlpha(alpha * textAlpha);
    }

    public static void updateViewHeight(View view, int height) {
        if (view == null)
            return;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null || params.height == height)
            return;

        params.height = height;
        view.setLayoutParams(params);
    }

    public static void updateViewWidth(View view, int width) {
        if (view == null)
            return;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null || params.width == width)
            return;

        params.width = width;
        view.setLayoutParams(params);
    }

    public static void setText(View view, String text) {
        if (view == null || !(view instanceof TextView))
            return;

        ((TextView) view).setText(text);
    }
    public static void setPaddingLeft(View view, int left) {
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingBottom();
        view.setPadding(left, top, right, bottom);
    }

    public static void setViewPadding(View view ,int left,int top,int right,int bottom){
        view.setPadding(left,top,right,bottom);
    }

    public static void setMarginLeft(View view, int left) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.leftMargin = left;
            view.setLayoutParams(params);
        }else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.leftMargin = left;
            view.setLayoutParams(params);
        }else if (view.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            params.leftMargin = left;
            view.setLayoutParams(params);
        }
    }


    /**
     * EditText竖直方向是否可以滚动
     * @param editText  需要判断的EditText
     * @return  true：可以滚动   false：不可以滚动
     */
    public static boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    public static void setCommonTabBg(Context context, CommonAlphaBgImageView tabBgView, List<Drawable> drawableArrayList) {
        if (context == null || tabBgView == null || drawableArrayList == null){
            return;
        }
        drawableArrayList.clear();
        if (SkinProfileUtil.isDefaultSkin()) {
            drawableArrayList.add(0, new BitmapDrawable(BitmapUtil.createColorBitmap(
                SkinResourcesUtils.getInstance().getColor(SkinColorType.TITLE))));
        } else {
            drawableArrayList.add(0, new BitmapDrawable(BitmapUtil.createColorBitmap(SkinResourcesUtils.getInstance().getColor(SkinColorType.DATE_PRESSED_TEXT))));
        }
        tabBgView.setDrawableLists(drawableArrayList);
    }
}
