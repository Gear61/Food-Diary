package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.MenuItem;

import com.randomappsinc.foodjournal.R;

public class HomepageFragmentController {

    private FragmentManager mFragmentManager;
    private int mContainerId;
    private DishesFragment mDishesFragment;
    private RestaurantsFragment mRestaurantsFragment;
    private CheckInsFragment mCheckInsFragment;

    public HomepageFragmentController(FragmentManager fragmentManager, int containerId) {
        mFragmentManager = fragmentManager;
        mContainerId = containerId;
        mDishesFragment = DishesFragment.newInstance(null);
        mRestaurantsFragment = RestaurantsFragment.newInstance(false);
        mCheckInsFragment = CheckInsFragment.newInstance(null);
    }

    public void onNavItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                swapInFragment(mDishesFragment);
                break;
            case R.id.restaurants:
                swapInFragment(mRestaurantsFragment);
                break;
            case R.id.check_ins:
                swapInFragment(mCheckInsFragment);
                break;
        }
    }

    private void swapInFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(mContainerId, fragment).commit();
    }
}
