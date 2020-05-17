package com.paolovalerdi.abbey.glide.collageimage

import com.paolovalerdi.abbey.model.Genre
import com.paolovalerdi.abbey.model.Playlist

/**
 * @author Paolo Valerdi
 */
data class CollageImage(
    val playlist: Playlist? = null,
    val genre: Genre? = null
) {

    override fun hashCode(): Int {
        if (playlist != null)
            return playlist.id
        else if (genre != null)
            return genre.id
        else return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Genre) {
            return genre!!.id == other.id
        } else if (other is Playlist) {
            return playlist!!.id == other.id
        }
        return super.equals(other)
    }

}