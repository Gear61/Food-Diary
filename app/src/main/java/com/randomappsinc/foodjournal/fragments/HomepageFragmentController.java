package com.randomappsinc.foodjournal.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;

import com.randomappsinc.foodjournal.R;

public class HomepageFragmentController {

    private FragmentManager mFragmentManager;
    private int mContainerId;
    private HomepageDishesFragment mDishesFragment;
    private SearchFragment mSearchFragment;
    private HomepageCheckInsFragment mCheckInsFragment;
    private SettingsFragment mSettingsFragment;
    @IdRes private int mCurrentViewId;

    public HomepageFragmentController(FragmentManager fragmentManager, int containerId) {
        mFragmentManager = fragmentManager;
        mContainerId = containerId;
        mDishesFragment = HomepageDishesFragment.newInstance();
        mSearchFragment = SearchFragment.newInstance();
        mCheckInsFragment = HomepageCheckInsFragment.newInstance();
        mSettingsFragment = SettingsFragment.newInstance();
    }

    public void onNavItemSelected(@IdRes int viewId) {
        if (mCurrentViewId == viewId) {
            return;
        }

        mCurrentViewId = viewId;
        switch (viewId) {
            case R.id.home:
                swapInFragment(mDishesFragment);
                break;
            case R.id.restaurants:
                swapInFragment(mSearchFragment);
                break;
            case R.id.check_ins:
                swapInFragment(mCheckInsFragment);
                break;
            case R.id.settings:
                swapInFragment(mSettingsFragment);
                break;
        }
    }

    /** Called by the app upon start up to load the home fragment */
    public void loadHome() {
        mCurrentViewId = R.id.home;
        swapInFragment(mDishesFragment);
    }

    private void swapInFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(mContainerId, fragment).commit();
    }

    public void refreshHomepageWithAddedDish() {
        if (mDishesFragment != null) {
            mDishesFragment.refreshWithAddedDish();
        }
    }
}
