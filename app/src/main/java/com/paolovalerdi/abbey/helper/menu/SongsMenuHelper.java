package com.paolovalerdi.abbey.helper.menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.ui.dialogs.AddToPlaylistSheet;
import com.paolovalerdi.abbey.ui.dialogs.DeleteSongsDialog;
import com.paolovalerdi.abbey.helper.MusicPlayerRemote;
import com.paolovalerdi.abbey.model.Song;

import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongsMenuHelper {

    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull ArrayList<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistSheet.Companion.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                //DeleteMediaDialog.Companion.deleteSong(songs).show(activity.getSupportFragmentManager(), null);
                return true;
        }
        return false;
    }
}
