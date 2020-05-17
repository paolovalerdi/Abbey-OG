package com.paolovalerdi.abbey.glide.palette

import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.util.Util

class BitmapPaletteResource(private val bitmapPaletteWrapper: BitmapPaletteWrapper) : Resource<BitmapPaletteWrapper> {

    override fun get(): BitmapPaletteWrapper = bitmapPaletteWrapper

    override fun getResourceClass(): Class<BitmapPaletteWrapper> = BitmapPaletteWrapper::class.java

    override fun getSize(): Int = Util.getBitmapByteSize(bitmapPaletteWrapper.bitmap)

    override fun recycle() {
        bitmapPaletteWrapper.bitmap.recycle()
    }

}
