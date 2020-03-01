package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.DishesFullViewGalleryAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class DishesFullViewGalleryActivity extends AppCompatActivity {

    public static final String POSITION_KEY = "position";

    @BindView(R.id.pictures_pager) ViewPager picturesPager;
    @BindView(R.id.favorite_toggle) TextView favoriteToggle;

    private DishesFullViewGalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishes_full_view_gallery);
        ButterKnife.bind(this);

        boolean fromRestaurant = getIntent().getBooleanExtra(Constants.FROM_RESTAURANT_KEY, false);

        int[] dishIds = getIntent().getIntArrayExtra(Constants.DISH_IDS_KEY);
        galleryAdapter = new DishesFullViewGalleryAdapter(getSupportFragmentManager(), dishIds, fromRestaurant);
        picturesPager.setAdapter(galleryAdapter);

        int initialPosition = getIntent().getIntExtra(POSITION_KEY, 0);
        picturesPager.setCurrentItem(initialPosition);
        if (initialPosition == 0) {
            refreshFavoritesToggle(picturesPager.getCurrentItem());
        }
    }

    private void refreshFavoritesToggle(int position) {
        int dishId = galleryAdapter.getDishId(position);
        Dish dish = DatabaseManager.get().getDishesDBManager().getDish(dishId);
        favoriteToggle.setText(dish.isFavorited() ? R.string.heart_filled_icon : R.string.heart_icon);
    }

    @OnPageChange(R.id.pictures_pager)
    public void onImageChanged(int position) {
        refreshFavoritesToggle(position);
    }

    @OnClick(R.id.favorite_toggle)
    public void toggleFavorite() {
        int dishId = galleryAdapter.getDishId(picturesPager.getCurrentItem());
        Dish dish = DatabaseManager.get().getDishesDBManager().getDish(dishId);
        boolean isFavoritedNow = !dish.isFavorited();
        dish.setIsFavorited(isFavoritedNow);
        DatabaseManager.get().getDishesDBManager().updateDish(dish);
        favoriteToggle.setText(isFavoritedNow ? R.string.heart_filled_icon : R.string.heart_icon);
    }

    @OnClick(R.id.edit)
    public void editDish() {
        Intent intent = new Intent(this, DishFormActivity.class);
        intent.putExtra(DishFormActivity.NEW_DISH_KEY, false);
        int dishId = galleryAdapter.getDishId(picturesPager.getCurrentItem());
        Dish dish = DatabaseManager.get().getDishesDBManager().getDish(dishId);
        intent.putExtra(DishFormActivity.DISH_KEY, dish);
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.share)
    public void sharePicture() {
        int currentPosition = picturesPager.getCurrentItem();
        String imagePath = galleryAdapter.getImagePath(currentPosition);
        DishUtils.sharePhotoWithUri(imagePath, this);
    }

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
