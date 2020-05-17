package com.paolovalerdi.abbey.repository

import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.util.extensions.toArtists
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

object ArtistsRepositoryKT {

    private val sortOrder: String
        get() = "${PreferenceUtil.artistSortOrder}, ${PreferenceUtil.artistAlbumSortOrder}, ${PreferenceUtil.albumSongSortOrder}"

    suspend fun getAllArtists(): List<Artist> {
        val songs = SongRepositoryKT.getSongsFrom(
            createCursor(null, null)!!
        )

        return songs.toArtists(false)
    }

    suspend fun getAllAlbumArtists() {

    }

    suspend fun getArtists(query: String) {

    }

    suspend fun getAlbumArtists(query: String) {

    }

    suspend fun getArtist(name: String) {
    }

    suspend fun getAlbumArtist(name: String) {

    }

    private fun createCursor(
        selection: String?,
        selectionsValues: Array<String>?
    ) = SongRepositoryKT.createSongCursor(
        selection,
        selectionsValues,
        sortOrder
    )

}