package com.paolovalerdi.abbey.glide.palette

import android.widget.ImageView

import com.bumptech.glide.request.target.ImageViewTarget

open class BitmapPaletteTarget(view: ImageView) : ImageViewTarget<BitmapPaletteWrapper>(view) {

    override fun setResource(bitmapPaletteWrapper: BitmapPaletteWrapper?) {
        if (bitmapPaletteWrapper != null) {
            view.setImageBitmap(bitmapPaletteWrapper.bitmap)
        }
    }

}
