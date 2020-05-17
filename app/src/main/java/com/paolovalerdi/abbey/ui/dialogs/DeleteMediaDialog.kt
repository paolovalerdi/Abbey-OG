package com.paolovalerdi.abbey.ui.dialogs

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.activities.saf.SAFGuideActivity
import com.paolovalerdi.abbey.ui.activities.saf.SAFGuideActivity.REQUEST_CODE_SAF_GUIDE
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.SAFUtil
import com.paolovalerdi.abbey.util.SAFUtil.REQUEST_SAF_PICK_FILE
import com.paolovalerdi.abbey.util.SAFUtil.REQUEST_SAF_PICK_TREE
import kotlinx.android.synthetic.main.dialog_delete.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Paolo Valerdi
 */
class DeleteMediaDialog : RoundedBottomSheetDialog() {

    companion object {

        private const val EXTRA_SONGS = "songs"
        private const val EXTRA_PLAYLISTS = "playlists"
        private const val MEDIA_KEY = "media"

        private const val MEDIA_SONGS = 1
        private const val MEDIA_PLAYLIST = 2

        fun deleteSong(song: Song) = deleteSong(listOf(song))

        fun deleteSong(songs: List<Song>) = DeleteMediaDialog().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(EXTRA_SONGS, ArrayList(songs))
                putInt(MEDIA_KEY, MEDIA_SONGS)
            }
        }

        fun deletePlaylist(playlist: Playlist) = deletePlaylist(listOf(playlist))

        fun deletePlaylist(playlist: List<Playlist>) = DeleteMediaDialog().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(EXTRA_PLAYLISTS, ArrayList(playlist))
                putInt(MEDIA_KEY, MEDIA_PLAYLIST)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_delete, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (arguments?.getInt(MEDIA_KEY) ?: -1) {
            MEDIA_SONGS -> {
                caseDeleteSong()
            }
            MEDIA_PLAYLIST -> {
                caseDeletePlaylist()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SAF_GUIDE -> {
                SAFUtil.openTreePicker(this)
            }
            REQUEST_SAF_PICK_TREE or REQUEST_SAF_PICK_FILE -> {
                deleteSongsInBackground(LoadingInfo(
                    requestCode = requestCode,
                    resultCode = resultCode,
                    intent = data)
                )
            }
        }
    }

    private fun caseDeleteSong() {

        // Fixes crash due to removing songs that are currently in queue
        if (MusicPlayerRemote.getPlayingQueue().isNotEmpty() or MusicPlayerRemote.isPlaying()) {
            MusicPlayerRemote.clearQueue()
        }

        val songsToRemove: List<Song> = arguments?.getParcelableArrayList(EXTRA_SONGS)
            ?: emptyList()

        val title = if (songsToRemove.size > 1) {
            HtmlCompat.fromHtml(getString(R.string.delete_x_songs, songsToRemove.size), FROM_HTML_MODE_COMPACT)
        } else HtmlCompat.fromHtml(getString(R.string.delete_song_x, songsToRemove[0].title), FROM_HTML_MODE_COMPACT)

        subHead.text = title

        dialogActions.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    if (songsToRemove.isNotEmpty()) {
                        deleteSongsInBackground(LoadingInfo(songs = songsToRemove))
                    }
                }
            }
            true
        }
    }

    private fun deleteSongs(songs: List<Song>, safUris: List<Uri>?) {
        MusicUtil.deleteTracks(requireActivity(), songs, safUris, this::dismiss)
    }

    private fun deleteSongsInBackground(info: LoadingInfo) {
        launch(IO) {
            try {
                if (info.isIntent.not()) {
                    if (SAFUtil.isSAFRequiredForSongs(info.songs).not()) {
                        deleteSongs(info.songs, null)
                    } else {
                        if (SAFUtil.isSDCardAccessGranted(requireContext())) {
                            deleteSongs(info.songs, null)
                        } else {
                            startActivityForResult(
                                Intent(requireActivity(), SAFGuideActivity::class.java),
                                REQUEST_CODE_SAF_GUIDE
                            )
                        }
                    }
                } else {
                    when (info.requestCode) {
                        REQUEST_SAF_PICK_TREE -> {
                            if (info.resultCode == RESULT_OK) {
                                SAFUtil.saveTreeUri(requireContext(), info.intent)
                                deleteSongs(info.songs, null)
                            }
                        }
                        REQUEST_SAF_PICK_FILE -> {
                            if (info.resultCode == RESULT_OK) {
                                deleteSongs(
                                    Collections.singletonList(info.songs[0]),
                                    Collections.singletonList(info.intent?.data!!))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun caseDeletePlaylist() {
        val playlists: List<Playlist> = arguments?.getParcelableArrayList(EXTRA_PLAYLISTS) ?: emptyList()
        val listSize = playlists.size
        val title = if (listSize > 1) {
            HtmlCompat.fromHtml(getString(R.string.delete_x_playlists, listSize), FROM_HTML_MODE_LEGACY)
        } else Html.fromHtml(getString(R.string.delete_playlist_x, playlists[0].name))

        subHead.text = title

        dialogActions.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    PlaylistsUtil.deletePlaylists(requireContext(), ArrayList(playlists))
                }
            }
            dismiss()
            true
        }
    }

    private data class LoadingInfo(
        val isIntent: Boolean = false,
        val songs: List<Song> = emptyList(),
        val safUris: List<Uri>? = null,
        val requestCode: Int = -1,
        val resultCode: Int = -1,
        val intent: Intent? = null
    )

}