package com.paolovalerdi.abbey.util.cimages

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.bumptech.glide.signature.ObjectKey
import com.paolovalerdi.abbey.App

class PlaylistSignatureUtil(context: Context) {

    companion object {

        private const val COLLAGE_SIGNATURE = "playlist_signature"

        @Volatile
        private var INSTANCE: PlaylistSignatureUtil? = null

        fun getInstance(): PlaylistSignatureUtil {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = PlaylistSignatureUtil(App.staticContext)
                INSTANCE = instance
                return instance
            }
        }

    }

    private val sharedPreferences = context.getSharedPreferences(COLLAGE_SIGNATURE, MODE_PRIVATE)

    fun getPlaylistSignature(id: Int): ObjectKey = ObjectKey(
        getPlaylistSignatureRaw(id).toString()
    )

    fun updatePlaylistSignature(id: Int) {
        sharedPreferences.edit {
            putLong(id.toString(), System.currentTimeMillis())
        }
    }

    private fun getPlaylistSignatureRaw(id: Int): Long = sharedPreferences.getLong(
        id.toString(),
        0
    )

}