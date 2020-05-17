package com.paolovalerdi.abbey.adapter.artist

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.AbsMultiSelectAdapter
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.menu.LibraryMenuHelper
import com.paolovalerdi.abbey.helper.menu.SongsMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil

class ArtistAdapter(
    private val fragment: AbsLibraryPagerRecyclerViewCustomGridSizeFragment<*, *>,
    private val layoutRes: Int,
    dataSet: List<Artist> = emptyList(),
    cabHolder: CabHolder?
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, Artist>(
    fragment.activity as AppCompatActivity,
    cabHolder,
    R.menu.menu_media_selection
) {

    companion object {
        private const val OFFSET_ITEM = 0
        private const val ARTIST = 1
    }

    init {
        setHasStableIds(true)
    }

    private var mutableDataSet: MutableList<Artist> = dataSet.toMutableList()
    var dataSet: List<Artist>
        get() = mutableDataSet
        set(value) {
            mutableDataSet = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun getItemId(position: Int): Long {
        val p = position - 1
        if (p < 0) return -2
        return dataSet[p].id.toLong()
    }

    override fun getIdentifier(position: Int): Artist? {
        val p = position - 1
        if (p < 0) return null
        return dataSet[p]
    }

    override fun getItemCount(): Int = dataSet.size + 1

    override fun getItemViewType(position: Int): Int = if (position == 0) OFFSET_ITEM else ARTIST

    override fun getName(item: Artist): String = item.name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == OFFSET_ITEM) {
        OffSetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_offset_header, parent, false))
    } else ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == OFFSET_ITEM) {
            (holder as OffSetViewHolder).title?.text = MusicUtil.getArtistCountString(holder.itemView.context, itemCount - 1)
        } else {
            val currentArtist = dataSet[position - 1]
            (holder as ViewHolder).apply {
                itemView.isActivated = isChecked(currentArtist)
                if (itemView.isActivated && layoutRes == R.layout.item_grid_circle) {
                    itemView.scaleY = 0.95f
                    itemView.scaleX = 0.95f
                } else {
                    itemView.scaleY = 1f
                    itemView.scaleX = 1f
                }
                title?.text = currentArtist.name
                subtitle?.text = MusicUtil.getArtistInfoString(holder.itemView.context, currentArtist)
                loadImage(this, currentArtist)
            }
        }
    }

    private fun loadImage(holder: ViewHolder, currentArtist: Artist?) {
        if (holder.artistImage == null) return
        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getArtistModel(currentArtist))
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .artistOptions(currentArtist)
            .into(holder.artistImage)
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Artist>) {
        SongsMenuHelper.handleMenuClick(fragment.activity!!, getSonList(selection), menuItem.itemId)
    }

    private fun getSonList(Artists: List<Artist>): ArrayList<Song> {
        val arrayList = arrayListOf<Song>()
        for (artist in Artists) {
            arrayList.addAll(artist.songs)
        }
        return arrayList
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        if (isInQuickSelectMode) {
                            toggleChecked(adapterPosition)
                        } else {
                            NavigationUtil.goToArtist(itemView.context as AppCompatActivity, dataSet[adapterPosition - 1].name)
                        }
                    }
                }
                setOnLongClickListener {
                    toggleChecked(adapterPosition)
                    true
                }
            }
            overFlowIcon?.visibility = View.GONE
            artistImage?.visibility = View.VISIBLE
        }
    }

    inner class OffSetViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            itemView.setOnClickListener(null)
            itemView.setOnLongClickListener(null)
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
                    LibraryMenuHelper.artistSortOderMenu(it) { sortOrder ->
                        fragment.setAndSaveSortOrder(sortOrder)
                    }
                }
            }
        }

    }
}