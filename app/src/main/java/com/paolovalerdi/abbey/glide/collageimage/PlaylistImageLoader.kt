package com.paolovalerdi.abbey.glide.collageimage

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

/**
 * @author Paolo Valerdi
 */
class PlaylistImageLoader(private val context: Context) : ModelLoader<CollageImage, InputStream> {

    override fun buildLoadData(
        model: CollageImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? = ModelLoader.LoadData(
        ObjectKey(model.toString()),
        PlaylistImageFetcher(model, context)
    )

    override fun handles(model: CollageImage): Boolean = true

    class Factory(private val context: Context) : ModelLoaderFactory<CollageImage, InputStream> {

        override fun build(
            multiFactory: MultiModelLoaderFactory
        ): ModelLoader<CollageImage, InputStream> = PlaylistImageLoader(context)

        override fun teardown() {}

    }

}