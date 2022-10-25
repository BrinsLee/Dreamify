package com.brins.commom.utils;

import android.support.annotation.IntRange;
import android.view.View;

/**
 * @Description: From one bug to another bug
 * @Author: weijiawei
 * @Date: 2022/8/5 17:06
 */
public class SystemBarUtil {
    public static void setSystemUiVisibility(View decorView,
                                             @IntRange(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int flag,
                                             @IntRange(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int toKeepFlag) {
        decorView.setSystemUiVisibility(flag | (decorView.getSystemUiVisibility() & toKeepFlag));
    }
}
