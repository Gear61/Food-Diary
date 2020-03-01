package com.randomappsinc.foodjournal.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.utils.DishUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        public void onError(Exception e) {
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
        Picasso.get()
                .load(imagePath)
                .error(defaultThumbnail)
                .fit()
                .centerInside()
                .into(picture, imageLoadingCallback);
    }

    @OnClick(R.id.share)
    public void sharePicture() {
        DishUtils.sharePhotoWithUri(imagePath, this);
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
        Picasso.get().cancelRequest(picture);
    }
}
