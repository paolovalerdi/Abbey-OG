package com.paolovalerdi.abbey.adapter.playlist

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
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
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Genre
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor

class GenreAdapterKt(
    private val fragmentKt: AbsLibraryPagerRecyclerViewCustomGridSizeFragment<*, *>,
    private val layoutRes: Int,
    dataSet: List<Genre> = emptyList(),
    cabHolder: CabHolder?
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, Genre>(
    fragmentKt.activity as AppCompatActivity,
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

    private var mutableDataSet: MutableList<Genre> = dataSet.toMutableList()
    var dataSet: List<Genre>
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

    override fun getIdentifier(position: Int): Genre? {
        val p = position - 1
        return if (p < 0) null else dataSet[p]
    }

    override fun getItemCount(): Int = dataSet.size + 1

    override fun getName(item: Genre): String = item.name

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Genre>) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = if (viewType == OFFSET_ITEM) {
        OffSetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_offset_header, parent, false))
    } else PlaylistViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == OFFSET_ITEM) {
            (holder as OffSetViewHolder).title?.text = MusicUtil.getGenreCountString(holder.itemView.context, itemCount - 1)
        } else {
            val genre = dataSet[position - 1]
            (holder as PlaylistViewHolder).apply {
                itemView.isActivated = isChecked(genre)
                if (itemView.isActivated && layoutRes == R.layout.item_grid_card) {
                    itemView.scaleY = 0.95f
                    itemView.scaleX = 0.95f
                } else {
                    holder.itemView.scaleY = 1f
                    holder.itemView.scaleX = 1f
                }
                title?.text = genre.name
                subtitle?.text = MusicUtil.getGenreInfoString(holder.itemView.context, genre)
                loadImage(this, genre)
            }
        }
    }

    private fun loadImage(holder: PlaylistViewHolder, playlist: Genre) {
        if (holder.albumCover == null) return
        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getGenreModel(playlist))
            .transition(AbbeyGlideExtension.getDefaultTransition<BitmapPaletteWrapper>())
            .genreOptions(playlist)
            .roundedCorners(layoutRes == R.layout.item_list, 16)
            .into(object : AbbeyColoredTarget(holder.albumCover) {
                override fun onColorReady(color: Int) {
                    setColors(holder, color)
                }
            })
    }

    private fun setColors(holder: GenreAdapterKt.PlaylistViewHolder, color: Int) {
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
                    LibraryMenuHelper.gridSize(it, fragmentKt.gridSize, fragmentKt.maxGridSize) { gridSize ->
                        fragmentKt.setAndSaveGridSize(gridSize)
                    }
                }
            }
        }

    }

    inner class PlaylistViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            overFlowIcon?.isGone = true

            itemView.apply {
                setOnClickListener {
                    NavigationUtil.goToGenre(fragmentKt.activity as MainActivity, dataSet[adapterPosition - 1])
                }
            }
        }

    }
}