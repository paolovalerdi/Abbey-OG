package com.paolovalerdi.abbey.repository

import android.content.ContentResolver
import android.database.Cursor
import android.database.MergeCursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.*
import android.util.Log
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.provider.BlacklistStore
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import java.util.*
import kotlin.math.min

class SongRepositoryKT private constructor(private val contentResolver: ContentResolver) {

    companion object {

        const val ALBUM_ARTIST_COLUMN = "album_artist"

        private const val BASE_SELECTION = "$IS_MUSIC = 1 AND $TITLE != '' "
        private const val BATCH_SIZE = 900
        private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        private val BASE_PROJECTION = arrayOf(
            BaseColumns._ID, // 0
            TITLE, // 1
            TRACK, // 2
            YEAR, // 3
            DURATION, // 4
            DATA, // 5
            DATE_MODIFIED, // 6
            DATE_ADDED, // 7
            ALBUM_ID, // 8
            ALBUM, // 9
            ARTIST_ID, // 10
            ARTIST, // 11
            ALBUM_ARTIST_COLUMN //12
        )


        @Volatile
        private var INSTANCE: SongRepositoryKT? = null

        fun create(contentResolver: ContentResolver) {
            INSTANCE = INSTANCE ?: synchronized(this) {
                val i = SongRepositoryKT(contentResolver)
                INSTANCE = i
                i
            }

        }

        suspend fun getAllSongs(): List<Song> = INSTANCE?.getAllSongs() ?: emptyList()

        suspend fun getSongs(query: String): List<Song> = INSTANCE?.getSongs(query) ?: emptyList()

        suspend fun getSong(queryId: Int): Song = INSTANCE?.getSong(queryId) ?: Song.EMPTY_SONG

        suspend fun getSongsFrom(cursor: Cursor): List<Song> = INSTANCE?.getSongsFrom(cursor) ?: emptyList()

        suspend fun getSongFrom(cursor: Cursor): Song = INSTANCE?.getSongFrom(cursor) ?: Song.EMPTY_SONG

        fun createSongCursor(
            selection: String? = null,
            selectionValues: Array<String>? = null,
            sortOrder: String? = null
        ): Cursor? = INSTANCE?.createSongCursor(selection, selectionValues, sortOrder)

    }

    private fun getAllSongs(): List<Song> {
        val cursor = createSongCursor(null, null, getSortOrder())
        cursor?.let { return getSongsFrom(it) }
        return emptyList()
    }

    private fun getSongs(query: String): List<Song> {
        val cursor = createSongCursor("$TITLE LIKE ? ", arrayOf("%$query%"))
        cursor?.let { return getSongsFrom(it) }
        return emptyList()
    }

    fun getSong(queryId: Int): Song {
        val cursor = createSongCursor("$_ID =? ", arrayOf(queryId.toString()))
        cursor?.let { return getSongFrom(it) }
        return Song.EMPTY_SONG
    }

    fun getSongsFrom(cursor: Cursor): List<Song> {
        val songs = mutableListOf<Song>()
        if (cursor.moveToFirst()) {
            do {
                songs.add(createSongFrom(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return songs
    }

    fun getSongFrom(cursor: Cursor): Song {
        var song = Song.EMPTY_SONG
        if (cursor.moveToFirst()) {
            song = createSongFrom(cursor)
        }
        cursor.close()
        return song
    }


    fun createSongCursor(
        selection: String? = null,
        selectionValues: Array<String>? = null,
        sortOrder: String? = null
    ): Cursor? {

        val fixedSelection = if (selection != null) {
            "$BASE_SELECTION AND $selection"
        } else {
            BASE_SELECTION
        }

        val paths = BlacklistStore.getInstance(App.staticContext).paths
        var remainig = paths.size
        var processed = 0

        val cursors = mutableListOf<Cursor>()
        while (remainig > 0) {
            val currentBatch = min(BATCH_SIZE, remainig)

            val batchSelection = generateBlackListSelection(fixedSelection, currentBatch)
            val batchSelectionValues = addBlacklistSelectionValues(selectionValues, paths.subList(processed, processed + currentBatch))

            try {
                val cursor = contentResolver.query(
                    MEDIA_STORE_URI,
                    BASE_PROJECTION,
                    batchSelection,
                    batchSelectionValues.toTypedArray(),
                    sortOrder
                )
                cursor?.let { cursors.add(it) }
            } catch (e: SecurityException) {
            }

            remainig -= currentBatch
            processed += currentBatch
        }
        if (cursors.isEmpty()) {
            return null
        }

        return MergeCursor(cursors.toTypedArray())
    }

    private fun createSongFrom(cursor: Cursor): Song {

        val id = cursor.getInt(0)
        val title = cursor.getString(1)
        val trackNumber = cursor.getInt(2)
        val year = cursor.getInt(3)
        val duration = cursor.getLong(4)
        val data = cursor.getString(5)
        val dateAdded = cursor.getLong(6)
        val dateModified = cursor.getLong(7)
        val albumId = cursor.getInt(8)
        val albumName = cursor.getString(9)
        val artistId = cursor.getInt(10)
        val artistName = cursor.getString(11) ?: "Unknown"
        val albumArtists = cursor.getString(12) ?: "Unknown"

        return Song(id, title, trackNumber, year, duration, data, dateAdded, dateModified, albumId, albumName, artistId, artistName, albumArtists)
    }

    private fun generateBlackListSelection(selection: String?, pathCount: Int): String {
        val newSelection = StringBuilder(if (selection != null) "$selection AND " else "")
        newSelection.append("$DATA NOT LIKE ?")
        for (index in 1 until pathCount) {
            newSelection.append("AND $DATA NOT LIKE ?")
        }
        return newSelection.toString()
    }

    private fun addBlacklistSelectionValues(selectionValues: Array<String>?, paths: List<String>): List<String> {
        val newSelectionValues: ArrayList<String>
        if (selectionValues == null) {
            newSelectionValues = ArrayList(paths.size)
        } else {
            newSelectionValues = ArrayList(selectionValues.size + paths.size)
            newSelectionValues.addAll(listOf(*selectionValues))
        }

        for (i in paths.indices) {
            newSelectionValues.add(paths[i] + "%")
        }

        return newSelectionValues
    }


    private fun getSortOrder() = PreferenceUtil.songSortOrder
}