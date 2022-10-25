package com.brins.commom.widget.accessibility;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

/**
 * @author jamywang
 * @since 2018/6/14 0:09
 */

public class AccessibilityViewEntity {
    private int srcId;

    private String remark;

    private WeakReference<ImageView> viewRef;

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Nullable
    public ImageView getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public void setView(ImageView view) {
        viewRef = new WeakReference<ImageView>(view);
    }
}
