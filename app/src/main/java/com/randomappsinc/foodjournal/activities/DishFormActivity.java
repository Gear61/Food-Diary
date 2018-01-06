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
import com.randomappsinc.foodjournal.fragments.RestaurantsFragment;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.CheckInsDBManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.PictureUtils;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DateTimeAdder;
import com.randomappsinc.foodjournal.views.RatingView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishFormActivity extends StandardActivity {

    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";
    public static final String DISH_KEY = "dish";

    private final DateTimeAdder.Listener mDateTimeListener = new DateTimeAdder.Listener() {
        @Override
        public void onDateTimeChosen(long timeChosen) {
            mDish.setTimeAdded(timeChosen);
            mDateTimeText.setText(TimeUtils.getTimeText(timeChosen));
        }
    };

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.dish_picture) ImageView mDishPicture;
    @BindView(R.id.rating_widget) View mRatingLayout;
    @BindView(R.id.dish_name_input) EditText mDishNameInput;
    @BindView(R.id.base_restaurant_cell) View mRestaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView mRestaurantName;
    @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
    @BindView(R.id.restaurant_categories) TextView mRestaurantCategories;
    @BindView(R.id.choose_restaurant_prompt) View mChooseRestaurantPrompt;
    @BindView(R.id.date_text) TextView mDateTimeText;
    @BindView(R.id.dish_description_input) EditText mDishDescriptionInput;

    private Dish mDish;
    private Dish mOriginalDish;
    private Restaurant mRestaurant;
    private RatingView mRatingView;
    private MaterialDialog mLeaveDialog;
    private DateTimeAdder mDateTimeAdder;
    private boolean mNewDishMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRatingView = new RatingView(mRatingLayout);
        mDateTimeAdder = new DateTimeAdder(getFragmentManager(), mDateTimeListener);
        mLeaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_dish_exit)
                .content(R.string.confirm_form_exit)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (mNewDishMode) {
                            File imageFile = PictureUtils.getPictureFileFromUri(mDish.getUriString());
                            imageFile.delete();
                        }
                        finish();
                    }
                })
                .build();

        mNewDishMode = getIntent().getBooleanExtra(NEW_DISH_KEY, false);

        // Adding a new dish
        if (mNewDishMode) {
            mDish = new Dish();
            mDish.setTimeAdded(System.currentTimeMillis());
            mDateTimeText.setText(TimeUtils.getTimeText(mDish.getTimeAdded()));

            String pictureUri = getIntent().getStringExtra(URI_KEY);
            mDish.setUriString(pictureUri);

            Restaurant autoFill = CheckInsDBManager.get().getAutoFillRestaurant();
            if (autoFill == null) {
                mRestaurantInfo.setVisibility(View.INVISIBLE);
                mChooseRestaurantPrompt.setVisibility(View.VISIBLE);
            } else {
                mRestaurant = autoFill;
                loadRestaurantInfo();
            }
        }
        // Editing an existing dish
        else {
            mDish = getIntent().getParcelableExtra(DISH_KEY);
            mRestaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(mDish.getRestaurantId());
            loadDishInfo();
        }

        mOriginalDish = new Dish(mDish);

        Picasso.with(this)
                .load(mDish.getUriString())
                .fit()
                .centerCrop()
                .into(mDishPicture);
    }

    private void loadDishInfo() {
        mRatingView.loadRating(mDish.getRating());
        mDishNameInput.setText(mDish.getTitle());
        loadRestaurantInfo();
        mDateTimeText.setText(TimeUtils.getTimeText(mDish.getTimeAdded()));
        mDishDescriptionInput.setText(mDish.getDescription());
    }

    private void loadRestaurantInfo() {
        mDish.setRestaurantId(mRestaurant.getId());
        mDish.setRestaurantName(mRestaurant.getName());

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
        if (mRestaurant.getCategoriesListText().isEmpty()) {
            mRestaurantCategories.setVisibility(View.GONE);
        } else {
            mRestaurantCategories.setText(mRestaurant.getCategoriesListText());
            mRestaurantCategories.setVisibility(View.VISIBLE);
        }
        if (mChooseRestaurantPrompt.getVisibility() != View.GONE) {
            mChooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        Intent intent = new Intent(this, FindRestaurantActivity.class);
        intent.putExtra(FindRestaurantActivity.PICKER_MODE_KEY, true);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.date_text)
    public void selectDate() {
        mDateTimeAdder.show(mDish.getTimeAdded());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mRestaurant = data.getParcelableExtra(RestaurantsFragment.RESTAURANT_KEY);
            loadRestaurantInfo();
        }
    }

    private void loadFormIntoDish() {
        mDish.setTitle(mDishNameInput.getText().toString().trim());
        mDish.setRating(mRatingView.getRating());
        mDish.setDescription(mDishDescriptionInput.getText().toString().trim());
    }

    @OnClick(R.id.save)
    public void saveDish() {
        String title = mDishNameInput.getText().toString().trim();

        if (title.isEmpty()) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_title_needed));
            return;
        }
        if (mRestaurant == null) {
            UIUtils.showSnackbar(mParent, getString(R.string.dish_restaurant_needed));
            return;
        }

        loadFormIntoDish();

        if (mNewDishMode) {
            CheckIn checkIn = DatabaseManager.get().getCheckInsDBManager().getAutoTagCheckIn(mDish);
            if (checkIn == null) {
                String ask = String.format(
                        getString(R.string.auto_create_check_in),
                        mDish.getRestaurantName());
                new MaterialDialog.Builder(this)
                        .cancelable(false)
                        .title(R.string.create_check_in)
                        .content(ask)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mDish.setTimeLastUpdated(System.currentTimeMillis());
                                if (which == DialogAction.POSITIVE) {
                                    DatabaseManager.get().getCheckInsDBManager().autoCreateCheckIn(mDish);
                                } else {
                                    DatabaseManager.get().getDishesDBManager().addDish(mDish);
                                }
                                setResult(Constants.DISH_ADDED);
                                finish();
                            }
                        })
                        .show();
            } else {
                // Auto-tag to recent check-in
                mDish.setTimeLastUpdated(System.currentTimeMillis());
                DatabaseManager.get().getDishesDBManager().addDish(mDish);
                checkIn.addTaggedDish(mDish);
                DatabaseManager.get().getCheckInsDBManager().updateCheckIn(checkIn);
                setResult(Constants.DISH_ADDED);
                finish();
            }
        } else {
            DatabaseManager.get().getDishesDBManager().updateDish(mDish);
            setResult(Constants.DISH_EDITED);
            finish();
        }
    }

    /** Return true if the confirm exit dialog is shown */
    public boolean confirmExit() {
        loadFormIntoDish();
        if (mNewDishMode || mDish.hasChangedInForm(mOriginalDish)) {
            mLeaveDialog.show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!confirmExit()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (confirmExit()) {
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
