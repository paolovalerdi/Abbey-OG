package com.paolovalerdi.abbey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.paolovalerdi.abbey.R;

public class ProPagerAdapter extends PagerAdapter {

    public enum ProPager {
        AMOLED(R.string.black_theme_name, "https://i.imgur.com/NOakdfV.png", R.color.md_grey_850, 0),
        PCARD(R.string.app_name, "https://i.imgur.com/lAJ80vr.png", R.color.md_amber_500, 1),
        PFLAT(R.string.flat, "https://i.imgur.com/Yg7lqc6.png", R.color.md_orange_200, 2);

        @StringRes
        public final int title;
        public final String imageUrl;
        public final int id;
        public final int bgColor;

        ProPager(@StringRes int title, String imageUrl, @ColorRes int bgColor, int id) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.id = id;
            this.bgColor = bgColor;
        }
    }

    private Context context;

    public ProPagerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ProPager feature = ProPager.values()[position];

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.preference_now_playing_screen_item, container, false);
        container.addView(layout);

        ImageView image = layout.findViewById(R.id.image);
        TextView title = layout.findViewById(R.id.title);
        LinearLayout itemContainer = layout.findViewById(R.id.item_container);

        itemContainer.setBackgroundColor(context.getResources().getColor(feature.bgColor));
        title.setTextColor(MaterialValueHelper.getSecondaryTextColor(context, ColorUtil.isColorLight(context.getResources().getColor(feature.bgColor))));
        Glide.with(context).load(feature.imageUrl).into(image);
        title.setText(feature.title);

        return layout;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ProPager.values().length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


}
