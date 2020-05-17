package com.paolovalerdi.abbey.ui.fragments.player;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.paolovalerdi.abbey.R;

public enum NowPlayingScreen {
    CARD(R.string.card, R.drawable.vd_player_card, 0),
    FLAT(R.string.flat, R.drawable.vd_player_flat, 1),
    ABBEY(R.string.app_name, R.drawable.vd_player_abbey, 2),
    MATERIAL(R.string.material, R.drawable.vd_player_material, 3),
    BLUR(R.string.blur, R.drawable.vd_player_blur, 4);

    @StringRes
    public final int titleRes;
    @DrawableRes
    public final int drawableResId;
    public final int id;

    NowPlayingScreen(@StringRes int titleRes, @DrawableRes int drawableResId, int id) {
        this.titleRes = titleRes;
        this.drawableResId = drawableResId;
        this.id = id;
    }
}
