package com.paolovalerdi.abbey.ui.fragments.player.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyNowPlayingColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicProgressViewUpdateHelper
import com.paolovalerdi.abbey.interfaces.NowPlayingColorCallback
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.model.lyrics.AbsSynchronizedLyrics
import com.paolovalerdi.abbey.model.lyrics.Lyrics
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover.*

abstract class AbsPlayerAlbumCoverFragment : Fragment(), MusicProgressViewUpdateHelper.Callback {

    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    protected var latestPosition = -1

    var lyrics: Lyrics? = null
        set(value) {
            field = value

            if (isLyricsLayoutBound.not()) return

            if (isLyricsLayoutVisible.not()) {
                hideLyrics()
                return
            }

            playerAlbumLyricsLine1.text = null
            playerAlbumLyricsLine2.text = null

            playerAlbumLyricsContainer.isVisible = true
            playerAlbumLyricsContainer.animate()
                .alpha(1f)
                .setDuration(300)

        }

    private val isLyricsLayoutVisible
        get() = if (lyrics == null) {
            false
        } else {
            (lyrics!!.isSynchronized)
                .and(lyrics!!.isValid)
                .and(PreferenceUtil.showSynchronizedLyrics)
        }

    private val isLyricsLayoutBound
        get() = (playerAlbumLyricsContainer != null)
            .and(playerAlbumLyricsLine1 != null)
            .and(playerAlbumLyricsLine2 != null)


    protected var nowPlayingColorCallback: NowPlayingColorCallback? = null

    abstract val layoutRes: Int

    abstract fun onPlayingQueueChange(newPlayingQueue: List<Song>)

    abstract fun onCurrentQueuePostionChange(newPosition: Int)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getPlayingQueue().observe(
            viewLifecycleOwner,
            Observer { playingQueue ->
                onPlayingQueueChange(playingQueue)
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentQueuePosition().observe(
            viewLifecycleOwner,
            Observer { position ->
                latestPosition = position
                onCurrentQueuePostionChange(latestPosition)
            }
        )


        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentSong().observe(
            viewLifecycleOwner,
            Observer { currentSong ->
                loadColor(currentSong)
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentLyrics().observe(
            viewLifecycleOwner,
            Observer { currentLyrics ->
                lyrics = currentLyrics
            }
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        layoutRes,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this, 500, 1000)
        progressViewUpdateHelper.start()
    }

    override fun onDestroyView() {
        progressViewUpdateHelper.stop()
        super.onDestroyView()
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        if (isLyricsLayoutBound.not()) return

        if (isLyricsLayoutVisible.not()) {
            hideLyrics()
            return
        }

        if (lyrics !is AbsSynchronizedLyrics) return

        val synchronizedLyrics = lyrics as AbsSynchronizedLyrics

        playerAlbumLyricsContainer.isVisible = true
        playerAlbumLyricsContainer.alpha = 1f

        val oldLine = playerAlbumLyricsLine2.text.toString()
        val line = synchronizedLyrics.getLine(progress)

        if (!(oldLine == line) or oldLine.isEmpty()) {
            playerAlbumLyricsLine1.text = oldLine
            playerAlbumLyricsLine2.text = line

            playerAlbumLyricsLine1.isVisible = true
            playerAlbumLyricsLine2.isVisible = true

            playerAlbumLyricsLine2.measure(
                View.MeasureSpec.makeMeasureSpec(
                    playerAlbumLyricsLine2.measuredWidth,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.UNSPECIFIED
            )

            val h = playerAlbumLyricsLine2.measuredHeight.toFloat()

            playerAlbumLyricsLine1.alpha = 1f
            playerAlbumLyricsLine1.translationY = 0f
            playerAlbumLyricsLine1.animate().alpha(0f).translationY(-h).duration = 300

            playerAlbumLyricsLine2.alpha = 0f
            playerAlbumLyricsLine2.translationY = h
            playerAlbumLyricsLine2.animate().alpha(1f).translationY(0f).duration = 300
        }

    }

    fun setCallbacks(nowPlayingColorCallback: NowPlayingColorCallback) {
        this.nowPlayingColorCallback = nowPlayingColorCallback
    }

    protected open fun loadColor(song: Song) {
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getSongModel(song))
            .songOptions(song)
            .dontAnimate()
            .into(object : AbbeyNowPlayingColoredTarget(dummyImage) {

                override fun onColorsReady(color: Int, topIsLight: Boolean) {
                    nowPlayingColorCallback?.onColorChanged(color, topIsLight)
                }

            })
    }

    private fun hideLyrics() {
        playerAlbumLyricsContainer.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                if (isLyricsLayoutBound.not()) return@withEndAction
                playerAlbumLyricsContainer.isVisible = false
                playerAlbumLyricsLine1.text = null
                playerAlbumLyricsLine2.text = null
            }
    }

}