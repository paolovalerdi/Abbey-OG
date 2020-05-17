package com.paolovalerdi.abbey.ui.fragments.player.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.MusicProgressViewUpdateHelper
import com.paolovalerdi.abbey.misc.SimpleOnSeekbarChangeListener
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.service.REPEAT_MODE_ALL
import com.paolovalerdi.abbey.service.REPEAT_MODE_NONE
import com.paolovalerdi.abbey.service.REPEAT_MODE_THIS
import com.paolovalerdi.abbey.service.SHUFFLE_MODE_SHUFFLE
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity
import com.paolovalerdi.abbey.ui.fragments.player.PlaybackVolumeSliderFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.setTint
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.util.preferences.SHOW_VOLUME_SLIDER
import com.paolovalerdi.abbey.views.PlayPauseDrawable


/**
 * @author Paolo Valerdi
 */
abstract class AbsPlaybackControlsFragment : Fragment()
    , MusicProgressViewUpdateHelper.Callback
    , SharedPreferences.OnSharedPreferenceChangeListener {

    protected lateinit var totalTime: AppCompatTextView
    protected lateinit var currentProgress: AppCompatTextView
    protected lateinit var progressBar: AppCompatSeekBar
    protected lateinit var volumeSliderFragment: PlaybackVolumeSliderFragment
    protected lateinit var playPauseDrawable: PlayPauseDrawable

    protected var lastPlaybackControlsColor = -1
        set(value) {
            field = value
            nextButton.setTint(field)
            prevButton.setTint(field)
            lastDisabledPlaybackControlsColor = field
        }

    protected var lastDisabledPlaybackControlsColor = -1
        set(value) {
            val alpha = if (field.isLight) 0.38f else 0.28f
            field = ColorUtil.withAlpha(value, alpha)
            repeatMode = repeatMode
            shuffleMode = shuffleMode

        }

    private var repeatMode: Int = -1
        set(value) {
            field = value
            when (field) {
                REPEAT_MODE_NONE -> {
                    repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                    repeatButton.setTint(lastDisabledPlaybackControlsColor)
                }
                REPEAT_MODE_ALL -> {
                    repeatButton.setImageResource(R.drawable.ic_repeat_white_24dp)
                    repeatButton.setTint(lastPlaybackControlsColor)
                }
                REPEAT_MODE_THIS -> {
                    repeatButton.setImageResource(R.drawable.ic_repeat_one_white_24dp)
                    repeatButton.setTint(lastPlaybackControlsColor)
                }
            }
        }

    private var shuffleMode: Int = -1
        set(value) {
            field = value
            when (field) {
                SHUFFLE_MODE_SHUFFLE -> shuffleButton.setTint(lastPlaybackControlsColor)
                else -> shuffleButton.setTint(lastDisabledPlaybackControlsColor)
            }
        }

    private lateinit var repeatButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper

    abstract val layoutRes: Int

    abstract fun setUpPlayPauseButton(view: View)

    abstract fun setColors(color: Int)

    abstract fun initPlaybackControlsColor(): Int

    abstract fun onCurrentSongChanged(song: Song)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentRepeatMode().observe(
            viewLifecycleOwner,
            Observer { repeatMode ->
                this.repeatMode = repeatMode
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentShuffleMode().observe(
            viewLifecycleOwner,
            Observer { shuffleMode ->
                this.shuffleMode = shuffleMode
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getCurrentSong().observe(
            viewLifecycleOwner,
            Observer { currentSong ->
                onCurrentSongChanged(currentSong)
            }
        )

        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getIsPlaying().observe(
            viewLifecycleOwner,
            Observer { isPlaying ->
                onPlayStateChanged(isPlaying)
            }
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindViews(view)
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        progressBar.max = total
        progressBar.progress = progress
        totalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        currentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SHOW_VOLUME_SLIDER) {
            volumeSliderFragment.view?.isVisible = PreferenceUtil.showVolumeSlider
        }
    }

    private fun onPlayStateChanged(isPlaying: Boolean) {
        if (isPlaying) {
            playPauseDrawable.setPause(true)
        } else {
            playPauseDrawable.setPlay(true)
        }
    }

    private fun bindViews(view: View) {
        repeatButton = view.findViewById(R.id.player_repeat_button)
        prevButton = view.findViewById(R.id.player_prev_button)
        nextButton = view.findViewById(R.id.player_next_button)
        shuffleButton = view.findViewById(R.id.player_shuffle_button)
        totalTime = view.findViewById(R.id.player_song_total_time)
        currentProgress = view.findViewById(R.id.player_song_current_progress)
        progressBar = view.findViewById(R.id.player_progress_slider)
        volumeSliderFragment = childFragmentManager.findFragmentById(R.id.fragment_volume_slider) as PlaybackVolumeSliderFragment
        volumeSliderFragment.view?.let {
            it.isVisible = PreferenceUtil.showVolumeSlider
        }

        lastPlaybackControlsColor = initPlaybackControlsColor()
        setUpMusicControllers()
        setUpPlayPauseButton(view)
    }

    private fun setUpMusicControllers() {
        playPauseDrawable = PlayPauseDrawable(requireContext())
        setUpProgressSlider()
        updateProgressTextColor()
        nextButton.apply {
            setTint(lastPlaybackControlsColor)
            setOnClickListener {
                MusicPlayerRemote.playNextSong()
            }
        }
        prevButton.apply {
            setTint(lastPlaybackControlsColor)
            setOnClickListener {
                MusicPlayerRemote.playPreviousSong()
            }
        }
        shuffleButton.setOnClickListener {
            MusicPlayerRemote.toggleShuffleMode()
        }
        repeatButton.setOnClickListener {
            MusicPlayerRemote.cycleRepeatMode()
        }
    }

    private fun setUpProgressSlider() {
        progressBar.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MusicPlayerRemote.seekTo(progress)
                    onUpdateProgressViews(MusicPlayerRemote.getSongProgressMillis(), MusicPlayerRemote.getSongDurationMillis())
                }
            }
        })
    }

    private fun updateProgressTextColor() {
        totalTime.setTextColor(lastPlaybackControlsColor)
        currentProgress.setTextColor(lastPlaybackControlsColor)
    }

}
