package com.randomappsinc.foodjournal.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.RestaurantTabsAdapter;
import com.randomappsinc.foodjournal.fragments.RestaurantsFragment;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewActivity extends StandardActivity {

    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.restaurant_thumbnail) ImageView mThumbnail;
    @BindView(R.id.restaurant_name) TextView mName;
    @BindView(R.id.restaurant_address) TextView mAddress;
    @BindView(R.id.tab_layout) TabLayout mRestaurantOptions;
    @BindView(R.id.view_pager) ViewPager mOptionsPager;

    private Restaurant mRestaurant;
    private MaterialDialog mDeleteConfirmationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_view);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRestaurant = getIntent().getParcelableExtra(RESTAURANT_KEY);

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!mRestaurant.getImageUrl().isEmpty()) {
            Picasso.with(this)
                    .load(mRestaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(mThumbnail);
        } else {
            mThumbnail.setImageDrawable(defaultThumbnail);
        }
        mName.setText(mRestaurant.getName());
        mAddress.setText(mRestaurant.getAddress());

        mOptionsPager.setAdapter(new RestaurantTabsAdapter(getFragmentManager(), mRestaurant.getId()));
        mRestaurantOptions.setupWithViewPager(mOptionsPager);

        mDeleteConfirmationDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_restaurant_deletion_title)
                .content(R.string.confirm_restaurant_deletion)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getRestaurantsDBManager().deleteRestaurant(mRestaurant);
                        setResult(RestaurantsFragment.RESTAURANT_DELETED_CODE);
                        finish();
                    }
                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_menu, menu);
        UIUtils.loadActionBarIcon(menu, R.id.delete, IoniconsIcons.ion_android_delete, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                mDeleteConfirmationDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
