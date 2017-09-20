package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishTaggerAdapter extends BaseAdapter {

    private ArrayList<Dish> mChosenDishes;
    private Context mContext;
    private List<Dish> mDishes;
    private Drawable mDefaultThumbnail;
    private Button mTagButton;

    public DishTaggerAdapter(Context context, CheckIn checkIn, Button tagButton) {
        mChosenDishes = checkIn.getTaggedDishes();
        mContext = context;
        mDishes = DatabaseManager.get().getDishesDBManager().getTaggingSuggestions(checkIn);
        mDefaultThumbnail = new IconDrawable(context, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        mTagButton = tagButton;

        refreshTagButtonText();
    }

    public void refreshTagButtonText() {
        String tagMessage = String.format(mContext.getString(R.string.tag_with_number), mChosenDishes.size());
        mTagButton.setText(tagMessage);
    }

    public ArrayList<Dish> getChosenDishes() {
        return mChosenDishes;
    }

    @Override
    public int getCount() {
        return mDishes.size();
    }

    @Override
    public Dish getItem(int position) {
        return mDishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class DishViewHolder {

        @BindView(R.id.dish_picture) ImageView mDishPicture;
        @BindView(R.id.dish_name) TextView mDishName;
        @BindView(R.id.dish_date) TextView mDishDate;
        @BindView(R.id.dish_description) TextView mDishDescription;
        @BindView(R.id.dish_checkbox) CheckBox mDishCheckbox;

        private int mPosition;

        public DishViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            mPosition = position;
            Dish dish = getItem(mPosition);

            Picasso.with(mContext)
                    .load(dish.getUriString())
                    .error(mDefaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(mDishPicture);

            mDishName.setText(dish.getTitle());
            mDishDate.setText(TimeUtils.getTimeText(dish.getTimeAdded()));

            if (dish.getDescription().isEmpty()) {
                mDishDescription.setVisibility(View.GONE);
            } else {
                String quotedText = "\"" + dish.getDescription() + "\"";
                mDishDescription.setText(quotedText);
                mDishDescription.setVisibility(View.VISIBLE);
            }

            mDishCheckbox.setCheckedImmediately(mChosenDishes.contains(dish));
        }

        @OnClick(R.id.parent)
        public void onCellClicked() {
            boolean newState = !mDishCheckbox.isChecked();
            mDishCheckbox.setChecked(newState);
            if (newState) {
                mChosenDishes.add(getItem(mPosition));
            } else {
                mChosenDishes.remove(getItem(mPosition));
            }
            refreshTagButtonText();
        }

        @OnClick(R.id.dish_checkbox)
        public void onCheckboxClicked() {
            boolean newState = mDishCheckbox.isChecked();
            if (newState) {
                mChosenDishes.add(getItem(mPosition));
            } else {
                mChosenDishes.remove(getItem(mPosition));
            }
            refreshTagButtonText();
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.dish_tagger_cell, parent, false);
            holder = new DishViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (DishViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
