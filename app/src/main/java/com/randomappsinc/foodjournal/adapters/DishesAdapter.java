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
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishesAdapter extends BaseAdapter {

    private List<Dish> mDishes;
    private Context mContext;
    private View mNoResults;
    private String mRestaurantId;
    private Drawable mDefaultThumbnail;

    public DishesAdapter(Context context, View noResults, String restaurantId) {
        mContext = context;
        mNoResults = noResults;
        mRestaurantId = restaurantId;
        mDefaultThumbnail = new IconDrawable(context, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        resyncWithDB();
    }

    public void resyncWithDB() {
        mDishes = mRestaurantId == null
                ? DatabaseManager.get().getDishesDBManager().getAllDishes()
                : DatabaseManager.get().getDishesDBManager().getDishes(mRestaurantId);
        if (mDishes.isEmpty()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
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

        @BindView(R.id.dish_info_text) TextView mDishInfoText;
        @BindView(R.id.dish_date) TextView mDishDate;
        @BindView(R.id.dish_rating_text) TextView mDishRatingText;
        @BindView(R.id.dish_picture) ImageView mDishPicture;
        @BindView(R.id.dish_description) TextView mDishDescription;

        public DishViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            Dish dish = getItem(position);

            mDishInfoText.setText(dish.getDishInfoText());
            mDishDate.setText(TimeUtils.getTimeText(dish.getTimeAdded()));

            if (dish.getRating() > 0) {
                mDishRatingText.setText(dish.getRatingText());
                mDishRatingText.setVisibility(View.VISIBLE);
            } else {
                mDishRatingText.setVisibility(View.GONE);
            }

            Picasso.with(mContext)
                    .load(dish.getUriString())
                    .error(mDefaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(mDishPicture);

            if (dish.getDescription().isEmpty()) {
                mDishDescription.setVisibility(View.GONE);
            } else {
                mDishDescription.setText(dish.getFeedDescription());
                mDishDescription.setVisibility(View.VISIBLE);
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.dish_cell, parent, false);
            holder = new DishViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (DishViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
