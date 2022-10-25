package com.brins.commom.base;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by weihaowu on 2019/12/9.
 */

/**
 * light，表示的是底部背景浅色，所以对应的是深色黑色状态栏文字，
 * dark相反
 * @return
 */
@IntDef({IStatusBarActionType.TYPE_NONE, IStatusBarActionType.TYPE_KEEP_LIGHT, IStatusBarActionType.TYPE_KEEP_DARK, IStatusBarActionType.TYPE_CHANGE_BY_SKIN})
@Retention(RetentionPolicy.SOURCE)
public @interface IStatusBarActionType {
    int TYPE_NONE = 0;  //不做任何处理
    int TYPE_KEEP_LIGHT = 1;    //维持白底黑字状态栏
    int TYPE_KEEP_DARK = 2;     //维持黑底白字状态栏
    int TYPE_CHANGE_BY_SKIN = 3;    //跟随当前酷狗皮肤改变。极简皮肤跟需要黑字状态栏的皮肤用白底黑字，其他皮肤用黑底白字
}