package com.paolovalerdi.abbey.adapter.epoxy

import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.dpToPx
import com.paolovalerdi.abbey.util.extensions.withModelFrom
import com.paolovalerdi.abbey.util.extensions.withModelsFrom
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.item_top_artist)
abstract class TopArtistModel : EpoxyModelWithHolder<TopArtistModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    lateinit var artist: Artist

    @EpoxyAttribute
    var position: Int = 0

    override fun bind(holder: TopArtistViewHolder) {
        holder.artistName.text = artist.name
        holder.artistInfo.text = MusicUtil.getArtistInfoString(holder.itemView.context, artist)
        holder.positionText.text = position.toString()
        GlideApp.with(holder.artistImage)
            .load(AbbeyGlideExtension.getArtistModel(artist))
            .artistOptions(artist)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(holder.artistImage)
        holder.itemView.setOnClickListener { NavigationUtil.goToArtist(holder.itemView.context as AppCompatActivity, artist.id) }
        holder.recyclerView.withModels {
            carousel {
                id("top_${artist.name}_model")
                padding(Carousel.Padding(holder.itemView.dpToPx(20f), holder.itemView.dpToPx(16f), holder.itemView.dpToPx(20f), holder.itemView.dpToPx(16f), holder.itemView.dpToPx(9f)))
                numViewsToShowOnScreen(3.8f)
                withModelsFrom(artist.albums) { album ->
                    AlbumViewModel_()
                        .id(album.id)
                        .album(album)
                }
            }
        }
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val artistName by bind<TextView>(R.id.title)
        val artistInfo by bind<TextView>(R.id.text)
        val positionText by bind<TextView>(R.id.image_text)
        val artistImage by bind<CircleImageView>(R.id.circle_image)
        val recyclerView by bind<EpoxyRecyclerView>(R.id.recyclerView)
    }

}