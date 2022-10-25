package com.brins.commom.widget.accessibility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.kugou.uilib.widget.imageview.KGUIImageView;

/**
 * @author jamywang
 * @since 2018/5/31 17:47
 */

public class AccessibilityImageView extends KGUIImageView {

    public static final String TAG = "AccessibilityImageView";

    private AccessibilityAttrProvider mProvider = new AccessibilityAttrProvider();

    public AccessibilityImageView(Context context) {
        this(context, null);
    }

    public AccessibilityImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccessibilityImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mProvider.init(context, attrs);
        //避免覆盖掉手动设置的
        if (mProvider.needSpeak() && TextUtils.isEmpty(getContentDescription())){
            setContentDescription(AccessibilityHelper.getInstance().getContentDescBySrcName(mProvider.getSrcId(), mProvider.getRemark(), this));
        }
    }

    public void setAccessibilityRemark(String remark){
        mProvider.setRemark(remark);
        setContentDescription(AccessibilityHelper.getInstance().getContentDescBySrcName(mProvider.getSrcId(), mProvider.getRemark()));
    }
}
