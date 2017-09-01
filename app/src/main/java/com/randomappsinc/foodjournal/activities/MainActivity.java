package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.IconItemsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends StandardActivity {
    @BindView(R.id.parent) View parent;
    @BindView(R.id.nav_options) ListView mNavOptions;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavOptions.setAdapter(new IconItemsAdapter(this, R.array.nav_drawer_tabs, R.array.nav_drawer_icons));
    }

    @OnItemClick(R.id.nav_options)
    public void onNavOptionClicked(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, RestaurantsActivity.class);
                break;
            case 1:
                intent = new Intent(this, MyLocationsActivity.class);
                break;
            case 2:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
