package com.randomappsinc.foodjournal.views;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.utils.MyApplication;
import com.squareup.picasso.Picasso;

import butterknife.BindString;
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
    @BindView(R.id.num_check_ins) TextView checkInsText;

    @BindString(R.string.x_check_ins) String checkInsTemplate;

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
            Picasso.get()
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(restaurantThumbnail);
        } else {
            restaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress());

        int numCheckIns = restaurant.getCheckIns().size();
        if (numCheckIns == 1) {
            checkInsText.setText(R.string.one_check_in);
        } else {
            checkInsText.setText(String.format(checkInsTemplate, numCheckIns));
        }
    }

    @OnClick(R.id.parent)
    public void onCellClicked() {
        listener.onRestaurantClicked(restaurant);
    }
}
