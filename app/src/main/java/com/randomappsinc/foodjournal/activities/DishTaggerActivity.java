package com.randomappsinc.foodjournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.adapters.DishTaggerAdapter;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.rey.material.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishTaggerActivity extends StandardActivity {

    public static final String DISHES_KEY = "dishes";

    @BindView(R.id.description) TextView mDescription;
    @BindView(R.id.dishes) ListView mDishes;
    @BindView(R.id.tag) Button mTagButton;

    private DishTaggerAdapter mDishTaggerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_tagger);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CheckIn checkIn = getIntent().getParcelableExtra(CheckInFormActivity.CHECK_IN_KEY);

        String header = String.format(
                getString(R.string.dish_tagger_header),
                checkIn.getRestaurantName(),
                TimeUtils.getTimeText(checkIn.getTimeAdded()));
        mDescription.setText(header);

        mDishTaggerAdapter = new DishTaggerAdapter(this, checkIn, mTagButton);
        mDishes.setAdapter(mDishTaggerAdapter);
    }

    @OnClick(R.id.tag)
    public void tagDishes() {
        Intent intent = new Intent();
        ArrayList<Dish> taggedDishes = mDishTaggerAdapter.getChosenDishes();
        intent.putParcelableArrayListExtra(DISHES_KEY, taggedDishes);
        setResult(RESULT_OK, intent);
        finish();
    }
}
