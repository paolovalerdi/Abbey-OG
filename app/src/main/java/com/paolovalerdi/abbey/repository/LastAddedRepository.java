package com.paolovalerdi.abbey.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.util.ArrayList;

public class LastAddedRepository {

    @NonNull
    public static ArrayList<Song> getLastAddedSongs(@NonNull Context context) {
        return SongRepository.getSongs(makeLastAddedCursor(context));
    }

    private static Cursor makeLastAddedCursor(@NonNull final Context context) {
        long cutoff = PreferenceUtil.INSTANCE.getLastAddedCutoffTimeMillis() / 1000;

        return SongRepository.makeSongCursor(
                context,
                MediaStore.Audio.Media.DATE_ADDED + ">?",
                new String[]{String.valueOf(cutoff)},
                MediaStore.Audio.Media.DATE_ADDED + " DESC");
    }
}
