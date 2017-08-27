package com.randomappsinc.foodjournal.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.foodjournal.Fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.Fragments.DishesFragment;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.MyApplication;

public class RestaurantTabsAdapter extends FragmentStatePagerAdapter {
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
                return DishesFragment.newInstance(mRestaurantId);
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
