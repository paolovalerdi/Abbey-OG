package com.paolovalerdi.abbey.util.cimages

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.edit
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.transition.Transition
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.glide.AbbeySimpleTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.glide.collageimage.CollageImage
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.util.ImageUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class PlaylistImageUtil(context: Context) {

    companion object {

        private const val PLAYLIST_CUSTOM_IMAGES = "custom_playlist_images_prefs"

        private const val FOLDER_NAME = "/custom_playlist_images/"

        @Volatile
        private var INSTANCE: PlaylistImageUtil? = null

        fun getInstance(): PlaylistImageUtil {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = PlaylistImageUtil(App.staticContext)
                INSTANCE = instance
                return instance
            }
        }

        fun getFile(playlist: Playlist): File {
            val dir = File(App.staticContext.filesDir, FOLDER_NAME)
            return File(dir, getFileName(playlist))
        }

        private fun getFileName(playlist: Playlist) = String.format(
            Locale.US,
            "#%d.jpeg",
            playlist.id
        )
    }

    val sharedPreferences = context.getSharedPreferences(PLAYLIST_CUSTOM_IMAGES, Context.MODE_PRIVATE)!!

    fun generateImageAndSave(playlist: Playlist) {
        GlideApp.with(App.staticContext)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(CollageImage(playlist))
            .into(object : AbbeySimpleTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    GlobalScope.launch(IO) {
                        savePlaylistFile(playlist, resource)
                    }
                }

            })
    }

    fun setImageAndSave(playlist: Playlist, uri: Uri) {
        GlideApp.with(App.instance)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(uri)
            .into(object : AbbeySimpleTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    launch {
                        savePlaylistFile(playlist, resource)
                    }
                }

            })
    }

    fun hasCustomPlaylistImage(playlist: Playlist) = sharedPreferences.getBoolean(
        getFileName(playlist),
        false
    )

    private suspend fun savePlaylistFile(playlist: Playlist, bitmap: Bitmap) = withContext(IO) {
        val dir = File(App.instance.filesDir, FOLDER_NAME)
        if (dir.exists().not()) {
            dir.mkdirs()
        }

        val file = File(dir, getFileName(playlist))
        var succesful = false
        try {
            val os = BufferedOutputStream(FileOutputStream(file))
            succesful = ImageUtil.resizeBitmap(bitmap, 2048).compress(Bitmap.CompressFormat.WEBP, 100, os)
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (succesful) {
            sharedPreferences.edit {
                putBoolean(getFileName(playlist), true)
            }
            PlaylistSignatureUtil.getInstance().updatePlaylistSignature(playlist.id)
            App.staticContext.contentResolver.notifyChange(Uri.parse("content://media"), null)
        }
    }


}