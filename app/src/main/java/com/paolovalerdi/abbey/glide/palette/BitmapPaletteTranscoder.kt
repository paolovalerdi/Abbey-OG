package com.paolovalerdi.abbey.glide.palette

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.paolovalerdi.abbey.util.extensions.generateWhiteAllowedPalette

class BitmapPaletteTranscoder : ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {

    override fun transcode(bitmapResource: Resource<Bitmap>, options: Options): Resource<BitmapPaletteWrapper>? {
        val bitmap = bitmapResource.get()
        val bitmapPaletteWrapper = BitmapPaletteWrapper(bitmap, bitmap.generateWhiteAllowedPalette())
        return BitmapPaletteResource(bitmapPaletteWrapper)
    }

}