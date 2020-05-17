package com.paolovalerdi.abbey.adapter.epoxy

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.*

@EpoxyModelClass(layout = R.layout.item_last_added_big)
abstract class LastAddedArtistModel : EpoxyModelWithHolder<LastAddedArtistModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    lateinit var artist: Artist

    @EpoxyAttribute
    var position: Int = 0

    override fun bind(holder: TopArtistViewHolder) {
        holder.artistName.text = artist.name
        holder.artistInfo.text = MusicUtil.getArtistInfoString(holder.itemView.context, artist)
        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getArtistModel(artist))
            .artistOptions(artist)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(object : AbbeyMediaColoredTarget(holder.artistImage) {
                override fun onColorsReady(background: Int, accent: Int) {
                    holder.artistName.primaryTextColorFor(background)
                    holder.artistInfo.secondaryTextColorFor(background)
                    holder.card.setCardBackgroundColor(background)
                    holder.button.apply {
                        strokeColor = ColorStateList.valueOf(getSecondaryTextColor(background.isLight))
                        setTextColor(getSecondaryTextColor(background.isLight))
                    }
                    ViewCompat.setBackgroundTintList(holder.gradient, ColorStateList.valueOf(background))
                }

            })
        holder.itemView.setOnClickListener { NavigationUtil.goToArtist(holder.itemView.context as AppCompatActivity, artist.id) }
        holder.button.setOnClickListener { MusicPlayerRemote.openQueue(artist.songs, 0, true) }
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
        holder.button.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val artistName by bind<TextView>(R.id.title)
        val artistInfo by bind<TextView>(R.id.text)
        val gradient by bind<View>(R.id.gradient)
        val card by bind<MaterialCardView>(R.id.card)
        val button by bind<MaterialButton>(R.id.button)
        val artistImage by bind<AppCompatImageView>(R.id.image)
    }

}