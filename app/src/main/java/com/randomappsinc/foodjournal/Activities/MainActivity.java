package com.randomappsinc.foodjournal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.API.RestClient;
import com.randomappsinc.foodjournal.Adapters.IconItemsAdapter;
import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.nav_options) ListView mNavOptions;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @BindView(R.id.pick_source) FloatingActionMenu mSourcePicker;
    @BindView(R.id.from_camera) FloatingActionButton mCameraPicker;
    @BindView(R.id.from_files) FloatingActionButton mFilesPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavOptions.setAdapter(new IconItemsAdapter(this, R.array.nav_drawer_tabs, R.array.nav_drawer_icons));

        mCameraPicker.setImageDrawable(new IconDrawable(this,
                IoniconsIcons.ion_android_camera).colorRes(R.color.white));
        mFilesPicker.setImageDrawable(new IconDrawable(this,
                IoniconsIcons.ion_android_folder).colorRes(R.color.white));

        RestClient restClient = RestClient.getInstance();
    }

    @OnItemClick(R.id.nav_options)
    public void onNavOptionClicked(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        Intent intent = null;
        switch (position) {
            case 2:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        startActivity(intent);
    }

    @OnClick({R.id.from_camera, R.id.from_files})
    public void startRace(View view) {
        mSourcePicker.close(true);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
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
