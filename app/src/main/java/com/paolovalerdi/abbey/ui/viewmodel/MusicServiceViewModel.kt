package com.paolovalerdi.abbey.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.interfaces.MusicServiceEventListener
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.model.lyrics.Lyrics
import com.paolovalerdi.abbey.util.MusicUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * @author Paolo Valerdi
 */
class MusicServiceViewModel : ViewModel(), MusicServiceEventListener {

    fun getPlayingQueue(): LiveData<List<Song>> = playingQueue

    fun getCurrentQueuePosition(): LiveData<Int> = currentQueuePosition

    fun getCurrentSong(): LiveData<Song> = currentSong

    fun getCurrentShuffleMode(): LiveData<Int> = currentShuffleMode

    fun getCurrentRepeatMode(): LiveData<Int> = currentRepeatMode

    fun getCurrentLyrics(): LiveData<Lyrics?> = lyrics

    fun getIsPlaying(): LiveData<Boolean> = isPlaying

    private val playingQueue = MutableLiveData<List<Song>>()
    private val currentSong = MutableLiveData<Song>()
    private val currentQueuePosition = MutableLiveData<Int>()
    private val lyrics = MutableLiveData<Lyrics?>()
    private val currentShuffleMode = MutableLiveData<Int>()
    private val currentRepeatMode = MutableLiveData<Int>()
    private val isPlaying = MutableLiveData<Boolean>()

    override fun onServiceConnected() {
        updateQueueAndPosition()
        updateCurrentSong()
        updateRepeatMode()
        updateShuffleMode()
    }

    override fun onMediaStoreChanged() {
        updateQueueAndPosition()
        Log.d("MEDIASTORECHANGED", "MediaStoreChanged calling from MusicServiceViewModel")
    }

    override fun onQueueChanged() {
        updateQueueAndPosition()
    }

    override fun onPlayingMetaChanged() {
        updateCurrentSong()
        currentQueuePosition.value = MusicPlayerRemote.getPosition()
    }

    override fun onRepeatModeChanged() {
        updateRepeatMode()
    }

    override fun onShuffleModeChanged() {
        updateShuffleMode()
    }

    override fun onPlayStateChanged() {
        isPlaying.value = MusicPlayerRemote.isPlaying()
    }

    private fun updateRepeatMode() {
        currentRepeatMode.value = MusicPlayerRemote.getRepeatMode()
    }

    private fun updateShuffleMode() {
        currentShuffleMode.value = MusicPlayerRemote.getShuffleMode()
    }

    private fun updateQueueAndPosition() {
        playingQueue.value = MusicPlayerRemote.getPlayingQueue()
        currentQueuePosition.value = MusicPlayerRemote.getPosition()
    }

    private fun updateCurrentSong() {
        currentSong.value = MusicPlayerRemote.getCurrentSong()
        updateLyrics()
    }

    private fun updateLyrics() {
        viewModelScope.launch(IO) {
            val l = MusicUtil.getLyrics(currentSong.value)
            val finalLyrics = if (l?.isEmpty() == true) null else Lyrics.parse(currentSong.value, l)
            lyrics.postValue(finalLyrics)
        }
    }

    override fun onServiceDisconnected() {}

}