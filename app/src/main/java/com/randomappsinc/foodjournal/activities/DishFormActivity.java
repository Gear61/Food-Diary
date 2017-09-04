package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.randomappsinc.foodjournal.fragments.DatePickerFragment;
import com.randomappsinc.foodjournal.fragments.DishesFragment;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.RatingView;
import com.squareup.picasso.Picasso;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishFormActivity extends StandardActivity {

    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";
    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.dish_picture) ImageView mDishPicture;
    @BindView(R.id.rating_widget) View mRatingLayout;
    @BindView(R.id.dish_name_input) EditText mDishNameInput;
    @BindView(R.id.base_restaurant_cell) View mRestaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView mRestaurantName;
    @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
    @BindView(R.id.choose_restaurant_prompt) View mChooseRestaurantPrompt;
    @BindView(R.id.date_text) TextView mDateText;
    @BindView(R.id.dish_description_input) EditText mDishDescriptionInput;
    @BindColor(R.color.dark_gray) int darkGray;

    private final DatePickerFragment.Listener mDateListener = new DatePickerFragment.Listener() {
        @Override
        public void onDateChosen(long dateTimeInMillis) {
            mDishTime = dateTimeInMillis;
            mDateText.setText(TimeUtils.getDateText(mDishTime));
            mDateText.setTextColor(darkGray);
        }

        @Override
        public long getCurrentTime() {
            return mDishTime;
        }
    };

    private Restaurant mRestaurant;
    private RatingView mRatingView;
    private String mPictureUri;
    private MaterialDialog mLeaveDialog;
    private DatePickerFragment mDatePickerFragment;
    private long mDishTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPictureUri = getIntent().getStringExtra(URI_KEY);
        Picasso.with(this)
                .load(mPictureUri)
                .fit()
                .centerCrop()
                .into(mDishPicture);

        mRestaurant = getIntent().getParcelableExtra(RESTAURANT_KEY);

        if (mRestaurant == null) {
            mRestaurantInfo.setVisibility(View.INVISIBLE);
            mChooseRestaurantPrompt.setVisibility(View.VISIBLE);
        } else {
            loadRestaurantInfo();
        }

        mRatingView = new RatingView(mRatingLayout);
        mLeaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_dish_exit)
                .content(R.string.confirm_leaving_dish_form)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .build();

        mDatePickerFragment = new DatePickerFragment();
        mDatePickerFragment.setListener(mDateListener);
    }

    private void loadRestaurantInfo() {
        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!mRestaurant.getImageUrl().isEmpty()) {
            Picasso.with(this)
                    .load(mRestaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(mRestaurantThumbnail);
        } else {
            mRestaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        mRestaurantName.setText(mRestaurant.getName());
        mRestaurantAddress.setText(mRestaurant.getAddress());
        if (mRestaurantInfo.getVisibility() != View.VISIBLE) {
            mRestaurantInfo.setVisibility(View.VISIBLE);
        }
        if (mChooseRestaurantPrompt.getVisibility() != View.GONE) {
            mChooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        Intent intent = new Intent(this, RestaurantsActivity.class);
        intent.putExtra(RestaurantsActivity.PICKER_MODE_KEY, true);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.date_text)
    public void selectDate() {
        mDatePickerFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mRestaurant = data.getParcelableExtra(RESTAURANT_KEY);
            loadRestaurantInfo();
        }
    }

    @OnClick(R.id.save)
    public void saveDish() {
        int rating = mRatingView.getRating();
        String title = mDishNameInput.getText().toString().trim();

        if (rating == 0) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_rating_needed));
            return;
        }
        if (title.isEmpty()) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_title_needed));
            return;
        }
        if (mRestaurant == null) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_restaurant_needed));
            return;
        }

        Dish dish = new Dish();
        dish.setUriString(mPictureUri);
        dish.setTitle(title);
        dish.setRating(rating);
        dish.setDescription(mDishDescriptionInput.getText().toString().trim());
        dish.setTimeAdded(mDishTime);
        DatabaseManager.get().getDishesDBManager().addDish(dish, mRestaurant.getId());
        setResult(DishesFragment.DISH_ADDED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mLeaveDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
