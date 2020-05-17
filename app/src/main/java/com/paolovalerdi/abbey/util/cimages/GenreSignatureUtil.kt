package com.paolovalerdi.abbey.util.cimages

import android.content.Context
import androidx.core.content.edit
import com.bumptech.glide.signature.ObjectKey
import com.paolovalerdi.abbey.App

class GenreSignatureUtil(context: Context) {

    companion object {

        private const val GENRE_SIGNATURE = "genre_signature"

        @Volatile
        private var INSTANCE: GenreSignatureUtil? = null

        fun getInstance(): GenreSignatureUtil {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = GenreSignatureUtil(App.staticContext)
                INSTANCE = instance
                return instance
            }
        }
    }

    private val sharedPreferences = context.getSharedPreferences(GENRE_SIGNATURE, Context.MODE_PRIVATE)

    fun updateGenreSignature(id: Int) {
        sharedPreferences.edit {
            putLong(id.toString(), System.currentTimeMillis())
        }
    }

    fun getGenreSignature(id: Int): ObjectKey = ObjectKey(
        getGenreSignatureRaw(id).toString()
    )

    private fun getGenreSignatureRaw(id: Int): Long = sharedPreferences.getLong(
        id.toString(),
        0
    )

}