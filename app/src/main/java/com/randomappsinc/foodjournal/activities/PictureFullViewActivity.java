package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureFullViewActivity extends AppCompatActivity {

    public static final String IMAGE_PATH_KEY = "imagePath";

    private final Callback imageLoadingCallback = new Callback() {
        @Override
        public void onSuccess() {
            if (parent != null) {
                parent.animate().alpha(1.0f).setDuration(getResources().getInteger(R.integer.default_anim_length));
            }
        }

        @Override
        public void onError() {
            UIUtils.showToast(R.string.image_load_fail, Toast.LENGTH_LONG);
        }
    };

    @BindView(R.id.parent) @Nullable View parent;
    @BindView(R.id.picture) ImageView picture;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_full_view);
        ButterKnife.bind(this);

        Drawable defaultThumbnail = new IconDrawable(
                this,
                IoniconsIcons.ion_image).colorRes(R.color.dark_gray);

        imagePath = getIntent().getStringExtra(IMAGE_PATH_KEY);
        Picasso.with(this)
                .load(imagePath)
                .error(defaultThumbnail)
                .fit()
                .centerInside()
                .into(picture, imageLoadingCallback);
    }

    @OnClick(R.id.share)
    public void sharePicture() {
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

    @OnClick(R.id.close)
    public void closePage() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelRequest(picture);
    }
}
