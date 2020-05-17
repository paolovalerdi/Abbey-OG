package com.paolovalerdi.abbey.glide

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ATHUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteTarget
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.extensions.getSuitableColorFor
import com.paolovalerdi.abbey.util.extensions.isTopRegionLight
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

abstract class AbbeyNowPlayingColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val currentNowPlayingScreen = PreferenceUtil.nowPlayingScreen

    private val defaultFooterColor: Int
        get() = if (currentNowPlayingScreen == NowPlayingScreen.ABBEY) {
            Color.WHITE
        } else ATHUtil.resolveColor(view.context, R.attr.defaultFooterColor)

    private val backgroundColor: Int = when (currentNowPlayingScreen) {
        NowPlayingScreen.ABBEY -> Color.parseColor("#161616")
        NowPlayingScreen.BLUR -> Color.parseColor("#161616")
        else -> ThemeStore.primaryColor(view.context)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorsReady(defaultFooterColor, false)
    }

    override fun onResourceReady(resource: BitmapPaletteWrapper, glideAnimation: Transition<in BitmapPaletteWrapper>?) {
        super.onResourceReady(resource, glideAnimation)
        val lightColors = (currentNowPlayingScreen == NowPlayingScreen.ABBEY) or (currentNowPlayingScreen == NowPlayingScreen.BLUR)
        onColorsReady(
            resource.palette.getSuitableColorFor(backgroundColor, defaultFooterColor, lightColors),
            resource.bitmap.isTopRegionLight(view.height / 4, view.width)
        )
    }

    abstract fun onColorsReady(color: Int, topIsLight: Boolean = false)
}
