package com.randomappsinc.foodjournal.views;

import android.view.View;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.TotalStats;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TotalStatsView {

    @BindView(R.id.total_dishes_text) TextView totalDishesText;
    @BindView(R.id.total_restaurants_text) TextView totalRestaurantsText;
    @BindView(R.id.total_check_ins_text) TextView totalCheckInsText;
    @BindView(R.id.total_favorites_text) TextView totalFavoritesText;

    @BindString(R.string.x_dishes) String xDishesTemplate;
    @BindString(R.string.x_restaurants) String xRestaurantsTemplate;
    @BindString(R.string.x_check_ins_recorded) String xCheckInsTemplate;
    @BindString(R.string.x_favorites) String xFavoritesTemplate;

    public TotalStatsView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void reloadData() {
        TotalStats totalStats = DatabaseManager.get().getStatsDBManager().getTotalStats();

        int totalDishes = totalStats.getNumDishes();
        if (totalDishes == 1) {
            totalDishesText.setText(R.string.one_dish);
        } else {
            totalDishesText.setText(String.format(xDishesTemplate, totalDishes));
        }

        int totalRestaurants = totalStats.getNumRestaurants();
        if (totalRestaurants == 1) {
            totalRestaurantsText.setText(R.string.one_restaurant);
        } else {
            totalRestaurantsText.setText(String.format(xRestaurantsTemplate, totalRestaurants));
        }

        int totalCheckIns = totalStats.getNumCheckIns();
        if (totalCheckIns == 1) {
            totalCheckInsText.setText(R.string.one_check_in_recorded);
        } else {
            totalCheckInsText.setText(String.format(xCheckInsTemplate, totalCheckIns));
        }

        int totalFavorites = totalStats.getNumFavorites();
        if (totalFavorites == 1) {
            totalFavoritesText.setText(R.string.one_favorite);
        } else {
            totalFavoritesText.setText(String.format(xFavoritesTemplate, totalFavorites));
        }
    }
}
