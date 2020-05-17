package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paolovalerdi.abbey.adapter.song.SongAdapter
import com.paolovalerdi.abbey.adapter.song.SongAdapterWithHeader
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel.Companion.ALL_SONGS
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

/**
 * @author Paolo Valerdi
 */
class SongsFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<SongAdapterWithHeader, GridLayoutManager>() {

    private var cachedSongs: List<Song>? = null
        set(value) {
            field = value
            field?.run {
                mAdapter?.dataSet = this
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getAllSongs().observe(viewLifecycleOwner,
            Observer<List<Song>> { songs ->
                cachedSongs = songs
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

    override fun createAdapter(): SongAdapterWithHeader = SongAdapter(
        this,
        itemLayoutRes,
        cachedSongs ?: emptyList(),
        libraryFragment
    )

    override fun loadGridSize(): Int = PreferenceUtil.songGridSize


    override fun saveGridSize() {
        PreferenceUtil.songGridSize = gridSize
    }

    override fun loadGridSizeLand(): Int = PreferenceUtil.songGridSizeLand

    override fun saveGridSizeLand() {
        PreferenceUtil.songGridSizeLand = gridSize
    }

    override fun updateGridSize(newGridSize: Int) {
        mLayoutManager.spanCount = newGridSize
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String = PreferenceUtil.songSortOrder

    override fun saveSortOrder(sortOrder: String) {
        PreferenceUtil.songSortOrder = sortOrder
    }

    override fun updateSortOrder() {
        libraryFragment.viewModel.forceLoad(ALL_SONGS)
    }

}
