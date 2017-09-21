package com.randomappsinc.foodjournal.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullPictureActivity extends AppCompatActivity{

    public static final String IMAGE_URI_KEY = "imageUri";

    @BindView(R.id.dish_picture) ImageView mDishPicture;
    @BindView(R.id.toolbar) View mToolbar;

    private final Callback mImageLoadingCallback = new Callback() {
        @Override
        public void onSuccess() {
            mDishPicture.animate().alpha(1.0f).setDuration(getResources().getInteger(R.integer.default_anim_length));
            mToolbar.animate().alpha(1.0f).setDuration(getResources().getInteger(R.integer.default_anim_length));
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
        String imageUri = getIntent().getStringExtra(IMAGE_URI_KEY);
        Picasso.with(this)
                .load(imageUri)
                .error(defaultThumbnail)
                .fit()
                .centerCrop()
                .into(mDishPicture, mImageLoadingCallback);
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
}
