package com.paolovalerdi.abbey.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import com.paolovalerdi.abbey.util.extensions.makeToast
import kotlinx.android.synthetic.main.dialog_playlist_actions.*

/**
 * @author Paolo Valerdi
 */
class CreatePlaylistDialogKt : RoundedBottomSheetDialog() {

    companion object {

        private const val SONGS = "songs"

        fun create() = CreatePlaylistDialogKt()

        fun create(song: Song) = create(listOf(song))

        fun create(songs: List<Song>) = CreatePlaylistDialogKt().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(SONGS, ArrayList(songs))
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_playlist_actions, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songsToAdd: List<Song> = arguments?.getParcelableArrayList(SONGS) ?: emptyList()

        title.text = resources.getString(R.string.new_playlist_title)
        dialogButtonSave.apply {
            applyAccentColor()
            text = resources.getString(R.string.create_action)
            setOnClickListener {
                dialogEditText.text?.let { text ->
                    val name = text.toString().trim()
                    if (name.isNotEmpty()) {
                        if (PlaylistsUtil.doesPlaylistExist(requireContext(), name).not()) {
                            val id = PlaylistsUtil.createPlaylist(requireContext(), name)
                            if (songsToAdd.isNotEmpty()) {
                                PlaylistsUtil.addToPlaylist(requireContext(), songsToAdd, id, true)
                            }
                            dismiss()
                        } else {
                            requireContext().makeToast(resources.getString(R.string.playlist_exists, name))
                        }
                    }
                }
            }
        }
    }

}