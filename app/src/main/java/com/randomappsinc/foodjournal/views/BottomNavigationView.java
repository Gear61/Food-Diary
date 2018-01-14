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

    @BindView(R.id.home) TextView mHomeButton;
    @BindView(R.id.search) TextView mSearchButton;
    @BindView(R.id.check_ins) TextView mCheckInsButton;
    @BindView(R.id.settings) TextView mSettingsButton;
    @BindColor(R.color.dark_gray) int darkGray;
    @BindColor(R.color.app_red) int red;

    @NonNull private Listener mListener;
    private TextView mCurrentlySelected;

    public BottomNavigationView(View parent, @NonNull Listener listener) {
        ButterKnife.bind(this, parent);
        mListener = listener;

        mCurrentlySelected = mHomeButton;
        mHomeButton.setTextColor(red);
    }

    @OnClick(R.id.home)
    public void onHomeClicked() {
        if (mCurrentlySelected == mHomeButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        mCurrentlySelected = mHomeButton;
        mHomeButton.setTextColor(red);
        mListener.onNavItemSelected(R.id.home);
    }

    @OnClick(R.id.search)
    public void onSearchClicked() {
        if (mCurrentlySelected == mSearchButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        mSearchButton.setTextColor(red);
        mCurrentlySelected = mSearchButton;
        mListener.onNavItemSelected(R.id.restaurants);
    }

    @OnClick(R.id.camera)
    public void takePicture() {
        mListener.takePicture();
    }

    @OnClick(R.id.check_ins)
    public void onCheckInsClicked() {
        if (mCurrentlySelected == mCheckInsButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        mCheckInsButton.setTextColor(red);
        mCurrentlySelected = mCheckInsButton;
        mListener.onNavItemSelected(R.id.check_ins);
    }

    @OnClick(R.id.settings)
    public void onSettingsClicked() {
        if (mCurrentlySelected == mSettingsButton) {
            return;
        }

        mCurrentlySelected.setTextColor(darkGray);
        mSettingsButton.setTextColor(red);
        mCurrentlySelected = mSettingsButton;
        mListener.onNavItemSelected(R.id.settings);
    }
}
