package com.paolovalerdi.abbey.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import com.paolovalerdi.abbey.interfaces.MusicServiceEventListener
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
open class AbsMusicServiceFragment : AbsCoroutineFragment(), MusicServiceEventListener {

    private var activity: AbsMusicServiceActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            activity = context as AbsMusicServiceActivity
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " + AbsMusicServiceActivity::class.java.simpleName)
        }

    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMusicServiceEventListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.removeMusicServiceEventListener(this)
    }

    override fun onPlayingMetaChanged() {

    }

    override fun onServiceConnected() {

    }

    override fun onServiceDisconnected() {

    }

    override fun onQueueChanged() {

    }

    override fun onPlayStateChanged() {

    }

    override fun onRepeatModeChanged() {

    }

    override fun onShuffleModeChanged() {

    }

    override fun onMediaStoreChanged() {

    }
}
