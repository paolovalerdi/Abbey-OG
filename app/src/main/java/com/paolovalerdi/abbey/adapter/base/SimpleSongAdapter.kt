package com.paolovalerdi.abbey.adapter.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor
import java.util.*

class SimpleSongAdapter(private var dataSet: List<Song> = emptyList()) : RecyclerView.Adapter<SimpleSongAdapter.GenreSongViewHolder>() {

    private var backgroundColor = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenreSongViewHolder {
        backgroundColor = ContextCompat.getColor(parent.context, R.color.md_grey_900)
        return GenreSongViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_list,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: GenreSongViewHolder, position: Int) {
        val song = dataSet[position]
        holder.apply {
            title?.text = song.title
            title?.primaryTextColorFor(backgroundColor)
            subtitle?.text = MusicUtil.getSongInfoString(song)
            subtitle?.secondaryTextColorFor(backgroundColor)
            GlideApp.with(itemView.context)
                .load(AbbeyGlideExtension.getSongModel(song))
                .songOptions(song)
                .into(albumCover!!)
        }
    }

    fun swapDataSet(songs: List<Song>) {
        dataSet = songs
        notifyDataSetChanged()
    }

    fun setColors(@ColorInt backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        notifyDataSetChanged()
    }

    inner class GenreSongViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            itemView.setOnClickListener {
                MusicPlayerRemote.openQueue(dataSet as ArrayList<Song>, adapterPosition, true)
            }
            overFlowIcon?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(itemView.context as AppCompatActivity) {
                override fun getSong(): Song = dataSet[adapterPosition]
            })
        }

    }

}