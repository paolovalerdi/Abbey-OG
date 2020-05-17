package com.paolovalerdi.abbey.adapter.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Playlist

/**
 * @author Paolo Valerdi
 */
class AddToPlaylistAdapter(
    private val dataSet: List<Playlist>,
    private val itemClickListener: (itemId: Int) -> Unit
) : RecyclerView.Adapter<AddToPlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_list,
                parent,
                false
            )
    )

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlaylist = dataSet[position]
        holder.title?.text = currentPlaylist.name
        holder.subtitle?.text = currentPlaylist.getInfoString(holder.itemView.context)
        loadPlaylistImage(holder, currentPlaylist)
    }

    private fun loadPlaylistImage(holder: ViewHolder, playlist: Playlist) {
        if (holder.albumCover == null) return

        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getPlaylistModel(playlist))
            .playlistOptions(playlist)
            .roundedCorners(true, 16)
            .into(holder.albumCover)
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            overFlowIcon?.isVisible = false

            itemView.setOnClickListener {
                itemClickListener(dataSet[adapterPosition].id)
            }
        }

    }

}