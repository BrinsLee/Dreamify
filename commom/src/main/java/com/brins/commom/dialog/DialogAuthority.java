package com.brins.commom.dialog;

/**
 * Created by buronehuang on 2019/1/17.
 */

public class DialogAuthority {
    /**
     * [0, 99]: lesser number for larger authority
     * [100, +&]: larger number for larger authority
     */

    public static final float STORMY = 0;

    /* join to show only if there is zero dialog in queue */
    public static final float LAZY = 1; //代码作者老司机，我只是代码搬运工。

    public static final float ORDER  = 100;


    /* the followings competes authority in order queue */

    /* 只有同一时段内要求显示的弹窗之间存在需要优先显示的弹窗，才需要
       为该弹窗赋予指定优先级。只要求不重叠则不需要指定优先级，会按加
       入队列的先后顺序依次显示。 */

    /* 请严格按照优先级【从低到高】依次【从上到下】进行声明 */

    /* 若需要在头部/中部插入新的优先级，允许且提倡修改已定义弹窗的优先
       级的绝对值（只要不改变原弹窗优先级之间的相对高低关系）。只有当需
       要修改的值太多时，才能使用浮点值 */
    public static final float ORDER_LYRIC_DIALOG = ORDER + 0.5f;//歌词弹窗引导 => 隐私协议弹窗>升级弹窗>订单弹窗>状态栏歌词弹窗>其他普通
    public static final float ORDER_DIALOG = ORDER + 1;//比账号弹窗的优先级100高一点

    /**
     * 隐私协议弹窗，优先级很高，并且两个弹窗有可能接连显示
     */
    public static final float PRIVACY_NOTICE_DIALOG1 = ORDER + 2;
    public static final float PRIVACY_NOTICE_DIALOG2 = ORDER + 2;

    public static final float UPDATE_APP = ORDER + 1; //app升级提示 > 启动后检测版本并自动弹出

}
