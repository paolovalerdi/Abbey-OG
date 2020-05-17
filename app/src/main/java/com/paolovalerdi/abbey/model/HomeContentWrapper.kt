package com.paolovalerdi.abbey.model

/**
 * @author Paolo Valerdi
 */
data class HomeContentWrapper(
    val topArtist: List<Artist> = emptyList(),
    val lastAddedAlbums: List<Artist> = emptyList(),
    val recentlyPlayed: List<Album> = emptyList(),
    val genres: List<Genre> = emptyList()
)