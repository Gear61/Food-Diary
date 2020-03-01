package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.TopDish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.views.TopDishView;

import java.util.List;

public class TopDishesAdapter implements TopDishView.Listener {

    public interface Listener {
        void onTopDishClicked(List<Dish> dishes);
    }

    private ViewGroup rootView;
    private @NonNull Listener listener;

    public TopDishesAdapter(ViewGroup rootView, @NonNull Listener listener) {
        this.rootView = rootView;
        this.listener = listener;
    }

    public void loadTopDishes(Context context) {
        rootView.removeAllViews();
        List<TopDish> topDishes = DatabaseManager.get().getStatsDBManager().getTopDishes();

        if (topDishes.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View noDishes = inflater.inflate(R.layout.no_top_dishes, rootView, false);
            rootView.addView(noDishes);
        } else {
            for (TopDish topDish : topDishes) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View dishCell = inflater.inflate(R.layout.top_dish_cell, rootView, false);
                TopDishView dishView = new TopDishView(dishCell, this);
                dishView.loadTopDish(topDish);
                rootView.addView(dishCell);
            }
        }
    }

    @Override
    public void onTopDishClicked(List<Dish> dishes) {
        listener.onTopDishClicked(dishes);
    }
}
