package com.paolovalerdi.abbey.helper.menu;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.ui.dialogs.AddToPlaylistSheet;
import com.paolovalerdi.abbey.ui.dialogs.DeleteSongsDialog;
import com.paolovalerdi.abbey.ui.dialogs.SongDetailDialog;
import com.paolovalerdi.abbey.helper.MusicPlayerRemote;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.ui.activities.tageditor.AbsTagEditorActivity;
import com.paolovalerdi.abbey.ui.activities.tageditor.SongTagEditorActivity;
import com.paolovalerdi.abbey.util.MusicUtil;
import com.paolovalerdi.abbey.util.NavigationUtil;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongMenuHelper {
    public static final int MENU_RES = R.menu.menu_item_song;


    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull Song song, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_set_as_ringtone:
                MusicUtil.setRingtone(activity, song.id);
                return true;
            case R.id.action_share:
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song, activity), null));
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(song).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                //DeleteMediaDialog.Companion.deleteSong(song).show(activity.getSupportFragmentManager(), null);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistSheet.Companion.create(song).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(song);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(song);
                return true;
            case R.id.action_tag_editor:
                Intent tagEditorIntent = new Intent(activity, SongTagEditorActivity.class);
                tagEditorIntent.putExtra(AbsTagEditorActivity.EXTRA_ID, song.id);
                activity.startActivity(tagEditorIntent);
                return true;
            case R.id.action_details:
                SongDetailDialog.create(song).show(activity.getSupportFragmentManager(), "SONG_DETAILS");
                return true;
            case R.id.action_go_to_album:
                NavigationUtil.goToAlbum(activity, song.albumId);
                return true;
            case R.id.action_go_to_artist:
                NavigationUtil.goToArtist(activity, song.artistId);
                return true;
        }
        return false;
    }

    public static abstract class OnClickSongMenu implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private AppCompatActivity activity;

        public OnClickSongMenu(@NonNull AppCompatActivity activity) {
            this.activity = activity;
        }

        public OnClickSongMenu(@NonNull Context context) {

        }

        public int getMenuRes() {
            return MENU_RES;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(activity, v);
            popupMenu.setGravity(Gravity.END);
            popupMenu.inflate(getMenuRes());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return handleMenuClick(activity, getSong(), item.getItemId());
        }

        public abstract Song getSong();
    }
}
