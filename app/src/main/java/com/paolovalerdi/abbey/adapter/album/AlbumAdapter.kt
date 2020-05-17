package com.paolovalerdi.abbey.adapter.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.helper.menu.LibraryMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil

/**
 * @author Paolo Valerdi
 */
class AlbumAdapter(
    private val fragment: AbsLibraryPagerRecyclerViewCustomGridSizeFragment<*, *>,
    layoutRes: Int,
    dataSet: List<Album> = emptyList(),
    cabHolder: CabHolder?
) : AbsAlbumAdapter(
    fragment.activity as AppCompatActivity,
    layoutRes,
    dataSet,
    cabHolder
) {

    private val OFFSET_ITEM = 0
    private val ALBUM = 1

    override fun getItemId(position: Int): Long {
        val p = position - 1
        if (p < 0) return -2
        return super.getItemId(p)
    }

    override fun getIdentifier(position: Int): Album? {
        val p = position - 1
        if (p < 0) return null
        return super.dataSet[p]
    }

    override fun getItemCount(): Int {
        val superItemCount = super.getItemCount()
        return if (superItemCount == 0) 0 else superItemCount + 1
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) OFFSET_ITEM else ALBUM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == OFFSET_ITEM) {
            OffSetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_offset_header, parent, false))
        } else
            super.onCreateViewHolder(parent, viewType)

    override fun createViewHolder(itemView: View, itemViewType: Int): RecyclerView.ViewHolder =
        LibraryAlbumViewHolder(itemView)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == OFFSET_ITEM) {
            (holder as OffSetViewHolder).title?.text = MusicUtil.getAlbumCountString(activity, itemCount - 1)
        } else {
            super.onBindViewHolder(holder, position - 1)
        }
    }

    inner class LibraryAlbumViewHolder(itemView: View) : AbsAlbumAdapter.AlbumViewHolder(itemView) {

        init {
            itemView.apply {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        if (isInQuickSelectMode) {
                            toggleChecked(adapterPosition)
                        } else {
                            NavigationUtil.goToAlbum(activity, dataSet[adapterPosition - 1].id)
                        }
                    }
                }
                setOnLongClickListener {
                    toggleChecked(adapterPosition)
                    true
                }
            }

        }

    }

    inner class OffSetViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            title?.visibility = View.VISIBLE

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
                    LibraryMenuHelper.albumSortOderMenu(it) { sortOrder ->
                        fragment.setAndSaveSortOrder(sortOrder)
                    }
                }
            }
        }

    }
}