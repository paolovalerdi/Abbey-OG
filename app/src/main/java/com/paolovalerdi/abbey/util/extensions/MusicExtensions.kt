package com.paolovalerdi.abbey.util.extensions

import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.model.Song

fun List<Song>.toAlbums(): List<Album> {
    val map = linkedMapOf<Int, Album>()
    forEach { song ->
        val albumID = song.albumId
        val album = map[albumID] ?: Album()
        album.songs.add(song)
        map[albumID] = album
    }

    return map.values.toList()
}

fun List<Song>.toAlbums(byAlbumArtists: Boolean = true): List<Album> {
    val map = linkedMapOf<String, Album>()
    forEach { song ->
        val key = if (byAlbumArtists) song.albumArtists else song.artistName
        val album = map[key] ?: Album()
        album.songs.add(song)
        map[key] = album
    }

    return map.values.toList()
}

fun List<Album>.mapToArtists(byAlbumArtists: Boolean): List<Artist> {
    val map = linkedMapOf<String, Artist>()
    forEach { album ->
        val key = if (byAlbumArtists) album.albumArtistName else album.artistName
        val artist = map[key] ?: Artist(key)
        artist.albums.add(album)
        map[key] = artist
    }

    return map.values.toList()
}

fun List<Song>.toArtists(asAlbumArtists: Boolean = true): List<Artist> {
    val albums = toAlbums()
    val map = linkedMapOf<String, Artist>()
    albums.forEach { album ->
        val key = if (asAlbumArtists) album.albumArtistName else album.artistName
        val artist = map[key] ?: Artist(key)
        artist.albums.add(album)
        map[key] = artist
    }

    return map.values.toList()
}