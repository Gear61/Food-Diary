package com.randomappsinc.foodjournal.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.foodjournal.fragments.DishFullViewFragment;
import com.randomappsinc.foodjournal.models.Dish;

import java.util.List;

public class DishesFullViewGalleryAdapter extends FragmentStatePagerAdapter {

    private List<Dish> mDishes;

    public DishesFullViewGalleryAdapter(FragmentManager fragmentManager, List<Dish> dishes) {
        super(fragmentManager);
        mDishes = dishes;
    }

    public String getImagePath(int position) {
        return mDishes.get(position).getUriString();
    }

    @Override
    public Fragment getItem(int position) {
        return DishFullViewFragment.newInstance(mDishes.get(position));
    }

    @Override
    public int getCount() {
        return mDishes.size();
    }
}
