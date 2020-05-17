package com.paolovalerdi.abbey.ui.fragments.player.flat

import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.PlayPauseButtonOnClickHandler
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlaybackControlsFragment
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.ViewUtil
import com.paolovalerdi.abbey.util.extensions.COLOR_TRANSITION_DURATION
import com.paolovalerdi.abbey.util.extensions.getColorControlNormal
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.setTint
import kotlinx.android.synthetic.main.fragment_flat_player_playback_controls.*

class FlatPlayerPlaybackControlsFragment : AbsPlaybackControlsFragment() {

    @ColorInt
    private var lastColor = -1

    override val layoutRes = R.layout.fragment_flat_player_playback_controls

    override fun initPlaybackControlsColor() = Color.TRANSPARENT

    override fun setUpPlayPauseButton(view: View) {
        player_play_pause__button.apply {
            setImageDrawable(playPauseDrawable)
            setOnClickListener(PlayPauseButtonOnClickHandler())
        }
    }

    override fun setColors(color: Int) {
        view?.let {
            ViewUtil.createBackgroundColorTransition(it, lastColor, color).apply {
                duration = COLOR_TRANSITION_DURATION
                start()
            }
            lastColor = color
        }
        lastPlaybackControlsColor = requireContext().getColorControlNormal(color.isLight)

        progressBar.thumb.mutate().setTint(lastPlaybackControlsColor)
        player_play_pause__button.setTint(lastPlaybackControlsColor)
        totalTime.setTextColor(lastPlaybackControlsColor)
        currentProgress.setTextColor(lastPlaybackControlsColor)
        volumeSliderFragment.findSuitableColorFor(color)
    }

    override fun onCurrentSongChanged(song: Song) {}

}