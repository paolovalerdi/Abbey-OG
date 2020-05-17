package com.paolovalerdi.abbey.ui.fragments.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.annotation.ColorInt
import com.kabouzeid.appthemehelper.ThemeStore
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.MusicProgressViewUpdateHelper
import com.paolovalerdi.abbey.helper.PlayPauseButtonOnClickHandler
import com.paolovalerdi.abbey.ui.fragments.AbsMusicServiceFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.*
import com.paolovalerdi.abbey.views.PlayPauseDrawable
import kotlinx.android.synthetic.main.fragment_mini_player.*

class MiniPlayerFragment : AbsMusicServiceFragment(), MusicProgressViewUpdateHelper.Callback {

    private lateinit var playPauseDrawable: PlayPauseDrawable
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_mini_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(FlingPlayBackController(requireContext()))
        setUpMiniPlayer()
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun onServiceConnected() {
        updateSongsInfo()
        updatePlayPauseState(false)
    }

    override fun onPlayingMetaChanged() {
        updateSongsInfo()
    }

    override fun onPlayStateChanged() {
        updatePlayPauseState(true)
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressBar.max = total
        progressBar.progress = progress
    }

    fun setColors(@ColorInt primaryColor: Int, @ColorInt accentColor: Int) {
        view?.setBackgroundColor(primaryColor)
        miniPlayerTitle.primaryTextColorFor(primaryColor)
        miniPlayerTex.secondaryTextColorFor(primaryColor)
        miniPlayerPlayPauseButton.setTintFor(primaryColor)
        progressBar.progressTintList = ColorStateList.valueOf(accentColor)
    }

    private fun setUpMiniPlayer() {
        setUpPlayPauseButton()
        progressBar.progressTintList = ColorStateList.valueOf(ThemeStore.accentColor(activity!!))
    }

    private fun setUpPlayPauseButton() {
        playPauseDrawable = PlayPauseDrawable(requireContext())
        miniPlayerPlayPauseButton.setImageDrawable(playPauseDrawable)
        miniPlayerPlayPauseButton.setTint(requireContext().resolveAttrColor(R.attr.colorControlNormal))
        miniPlayerPlayPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updateSongsInfo() {
        val currentSong = MusicPlayerRemote.getCurrentSong()
        GlideApp.with(this)
            .load(AbbeyGlideExtension.getSongModel(currentSong))
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .roundedCorners(true, 16)
            .into(albumArt)
        miniPlayerTitle.text = currentSong.title
        miniPlayerTex.text = MusicUtil.getSongInfoString(currentSong)
    }

    private fun updatePlayPauseState(animate: Boolean) {
        if (MusicPlayerRemote.isPlaying()) {
            playPauseDrawable.setPause(animate)
        } else {
            playPauseDrawable.setPlay(animate)
        }
    }

    private class FlingPlayBackController(context: Context) : View.OnTouchListener {

        internal var flingPlayBackController: GestureDetector

        init {
            flingPlayBackController = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                    if (Math.abs(velocityX) > Math.abs(velocityY)) {
                        if (velocityX < 0) {
                            MusicPlayerRemote.playNextSong()
                            return true
                        } else if (velocityX > 0) {
                            MusicPlayerRemote.playPreviousSong()
                            return true
                        }
                    }
                    return false
                }
            })
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return flingPlayBackController.onTouchEvent(event)
        }
    }

}