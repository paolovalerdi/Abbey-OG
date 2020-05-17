package com.paolovalerdi.abbey.adapter.song

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.extensions.setTint
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureManager
import com.thesurix.gesturerecycler.GestureViewHolder


/**
 * @author Paolo Valerdi
 */
class QueueAdapter(
    private var current: Int,
    private val withOnSurfaceColor: Boolean = false,
    private val isBlurTheme: Boolean = false
) : GestureAdapter<Song, QueueAdapter.QueueViewHolder>() {

    companion object {

        private const val HISTORY = 0
        private const val CURRENT = 1
        private const val UP_NEXT = 2

    }

    init {

        setHasStableIds(true)

    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = QueueViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_list,
                parent,
                false
            ),
        withOnSurfaceColor,
        isBlurTheme
    )

    override fun onBindViewHolder(
        holder: QueueViewHolder,
        position: Int
    ) {
        val itemType = getItemType(position)
        val alpha = if (itemType == HISTORY || itemType == CURRENT) 0.5f else 1f
        val item = getItem(position)

        applyAlpha(alpha, holder)
        holder.imageText?.text = (position - current).toString()
        holder.title?.text = item.title
        holder.subtitle?.text = MusicUtil.getSongInfoString(item)
        holder.overFlowIcon?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(holder.itemView.context as AppCompatActivity) {
            override fun getSong(): Song = item
        })

    }

    override fun onViewRecycled(holder: QueueViewHolder) {
        super.onViewRecycled(holder)
        holder.overFlowIcon?.setOnClickListener(null)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        GestureManager.Builder(recyclerView)
            .setSwipeEnabled(true)
            .setLongPressDragEnabled(true)
            .build()

        setDataChangeListener(object : OnDataChangeListener<Song> {
            override fun onItemRemoved(item: Song, position: Int) {
                MusicPlayerRemote.removeFromQueue(position)
                val snackbar = Snackbar.make(recyclerView, item.title + " removed", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        MusicPlayerRemote.addSong(position, item)
                    }
                }.show()

            }

            override fun onItemReorder(item: Song, fromPos: Int, toPos: Int) {
                MusicPlayerRemote.moveSong(fromPos, toPos)
            }

        })
    }

    fun swapDataSet(dataSet: List<Song>) {
        setData(dataSet, null)
    }

    fun setCurrent(current: Int) {
        this.current = current
        notifyDataSetChanged()
    }

    private fun applyAlpha(alpha: Float, viewHolder: QueueViewHolder) {
        viewHolder.title?.alpha = alpha
        viewHolder.subtitle?.alpha = alpha
        viewHolder.imageText?.alpha = alpha
        viewHolder.overFlowIcon?.alpha = alpha
    }

    private fun getItemType(position: Int): Int = when {
        position < current -> HISTORY
        position == current -> CURRENT
        else -> UP_NEXT
    }

    class QueueViewHolder(itemView: View, withOnSurfaceColor: Boolean, isBlurTheme: Boolean) : GestureViewHolder(itemView) {

        val imageText: TextView? = itemView.findViewById(R.id.image_text)
        val title: TextView? = itemView.findViewById(R.id.title)
        val subtitle: TextView? = itemView.findViewById(R.id.text)

        private val dummySpace: View? = itemView.findViewById(R.id.dummy_space)
        val overFlowIcon: AppCompatImageView? = itemView.findViewById(R.id.menu)

        init {

            if (withOnSurfaceColor) {
                itemView.setBackgroundColor(itemView.context.resolveAttrColor(R.attr.colorSurfaceElevated))
            } else if (isBlurTheme) {
                title?.setTextColor(Color.WHITE)
                subtitle?.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_dark_secondary))
                overFlowIcon?.setTint(Color.WHITE)
                imageText?.setTextColor(Color.WHITE)
            }

            imageText?.isVisible = true

            itemView.setOnClickListener {
                MusicPlayerRemote.openQueue(MusicPlayerRemote.getPlayingQueue(), adapterPosition, true)
            }

        }

        override val draggableView: View? = null

        override fun canDrag() = true

        override fun canSwipe() = true

    }

}

