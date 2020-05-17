package com.paolovalerdi.abbey.ui.fragments.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.misc.SimpleOnSeekbarChangeListener
import com.paolovalerdi.abbey.util.extensions.*
import kotlinx.android.synthetic.main.fragment_player_playback_volume_slider.*

/**
 * @author Paolo Valerdi
 */
class PlaybackVolumeSliderFragment : Fragment() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeChangeListener: VolumeChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_player_playback_volume_slider, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumeChangeListener = VolumeChangeListener()
        activity?.registerReceiver(volumeChangeListener, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
        setUpVolumeSlider()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(volumeChangeListener)
    }

    fun setUpBaseBackgroundProgressColor() {
        volumeSeekBar.progressBackgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.ate_control_disabled_dark)
        )
        volumeMax.setTint(Color.WHITE)
        volumeMinButton.setTint(Color.WHITE)
    }

    fun updateColor(@ColorInt from: Int, @ColorInt to: Int) {
        volumeSeekBar.animateColorChanging(from, to)
    }

    fun findSuitableColorFor(@ColorInt color: Int) {
        volumeSeekBar.tintFor(color)
        volumeMinButton.setTintFor(color)
        volumeMax.setTintFor(color)
    }

    private fun setUpVolumeSlider() {
        volumeSeekBar.apply {
            progress = audioManager.mediaVolume
            max = audioManager.mediaMaxVolume
            setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    audioManager.setStreamVolume(STREAM_MUSIC, progress, 0)
                }
            })
        }

        volumeMinButton.setOnClickListener {
            -audioManager
        }

        volumeMax.setOnClickListener {
            +audioManager
        }
    }

    private inner class VolumeChangeListener : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            volumeSeekBar.progress = audioManager.mediaVolume
        }

    }

}
