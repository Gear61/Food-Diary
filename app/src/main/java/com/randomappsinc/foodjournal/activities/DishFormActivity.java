package com.randomappsinc.foodjournal.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.randomappsinc.foodjournal.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishFormActivity extends StandardActivity {

    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";

    @BindView(R.id.dish_picture) ImageView mDishPicture;

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
    }
}
