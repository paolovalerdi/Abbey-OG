package com.paolovalerdi.abbey.adapter.playlist

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.AbsMultiSelectAdapter
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.glide.AbbeyColoredTarget
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.helper.menu.LibraryMenuHelper
import com.paolovalerdi.abbey.helper.menu.PlaylistMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor

class PlaylistAdapter(
    private val fragment: AbsLibraryPagerRecyclerViewCustomGridSizeFragment<*, *>,
    private val layoutRes: Int,
    private val withOverFlow: Boolean,
    dataSet: List<Playlist> = emptyList(),
    cabHolder: CabHolder?,
    val onMultipleItemAction: (itemId: Int, items: List<Playlist>) -> Unit
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, Playlist>(
    fragment.activity as AppCompatActivity,
    cabHolder,
    R.menu.menu_playlists_selection
) {

    companion object {

        private const val OFFSET_ITEM = 0
        private const val PLAYLIST = 1

    }

    init {
        setHasStableIds(true)
    }

    private var mutableDataSet: MutableList<Playlist> = dataSet.toMutableList()
    var dataSet: List<Playlist>
        get() = mutableDataSet
        set(value) {
            mutableDataSet = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int = if (position == 0)
        OFFSET_ITEM else PLAYLIST

    override fun getItemId(position: Int): Long {
        val p = position - 1
        return if (p < 0) -2 else dataSet[p].id.toLong()
    }

    override fun getIdentifier(position: Int): Playlist? {
        val p = position - 1
        return if (p < 0) null else dataSet[p]
    }

    override fun getItemCount(): Int = dataSet.size + 1

    override fun getName(item: Playlist): String = item.name

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Playlist>) {
        onMultipleItemAction(menuItem.itemId, selection)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = if (viewType == OFFSET_ITEM) {
        OffSetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_offset_header, parent, false))
    } else PlaylistViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == OFFSET_ITEM) {
            (holder as OffSetViewHolder).title?.text = MusicUtil.getPlaylistsCountString(holder.itemView.context, itemCount - 1)
        } else {
            val playlist = dataSet[position - 1]
            (holder as PlaylistViewHolder).apply {
                itemView.isActivated = isChecked(playlist)
                if (itemView.isActivated && layoutRes == R.layout.item_grid_card) {
                    itemView.scaleY = 0.95f
                    itemView.scaleX = 0.95f
                } else {
                    holder.itemView.scaleY = 1f
                    holder.itemView.scaleX = 1f
                }
                title?.text = playlist.name
                subtitle?.text = playlist.getInfoString(itemView.context)
                loadImage(this, playlist)
            }
        }
    }

    private fun loadImage(holder: PlaylistViewHolder, playlist: Playlist) {
        if (holder.albumCover == null) return
        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getPlaylistModel(playlist))
            .transition(AbbeyGlideExtension.getDefaultTransition<BitmapPaletteWrapper>())
            .playlistOptions(playlist)
            .roundedCorners(layoutRes == R.layout.item_list, 16)
            .into(object : AbbeyColoredTarget(holder.albumCover) {
                override fun onColorReady(color: Int) {
                    setColors(holder, color)
                }
            })
    }

    private fun setColors(holder: PlaylistViewHolder, color: Int) {
        if (holder.cardContainer != null) {
            holder.cardContainer.setCardBackgroundColor(color)
            holder.title?.primaryTextColorFor(color)
            holder.subtitle?.secondaryTextColorFor(color)
        }
    }

    inner class OffSetViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            itemView.setOnClickListener(null)
            itemView.setOnLongClickListener(null)
            title?.visibility = View.VISIBLE

            gridSizeButton?.apply {
                isVisible = true
                setOnClickListener {
                    LibraryMenuHelper.gridSize(it, fragment.gridSize, fragment.maxGridSize) { gridSize ->
                        fragment.setAndSaveGridSize(gridSize)
                    }
                }
            }
        }

    }

    inner class PlaylistViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            overFlowIcon?.apply {
                visibility = if (withOverFlow) View.VISIBLE else View.GONE
                setOnClickListener {
                    val popUpMenu = PopupMenu(itemView.context, it)
                    popUpMenu.inflate(R.menu.menu_item_playlist)
                    popUpMenu.setOnMenuItemClickListener { item ->
                        PlaylistMenuHelper.handleMenuClick(itemView.context as AppCompatActivity, dataSet[adapterPosition - 1], item)
                    }
                    popUpMenu.show()
                }
            }

            itemView.apply {
                setOnLongClickListener {
                    toggleChecked(adapterPosition)
                }
                setOnClickListener {
                    if (isInQuickSelectMode.and(itemViewType != OFFSET_ITEM)) {
                        toggleChecked(adapterPosition)
                    } else {
                        NavigationUtil.goToPlaylist(fragment.activity as MainActivity, dataSet[adapterPosition - 1])

                    }
                }
            }
        }

    }
}