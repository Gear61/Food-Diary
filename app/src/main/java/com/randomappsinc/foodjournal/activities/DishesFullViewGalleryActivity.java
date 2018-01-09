package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.DishesFullViewGalleryAdapter;
import com.randomappsinc.foodjournal.models.Dish;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishesFullViewGalleryActivity extends AppCompatActivity {

    public static final String DISH_KEY = "dish";
    public static final String DISHES_KEY = "dishes";
    public static final String POSITION_KEY = "position";

    @BindView(R.id.pictures_pager) ViewPager mPicturesPager;

    private DishesFullViewGalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dishes_full_view_gallery);
        ButterKnife.bind(this);

        Dish dish = getIntent().getParcelableExtra(DISH_KEY);
        if (dish == null) {
            ArrayList<Dish> dishes = getIntent().getParcelableArrayListExtra(DISHES_KEY);
            mGalleryAdapter = new DishesFullViewGalleryAdapter(getFragmentManager(), dishes);
        } else {
            ArrayList<Dish> singleDish = new ArrayList<>();
            singleDish.add(dish);
            mGalleryAdapter = new DishesFullViewGalleryAdapter(getFragmentManager(), singleDish);
        }
        mPicturesPager.setAdapter(mGalleryAdapter);
        mPicturesPager.setCurrentItem(getIntent().getIntExtra(POSITION_KEY, 0));
    }

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @OnClick(R.id.share)
    public void sharePicture() {
        int currentPosition = mPicturesPager.getCurrentItem();
        String imagePath = mGalleryAdapter.getImagePath(currentPosition);
        String filePath = imagePath.substring(imagePath.lastIndexOf('/'));
        String completePath = Environment.getExternalStorageDirectory().getPath()
                + "/Android/data/com.randomappsinc.foodjournal/files/Pictures"
                + filePath;
        File imageFile = new File(completePath);
        if (!imageFile.exists()) {
            return;
        }
        Uri cleanImageUri = FileProvider.getUriForFile(this, "com.randomappsinc.foodjournal.fileprovider", imageFile);

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
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
