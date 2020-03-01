package com.randomappsinc.foodjournal.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.fragments.RestaurantDishesFragment;
import com.randomappsinc.foodjournal.utils.MyApplication;

public class RestaurantTabsAdapter extends FragmentPagerAdapter {

    private String[] restaurantTabs;
    private String restaurantId;

    public RestaurantTabsAdapter (FragmentManager fragmentManager, String restaurantId) {
        super(fragmentManager);
        restaurantTabs = MyApplication.getAppContext().getResources().getStringArray(R.array.restaurant_tabs);
        this.restaurantId = restaurantId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RestaurantDishesFragment.newInstance(restaurantId);
            case 1:
                return CheckInsFragment.newInstance(restaurantId);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return restaurantTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return restaurantTabs[position];
    }
}
