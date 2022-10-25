package com.brins.commom.widget;

import android.support.annotation.IntRange;


/**
 * 用一个整形值保存多个时间值，值的范围：0~255，以后满足不了再扩展。
 * 0～8位：primaryTime
 * 9～16位：secondaryTime
 * 17~32位：未使用
 */
public class TimeSpec {
    private static final int MODE_SHIFT = 8;
    private static final int MODE_PRIMARY_MASK = 0xFF;
    private static final int MODE_SECONDARY_MASK = 0xFF00;

    /**
     * @param primaryTime range:0~255, unit:second(s)
     * @param secondaryTime range:0~255, unit:second(s)  如果你的参数是一个float或者double类型，并且一定要在某个范围内，你可以使用@FloatRange注解
     * @return
     */
    public static int makeTime(@IntRange(from = 0, to = 255) int primaryTime,
                               @IntRange(from = 0, to = 255) int secondaryTime) {
        return (MODE_PRIMARY_MASK & primaryTime)
                + (secondaryTime << MODE_SHIFT & MODE_SECONDARY_MASK);
    }

    public static int makeTime(int primaryTime) {
        return makeTime(primaryTime, 0);
    }

    public static int getPrimaryTime(int timeSpec) {
        return timeSpec & MODE_PRIMARY_MASK;
    }

    public static int getSecondaryTime(int timeSpec) {
        return (timeSpec & MODE_SECONDARY_MASK) >> MODE_SHIFT;
    }
}
