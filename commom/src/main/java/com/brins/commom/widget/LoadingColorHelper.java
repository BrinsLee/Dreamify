package com.brins.commom.widget;

import android.graphics.Color;
import com.brins.commom.entity.SkinColorType;
import com.brins.commom.profile.SkinProfileUtil;
import com.brins.commom.skin.SkinResourcesUtils;

public class LoadingColorHelper {

    /**
     * {
     *     K_SIGN_COLOR, // K字标的颜色
     *     K_SIGN_BACKGROUND_COLOR, // K字外圈背景的颜色
     *     ARC_COLOR, // 扇形进度条颜色
     *     MESSAGE_COLOR // 信息颜色
     * }
     * */

    public static final int[] COLOR_ARRAY_TEST = new int[] {
            0xffff0000,
            0x33ff0000,
            0xffff0000,
            0xffff0000,
    };

    public static final int TYPE_LITE_REGULAR = 1;
    public static final int TYPE_LITE_DELAY = 2;
    public static final int TYPE_DARK_REGULAR = 3;
    public static final int TYPE_DARK_DELAY = 4;

    //
    public static final int[] COLOR_ARRAY_LITE_REGULAR = new int[] {
            0xff5ecef7,
            0x205ecef7,
            0xff5ecef7,
            0xb2000000
    };
    public static final int[] COLOR_ARRAY_LITE_DELAY = new int[] {
            0xfffeb454,
            0x20feb454,
            0xfffeb454,
            0xb2000000
    };
    public static final int[] COLOR_ARRAY_DARK_REGULAR = new int[] {
            0xff5ecef7,
            Color.WHITE,
            Color.WHITE,
            0xb2ffffff
    };
    public static final int[] COLOR_ARRAY_DARK_DELAY = new int[] {
            0xfffeb454,
            Color.WHITE,
            Color.WHITE,
            0xb2ffffff
    };

    private static final int[] COLOR_NONE = new int[] {
            0, 0, 0, 0
    };

    public static int getRegularTextColor() {
//        if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
//            return SkinResourcesUtils.getInstance().getColor(SkinColorType.SECONDARY_TEXT);
//        }
//        return getRegularColor(true)[3];
        return SkinResourcesUtils.getInstance().getColor(SkinColorType.SECONDARY_TEXT);
    }

    private static boolean isLiteMode() {
        return SkinProfileUtil.isDefaultSkin() || SkinProfileUtil.isDefaultLocalSimpleSkin() || SkinProfileUtil.isSolidSkin();
    }

    public static int[] getRegularColor(boolean withDarkAdjust) {
        if (!withDarkAdjust) {
            return COLOR_ARRAY_LITE_REGULAR;
        }
        if (isLiteMode()) {
            return COLOR_ARRAY_LITE_REGULAR;
        } else {
            return COLOR_ARRAY_DARK_REGULAR;
        }
    }

    public static int[] getColorWithNoSkin(boolean isLight, boolean delay) {
        if (isLight) {
            if (!delay) {
                return COLOR_ARRAY_LITE_REGULAR;
            } else {
                return COLOR_ARRAY_LITE_DELAY;
            }
        } else {
            if (!delay) {
                return COLOR_ARRAY_DARK_REGULAR;
            } else {
                return COLOR_ARRAY_DARK_DELAY;
            }
        }
    }

    public static int[] getDelayColor(boolean withDarkAdjust) {
        if (!withDarkAdjust) {
            return COLOR_ARRAY_LITE_DELAY;
        }
        if (isLiteMode()) {
            return COLOR_ARRAY_LITE_DELAY;
        } else {
            return COLOR_ARRAY_DARK_DELAY;
        }
    }

}
