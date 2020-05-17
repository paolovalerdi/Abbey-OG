package com.paolovalerdi.abbey.glide.collageimage

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.*
import com.paolovalerdi.abbey.model.Album
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

object CollageImageUtil {

    private const val IMAGE_SIZE = 1000
    private const val PARTS = 3
    private const val DEGREES = 9f

    private var isLowMemory: Boolean? = null

    fun isLowMemoryRamDevice(context: Context): Boolean {
        if (isLowMemory == null) {
            val manager = context.applicationContext.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
            isLowMemory = manager.isLowRamDevice
        }
        return isLowMemory!!
    }

    fun arrangeImages(albums: ArrayList<Album>): List<AlbumCover> = when {
        albums.size == 1 -> {
            val item0 = AlbumCover(albums[0].safeGetFirstSong().data)
            listOf(item0, item0, item0, item0, item0, item0, item0, item0, item0)
        }
        albums.size == 2 -> {
            val item0 = AlbumCover(albums[0].safeGetFirstSong().data)
            val item1 = AlbumCover(albums[1].safeGetFirstSong().data)
            listOf(item0, item1, item0, item1, item0, item1, item0, item1, item0)
        }
        albums.size == 3 -> {
            val item0 = AlbumCover(albums[0].safeGetFirstSong().data)
            val item1 = AlbumCover(albums[1].safeGetFirstSong().data)
            val item2 = AlbumCover(albums[2].safeGetFirstSong().data)
            listOf(item0, item1, item2, item2, item0, item1, item1, item2, item0)
        }
        albums.size == 4 -> {
            val item0 = AlbumCover(albums[0].safeGetFirstSong().data)
            val item1 = AlbumCover(albums[1].safeGetFirstSong().data)
            val item2 = AlbumCover(albums[2].safeGetFirstSong().data)
            val item3 = AlbumCover(albums[3].safeGetFirstSong().data)
            listOf(item0, item1, item2, item3, item0, item1, item2, item3, item0)
        }
        albums.size in 5..8 -> {
            val item0 = AlbumCover(albums[0].safeGetFirstSong().data)
            val item1 = AlbumCover(albums[1].safeGetFirstSong().data)
            val item2 = AlbumCover(albums[2].safeGetFirstSong().data)
            val item3 = AlbumCover(albums[3].safeGetFirstSong().data)
            val item4 = AlbumCover(albums[4].safeGetFirstSong().data)
            listOf(item0, item1, item2, item3, item4, item2, item1, item4, item3)
        }
        albums.size > 9 -> {
            val items = arrayListOf<AlbumCover>()
            albums.take(9).forEach { album ->
                items.add(AlbumCover(album.safeGetFirstSong().data))
            }
            items
        }
        else -> {
            val items = arrayListOf<AlbumCover>()
            albums.forEach { album ->
                items.add(AlbumCover(album.safeGetFirstSong().data))
            }
            items
        }
    }

    fun mergeImages(images: List<InputStream>): InputStream? {
        if (images.isEmpty()) return null

        val result = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val onePartSize = IMAGE_SIZE / PARTS

        images.forEachIndexed { i, input ->
            val bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeStream(input),
                onePartSize,
                onePartSize,
                true
            )
            canvas.drawBitmap(bitmap, (onePartSize * (i % PARTS)).toFloat(), (onePartSize * (i / PARTS)).toFloat(), paint)
            bitmap.recycle()
        }

        paint.color = Color.WHITE
        paint.strokeWidth = 8f

        val oneThirdSize = (IMAGE_SIZE / 3).toFloat()
        val twoThirdSize = (IMAGE_SIZE / 3 * 2).toFloat()
        canvas.drawLine(oneThirdSize, 0f, oneThirdSize, IMAGE_SIZE.toFloat(), paint)
        canvas.drawLine(twoThirdSize, 0f, twoThirdSize, IMAGE_SIZE.toFloat(), paint)
        canvas.drawLine(0f, oneThirdSize, IMAGE_SIZE.toFloat(), oneThirdSize, paint)
        canvas.drawLine(0f, twoThirdSize, IMAGE_SIZE.toFloat(), twoThirdSize, paint)

        val finalImage = rotateImage(result, IMAGE_SIZE, DEGREES)
        result.recycle()

        val bos = ByteArrayOutputStream()
        finalImage.compress(Bitmap.CompressFormat.WEBP, 50, bos)


        return ByteArrayInputStream(bos.toByteArray())
    }

    private fun rotateImage(bitmap: Bitmap, imageSize: Int, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)

        val rotated = Bitmap.createBitmap(bitmap, 0, 0, imageSize, imageSize, matrix, true)
        bitmap.recycle()
        val cropStart = imageSize * 25 / 100
        val cropEnd: Int = (cropStart * 1.5).toInt()
        val cropped = Bitmap.createBitmap(rotated, cropStart, cropStart, imageSize - cropEnd, imageSize - cropEnd)
        rotated.recycle()

        return cropped
    }

}