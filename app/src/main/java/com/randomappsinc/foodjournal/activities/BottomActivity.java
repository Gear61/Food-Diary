package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomActivity extends StandardActivity {

    @BindView(R.id.bottom_navigation) BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        ButterKnife.bind(this);

        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.home, IoniconsIcons.ion_android_home, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.restaurants, IoniconsIcons.ion_android_restaurant, this);
        UIUtils.loadBottomNavIcon(mBottomNavigationView, R.id.check_ins, IoniconsIcons.ion_android_checkmark_circle, this);
    }
}
