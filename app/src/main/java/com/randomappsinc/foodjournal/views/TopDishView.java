package com.randomappsinc.foodjournal.views;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.TopDish;
import com.randomappsinc.foodjournal.utils.MyApplication;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopDishView {

    public interface Listener {
        void onTopDishClicked(List<Dish> dishes);
    }

    @BindView(R.id.dish_thumbnail) ImageView dishThumbnail;
    @BindView(R.id.dish_name) TextView dishName;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.num_photos) TextView numPhotosText;

    @BindString(R.string.x_photos) String photosTemplate;

    private TopDish topDish;
    private @NonNull Listener listener;

    public TopDishView(View view, @NonNull Listener listener) {
        ButterKnife.bind(this, view);
        this.listener = listener;
    }

    public void loadTopDish(TopDish topDish) {
        this.topDish = topDish;
        loadTopDishIntoView();
    }

    private void loadTopDishIntoView() {
        Drawable defaultThumbnail = new IconDrawable(
                MyApplication.getAppContext(),
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!topDish.getThumbnail().isEmpty()) {
            Picasso.get()
                    .load(topDish.getThumbnail())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(dishThumbnail);
        } else {
            dishThumbnail.setImageDrawable(defaultThumbnail);
        }
        dishName.setText(topDish.getDishName());
        restaurantName.setText(topDish.getRestaurantName());

        int numPhotos = topDish.getNumInstances();
        if (numPhotos == 1) {
            numPhotosText.setText(R.string.one_photo);
        } else {
            numPhotosText.setText(String.format(photosTemplate, numPhotos));
        }
    }

    @OnClick(R.id.parent)
    public void onCellClicked() {
        listener.onTopDishClicked(topDish.getInstances());
    }
}
