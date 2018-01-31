package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.DishesFullViewGalleryAdapter;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class DishesFullViewGalleryActivity extends AppCompatActivity {

    public static final String DISH_KEY = "dish";
    public static final String DISHES_KEY = "dishes";
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

        Dish dish = getIntent().getParcelableExtra(DISH_KEY);
        if (dish == null) {
            ArrayList<Dish> dishes = getIntent().getParcelableArrayListExtra(DISHES_KEY);
            galleryAdapter = new DishesFullViewGalleryAdapter(getFragmentManager(), dishes, fromRestaurant);
        } else {
            galleryAdapter = new DishesFullViewGalleryAdapter(getFragmentManager(), dish, fromRestaurant);
        }
        picturesPager.setAdapter(galleryAdapter);

        int initialPosition = getIntent().getIntExtra(POSITION_KEY, 0);
        picturesPager.setCurrentItem(initialPosition);
        if (initialPosition == 0) {
            refreshFavoritesToggle(picturesPager.getCurrentItem());
        }
    }

    private void refreshFavoritesToggle(int position) {
        Dish dish = galleryAdapter.getDish(position);
        favoriteToggle.setText(dish.isFavorited() ? R.string.heart_filled_icon : R.string.heart_icon);
    }

    @OnPageChange(R.id.pictures_pager)
    public void onImageChanged(int position) {
        refreshFavoritesToggle(position);
    }

    @OnClick(R.id.favorite_toggle)
    public void toggleFavorite() {
        Dish dish = galleryAdapter.getDish(picturesPager.getCurrentItem());
        boolean isFavoritedNow = !dish.isFavorited();
        dish.setIsFavorited(isFavoritedNow);
        DatabaseManager.get().getDishesDBManager().updateDish(dish);
        favoriteToggle.setText(isFavoritedNow ? R.string.heart_filled_icon : R.string.heart_icon);
    }

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @OnClick(R.id.share)
    public void sharePicture() {
        int currentPosition = picturesPager.getCurrentItem();
        String imagePath = galleryAdapter.getImagePath(currentPosition);
        String filePath = imagePath.substring(imagePath.lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.randomappsinc.foodjournal/files/Pictures"
                + filePath;
        File imageFile = new File(completePath);
        if (!imageFile.exists()) {
            return;
        }
        Uri cleanImageUri = FileProvider.getUriForFile(
                this,
                "com.randomappsinc.foodjournal.fileprovider",
                imageFile);

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setStream(cleanImageUri)
                .getIntent();
        shareIntent.setData(cleanImageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
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
