package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.views.TopRestaurantView;

import java.util.List;

public class TopRestaurantsAdapter implements TopRestaurantView.Listener {

    public interface Listener {
        void onRestaurantClicked(Restaurant restaurant);
    }

    private ViewGroup rootView;
    private @NonNull Listener listener;

    public TopRestaurantsAdapter(ViewGroup rootView, @NonNull Listener listener) {
        this.rootView = rootView;
        this.listener = listener;
    }

    public void loadTopRestaurants(Context context) {
        rootView.removeAllViews();
        List<Restaurant> topRestaurants = DatabaseManager.get().getStatsDBManager().getTopRestaurants();

        if (topRestaurants.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View noRestaurants = inflater.inflate(R.layout.no_top_restaurants, rootView, false);
            rootView.addView(noRestaurants);
        } else {
            for (Restaurant restaurant : topRestaurants) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View restaurantCell = inflater.inflate(R.layout.top_restaurant_cell, rootView, false);
                TopRestaurantView restaurantView = new TopRestaurantView(restaurantCell, this);
                restaurantView.loadRestaurant(restaurant);
                rootView.addView(restaurantCell);
            }
        }
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        listener.onRestaurantClicked(restaurant);
    }
}
