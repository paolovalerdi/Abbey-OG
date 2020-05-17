package com.paolovalerdi.abbey.ui.activities.detail

import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Genre
import com.paolovalerdi.abbey.util.MusicUtil
import kotlinx.android.synthetic.main.activity_media_detail.*

class GenreDetailActivity : MediaDetailsActivity<Genre>() {
    override fun loadMediaDetails(model: Genre) {
        setMediaTitle(model.name)
        detailsText.text = MusicUtil.getGenreInfoString(this, model)
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getGenreModel(model))
            .genreOptions(model)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(object : AbbeyMediaColoredTarget(detailsImage) {
                override fun onColorsReady(background: Int, accent: Int) {
                    setColors(background, accent)
                }
            })
    }

    override var backgroundColor: Int = 0
        set(value) {
            field = value
            bindController()
        }

}
