package com.paolovalerdi.abbey.adapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.AbsMultiSelectAdapter
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolde
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment

abstract class LibraryMediaAdapter<T>(
    private val context: AbsLibraryPagerRecyclerViewCustomGridSizeFragment<*, *>,
    @LayoutRes layoutRes: Int,
    cabHolder: CabHolder?,
    @MenuRes menuRes: Int = R.menu.menu_media_selection
) : AbsMultiSelectAdapter<RecyclerView.ViewHolder, T>(
    context.requireActivity(),
    cabHolder,
    menuRes
) {

    companion object {

        private const val HEADER = 0
        private const val MEDIA = 1

    }

    var dataSet: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    abstract fun onBindHeader(holder: LibraryHeaderViewHolder)

    abstract fun onBindMedia(item: T, holder: RecyclerView.ViewHolder)

    abstract fun loadImage(item: T, holder: RecyclerView.ViewHolder)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER) {
            onBindHeader(holder as LibraryHeaderViewHolder)
        } else {
            onBindMedia(dataSet[position - 1], holder)
        }
    }


    class LibraryHeaderViewHolder(itemView: View) : MediaEntryViewHolde(itemView) {

        init {
            title?.isVisible = true
        }

    }


}