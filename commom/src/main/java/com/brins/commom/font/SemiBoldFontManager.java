package com.brins.commom.font;

import android.content.Context;
import android.graphics.Typeface;
import com.brins.commom.app.DRCommonApplication;
import com.brins.commom.playing.PlayingItemViewUtil;

/**
 * Created by tingcao on 2019/11/25.
 */
public class SemiBoldFontManager {

    private Typeface mFontType;

    private static class InstanceHolder {
        public static SemiBoldFontManager INSTANCE = new SemiBoldFontManager();
    }

    public static SemiBoldFontManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private SemiBoldFontManager() {
        Context  context = DRCommonApplication.getContext();
        //读取字体文件存在ICON操作低端机可能会出现卡顿的问题
        if(!PlayingItemViewUtil.isLowMachine()){
            try{
                mFontType = Typeface.createFromAsset(context.getAssets(), "fonts/akrobat-semibold.ttf");
            }catch (Exception e){
                mFontType = Typeface.DEFAULT;
            }
        }else {
            mFontType = Typeface.DEFAULT;
        }
    }

    public Typeface getFontType() {
        return mFontType;
    }
}
