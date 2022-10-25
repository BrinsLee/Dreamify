
package com.brins.commom.entity;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 屏幕大小类型
 * 
 * @author heyangbin
 */
@IntDef({ScreenSizeType.XSCREEN, ScreenSizeType.HSCREEN, ScreenSizeType.MSCREEN, ScreenSizeType.LSCREEN})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScreenSizeType {
    int XSCREEN= 0;
    int HSCREEN= 1;
    int MSCREEN= 2;
    int LSCREEN= 3;
}
