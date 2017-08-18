package com.randomappsinc.foodjournal.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.randomappsinc.foodjournal.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 8/17/17.
 */

public class AddOrEditDishActivity extends StandardActivity {
    public static final String NEW_DISH_KEY = "newDish";
    public static final String URI_KEY = "uri";

    @BindView(R.id.dish_picture) ImageView mDishPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_edit_dish_page);
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
