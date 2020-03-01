package com.randomappsinc.foodjournal.fragments;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.randomappsinc.foodjournal.R;

public class HomepageFragmentController {

    private FragmentManager fragmentManager;
    private int containerId;
    private HomeFeedFragment dishesFragment;
    private SearchFragment searchFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment profileFragment;
    @IdRes private int currentViewId;

    public HomepageFragmentController(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.dishesFragment = HomeFeedFragment.newInstance();
        this.searchFragment = SearchFragment.newInstance();
        this.favoritesFragment = FavoritesFragment.newInstance();
        this.profileFragment = ProfileFragment.newInstance();
    }

    public void onNavItemSelected(@IdRes int viewId) {
        if (currentViewId == viewId) {
            return;
        }

        currentViewId = viewId;
        switch (viewId) {
            case R.id.home:
                swapInFragment(dishesFragment);
                break;
            case R.id.search:
                swapInFragment(searchFragment);
                break;
            case R.id.favorites:
                swapInFragment(favoritesFragment);
                break;
            case R.id.profile:
                swapInFragment(profileFragment);
                break;
        }
    }

    /** Called by the app upon start up to load the home fragment */
    public void loadHome() {
        currentViewId = R.id.home;
        swapInFragment(dishesFragment);
    }

    private void swapInFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(containerId, fragment).commit();
    }

    public void refreshHomepageWithAddedDish() {
        if (dishesFragment != null) {
            dishesFragment.refreshWithAddedDish();
        }
    }
}
