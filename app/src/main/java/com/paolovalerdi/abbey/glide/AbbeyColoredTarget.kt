package com.paolovalerdi.abbey.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ATHUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteTarget
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.util.extensions.getSuitableColorFor

abstract class AbbeyColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(view.context, R.attr.defaultFooterColor)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(resource: BitmapPaletteWrapper, glideAnimation: Transition<in BitmapPaletteWrapper>?) {
        super.onResourceReady(resource, glideAnimation)
        onColorReady(resource.palette.getSuitableColorFor(
            ThemeStore.primaryColor(view.context),
            defaultFooterColor,
            false)
        )
    }

    abstract fun onColorReady(color: Int)

}
