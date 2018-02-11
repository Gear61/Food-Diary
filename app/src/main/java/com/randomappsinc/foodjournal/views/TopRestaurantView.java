package com.randomappsinc.foodjournal.views;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.utils.MyApplication;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopRestaurantView {

    public interface Listener {
        void onRestaurantClicked(Restaurant restaurant);
    }

    @BindView(R.id.restaurant_thumbnail) ImageView restaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.restaurant_address) TextView restaurantAddress;

    private Restaurant restaurant;
    private @NonNull Listener listener;

    public TopRestaurantView(View view, @NonNull Listener listener) {
        ButterKnife.bind(this, view);
        this.listener = listener;
    }

    public void loadRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        loadRestaurantInfoIntoView();
    }

    private void loadRestaurantInfoIntoView() {
        Drawable defaultThumbnail = new IconDrawable(
                MyApplication.getAppContext(),
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!restaurant.getImageUrl().isEmpty()) {
            Picasso.with(MyApplication.getAppContext())
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(restaurantThumbnail);
        } else {
            restaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress());
    }

    @OnClick(R.id.parent)
    public void onCellClicked() {
        listener.onRestaurantClicked(restaurant);
    }
}
