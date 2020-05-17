package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.artist.ArtistAdapter
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel.Companion.ALL_ARTIST

/**
 * @author Paolo Valerdi
 */
class ArtistsFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<ArtistAdapter, GridLayoutManager>() {

    private var cachedArtists: List<Artist>? = null
        set(value) {
            field = value
            field?.run {
                mAdapter?.dataSet = this
            }
        }

    override val itemLayoutRes: Int
        get() = if (gridSize > maxGridSizeForList) {
            R.layout.item_grid_circle
        } else R.layout.item_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getAllArtists().observe(viewLifecycleOwner,
            Observer<List<Artist>> { artist ->
                cachedArtists = artist
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

    override fun createAdapter(): ArtistAdapter = ArtistAdapter(
        this,
        itemLayoutRes,
        cachedArtists ?: emptyList(),
        libraryFragment
    )

    override fun loadGridSize(): Int = PreferenceUtil.artistGridSize

    override fun saveGridSize() {
        PreferenceUtil.artistGridSize = gridSize
    }

    override fun loadGridSizeLand(): Int = PreferenceUtil.artistGridSizeLand

    override fun saveGridSizeLand() {
        PreferenceUtil.artistGridSizeLand = gridSize
    }

    override fun updateGridSize(newGridSize: Int) {
        mLayoutManager.spanCount = newGridSize
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String = PreferenceUtil.artistSortOrder

    override fun saveSortOrder(sortOrder: String) {
        PreferenceUtil.artistSortOrder = sortOrder
    }

    override fun updateSortOrder() {
        libraryFragment.viewModel.forceLoad(ALL_ARTIST)
    }

}
