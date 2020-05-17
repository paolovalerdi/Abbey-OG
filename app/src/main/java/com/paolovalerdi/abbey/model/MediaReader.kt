package com.paolovalerdi.abbey.model

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class MediaReader(
    path: String
) {

    private val file = AudioFileIO.read(File(path)).tag

    fun getArtist(): String? {
        val tagValue = file.getAll(FieldKey.ARTIST) ?: return null
        return StringBuilder().apply {
            tagValue.forEachIndexed { index, value ->
                if (index == tagValue.size - 1) append(value) else append(value, ", ")
            }
        }.toString()
    }

    fun getAlbumArtist(): String? = file?.getFirst(FieldKey.ALBUM_ARTIST)

}