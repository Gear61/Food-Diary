package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullPictureActivity extends AppCompatActivity{

    public static final String IMAGE_URI_KEY = "imageUri";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.dish_picture) ImageView mDishPicture;

    private String mImageUri;

    private final Callback mImageLoadingCallback = new Callback() {
        @Override
        public void onSuccess() {
            parent.animate().alpha(1.0f).setDuration(getResources().getInteger(R.integer.default_anim_length));
        }

        @Override
        public void onError() {
            Toast.makeText(FullPictureActivity.this, R.string.image_load_fail, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_picture);
        ButterKnife.bind(this);

        Drawable defaultThumbnail = new IconDrawable(this, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        mImageUri = getIntent().getStringExtra(IMAGE_URI_KEY);
        Picasso.with(this)
                .load(mImageUri)
                .error(defaultThumbnail)
                .fit()
                .centerInside()
                .into(mDishPicture, mImageLoadingCallback);
    }

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @OnClick(R.id.share)
    public void sharePicture() {
        String filePath = mImageUri.substring(mImageUri.lastIndexOf('/'));
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
