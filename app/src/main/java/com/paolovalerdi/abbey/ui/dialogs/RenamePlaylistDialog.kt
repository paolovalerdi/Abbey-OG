package com.paolovalerdi.abbey.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import kotlinx.android.synthetic.main.dialog_playlist_actions.*

class RenamePlaylistDialog : RoundedBottomSheetDialog() {

    companion object {

        private const val PLAYLIST_ID = "playlist_id"

        fun create(playlistId: Long): RenamePlaylistDialog {
            val dialog = RenamePlaylistDialog()
            val args = Bundle()
            args.putLong(PLAYLIST_ID, playlistId)
            dialog.arguments = args
            return dialog
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_playlist_actions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getLong(PLAYLIST_ID)

        dialogButtonSave.apply {
            applyAccentColor()
            text = getString(R.string.action_rename)
            setOnClickListener {
                playlistId?.let { id ->
                    val name = dialogEditText.text
                    name?.let { newName ->
                        if (newName.isNotEmpty()) {
                            PlaylistsUtil.renamePlaylist(activity as AppCompatActivity, id, newName.toString())
                        }
                    }
                    dismiss()
                }
            }
        }
    }

}