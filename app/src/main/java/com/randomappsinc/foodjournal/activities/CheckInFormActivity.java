package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.fragments.RestaurantsFragment;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DateTimeAdder;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckInFormActivity extends StandardActivity {

    public static final String ADDER_MODE_KEY = "adderMode";
    public static final String RESTAURANT_ID_KEY = "restaurantId";
    public static final String CHECK_IN_KEY = "checkIn";

    private final DateTimeAdder.Listener mDateTimeListener = new DateTimeAdder.Listener() {
        @Override
        public void onDateTimeChosen(long timeChosen) {
            mCheckIn.setTimeAdded(timeChosen);
            mDateTimeInput.setText(TimeUtils.getTimeText(timeChosen));
        }
    };

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.base_restaurant_cell) View mRestaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView mRestaurantName;
    @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
    @BindView(R.id.choose_restaurant_prompt) View mChooseRestaurantPrompt;
    @BindView(R.id.experience_input) EditText mExperienceInput;
    @BindView(R.id.date_input) TextView mDateTimeInput;

    private CheckIn mCheckIn;
    private MaterialDialog mDeleteConfirmationDialog;
    private boolean mAdderMode;
    private DateTimeAdder mDateTimeAdder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateTimeAdder = new DateTimeAdder(getFragmentManager(), mDateTimeListener);

        mAdderMode = getIntent().getBooleanExtra(ADDER_MODE_KEY, false);

        if (mAdderMode) {
            mCheckIn = new CheckIn();
            mCheckIn.setTimeAdded(System.currentTimeMillis());

            String restaurantId = getIntent().getStringExtra(RESTAURANT_ID_KEY);
            if (restaurantId != null) {
                Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(restaurantId);
                mCheckIn.setRestaurantId(restaurantId);
                mCheckIn.setRestaurantName(restaurant.getName());
                loadRestaurantInfo(restaurant);
            }
        } else {
            mCheckIn = getIntent().getParcelableExtra(CHECK_IN_KEY);
            Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(mCheckIn.getRestaurantId());
            loadRestaurantInfo(restaurant);
            mExperienceInput.setText(mCheckIn.getMessage());
        }

        mDateTimeInput.setText(TimeUtils.getTimeText(mCheckIn.getTimeAdded()));

        mDeleteConfirmationDialog = new MaterialDialog.Builder(this)
                .title(R.string.check_in_delete_title)
                .content(R.string.check_in_delete_content)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getCheckInsDBManager().deleteCheckIn(mCheckIn);
                        setResult(CheckInsFragment.DELETED_RESULT);
                        finish();
                    }
                })
                .build();
    }

    private void loadRestaurantInfo(Restaurant restaurant) {
        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!restaurant.getImageUrl().isEmpty()) {
            Picasso.with(this)
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(mRestaurantThumbnail);
        } else {
            mRestaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        mRestaurantName.setText(restaurant.getName());
        mRestaurantAddress.setText(restaurant.getAddress());
        if (mRestaurantInfo.getVisibility() != View.VISIBLE) {
            mRestaurantInfo.setVisibility(View.VISIBLE);
        }
        if (mChooseRestaurantPrompt.getVisibility() != View.GONE) {
            mChooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        boolean noRestaurants = DatabaseManager.get().getRestaurantsDBManager().getNumUserRestaurants() == 0;
        Intent intent = new Intent(this, noRestaurants ? FindRestaurantActivity.class : RestaurantsActivity.class);
        intent.putExtra(RestaurantsActivity.PICKER_MODE_KEY, true);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Restaurant restaurant = data.getParcelableExtra(RestaurantsFragment.RESTAURANT_KEY);
            mCheckIn.setRestaurantId(restaurant.getId());
            mCheckIn.setRestaurantName(restaurant.getName());
            loadRestaurantInfo(restaurant);
        }
    }

    @OnClick(R.id.date_input)
    public void setDate() {
        mDateTimeAdder.show(mCheckIn.getTimeAdded());
    }

    @OnClick(R.id.tag_dish)
    public void tagDish() {
        if (mCheckIn.getRestaurantId() == null) {
            UIUtils.showSnackbar(mParent, getString(R.string.check_in_location_needed));
            return;
        }

        Intent intent = new Intent(this, DishTaggerActivity.class);
        intent.putExtra(CHECK_IN_KEY, mCheckIn);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.save)
    public void onCheckInSaved() {
        if (mCheckIn.getRestaurantId() == null) {
            UIUtils.showSnackbar(mParent, getString(R.string.check_in_location_needed));
            return;
        }

        mCheckIn.setMessage(mExperienceInput.getText().toString().trim());
        if (mAdderMode) {
            DatabaseManager.get().getCheckInsDBManager().addCheckIn(mCheckIn);
            setResult(CheckInsFragment.ADDED_RESULT);
        } else {
            DatabaseManager.get().getCheckInsDBManager().updateCheckIn(mCheckIn);
            setResult(CheckInsFragment.EDITED_RESULT);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mAdderMode) {
            getMenuInflater().inflate(R.menu.content_menu, menu);
            UIUtils.loadActionBarIcon(menu, R.id.delete, IoniconsIcons.ion_android_delete, this);
        }
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
