package com.paolovalerdi.abbey.repository

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore.Audio.Genres
import com.paolovalerdi.abbey.model.Genre
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

// TODO: Implement getGenreSongs
/**
 * @author Paolo Valerdi
 */
object GenresRepositoryKT {

    private val GENRE_PROJECTION = arrayOf(
        Genres._ID,
        Genres.NAME
    )

    private val GENRES_URI = Genres.EXTERNAL_CONTENT_URI

    suspend fun getAllGenres(
        contentResolver: ContentResolver
    ): List<Genre> = getGenresFromCursor(
        contentResolver,
        makeGenreCursor(contentResolver)
    )

    suspend fun getGenre(
        contentResolver: ContentResolver,
        genreID: Int
    ): Genre {
        makeGenreCursor(
            contentResolver,
            "${Genres._ID} =? ",
            arrayOf(genreID.toString())
        )?.run {
            return getGenreFromCursor(
                contentResolver,
                this
            )
        }
        return Genre(0, "", 0)
    }

    private fun getGenresFromCursor(
        contentResolver: ContentResolver,
        cursor: Cursor?
    ): List<Genre> {
        val genres = mutableListOf<Genre>()
        cursor?.run {
            if (moveToFirst()) {
                do {
                    val genre = getGenreFromCursor(contentResolver, this)
                    if (genre.songCount > 0) {
                        genres.add(genre)
                    } else {
                        try {
                            contentResolver.delete(
                                GENRES_URI,
                                "${Genres._ID} == ${genre.id}",
                                null
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } while (moveToNext())
            }
            close()
        }
        return genres
    }

    private fun getGenreFromCursor(
        contentResolver: ContentResolver,
        cursor: Cursor
    ): Genre = Genre(0, "", 0)

    private fun makeGenreCursor(
        contentResolver: ContentResolver,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): Cursor? {
        return try {
            contentResolver.query(
                GENRES_URI,
                GENRE_PROJECTION,
                selection,
                selectionArgs,
                PreferenceUtil.genreSortOder
            )
        } catch (e: SecurityException) {
            null
        }
    }

}