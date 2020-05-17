package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.playlist.PlaylistAdapter
import com.paolovalerdi.abbey.ui.dialogs.ClearSmartPlaylistDialog
import com.paolovalerdi.abbey.ui.dialogs.DeleteMediaDialog
import com.paolovalerdi.abbey.helper.menu.SongsMenuHelper
import com.paolovalerdi.abbey.model.AbsCustomPlaylist
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.model.smartplaylist.AbsSmartPlaylist
import com.paolovalerdi.abbey.repository.PlaylistSongRepository
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.extensions.makeToast
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * @author Paolo Valerdi
 */
class PlaylistFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<PlaylistAdapter, GridLayoutManager>() {

    private var cachedPlaylist: List<Playlist>? = null
        set(value) {
            field = value
            field?.run {
                mAdapter?.dataSet = this
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getAllPlaylist().observe(viewLifecycleOwner,
            Observer<List<Playlist>> { playlist ->
                cachedPlaylist = playlist
            })
    }

    override fun createLayoutManager(): GridLayoutManager = GridLayoutManager(requireContext(), gridSize).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (position == 0)
                    gridSize
                else 1
        }
    }

    override fun createAdapter(): PlaylistAdapter = PlaylistAdapter(
        this,
        itemLayoutRes,
        true,
        cachedPlaylist ?: emptyList(),
        libraryFragment
    ) { itemId, items ->
        when (itemId) {
            R.id.action_delete_playlist -> {
                val nonSmartPlaylists = emptyList<Playlist>().toMutableList()
                items.forEach { playlist ->
                    if (playlist is AbsSmartPlaylist) {
                        if (playlist.isClearable) {
                            ClearSmartPlaylistDialog.create(playlist).show(childFragmentManager, null)
                        }
                    } else {
                        nonSmartPlaylists.add(playlist)
                    }
                }
                DeleteMediaDialog.deletePlaylist(nonSmartPlaylists).show(childFragmentManager, null)
            }
            R.id.action_save_playlist -> {
                lifecycleScope.launch {
                    var successes = 0
                    var failures = 0
                    var dir = ""
                    var msg = ""
                    withContext(IO) {
                        items.forEach { playlist ->
                            try {
                                dir = PlaylistsUtil.savePlaylist(requireContext(), playlist).parent
                                successes++
                            } catch (e: IOException) {
                                failures++
                                e.printStackTrace()
                            }
                        }
                        msg = if (failures == 0) {
                            String.format(getString(R.string.saved_x_playlists_to_x, successes, dir))
                        } else String.format(getString(R.string.saved_x_playlists_to_x_failed_to_save_x, successes, dir, failures))
                    }
                    requireContext().makeToast(msg)
                }
            }
            else -> {
                lifecycleScope.launch {
                    val song = arrayListOf<Song>()
                    withContext(IO) {
                        items.forEach { playlist ->
                            if (playlist is AbsCustomPlaylist) {
                                song.addAll(playlist.getSongs(requireContext()))
                            } else {
                                song.addAll(PlaylistSongRepository.getPlaylistSongList(requireContext(), playlist.id))
                            }
                        }
                    }
                    SongsMenuHelper.handleMenuClick(requireActivity(), song, itemId)
                }
            }
        }
    }

    override fun loadGridSize(): Int = PreferenceUtil.playlistGridSize

    override fun saveGridSize() {
        PreferenceUtil.playlistGridSize = gridSize
    }

    override fun loadGridSizeLand(): Int = PreferenceUtil.playlistGridSizeLand

    override fun saveGridSizeLand() {
        PreferenceUtil.playlistGridSizeLand = gridSize
    }

    override fun updateGridSize(newGridSize: Int) {
        mLayoutManager.spanCount = newGridSize
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String = ""
    override fun saveSortOrder(sortOrder: String) {}
    override fun updateSortOrder() {}
}