package com.paolovalerdi.abbey.helper.menu;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.paolovalerdi.abbey.App;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.ui.dialogs.AddToPlaylistSheet;
import com.paolovalerdi.abbey.ui.dialogs.DeleteMediaDialog;
import com.paolovalerdi.abbey.ui.dialogs.RenamePlaylistDialog;
import com.paolovalerdi.abbey.helper.MusicPlayerRemote;
import com.paolovalerdi.abbey.misc.WeakContextAsyncTask;
import com.paolovalerdi.abbey.model.Playlist;
import com.paolovalerdi.abbey.util.PlaylistsUtil;

import java.io.IOException;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class PlaylistMenuHelper {

    public static boolean handleMenuClick(@NonNull AppCompatActivity activity, @NonNull final Playlist playlist, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                MusicPlayerRemote.openQueue(playlist.getSongs(activity), 0, true);
                return true;
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(playlist.getSongs(activity));
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(playlist.getSongs(activity));
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistSheet.Companion.create(playlist.getSongs(activity)).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_rename_playlist:
                RenamePlaylistDialog.Companion.create(playlist.id).show(activity.getSupportFragmentManager(), "RENAME_PLAYLIST");
                return true;
            case R.id.action_delete_playlist:
                DeleteMediaDialog.Companion.deletePlaylist(playlist).show(activity.getSupportFragmentManager(), null);
                return true;
            case R.id.action_save_playlist:
                new SavePlaylistAsyncTask(activity).execute(playlist);
                return true;
        }
        return false;
    }

    private static class SavePlaylistAsyncTask extends WeakContextAsyncTask<Playlist, String, String> {
        public SavePlaylistAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(Playlist... params) {
            try {
                return String.format(App.Companion.getInstance().getApplicationContext().getString(R.string.saved_playlist_to), PlaylistsUtil.savePlaylist(App.Companion.getInstance().getApplicationContext(), params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return String.format(App.Companion.getInstance().getApplicationContext().getString(R.string.failed_to_save_playlist), e);
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }
    }
}
