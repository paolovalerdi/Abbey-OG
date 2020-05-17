package com.paolovalerdi.abbey.helper

import android.content.Context
import android.os.Bundle
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.repository.SongRepository

object MediaSearchHelper {

    fun search(context: Context, query: String, extras: Bundle): ArrayList<Song> {
        val songs = SongRepository.getAllSongs(context.applicationContext)
            .filter { song ->
                song.title.containsCaseInsensitive(query)
                    .or(song.albumName.containsCaseInsensitive(query))
                    .or(song.artistName.containsCaseInsensitive(query))
            }

        return if (songs.isEmpty()) {
            SongRepository.getAllSongs(context)
        } else {
            ArrayList(songs)
        }
    }

}

fun String?.containsCaseInsensitive(other: String?) = if (this == null && other == null) {
    true
} else if (this != null && other != null) {
    toLowerCase().contains(other.toLowerCase())
} else {
    false
}