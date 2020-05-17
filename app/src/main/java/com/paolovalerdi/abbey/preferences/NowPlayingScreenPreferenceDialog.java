package com.paolovalerdi.abbey.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.paolovalerdi.abbey.App;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.ui.dialogs.PurchaseDialog;
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen;
import com.paolovalerdi.abbey.util.ViewUtil;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class NowPlayingScreenPreferenceDialog extends DialogFragment implements ViewPager.OnPageChangeListener {

    private int whichButtonClicked;
    private int viewPagerPosition;

    public static NowPlayingScreenPreferenceDialog newInstance() {
        return new NowPlayingScreenPreferenceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.preference_dialog_now_playing_screen, null);
        ViewPager viewPager = view.findViewById(R.id.now_playing_screen_view_pager);
        viewPager.setAdapter(new NowPlayingScreenAdapter(getContext()));
        viewPager.addOnPageChangeListener(this);
        viewPager.setPageMargin((int) ViewUtil.convertDpToPixel(32, getResources()));
        viewPager.setCurrentItem(PreferenceUtil.INSTANCE.getNowPlayingScreen().ordinal());

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.pref_title_now_playing_screen_appearance)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    whichButtonClicked = which;
                })
                .setView(view)
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (whichButtonClicked == AlertDialog.BUTTON_POSITIVE) {
            if (!App.Companion.isProVersion() && viewPagerPosition > 0) {
                PurchaseDialog.Companion.newInstance().show(getFragmentManager(), null);
                dismiss();
            } else {
                PreferenceUtil.INSTANCE.setNowPlayingScreen(NowPlayingScreen.values()[viewPagerPosition]);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.viewPagerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class NowPlayingScreenAdapter extends PagerAdapter {

        private Context context;

        NowPlayingScreenAdapter(Context context) {
            this.context = context;
        }

        @Override
        @NonNull
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            NowPlayingScreen nowPlayingScreen = NowPlayingScreen.values()[position];

            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.preference_now_playing_screen_item, collection, false);
            collection.addView(layout);

            AppCompatImageView image = layout.findViewById(R.id.image);
            TextView title = layout.findViewById(R.id.title);
            title.setText(nowPlayingScreen.titleRes);

            int colorSurface = ATHUtil.resolveColor(context, R.attr.colorSurface);
            int colorSurfaceElevated = ATHUtil.resolveColor(context, R.attr.colorSurfaceElevated);
            int colorControlNormal = ATHUtil.resolveColor(context, R.attr.colorControlNormal);
            int colorAccent = ThemeStore.accentColor(context);

            VectorDrawableCompat v = VectorDrawableCompat.create(context.getResources(), nowPlayingScreen.drawableResId, null);
            VectorDrawableCompat.VFullPath statusBar = (VectorDrawableCompat.VFullPath) v.getTargetByName("path_status_bar");
            if (statusBar != null) {
                statusBar.setFillColor(colorAccent);
            }
            VectorDrawableCompat.VFullPath surface = (VectorDrawableCompat.VFullPath) v.getTargetByName("path_surface");
            if (surface != null) {
                surface.setFillColor(colorSurface);
            }
            VectorDrawableCompat.VFullPath cover = (VectorDrawableCompat.VFullPath) v.getTargetByName("path_cover");
            if (cover != null) {
                cover.setFillColor(colorControlNormal);
                cover.setFillAlpha(0.45f);
            }
            VectorDrawableCompat.VFullPath queue = (VectorDrawableCompat.VFullPath) v.getTargetByName("path_queue");
            if (queue != null) {
                queue.setFillColor(colorSurfaceElevated);
            }
            VectorDrawableCompat.VFullPath fab = (VectorDrawableCompat.VFullPath) v.getTargetByName("path_fab");
            if (fab != null) {
                fab.setFillColor(colorAccent);
            }

            image.setImageDrawable(v);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return NowPlayingScreen.values().length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getString(NowPlayingScreen.values()[position].titleRes);
        }
    }
}
