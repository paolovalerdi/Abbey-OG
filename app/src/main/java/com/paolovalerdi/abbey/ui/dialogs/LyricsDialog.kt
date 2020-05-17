package com.paolovalerdi.abbey.ui.dialogs

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.lyrics.Lyrics
import com.paolovalerdi.abbey.network.lyrics.LyricsRestService
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import com.paolovalerdi.abbey.util.extensions.makeToast
import kotlinx.android.synthetic.main.dialog_lyrics.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * @author Paolo Valerdi
 */
class LyricsDialog : RoundedBottomSheetDialog() {

    companion object {

        fun create(lyrics: Lyrics?) = LyricsDialog().apply {
            arguments = Bundle().apply {
                putString("title", lyrics?.song?.title ?: MusicPlayerRemote.getCurrentSong().title)
                putString("artist", lyrics?.song?.artistName
                    ?: MusicPlayerRemote.getCurrentSong().artistName)
                putString("album", lyrics?.song?.albumName
                    ?: MusicPlayerRemote.getCurrentSong().albumName)
                putString("lyrics", lyrics?.text)
            }
        }

    }

    private var lyricsRestService: LyricsRestService? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_lyrics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mText = arguments?.getString("title")!!
        val mArtist = arguments?.getString("artist")!!
        val mAlbum = arguments?.getString("album")!!
        val lyrics = arguments?.getString("lyrics")

        title.text = mText
        subtitle.text = MusicUtil.buildInfoString(mArtist, mAlbum)

        if (lyrics == null) {
            lyricsRestService = LyricsRestService(requireContext())
            lyricsContainer.isVisible = false
            searchLyricsIcon.setOnClickListener {
                lyricsProgressBar.isVisible = true
                searchLyrics(mArtist, mText)
            }
        } else {
            searchLyricsIcon.isVisible = false
            lyricsContainer.isVisible = true
            lyricsView.text = lyrics
        }
    }

    private fun searchLyrics(artist: String, title: String) {
        launch {
            try {
                val result = withContext(IO) { lyricsRestService?.searchLyrics(artist, title) }
                if (result?.isSuccessful == true) {
                    lyricsProgressBar.isVisible = false
                    lyricsContainer.isVisible = true
                    lyricsView.text = result.body()
                } else {
                    lyricsProgressBar.isVisible = false
                    error.isVisible = true
                    googleLyricsButton.applyAccentColor()
                    googleLyricsButton.setOnClickListener {
                        googleSearch(artist, title)
                    }
                }
            } catch (e: Exception) {
                requireContext().makeToast("Something went wrong, couldn't connect")
                e.printStackTrace()
                lyricsProgressBar.isVisible = false
                error.isVisible = true
                googleLyricsButton.applyAccentColor()
                googleLyricsButton.setOnClickListener {
                    googleSearch(artist, title)
                }
            }
        }
    }

    private fun googleSearch(artist: String, title: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, "$title by $artist lyrics")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // Start search intent if possible: https://stackoverflow.com/questions/36592450/unexpected-intent-with-action-web-search
        if (Intent.ACTION_WEB_SEARCH == intent.action && intent.extras != null) {
            val query = intent.extras!!.getString(SearchManager.QUERY, null)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + query!!))
            val browserExists = intent.resolveActivityInfo(requireActivity().packageManager, 0) != null
            if (browserExists) {
                startActivity(browserIntent)
                return
            }
        }

        Toast.makeText(requireContext(), R.string.error_no_app_for_intent, Toast.LENGTH_LONG).show()
    }

}
