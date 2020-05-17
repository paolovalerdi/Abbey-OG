package com.paolovalerdi.abbey.adapter.song

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.menu.LibraryMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.SongsFragment
import com.paolovalerdi.abbey.util.MusicUtil

/**
 * @author Paolo Valerdi
 */
class SongAdapter(
    private val fragment: SongsFragment,
    layoutRes: Int,
    dataSet: List<Song> = emptyList(),
    cabHolder: CabHolder?
) : SongAdapterWithHeader(
    fragment.activity as AppCompatActivity,
    layoutRes,
    dataSet,
    cabHolder
) {

    init {
        setHasStableIds(true)
    }

    override fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = OffSetViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_list_offset_header,
            parent,
            false
        )
    )

    override fun onBindHeader(holder: RecyclerView.ViewHolder) {
        (holder as OffSetViewHolder).apply {
            title?.text = MusicUtil.getSongCountString(itemView.context, itemCount - 1)
        }
    }

    inner class OffSetViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {
        init {
            title?.visibility = View.VISIBLE

            shuffleButton?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    MusicPlayerRemote.openAndShuffleQueue(dataSet as java.util.ArrayList<Song>, true)
                }
            }

            gridSizeButton?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    LibraryMenuHelper.gridSize(it, fragment.gridSize, fragment.maxGridSize) { gridSize ->
                        fragment.setAndSaveGridSize(gridSize)
                    }
                }
            }

            sortOrderButton?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    LibraryMenuHelper.songSortOderMenu(it) { sortOrder ->
                        fragment.setAndSaveSortOrder(sortOrder)
                    }
                }
            }
        }
    }
}