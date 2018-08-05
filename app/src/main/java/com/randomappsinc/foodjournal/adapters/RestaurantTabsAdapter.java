package com.randomappsinc.foodjournal.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.fragments.RestaurantDishesFragment;
import com.randomappsinc.foodjournal.utils.MyApplication;

public class RestaurantTabsAdapter extends FragmentPagerAdapter {

    private String[] mRestaurantTabs;
    private String mRestaurantId;

    public RestaurantTabsAdapter (FragmentManager fragmentManager, String restaurantId) {
        super(fragmentManager);
        mRestaurantTabs = MyApplication.getAppContext().getResources().getStringArray(R.array.restaurant_tabs);
        mRestaurantId = restaurantId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RestaurantDishesFragment.newInstance(mRestaurantId);
            case 1:
                return CheckInsFragment.newInstance(mRestaurantId);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mRestaurantTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mRestaurantTabs[position];
    }
}
