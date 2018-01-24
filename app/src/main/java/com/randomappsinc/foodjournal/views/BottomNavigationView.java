package com.randomappsinc.foodjournal.views;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomNavigationView {

    public interface Listener {
        void onNavItemSelected(@IdRes int viewId);

        void takePicture();
    }

    @BindView(R.id.home) TextView homeButton;
    @BindView(R.id.search) TextView searchButton;
    @BindView(R.id.favorites) TextView favoritesButton;
    @BindView(R.id.settings) TextView settingsButton;
    @BindColor(R.color.dark_gray) int darkGray;
    @BindColor(R.color.app_red) int red;

    @NonNull private Listener mListener;
    private TextView mCurrentlySelected;

    public BottomNavigationView(View parent, @NonNull Listener listener) {
        ButterKnife.bind(this, parent);
        mListener = listener;

        mCurrentlySelected = homeButton;
        homeButton.setTextColor(red);
    }

    @OnClick(R.id.home)
    public void onHomeClicked() {
        if (mCurrentlySelected == homeButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        mCurrentlySelected = homeButton;
        homeButton.setTextColor(red);
        mListener.onNavItemSelected(R.id.home);
    }

    @OnClick(R.id.search)
    public void onSearchClicked() {
        if (mCurrentlySelected == searchButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        searchButton.setTextColor(red);
        mCurrentlySelected = searchButton;
        mListener.onNavItemSelected(R.id.restaurants);
    }

    @OnClick(R.id.camera)
    public void takePicture() {
        mListener.takePicture();
    }

    @OnClick(R.id.favorites)
    public void onFavoritesClicked() {
        if (mCurrentlySelected == favoritesButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        favoritesButton.setTextColor(red);
        mCurrentlySelected = favoritesButton;
        mListener.onNavItemSelected(R.id.favorites);
    }

    @OnClick(R.id.settings)
    public void onSettingsClicked() {
        if (mCurrentlySelected == settingsButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        settingsButton.setTextColor(red);
        mCurrentlySelected = settingsButton;
        mListener.onNavItemSelected(R.id.settings);
    }
}
