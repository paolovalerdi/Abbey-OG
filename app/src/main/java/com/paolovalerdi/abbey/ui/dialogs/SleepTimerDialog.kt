package com.paolovalerdi.abbey.ui.dialogs

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.TintHelper
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.service.ACTION_PENDING_QUIT
import com.paolovalerdi.abbey.service.ACTION_QUIT
import com.paolovalerdi.abbey.service.MusicService
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.dialog_sleep_timer.*
import java.util.*

/**
 * @author Paolo Valerdi
 */
class SleepTimerDialog : RoundedBottomSheetDialog() {

    private var seekBarProgress: Int = 0

    private lateinit var timerUpdater: TimerUpdater

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_sleep_timer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerUpdater = TimerUpdater()
        seekBarProgress = PreferenceUtil.lastSleepTimerValue
        shouldFinishLastSong.isChecked = PreferenceUtil.sleepTimerFinishLastSong
        updateTimeDisplayTime()

        TintHelper.setTintAuto(sleepTimerSeekBar, ThemeStore.accentColor(context!!), false)
        sleepTimerSeekBar.progress = seekBarProgress
        sleepTimerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 1) {
                    seekBar?.progress = 1
                    return
                }
                seekBarProgress = progress
                updateTimeDisplayTime()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                PreferenceUtil.lastSleepTimerValue = seekBarProgress
            }

        })

        cancelSleepTimerButton.setOnClickListener {
            val isRunning = makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE)
            if (isRunning != null) {
                val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(isRunning)
                isRunning.cancel()
                Toast.makeText(activity, activity?.resources?.getString(R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }


        setSleepTimerButton.applyAccentColor()
        setSleepTimerButton.setOnClickListener {
            PreferenceUtil.sleepTimerFinishLastSong = shouldFinishLastSong.isChecked
            val minutes = seekBarProgress
            val pi = makeTimerPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT)
            val nextSleepTimerElapsedTime = SystemClock.elapsedRealtime() + minutes * 60 * 1000
            PreferenceUtil.nextSleepTimerElapsedRealTime = nextSleepTimerElapsedTime
            val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime, pi)
            Toast.makeText(activity, activity?.resources?.getString(R.string.sleep_timer_set, minutes), Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timerUpdater.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE) != null) {
            timerUpdater.start()
        }
    }

    private fun updateTimeDisplayTime() {
        timerDisplay.text = String.format(Locale.getDefault(), "%d min", seekBarProgress)
    }

    private fun makeTimerPendingIntent(flag: Int): PendingIntent? =
        PendingIntent.getService(activity, 0, makeTimerIntent(), flag)

    private fun makeTimerIntent(): Intent =
        Intent(activity, MusicService::class.java)
            .setAction(if (shouldFinishLastSong.isChecked) ACTION_PENDING_QUIT else ACTION_QUIT)

    private inner class TimerUpdater internal constructor() : CountDownTimer(
        PreferenceUtil.nextSleepTimerElapsedRealTime - SystemClock.elapsedRealtime(), 1000
    ) {
        override fun onTick(millisUntilFinished: Long) {
            cancelSleepTimerButton?.text = String.format("%s (%s)", getString(R.string.cancel_current_timer), MusicUtil.getReadableDurationString(millisUntilFinished))
        }

        override fun onFinish() {
            cancelSleepTimerButton?.visibility = View.GONE
            cancelSleepTimerButton?.text = null
        }
    }

}