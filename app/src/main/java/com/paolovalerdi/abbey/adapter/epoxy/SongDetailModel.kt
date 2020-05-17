package com.paolovalerdi.abbey.adapter.epoxy

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper.MENU_RES
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.setTintFor

@EpoxyModelClass(layout = R.layout.item_list)
abstract class SongDetailModel : EpoxyModelWithHolder<SongDetailModel.EpoxySongViewHolder>() {

    @EpoxyAttribute
    lateinit var song: Song

    @EpoxyAttribute
    var backgroundColor: Int = 0

    @EpoxyAttribute
    var withOverFlow: Boolean = true

    @EpoxyAttribute
    var onClickListener: View.OnClickListener? = null

    override fun bind(holder: EpoxySongViewHolder) {
        holder.itemView.setOnClickListener(onClickListener)
        holder.itemView.setBackgroundColor(backgroundColor)
        holder.title.apply {
            text = song.title
            primaryTextColorFor(backgroundColor)
        }
        holder.subtitle.apply {
            text = MusicUtil.getSongInfoString(song)
            secondaryTextColorFor(backgroundColor)
        }
        holder.overFlow.apply {
            setTintFor(backgroundColor)
            // WTF?
            /* val unsafeContext = if (context is ContextThemeWrapper) {
                 (context as ContextThemeWrapper).baseContext
             } else context*/
            setOnClickListener(object : SongMenuHelper.OnClickSongMenu(context as AppCompatActivity) {
                override fun getSong(): Song = this@SongDetailModel.song

                override fun getMenuRes(): Int = MENU_RES

            })
        }
        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getSongModel(song))
            .songOptions(song)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .roundedCorners(true, 16)
            .into(holder.albumImage)
    }

    override fun unbind(holder: EpoxySongViewHolder) {
        holder.itemView.setOnClickListener(null)
        holder.overFlow.setOnClickListener(null)
    }

    class EpoxySongViewHolder : KotlinEpoxyHolder() {
        val albumImage by bind<AppCompatImageView>(R.id.image)
        val title by bind<TextView>(R.id.title)
        val subtitle by bind<TextView>(R.id.text)
        val overFlow by bind<AppCompatImageView>(R.id.menu)

    }

}