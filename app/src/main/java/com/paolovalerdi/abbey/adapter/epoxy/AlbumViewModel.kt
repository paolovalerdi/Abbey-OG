package com.paolovalerdi.abbey.adapter.epoxy

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil

@EpoxyModelClass(layout = R.layout.item_last_added)
abstract class AlbumViewModel : EpoxyModelWithHolder<AlbumViewModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    lateinit var album: Album

    @EpoxyAttribute
    var forHome = true

    override fun bind(holder: TopArtistViewHolder) {
        if (forHome.not()) {
            holder.title.isVisible = false
            holder.subtitle.isVisible = false
            holder.fab.isVisible = false
        }
        holder.itemView.setOnClickListener { NavigationUtil.goToAlbum(holder.itemView.context as AppCompatActivity, album.id) }
        holder.fab.setOnClickListener { MusicPlayerRemote.openQueue(album.songs, 0, true) }
        holder.title.text = album.title
        holder.subtitle.text = MusicUtil.getSongCountString(holder.itemView.context, album.songCount)
        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getSongModel(album.safeGetFirstSong()))
            .songOptions(album.safeGetFirstSong())
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(holder.albumImage)
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
        holder.fab.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val albumImage by bind<AppCompatImageView>(R.id.image)
        val title by bind<TextView>(R.id.title)
        val subtitle by bind<TextView>(R.id.text)
        val fab by bind<FloatingActionButton>(R.id.fab)
    }

}