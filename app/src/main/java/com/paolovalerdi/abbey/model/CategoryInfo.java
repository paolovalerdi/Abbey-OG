package com.paolovalerdi.abbey.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.paolovalerdi.abbey.R;

public class CategoryInfo implements Parcelable {

    public Category category;
    public boolean visible;

    public CategoryInfo(Category category, boolean visible) {
        this.category = category;
        this.visible = visible;
    }

    private CategoryInfo(Parcel source) {
        category = (Category) source.readSerializable();
        visible = source.readInt() == 1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(category);
        dest.writeInt(visible ? 1 : 0);
    }

    public static final Parcelable.Creator<CategoryInfo> CREATOR = new Parcelable.Creator<CategoryInfo>() {
        public CategoryInfo createFromParcel(Parcel source) {
            return new CategoryInfo(source);
        }

        public CategoryInfo[] newArray(int size) {
            return new CategoryInfo[size];
        }
    };

    public enum Category {
        HOME(R.string.home, R.drawable.ic_home),
        SONGS(R.string.songs, R.drawable.ic_music_note),
        ALBUMS(R.string.albums, R.drawable.ic_album),
        ARTISTS(R.string.artists, R.drawable.ic_people),
        GENRES(R.string.genres, R.drawable.ic_genres),
        PLAYLISTS(R.string.playlists, R.drawable.ic_queue_music);

        public final int stringRes;
        public final int iconRes;

        Category(int stringRes, int iconRes) {
            this.stringRes = stringRes;
            this.iconRes = iconRes;
        }
    }
}
