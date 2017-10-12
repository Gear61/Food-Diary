package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.HomepageFragmentController;
import com.randomappsinc.foodjournal.persistence.PreferencesManager;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity {

    @BindView(R.id.bottom_navigation) BottomNavigationViewEx mBottomNavigationView;
    @BindColor(R.color.dark_gray) int darkGray;
    @BindColor(R.color.app_red) int red;

    private int mCurrentNavId = -1;
    private HomepageFragmentController mNavigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNavigationController = new HomepageFragmentController(getFragmentManager(), R.id.container);

        mBottomNavigationView.enableAnimation(false);
        mBottomNavigationView.enableShiftingMode(false);
        mBottomNavigationView.enableItemShiftingMode(false);
        mBottomNavigationView.setTextVisibility(false);

        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.home, IoniconsIcons.ion_android_home, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.restaurants, IoniconsIcons.ion_android_restaurant, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.camera, IoniconsIcons.ion_android_camera, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.check_ins, IoniconsIcons.ion_android_checkmark_circle, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.favorites, IoniconsIcons.ion_android_favorite, this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.home);

        if (PreferencesManager.get().shouldAskForRating()) {
            showRatingPrompt();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == mCurrentNavId) {
                        return false;
                    }

                    mNavigationController.onNavItemSelected(item);

                    if (mCurrentNavId != -1) {
                        mBottomNavigationView
                                .getMenu()
                                .findItem(mCurrentNavId)
                                .getIcon()
                                .setColorFilter(darkGray, PorterDuff.Mode.SRC_ATOP);
                    }
                    item.getIcon().setColorFilter(red, PorterDuff.Mode.SRC_ATOP);

                    mCurrentNavId = item.getItemId();
                    return true;
                }
            };

    private void showRatingPrompt() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                            showToast(R.string.play_store_error);
                            return;
                        }
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void showToast(@StringRes int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mNavigationController.closeAddDishMenu();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
