package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.views.RatingView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishFormActivity extends StandardActivity {

    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";
    public static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.dish_picture) ImageView mDishPicture;
    @BindView(R.id.dish_name_input) EditText mDishNameInput;
    @BindView(R.id.base_restaurant_cell) View mRestaurantInfo;
    @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.restaurant_name) TextView mRestaurantName;
    @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
    @BindView(R.id.choose_restaurant_prompt) View mChooseRestaurantPrompt;
    @BindView(R.id.rating_widget) View mRatingLayout;

    private Restaurant mRestaurant;
    private RatingView mRatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_form);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int titleId = getIntent().getBooleanExtra(NEW_DISH_KEY, true)
                ? R.string.add_dish
                : R.string.edit_dish;
        setTitle(titleId);

        String pictureUri = getIntent().getStringExtra(URI_KEY);
        Picasso.with(this)
                .load(pictureUri)
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
    }
}
