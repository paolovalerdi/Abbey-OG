package com.paolovalerdi.abbey.adapter.epoxy

import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.*

@EpoxyModelClass(layout = R.layout.item_most_played_artist)
abstract class MostPlayedArtistModel : EpoxyModelWithHolder<MostPlayedArtistModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    lateinit var artist: Artist

    override fun bind(holder: TopArtistViewHolder) {
        holder.title.text = artist.name
        holder.subtitle.text = MusicUtil.getSongCountString(holder.itemView.context, artist.songCount)
        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getArtistModel(artist))
            .artistOptions(artist)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(object : AbbeyMediaColoredTarget(holder.albumImage) {
                override fun onColorsReady(background: Int, accent: Int) {
                    holder.itemView.setBackgroundColor(background)
                    holder.title.primaryTextColorFor(background)
                    holder.badge.setTint(getPrimaryTextColor(background.isLight))
                    holder.subtitle.secondaryTextColorFor(background)
                    ViewCompat.setBackgroundTintList(holder.gradient, ColorStateList.valueOf(background))
                }
            })
        holder.itemView.doOnLayout {
            Carousel.setDefaultGlobalSnapHelperFactory(null)
            holder.recyclerView.withModels {
                carousel {
                    id("${artist.name} model")
                    numViewsToShowOnScreen(1.7f)
                    padding(Carousel.Padding(holder.albumImage.width, 0, holder.itemView.dpToPx(20f), 0, holder.itemView.dpToPx(9f)))
                    withModelsFrom(artist.albums) { album ->
                        AlbumViewModel_()
                            .id(album.id)
                            .album(album)
                    }
                }
            }

        }
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val albumImage by bind<AppCompatImageView>(R.id.image)
        val badge by bind<AppCompatImageView>(R.id.badgeIcon)
        val title by bind<TextView>(R.id.title)
        val subtitle by bind<TextView>(R.id.text)
        val recyclerView by bind<EpoxyRecyclerView>(R.id.recyclerView)
        val gradient by bind<View>(R.id.gradient)
    }

}