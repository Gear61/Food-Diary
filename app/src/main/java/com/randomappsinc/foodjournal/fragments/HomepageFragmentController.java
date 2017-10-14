package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;

import com.randomappsinc.foodjournal.R;

public class HomepageFragmentController {

    private FragmentManager mFragmentManager;
    private int mContainerId;
    private DishesFragment mDishesFragment;
    private RestaurantsFragment mRestaurantsFragment;
    private CheckInsFragment mCheckInsFragment;
    private SettingsFragment mSettingsFragment;

    public HomepageFragmentController(FragmentManager fragmentManager, int containerId) {
        mFragmentManager = fragmentManager;
        mContainerId = containerId;
        mDishesFragment = DishesFragment.newInstance(null);
        mRestaurantsFragment = RestaurantsFragment.newInstance(false);
        mCheckInsFragment = CheckInsFragment.newInstance(null);
        mSettingsFragment = SettingsFragment.newInstance();
    }

    public void closeAddDishMenu() {
        mDishesFragment.closeAddDishMenu();
    }

    public void onNavItemSelected(@IdRes int viewId) {
        switch (viewId) {
            case R.id.home:
                swapInFragment(mDishesFragment);
                break;
            case R.id.restaurants:
                swapInFragment(mRestaurantsFragment);
                break;
            case R.id.check_ins:
                swapInFragment(mCheckInsFragment);
                break;
            case R.id.settings:
                swapInFragment(mSettingsFragment);
                break;
        }
    }

    private void swapInFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(mContainerId, fragment).commit();
    }

    public void takePicture() {
        if (mDishesFragment != null) {
            mDishesFragment.takePicture();
        }
    }
}
