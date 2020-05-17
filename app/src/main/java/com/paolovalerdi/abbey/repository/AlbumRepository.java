package com.paolovalerdi.abbey.repository;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paolovalerdi.abbey.model.Album;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AlbumRepository {

    private static String getSongLoaderSortOrder(Context context) {
        return PreferenceUtil.INSTANCE.getAlbumSortOrder() + ", " + PreferenceUtil.INSTANCE.getAlbumSongSortOrder();
    }

    @NonNull
    public static ArrayList<Album> getAllAlbums(@NonNull final Context context) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                null,
                null,
                getSongLoaderSortOrder(context))
        );
        return splitIntoAlbums(songs);
    }

    @NonNull
    public static ArrayList<Album> getAlbums(@NonNull final Context context, String query) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                AudioColumns.ALBUM + " LIKE ?",
                new String[]{"%" + query + "%"},
                getSongLoaderSortOrder(context))
        );
        return splitIntoAlbums(songs);
    }

    @NonNull
    public static Album getAlbum(@NonNull final Context context, int albumId) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(context, AudioColumns.ALBUM_ID + "=?", new String[]{String.valueOf(albumId)}, getSongLoaderSortOrder(context)));
        Album album = new Album(songs);
        sortSongsByTrackNumber(album);
        return album;
    }

    @NonNull
    public static Album getAlbum(@NonNull final Context context, String albumName) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(context, AudioColumns.ALBUM + "=?", new String[]{String.valueOf(albumName)}, getSongLoaderSortOrder(context)));
        return new Album(songs);
    }

    @NonNull
    public static ArrayList<Album> splitIntoAlbums(@Nullable final ArrayList<Song> songs) {
        ArrayList<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getOrCreateAlbum(albums, song.albumId).songs.add(song);
            }
        }
        for (Album album : albums) {
            sortSongsByTrackNumber(album);
        }
        return albums;
    }

    private static Album getOrCreateAlbum(ArrayList<Album> albums, int albumId) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).albumId == albumId) {
                return album;
            }
        }
        Album album = new Album();
        albums.add(album);
        return album;
    }

    private static void sortSongsByTrackNumber(Album album) {
        Collections.sort(album.songs, (o1, o2) -> o1.trackNumber - o2.trackNumber);
    }
}
