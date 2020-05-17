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
import com.paolovalerdi.abbey.model.Genre
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

class GenreImageUtil(context: Context) {

    companion object {

        private const val GENRE_CUSTOM_IMAGES = "custom_genre_images_prefs"

        private const val FOLDER_NAME = "/custom_genre_images/"

        @Volatile
        private var INSTANCE: GenreImageUtil? = null

        fun getInstance(): GenreImageUtil {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = GenreImageUtil(App.staticContext)
                INSTANCE = instance
                return instance
            }
        }

        fun getFile(genre: Genre): File {
            val dir = File(App.staticContext.filesDir, FOLDER_NAME)
            return File(dir, getFileName(genre))
        }

        private fun getFileName(genre: Genre) = String.format(
            Locale.US,
            "#%d.jpeg",
            genre.id
        )

    }

    val sharedPreferences = context.getSharedPreferences(GENRE_CUSTOM_IMAGES, Context.MODE_PRIVATE)!!

    fun generateImageAndSave(genre: Genre) {
        GlideApp.with(App.staticContext)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(CollageImage(genre = genre))
            .into(object : AbbeySimpleTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    GlobalScope.launch(IO) {
                        saveGenreFile(genre, resource)
                    }
                }

            })
    }


    fun setImageAndSave(genre: Genre, uri: Uri) {
        GlideApp.with(App.instance)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(uri)
            .into(object : AbbeySimpleTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    launch {
                        saveGenreFile(genre, resource)
                    }
                }

                override fun onDestroy() {
                    super.onDestroy()
                }
            })
    }

    fun hasCustomPlaylistImage(genre: Genre) = sharedPreferences.getBoolean(
        getFileName(genre),
        false
    )

    private suspend fun saveGenreFile(genre: Genre, bitmap: Bitmap) = withContext(IO) {
        val dir = File(App.instance.filesDir, FOLDER_NAME)
        if (dir.exists().not()) {
            dir.mkdirs()
        }

        val file = File(dir, getFileName(genre))
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
                putBoolean(getFileName(genre), true)
            }
            GenreSignatureUtil.getInstance().updateGenreSignature(genre.id)
            App.staticContext.contentResolver.notifyChange(Uri.parse("content://media"), null)
        }
    }

}