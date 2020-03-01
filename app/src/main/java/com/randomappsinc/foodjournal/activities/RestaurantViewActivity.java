package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.tabs.TabLayout;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.RestaurantTabsAdapter;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewActivity extends StandardActivity {

    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.restaurant_thumbnail) ImageView thumbnail;
    @BindView(R.id.restaurant_name) TextView name;
    @BindView(R.id.restaurant_address) TextView address;
    @BindView(R.id.restaurant_categories) TextView categories;
    @BindView(R.id.tab_layout) TabLayout restaurantOptions;
    @BindView(R.id.view_pager) ViewPager optionsPager;

    private Restaurant restaurant;
    private MaterialDialog deleteConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_view);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            restaurant = savedInstanceState.getParcelable(Constants.RESTAURANT_KEY);
        } else if (getIntent().getData() != null) {
            // Coming from here an implicit intent via clicking on a dish title
            String path = getIntent().getData().getPath();

            // Remove the / that starts the path
            String restaurantId = path.substring(1);
            restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(restaurantId);
        } else {
            restaurant = getIntent().getParcelableExtra(RESTAURANT_KEY);
        }

        RestClient.getInstance().updateRestaurantInfo(restaurant);

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!restaurant.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(thumbnail);
        } else {
            thumbnail.setImageDrawable(defaultThumbnail);
        }
        name.setText(restaurant.getName());
        address.setText(restaurant.getAddress());
        if (restaurant.getCategoriesListText().isEmpty()) {
            categories.setVisibility(View.GONE);
        } else {
            categories.setText(restaurant.getCategoriesListText());
            categories.setVisibility(View.VISIBLE);
        }

        optionsPager.setAdapter(new RestaurantTabsAdapter(getSupportFragmentManager(), restaurant.getId()));
        restaurantOptions.setupWithViewPager(optionsPager);

        deleteConfirmationDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_restaurant_deletion_title)
                .content(R.string.confirm_restaurant_deletion)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getRestaurantsDBManager().deleteRestaurant(restaurant);
                        UIUtils.showToast(R.string.restaurant_deleted, Toast.LENGTH_LONG);
                        finish();
                    }
                })
                .build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.RESTAURANT_KEY, restaurant);
        super.onSaveInstanceState(outState);
    }

    private void navigateToRestaurant() {
        String mapUri = "google.navigation:q=" + restaurant.getAddress() + " " + restaurant.getName();
        startActivity(Intent.createChooser(
                new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUri)),
                getString(R.string.navigate_with)));
    }

    private void callRestaurant() {
        String phoneUri = "tel:" + restaurant.getPhoneNumber();
        startActivity(Intent.createChooser(
                new Intent(Intent.ACTION_DIAL, Uri.parse(phoneUri)),
                getString(R.string.call_with)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.navigate, IoniconsIcons.ion_navigate, this);
        UIUtils.loadActionBarIcon(menu, R.id.call, IoniconsIcons.ion_android_call, this);
        UIUtils.loadActionBarIcon(menu, R.id.delete, IoniconsIcons.ion_android_delete, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigate:
                navigateToRestaurant();
                return true;
            case R.id.call:
                callRestaurant();
                return true;
            case R.id.delete:
                deleteConfirmationDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
