package com.brins.commom.widget.accessibility;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.brins.commom.R;
import com.brins.commom.skin.SkinConfig;

/**
 * 描述
 *
 * @author jamywang
 * @since 2018/6/30 16:02
 */

public class AccessibilityAttrProvider {
    public String mIconName;

    public String mRemark;

    boolean mNeedSpeak;

    int mSrcId;

    String contentDescription;

    public void init(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccessibilityImageView);
        if (ta != null) {
            mIconName = ta.getString(R.styleable.AccessibilityImageView_img_name);
            mRemark = ta.getString(R.styleable.AccessibilityImageView_remark);
            mNeedSpeak = ta.getBoolean(R.styleable.AccessibilityImageView_need_speak,true);
            if (attrs != null) {
                contentDescription = attrs.getAttributeValue(SkinConfig.XML_NAME_SPACE, "contentDescription");
            }

//            KGLog.d(TAG, "AccessibilityImageView:   initattr  " + mIconName + "     " + mRemark);
            ta.recycle();
        }
        mSrcId = getViewSourceId(attrs);
    }

    public int getViewSourceId(AttributeSet attrs){
        if (attrs != null){
            final String xmlns="http://schemas.android.com/apk/res/android";
            int xmlRes = attrs.getAttributeResourceValue(xmlns, "src", -1);
//            Log.d("AccessibilityHelper", "xml res id:  " + xmlRes);
            return xmlRes;
        }
        return 0;
    }

    public void setRemark(String mRemark) {
        this.mRemark = mRemark;
    }

    public String getRemark() {
        return mRemark;
    }

    public int getSrcId() {
        return mSrcId;
    }

    public boolean needSpeak() {
        return mNeedSpeak;
    }

    public String getContentDescription() {
        return contentDescription;
    }
}
