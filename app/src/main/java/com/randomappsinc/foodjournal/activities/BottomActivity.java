package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomActivity extends StandardActivity {

    @BindView(R.id.bottom_navigation) BottomNavigationView mBottomNavigationView;
    @BindColor(R.color.dark_gray) int darkGray;
    @BindColor(R.color.app_red) int red;

    private int mCurrentNavId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);

        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.home, IoniconsIcons.ion_android_home, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.restaurants, IoniconsIcons.ion_android_restaurant, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.check_ins, IoniconsIcons.ion_android_checkmark_circle, this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(mNavItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.home);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == mCurrentNavId) {
                        return false;
                    }

                    switch (item.getItemId()) {
                        case R.id.home:
                            break;
                        case R.id.restaurants:
                            break;
                        case R.id.check_ins:
                            break;
                    }

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
