package com.paolovalerdi.abbey.repository

import android.provider.MediaStore.Audio.AudioColumns.ALBUM
import android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.util.extensions.toAlbums
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

object AlbumsRepositoryKT {

    private fun getSortOrder() = "${PreferenceUtil.albumSortOrder}, ${PreferenceUtil.albumSongSortOrder}"

    suspend fun getAllAlbums(): List<Album> {
        val songs = SongRepositoryKT.getSongsFrom(
            SongRepositoryKT.createSongCursor(
                null,
                null,
                getSortOrder()
            )!!
        )

        return songs.toAlbums()
    }

    suspend fun getAlbums(query: String): List<Album> {
        val songs = SongRepositoryKT.getSongsFrom(
            SongRepositoryKT.createSongCursor(
                "$ALBUM LIKE ?",
                arrayOf("%$query%"),
                getSortOrder()
            )!!
        )

        return songs.toAlbums()
    }

    suspend fun getAlbum(albumID: Int): List<Album> {
        val songs = SongRepositoryKT.getSongsFrom(
            SongRepositoryKT.createSongCursor(
                "$ALBUM_ID =?",
                arrayOf(albumID.toString()),
                getSortOrder()
            )!!
        )

        return songs.toAlbums()
    }

}