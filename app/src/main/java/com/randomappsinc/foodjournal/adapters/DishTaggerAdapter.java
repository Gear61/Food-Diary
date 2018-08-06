package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishTaggerAdapter extends BaseAdapter {

    private ArrayList<Dish> chosenDishes;
    private Context context;
    private List<Dish> taggingOptions;
    private Drawable defaultThumbnail;
    private TextView tagButton;

    public DishTaggerAdapter(Context context, CheckIn checkIn, TextView tagButton) {
        chosenDishes = checkIn.getTaggedDishes();
        this.context = context;
        taggingOptions = DatabaseManager.get().getDishesDBManager().getTaggingSuggestions(checkIn);
        defaultThumbnail = new IconDrawable(context, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        this.tagButton = tagButton;

        refreshTagButtonText();
    }

    private void refreshTagButtonText() {
        String tagMessage = String.format(context.getString(R.string.tag_with_number), chosenDishes.size());
        tagButton.setText(tagMessage);
    }

    public ArrayList<Dish> getChosenDishes() {
        return chosenDishes;
    }

    @Override
    public int getCount() {
        return taggingOptions.size();
    }

    @Override
    public Dish getItem(int position) {
        return taggingOptions.get(position);
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

            Picasso.get()
                    .load(dish.getUriString())
                    .error(defaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(mDishPicture);

            mDishName.setText(dish.getTitle());
            mDishDate.setText(TimeUtils.getDefaultTimeText(dish.getTimeAdded()));

            if (dish.getDescription().isEmpty()) {
                mDishDescription.setVisibility(View.GONE);
            } else {
                String quotedText = "\"" + dish.getDescription() + "\"";
                mDishDescription.setText(quotedText);
                mDishDescription.setVisibility(View.VISIBLE);
            }

            mDishCheckbox.setChecked(chosenDishes.contains(dish));
            mDishCheckbox.jumpDrawablesToCurrentState();
        }

        @OnClick(R.id.parent)
        public void onCellClicked() {
            boolean newState = !mDishCheckbox.isChecked();
            mDishCheckbox.setChecked(newState);
            if (newState) {
                chosenDishes.add(getItem(mPosition));
            } else {
                chosenDishes.remove(getItem(mPosition));
            }
            refreshTagButtonText();
        }

        @OnClick(R.id.dish_checkbox)
        public void onCheckboxClicked() {
            boolean newState = mDishCheckbox.isChecked();
            if (newState) {
                chosenDishes.add(getItem(mPosition));
            } else {
                chosenDishes.remove(getItem(mPosition));
            }
            refreshTagButtonText();
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
