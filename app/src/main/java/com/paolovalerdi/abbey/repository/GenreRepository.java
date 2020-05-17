package com.paolovalerdi.abbey.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Genres;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paolovalerdi.abbey.model.Genre;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class GenreRepository {

    @NonNull
    public static ArrayList<Genre> getAllGenres(@NonNull final Context context) {
        return getGenresFromCursor(context, makeGenreCursor(context));
    }

    // TODO: Replace with a proper query instead of linear search.
    public static Genre getGenre(@NonNull final Context context, final int genreId) {
        List<Genre> genres = getAllGenres(context);
        for (Genre genre : genres) {
            if (genreId == genre.id) return genre;
        }
        return new Genre(-1, "", -1);
    }

    @NonNull
    public static ArrayList<Song> getSongs(@NonNull final Context context, final int genreId) {
        return SongRepository.getSongs(makeGenreSongCursor(context, genreId));
    }

    @NonNull
    private static ArrayList<Genre> getGenresFromCursor(@NonNull final Context context, @Nullable final Cursor cursor) {
        final ArrayList<Genre> genres = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Genre genre = getGenreFromCursor(context, cursor);
                    if (genre.songCount > 0) {
                        genres.add(genre);
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            context.getContentResolver().delete(Genres.EXTERNAL_CONTENT_URI, Genres._ID + " == " + genre.id, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // nothing we can do then
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return genres;
    }

    @NonNull
    private static Genre getGenreFromCursor(@NonNull final Context context, @NonNull final Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        final int songs = getSongs(context, id).size();
        return new Genre(id, name, songs);
    }

    @Nullable
    private static Cursor makeGenreSongCursor(@NonNull final Context context, int genreId) {
        try {
            return context.getContentResolver().query(
                    Genres.Members.getContentUri("external", genreId),
                    SongRepository.BASE_PROJECTION, SongRepository.BASE_SELECTION, null, PreferenceUtil.INSTANCE.getSongSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }

    @Nullable
    private static Cursor makeGenreCursor(@NonNull final Context context) {
        final String[] projection = new String[]{
                Genres._ID,
                Genres.NAME
        };

        try {
            return context.getContentResolver().query(
                    Genres.EXTERNAL_CONTENT_URI,
                    projection, null, null, PreferenceUtil.INSTANCE.getGenreSortOder());
        } catch (SecurityException e) {
            return null;
        }
    }
}