package com.paolovalerdi.abbey.adapter.song

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.AbsMultiSelectAdapter
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.glide.AbbeyColoredTarget
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper
import com.paolovalerdi.abbey.helper.menu.SongsMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor

/**
 * @author Paolo Valerdi
 */
abstract class SongAdapterWithHeader(
    private val activity: AppCompatActivity,
    private val layoutRes: Int,
    dataSet: List<Song> = emptyList(),
    cabHolder: CabHolder?
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, Song>(
    activity,
    cabHolder,
    R.menu.menu_media_selection
) {

    companion object {
        const val OFFSET_ITEM = 0
        const val SONG = 1
    }

    private var mutableDataSet: MutableList<Song> = dataSet.toMutableList()
    var dataSet: List<Song>
        get() = mutableDataSet
        set(value) {
            mutableDataSet = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int = if (position == 0)
        OFFSET_ITEM else SONG

    override fun getName(item: Song): String = item.title

    override fun getItemId(position: Int): Long {
        val p = position - 1
        if (p < 0) return -2
        return dataSet[p].id.toLong()
    }

    override fun getIdentifier(position: Int): Song {
        val p = position - 1
        if (p < 0) return Song.EMPTY_SONG
        return dataSet[p]
    }

    override fun getItemCount(): Int = dataSet.size + 1

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Song>) {
        SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == OFFSET_ITEM) {
        createHeaderViewHolder(parent)
    } else ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == OFFSET_ITEM) {
            onBindHeader(holder)
        } else {
            val currentSong = dataSet[position - 1]
            (holder as ViewHolder).apply {
                itemView.isActivated = isChecked(currentSong)
                title?.apply {
                    text = currentSong.title
                }
                subtitle?.apply {
                    text = MusicUtil.getSongInfoString(currentSong)
                }
                loadImage(this, currentSong)
            }
        }
    }

    protected fun loadImage(holder: ViewHolder, currentSong: Song?) {
        if (holder.albumCover == null) return

        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getSongModel(currentSong))
            .transition(AbbeyGlideExtension.getDefaultTransition<BitmapPaletteWrapper>())
            .songOptions(currentSong)
            .roundedCorners(layoutRes == R.layout.item_list, 16)
            .into(object : AbbeyColoredTarget(holder.albumCover) {
                override fun onColorReady(color: Int) {
                    setColors(holder, color)
                }
            })

    }

    private fun setColors(holder: ViewHolder, color: Int) {
        if (holder.cardContainer != null) {
            holder.cardContainer.setCardBackgroundColor(color)
            holder.title?.primaryTextColorFor(color)
            holder.subtitle?.secondaryTextColorFor(color)
        }
    }

    abstract fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    abstract fun onBindHeader(holder: RecyclerView.ViewHolder)

    inner class ViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    if (isInQuickSelectMode) {
                        toggleChecked(adapterPosition)
                    } else {
                        MusicPlayerRemote.openQueue(dataSet as ArrayList<Song>, adapterPosition - 1, true)
                    }
                }
                setOnLongClickListener {
                    toggleChecked(adapterPosition)
                }
            }

            overFlowIcon?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(itemView.context as AppCompatActivity) {
                override fun getSong(): Song = dataSet[adapterPosition - 1]
                override fun getMenuRes(): Int = SongMenuHelper.MENU_RES
            })

        }
    }

}