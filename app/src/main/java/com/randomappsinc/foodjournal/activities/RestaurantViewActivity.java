package com.randomappsinc.foodjournal.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.RestaurantTabsAdapter;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewActivity extends StandardActivity {
    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.restaurant_thumbnail) ImageView mThumbnail;
    @BindView(R.id.restaurant_name) TextView mName;
    @BindView(R.id.restaurant_address) TextView mAddress;
    @BindView(R.id.tab_layout) TabLayout mRestaurantOptions;
    @BindView(R.id.view_pager) ViewPager mOptionsPager;

    private Restaurant mRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_view);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRestaurant = getIntent().getParcelableExtra(RESTAURANT_KEY);

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!mRestaurant.getImageUrl().isEmpty()) {
            Picasso.with(this)
                    .load(mRestaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(mThumbnail);
        } else {
            mThumbnail.setImageDrawable(defaultThumbnail);
        }
        mName.setText(mRestaurant.getName());
        mAddress.setText(mRestaurant.getAddress());

        mOptionsPager.setAdapter(new RestaurantTabsAdapter(getFragmentManager(), mRestaurant.getId()));
        mRestaurantOptions.setupWithViewPager(mOptionsPager);
    }
}
