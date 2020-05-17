package com.paolovalerdi.abbey.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.playlist.AddToPlaylistAdapter
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.repository.PlaylistRepository
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import kotlinx.android.synthetic.main.dialog_add_to_playlist.*

/**
 * @author Paolo Valerdi
 */
class AddToPlaylistSheet : RoundedBottomSheetDialog() {

    companion object {

        private const val EXTRA_SONGS = "extra_song"

        fun create(song: Song): AddToPlaylistSheet = create(listOf(song))

        fun create(songs: List<Song>): AddToPlaylistSheet = AddToPlaylistSheet().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(EXTRA_SONGS, ArrayList(songs))
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.dialog_add_to_playlist,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songsToAdd: List<Song> = arguments?.getParcelableArrayList(EXTRA_SONGS) ?: emptyList()
        val dataSet = PlaylistRepository.getAllPlaylists(requireContext())
        val playlistAdapter = AddToPlaylistAdapter(dataSet) { itemId ->
            PlaylistsUtil.addToPlaylist(requireContext(), songsToAdd, itemId, true)
            dismiss()
        }

        addToPlaylistDialogRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
        }

        addToPlaylistDialogButton.apply {
            applyAccentColor()
            setOnClickListener {
                CreatePlaylistDialogKt.create(songsToAdd).show(requireFragmentManager(), "ADD_TO_PLAYLIST")
                dismiss()
            }
        }
    }

}