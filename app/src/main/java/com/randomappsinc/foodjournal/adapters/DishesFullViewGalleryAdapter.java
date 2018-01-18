package com.randomappsinc.foodjournal.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.foodjournal.fragments.DishFullViewFragment;
import com.randomappsinc.foodjournal.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishesFullViewGalleryAdapter extends FragmentStatePagerAdapter {

    private List<Dish> dishes;
    private boolean fromRestaurant;

    public DishesFullViewGalleryAdapter(FragmentManager fragmentManager, List<Dish> dishes, boolean fromRestaurant) {
        super(fragmentManager);
        this.dishes = dishes;
        this.fromRestaurant = fromRestaurant;
    }

    public DishesFullViewGalleryAdapter(FragmentManager fragmentManager, Dish dish, boolean fromRestaurant) {
        super(fragmentManager);
        this.dishes = new ArrayList<>();
        this.dishes.add(dish);
        this.fromRestaurant = fromRestaurant;
    }

    public String getImagePath(int position) {
        return dishes.get(position).getUriString();
    }

    @Override
    public Fragment getItem(int position) {
        return DishFullViewFragment.newInstance(dishes.get(position), fromRestaurant);
    }

    @Override
    public int getCount() {
        return dishes.size();
    }
}
