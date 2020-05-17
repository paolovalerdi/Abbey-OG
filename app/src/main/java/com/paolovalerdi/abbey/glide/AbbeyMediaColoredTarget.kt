package com.paolovalerdi.abbey.glide

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.transition.Transition
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteTarget
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.util.extensions.getColor
import com.paolovalerdi.abbey.util.extensions.getSuitableColorFor

abstract class AbbeyMediaColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val fallBackColor: Int
        get() = ContextCompat.getColor(view.context, R.color.md_grey_900)

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        onColorsReady(fallBackColor, Color.WHITE)
    }

    override fun onResourceReady(resource: BitmapPaletteWrapper, transition: Transition<in BitmapPaletteWrapper>?) {
        super.onResourceReady(resource, transition)
        resource.palette.let {
            val background = it.getColor(fallBackColor)
            val accent = it.getSuitableColorFor(background, Color.WHITE)
            onColorsReady(background, accent)
        }
    }

    abstract fun onColorsReady(background: Int, accent: Int)

}