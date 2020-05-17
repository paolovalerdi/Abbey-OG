package com.paolovalerdi.abbey.adapter.album

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.AbsMultiSelectAdapter
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.glide.AbbeyColoredTarget
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.helper.menu.SongsMenuHelper
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor

/**
 * @author Paolo Valerdi
 */
abstract class AbsAlbumAdapter(
    protected val activity: AppCompatActivity,
    private val layoutRes: Int,
    dataSet: List<Album> = emptyList(),
    cabHolder: CabHolder?
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, Album>(
    activity,
    cabHolder,
    R.menu.menu_media_selection
) {

    init {
        this.setHasStableIds(true)
    }

    private var mutableDataSet: MutableList<Album> = dataSet.toMutableList()
    var dataSet: List<Album>
        get() = mutableDataSet
        set(value) {
            mutableDataSet = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(activity).inflate(layoutRes, parent, false)
        return createViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val album = dataSet[position]
        (holder as AlbumViewHolder).itemView.isActivated = isChecked(album)
        holder.cardContainer?.isChecked = holder.itemView.isActivated && layoutRes == R.layout.item_grid_card
        holder.title?.text = album.title
        holder.subtitle?.text = album.artistName
        loadAlbumCover(holder, album)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = dataSet[position].id.toLong()

    override fun getIdentifier(position: Int): Album? = dataSet[position]

    override fun getName(item: Album): String = item.title

    override fun onMultipleItemAction(menuItem: MenuItem, selection: ArrayList<Album>) {
        SongsMenuHelper.handleMenuClick(activity, getSonList(selection), menuItem.itemId)
    }

    private fun getSonList(albums: List<Album>): ArrayList<Song> {
        val arrayList = arrayListOf<Song>()
        for (album in albums) {
            arrayList.addAll(album.songs)
        }
        return arrayList
    }

    protected open fun createViewHolder(itemView: View, itemViewType: Int): RecyclerView.ViewHolder = AlbumViewHolder(itemView)

    protected open fun loadAlbumCover(holder: AlbumViewHolder, album: Album) {

        if (holder.albumCover == null) return

        GlideApp.with(holder.itemView.context)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getSongModel(album.safeGetFirstSong()))
            .transition(AbbeyGlideExtension.getDefaultTransition<BitmapPaletteWrapper>())
            .songOptions(album.safeGetFirstSong())
            .roundedCorners(layoutRes == R.layout.item_list, 16)
            .into(object : AbbeyColoredTarget(holder.albumCover) {
                override fun onColorReady(color: Int) {
                    setColors(holder, color)
                }
            })
    }

    protected open fun setColors(holder: AlbumViewHolder, color: Int) {
        if (holder.cardContainer != null) {
            holder.cardContainer.rippleColor = ColorStateList.valueOf(ColorUtil.withAlpha(color, 0.35f))
            holder.cardContainer.setCardBackgroundColor(color)
            holder.title?.primaryTextColorFor(color)
            holder.subtitle?.secondaryTextColorFor(color)
        }
    }

    open inner class AlbumViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {
        init {
            itemView.apply {
                setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        if (isInQuickSelectMode) {
                            toggleChecked(adapterPosition)
                        } else {
                            NavigationUtil.goToAlbum(activity, dataSet[adapterPosition].id)
                        }
                    }
                }
                setOnLongClickListener {
                    toggleChecked(adapterPosition)
                }
            }
            overFlowIcon?.visibility = View.GONE
        }
    }
}