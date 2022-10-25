package com.brins.commom.playingbar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.brins.commom.R
import com.brins.commom.profile.SkinProfileUtil
import com.brins.commom.skin.SkinResourcesUtils
import com.brins.commom.utils.KGPlayingBarUtil

class BottomBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    init {
        setBackgroundColor(Color.WHITE)
    }

    fun resetBackground() {
        // 设置高度
        val height = if (SkinProfileUtil.isOnlineSkin() || SkinProfileUtil.isCustomSkin()) {
            KGPlayingBarUtil.getmBottomTabHeight() + KGPlayingBarUtil.getPlayingBarAvatarHeight() / 2
        } else {
            KGPlayingBarUtil.getmBottomTabHeight()
        }
        if (layoutParams == null) {
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, height
            )
            lp.gravity = Gravity.BOTTOM
            layoutParams = lp
        } else {
            layoutParams.height = height
        }
        // 设置背景
        //修改时请注意 下面设置底部bar的代码应该要和com.kugou.common.base.AbsSkinActivity.updateNavigationBg代码处一致！！！
        if (SkinProfileUtil.isDefaultLocalSimpleSkin() || SkinProfileUtil.isBlurOrSolidSkin()) {
            setBackgroundColor(Color.WHITE)
        } else if (SkinProfileUtil.isDefaultLocalDarkNightSkin()) {
            setBackgroundResource(R.color.kg11_main_bg_color_dark)
        } else {
            background = SkinResourcesUtils.getInstance().getBottomTabBg(height)
        }
    }

}