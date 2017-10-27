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
import com.randomappsinc.foodjournal.adapters.PictureFullViewGalleryAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureFullViewActivity extends AppCompatActivity {

    public static final String IMAGE_PATHS_KEY = "imageUrls";
    public static final String POSITION_KEY = "position";

    @BindView(R.id.pictures_pager) ViewPager mPicturesPager;

    private PictureFullViewGalleryAdapter mGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_full_view_activity);
        ButterKnife.bind(this);

        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra(IMAGE_PATHS_KEY);
        mGalleryAdapter = new PictureFullViewGalleryAdapter(getFragmentManager(), imageUrls);
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
