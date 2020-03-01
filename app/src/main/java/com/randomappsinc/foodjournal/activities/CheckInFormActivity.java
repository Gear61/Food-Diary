package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.DishGalleryAdapter;
import com.randomappsinc.foodjournal.fragments.CheckInsFragment;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DateTimeAdder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckInFormActivity extends StandardActivity {

    private static final int RESTAURANT_CODE = 1;
    private static final int DISH_TAGGING_CODE = 2;

    public static final String ADDER_MODE_KEY = "adderMode";
    public static final String CHECK_IN_KEY = "checkIn";

    private final DateTimeAdder.Listener mDateTimeListener = new DateTimeAdder.Listener() {
        @Override
        public void onDateTimeChosen(long timeChosen) {
            checkIn.setTimeAdded(timeChosen);
            dateTimeInput.setText(TimeUtils.getDefaultTimeText(timeChosen));
        }
    };

    @BindView(R.id.parent) View parent;
    @BindView(R.id.base_restaurant_cell) View restaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView restaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.restaurant_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_categories) TextView restaurantCategories;
    @BindView(R.id.choose_restaurant_prompt) View chooseRestaurantPrompt;
    @BindView(R.id.experience_input) EditText experienceInput;
    @BindView(R.id.date_input) TextView dateTimeInput;
    @BindView(R.id.tagged_dishes) RecyclerView taggedDishes;

    private CheckIn checkIn;
    private CheckIn originalCheckIn;
    private MaterialDialog deleteConfirmationDialog;
    private MaterialDialog leaveDialog;
    private boolean adderMode;
    private DateTimeAdder dateTimeAdder;
    private DishGalleryAdapter dishGalleryAdapter;
    private List<Dish> originallyTaggedDishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTimeAdder = new DateTimeAdder(getFragmentManager(), mDateTimeListener);

        adderMode = getIntent().getBooleanExtra(ADDER_MODE_KEY, false);
        if (adderMode) {
            checkIn = new CheckIn();
            checkIn.setTimeAdded(System.currentTimeMillis());

            String restaurantId = getIntent().getStringExtra(Constants.RESTAURANT_ID_KEY);
            if (restaurantId != null) {
                Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(restaurantId);
                loadRestaurantInfo(restaurant);
            }
        } else {
            checkIn = getIntent().getParcelableExtra(CHECK_IN_KEY);
            Restaurant restaurant = DatabaseManager.get()
                    .getRestaurantsDBManager()
                    .getRestaurant(checkIn.getRestaurantId());
            loadRestaurantInfo(restaurant);
            experienceInput.setText(checkIn.getMessage());
        }
        originalCheckIn = new CheckIn(checkIn);

        dateTimeInput.setText(TimeUtils.getDefaultTimeText(checkIn.getTimeAdded()));

        deleteConfirmationDialog = new MaterialDialog.Builder(this)
                .title(R.string.check_in_delete_title)
                .content(R.string.check_in_delete_content)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getCheckInsDBManager().deleteCheckIn(checkIn);
                        setResult(CheckInsFragment.DELETED_RESULT);
                        finish();
                    }
                })
                .build();

        leaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_check_in_exit)
                .content(R.string.confirm_form_exit)
                .negativeText(android.R.string.no)
                .positiveText(R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .build();

        dishGalleryAdapter = new DishGalleryAdapter(this);
        dishGalleryAdapter.setDishes(checkIn.getTaggedDishes());
        taggedDishes.setAdapter(dishGalleryAdapter);
        originallyTaggedDishes = checkIn.getTaggedDishes();
    }

    private void loadRestaurantInfo(Restaurant restaurant) {
        checkIn.setRestaurantId(restaurant.getId());
        checkIn.setRestaurantName(restaurant.getName());

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        if (!restaurant.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(restaurant.getImageUrl())
                    .error(defaultThumbnail)
                    .fit().centerCrop()
                    .into(restaurantThumbnail);
        } else {
            restaurantThumbnail.setImageDrawable(defaultThumbnail);
        }
        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getAddress());
        if (restaurantInfo.getVisibility() != View.VISIBLE) {
            restaurantInfo.setVisibility(View.VISIBLE);
        }
        if (restaurant.getCategoriesListText().isEmpty()) {
            restaurantCategories.setVisibility(View.GONE);
        } else {
            restaurantCategories.setText(restaurant.getCategoriesListText());
            restaurantCategories.setVisibility(View.VISIBLE);
        }
        if (chooseRestaurantPrompt.getVisibility() != View.GONE) {
            chooseRestaurantPrompt.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.restaurant_info_section)
    public void chooseRestaurant() {
        Intent intent = new Intent(this, FindRestaurantActivity.class);
        startActivityForResult(intent, RESTAURANT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case RESTAURANT_CODE:
                Restaurant restaurant = data.getParcelableExtra(Constants.RESTAURANT_KEY);
                loadRestaurantInfo(restaurant);
                break;
            case DISH_TAGGING_CODE:
                ArrayList<Dish> dishes = data.getParcelableArrayListExtra(DishTaggerActivity.DISHES_KEY);
                checkIn.setTaggedDishes(dishes);
                dishGalleryAdapter.setDishes(checkIn.getTaggedDishes());
                break;
        }
    }

    @OnClick(R.id.date_input)
    public void setDate() {
        dateTimeAdder.show(checkIn.getTimeAdded());
    }

    @OnClick(R.id.tag_dish)
    public void tagDish() {
        if (checkIn.getRestaurantId() == null) {
            UIUtils.showSnackbar(parent, getString(R.string.check_in_location_needed));
            return;
        }

        Intent intent = new Intent(this, DishTaggerActivity.class);
        intent.putExtra(CHECK_IN_KEY, checkIn);
        startActivityForResult(intent, DISH_TAGGING_CODE);
    }

    @OnClick(R.id.save)
    public void onCheckInSaved() {
        if (checkIn.getRestaurantId() == null) {
            UIUtils.showSnackbar(parent, getString(R.string.check_in_location_needed));
            return;
        }

        checkIn.setMessage(experienceInput.getText().toString().trim());
        if (adderMode) {
            DatabaseManager.get().getCheckInsDBManager().addCheckIn(checkIn, false);
            setResult(CheckInsFragment.ADDED_RESULT);
        } else {
            DatabaseManager.get().getCheckInsDBManager().updateCheckIn(checkIn);

            List<Dish> dishesToUntag = new ArrayList<>();
            // Untag dishes that were originally in the check-in but now aren't
            for (Dish originallyTaggedDish : originallyTaggedDishes) {
                if (!checkIn.getTaggedDishes().contains(originallyTaggedDish)) {
                    dishesToUntag.add(originallyTaggedDish);
                }
            }
            if (!dishesToUntag.isEmpty()) {
                DatabaseManager.get().getDishesDBManager().untagDishes(dishesToUntag);
            }

            setResult(CheckInsFragment.EDITED_RESULT);
        }
        finish();
    }

    public boolean showConfirmExitDialog() {
        checkIn.setMessage(experienceInput.getText().toString().trim());
        if (checkIn.hasChangedFromForm(originalCheckIn)) {
            leaveDialog.show();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!showConfirmExitDialog()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!adderMode) {
            getMenuInflater().inflate(R.menu.content_menu, menu);
            UIUtils.loadActionBarIcon(menu, R.id.delete, IoniconsIcons.ion_android_delete, this);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return showConfirmExitDialog() || super.onOptionsItemSelected(item);
            case R.id.delete:
                deleteConfirmationDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
