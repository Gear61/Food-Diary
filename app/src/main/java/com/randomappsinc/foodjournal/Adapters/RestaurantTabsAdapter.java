package com.randomappsinc.foodjournal.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.foodjournal.Fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.MyApplication;

public class RestaurantTabsAdapter extends FragmentStatePagerAdapter {
    private String[] restaurantTabs;

    public RestaurantTabsAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
        restaurantTabs = MyApplication.getAppContext().getResources().getStringArray(R.array.restaurant_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CheckInsFragment();
            case 1:
                return new CheckInsFragment();
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
