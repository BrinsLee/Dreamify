package com.brins.commom.utils;

import android.content.Context;

/**
 * @author lipeilin
 * @date 2022/10/14
 * @desc
 */
public class Utils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
