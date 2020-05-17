package com.paolovalerdi.abbey.adapter.epoxy

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.util.NavigationUtil

@EpoxyModelClass(layout = R.layout.item_grid_card_horizontal)
abstract class CardAlbumModel : EpoxyModelWithHolder<CardAlbumModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    lateinit var album: Album

    override fun bind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener { NavigationUtil.goToAlbum(holder.itemView.context as AppCompatActivity, album.id) }
        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getSongModel(album.safeGetFirstSong()))
            .songOptions(album.safeGetFirstSong())
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(holder.albumImage)
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val albumImage by bind<AppCompatImageView>(R.id.image)
    }

}