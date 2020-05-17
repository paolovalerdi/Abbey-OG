package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paolovalerdi.abbey.adapter.album.AlbumAdapter
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel.Companion.ALL_ALBUMS

/**
 * @author Paolo Valerdi
 */
class AlbumsFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<AlbumAdapter, GridLayoutManager>() {

    private var cachedAlbums: List<Album>? = null
        set(value) {
            field = value
            field?.run {
                mAdapter?.dataSet = this
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getAllAlbums().observe(viewLifecycleOwner,
            Observer<List<Album>> { albums ->
                cachedAlbums = albums
            })
    }

    override fun createLayoutManager(): GridLayoutManager = GridLayoutManager(activity, gridSize).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (position == 0)
                    gridSize
                else 1
        }
    }

    override fun createAdapter(): AlbumAdapter = AlbumAdapter(
        this,
        itemLayoutRes,
        cachedAlbums ?: emptyList(),
        libraryFragment
    )

    override fun loadGridSize(): Int = PreferenceUtil.albumGridSize

    override fun saveGridSize() {
        PreferenceUtil.albumGridSize = gridSize
    }

    override fun loadGridSizeLand(): Int = PreferenceUtil.albumGridSizeLand

    override fun saveGridSizeLand() {
        PreferenceUtil.albumGridSizeLand = gridSize
    }

    override fun updateGridSize(newGridSize: Int) {
        mLayoutManager.spanCount = newGridSize
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String = PreferenceUtil.albumSortOrder

    override fun saveSortOrder(sortOrder: String) {
        PreferenceUtil.albumSortOrder = sortOrder
    }

    override fun updateSortOrder() {
        libraryFragment.viewModel.forceLoad(ALL_ALBUMS)
    }
}
