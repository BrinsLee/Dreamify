
package com.brins.commom.entity;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 屏幕类型
 * 
 * @author chenys
 */
@IntDef({ScreenType.HSCREEN, ScreenType.MSCREEN, ScreenType.LSCREEN})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScreenType {
    int HSCREEN = 0;
    int MSCREEN = 1;
    int LSCREEN = 2;
}
