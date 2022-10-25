package com.brins.commom.widget.accessibility;

/**
 * @author jamywang
 * @since 2018/5/31 17:50
 */

public class AccessibilityImgEntity {
    public int imgId;
    public String imgName;
    public String contentDesc;
    public String remark;
    public boolean needSpeak;

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isNeedSpeak() {
        return needSpeak;
    }

    public void setNeedSpeak(boolean needSpeak) {
        needSpeak = needSpeak;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
