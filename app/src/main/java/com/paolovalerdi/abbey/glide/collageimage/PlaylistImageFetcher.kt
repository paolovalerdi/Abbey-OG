package com.paolovalerdi.abbey.glide.collageimage

import android.content.Context
import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.paolovalerdi.abbey.repository.AlbumRepository
import com.paolovalerdi.abbey.repository.GenreRepository
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.TagException
import java.io.*

/**
 * @author Paolo Valerdi
 */
class PlaylistImageFetcher(
    private val model: CollageImage,
    private val context: Context
) : DataFetcher<InputStream> {

    companion object {
        private val FALLBACKS = arrayOf("cover.jpg", "album.jpg", "folder.jpg", "cover.png", "album.png", "folder.png")
    }

    private var stream: FileInputStream? = null

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {

        if (CollageImageUtil.isLowMemoryRamDevice(context)) {
            callback.onDataReady(null)
            return
        }

        val retriever = MediaMetadataRetriever()

        val albumCovers = CollageImageUtil.arrangeImages(
            AlbumRepository.splitIntoAlbums(
                if (model.playlist != null) {
                    model.playlist.getSongs(context)
                } else {
                    GenreRepository.getSongs(context, model.genre!!.id)
                }
            )

        )

        val images = arrayListOf<InputStream>()

        try {
            for (cover in albumCovers) {
                retriever.setDataSource(cover.filePath)
                val image = retriever.embeddedPicture
                val inputStream: InputStream? = if (image != null) {
                    ByteArrayInputStream(image)
                } else fallback(cover.filePath)

                inputStream?.let {
                    images.add(it)
                }
            }
        } catch (e: OutOfMemoryError) {
            callback.onDataReady(null)
            retriever.release()
        } catch (e: IOException) {
            callback.onLoadFailed(e)
            retriever.release()
        } finally {
            retriever.release()
        }


        val result = CollageImageUtil.mergeImages(images)
        result?.let {
            callback.onDataReady(it)
        }

    }

    private fun fallback(path: String): InputStream? {
        try {
            val mP3File = MP3File(path)
            if (mP3File.hasID3v2Tag()) {
                val art = mP3File.tag.firstArtwork
                art?.run {
                    val imageData = art.binaryData
                    return ByteArrayInputStream(imageData)
                }
            }
        } catch (ignored: ReadOnlyFileException) {
        } catch (ignored: InvalidAudioFrameException) {
        } catch (ignored: TagException) {
        } catch (ignored: IOException) {
        }

        val parent = File(path).parentFile
        for (fallback in FALLBACKS) {
            val cover = File(parent, fallback)
            if (cover.exists()) {
                stream = FileInputStream(cover)
                return stream
            }
        }

        return null
    }

    override fun cleanup() {
        stream?.run {
            try {
                close()
            } catch (ignore: IOException) {

            }
        }
    }

    override fun cancel() {}
}

