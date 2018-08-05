package com.randomappsinc.foodjournal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.foodjournal.fragments.DishFullViewFragment;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

public class DishesFullViewGalleryAdapter extends FragmentPagerAdapter {

    private int[] dishIds;
    private boolean fromRestaurant;

    public DishesFullViewGalleryAdapter(FragmentManager fragmentManager, int[] dishIds, boolean fromRestaurant) {
        super(fragmentManager);
        this.dishIds = dishIds;
        this.fromRestaurant = fromRestaurant;
    }

    public int getDishId(int position) {
        return dishIds[position];
    }

    public String getImagePath(int position) {
        return DatabaseManager.get().getDishesDBManager().getDishImagePath(dishIds[position]);
    }

    @Override
    public Fragment getItem(int position) {
        return DishFullViewFragment.newInstance(dishIds[position], fromRestaurant);
    }

    @Override
    public int getCount() {
        return dishIds.length;
    }
}
