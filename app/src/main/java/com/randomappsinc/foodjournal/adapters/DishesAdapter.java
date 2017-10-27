package com.randomappsinc.foodjournal.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.randomappsinc.foodjournal.activities.PictureFullViewActivity;
import com.randomappsinc.foodjournal.fragments.DishesFragment;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.persistence.dbmanagers.DishesDBManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DishOptionsPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishesAdapter extends BaseAdapter {

    private List<Dish> mDishes;
    private DishesFragment mDishesFragment;
    private Activity mActivity;
    private View mNoResults;
    private String mRestaurantId;
    private Drawable mDefaultThumbnail;
    private DishOptionsPresenter mDishOptionsPresenter;
    private boolean mStopFetching;

    private final DishOptionsPresenter.Listener mListener = new DishOptionsPresenter.Listener() {
        @Override
        public void onDishDeleted(Dish dish) {
            updateWithDeletedDish(dish);
        }

        @Override
        public void editDish(Dish dish) {
            mDishesFragment.editDish(dish);
        }
    };

    public DishesAdapter(DishesFragment dishesFragment, View noResults, String restaurantId) {
        mDishesFragment = dishesFragment;
        mActivity = dishesFragment.getActivity();
        mNoResults = noResults;
        mRestaurantId = restaurantId;
        mDefaultThumbnail = new IconDrawable(mActivity, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        mDishOptionsPresenter = new DishOptionsPresenter(mListener, mActivity);
        fetchFirstPage();
    }

    public boolean canFetchMore() {
        return !mStopFetching;
    }

    private void fetchFirstPage() {
        mDishes = mRestaurantId == null
                ? DatabaseManager.get().getDishesDBManager().getDishesPage(null)
                : DatabaseManager.get().getDishesDBManager().getDishesPage(mRestaurantId, null);

        if (mDishes.size() < DishesDBManager.DISHES_PER_PAGE) {
            mStopFetching = true;
        }

        if (mDishes.isEmpty()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults.setVisibility(View.GONE);
        }
    }

    public void fetchNextPage() {
        Dish lastDish = mDishes.get(getCount() - 1);
        List<Dish> nextDishes = mRestaurantId == null
                ? DatabaseManager.get().getDishesDBManager().getDishesPage(lastDish)
                : DatabaseManager.get().getDishesDBManager().getDishesPage(mRestaurantId, lastDish);
        mDishes.addAll(nextDishes);

        if (mDishes.size() < DishesDBManager.DISHES_PER_PAGE) {
            mStopFetching = true;
        }

        notifyDataSetChanged();
    }

    public void updateWithAddedDish() {
        Dish dish = DatabaseManager.get().getDishesDBManager().getLastUpdatedDish();
        mDishes.add(0, dish);
        if (mNoResults.getVisibility() == View.VISIBLE) {
            mNoResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    public void updateWithEditedDish() {
        Dish dish = DatabaseManager.get().getDishesDBManager().getLastUpdatedDish();
        for (int i = 0; i < mDishes.size(); i++) {
            if (mDishes.get(i).getId() == dish.getId()) {
                mDishes.set(i, dish);
                break;
            }
        }
        notifyDataSetChanged();
    }

    private void updateWithDeletedDish(Dish dish) {
        for (int i = 0; i < mDishes.size(); i++) {
            if (mDishes.get(i).getId() == dish.getId()) {
                mDishes.remove(i);
                break;
            }
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

    class DishViewHolder {

        @BindView(R.id.dish_info_text) TextView mDishInfoText;
        @BindView(R.id.dish_date) TextView mDishDate;
        @BindView(R.id.favorite_toggle) TextView mFavoriteToggle;
        @BindView(R.id.dish_rating_text) TextView mDishRatingText;
        @BindView(R.id.dish_picture) ImageView mDishPicture;
        @BindView(R.id.dish_description) TextView mDishDescription;

        @BindColor(R.color.dark_gray) int darkGray;
        @BindColor(R.color.light_red) int lightRed;

        private int mPosition;

        DishViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void loadItem(int position) {
            mPosition = position;

            Dish dish = getItem(position);

            mDishInfoText.setText(dish.getDishInfoText());
            mDishDate.setText(TimeUtils.getTimeText(dish.getTimeAdded()));

            mFavoriteToggle.clearAnimation();
            mFavoriteToggle.setText(dish.isFavorited() ? R.string.heart_filled_icon : R.string.heart_icon);
            mFavoriteToggle.setTextColor(dish.isFavorited() ? lightRed : darkGray);

            if (dish.getRating() > 0) {
                mDishRatingText.setText(dish.getRatingText());
                mDishRatingText.setVisibility(View.VISIBLE);
            } else {
                mDishRatingText.setVisibility(View.GONE);
            }

            Picasso.with(mActivity)
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

        @OnClick(R.id.dish_picture)
        void dishPictureClicked() {
            Dish dish = getItem(mPosition);
            Intent intent = new Intent(mActivity, PictureFullViewActivity.class);
            ArrayList<String> imagePath = new ArrayList<>();
            imagePath.add(dish.getUriString());
            intent.putStringArrayListExtra(PictureFullViewActivity.IMAGE_PATHS_KEY, imagePath);
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(0, 0);
        }

        @OnClick(R.id.overflow_menu)
        void overflowClicked() {
            Dish dish = getItem(mPosition);
            mDishOptionsPresenter.showOptions(dish);
        }

        @OnClick(R.id.favorite_toggle)
        void toggleFavorite() {
            Dish dish = getItem(mPosition);
            boolean isFavorited = dish.isFavorited();
            dish.setIsFavorited(!isFavorited);
            DatabaseManager.get().getDishesDBManager().updateDish(dish);
            UIUtils.animateFavoriteToggle(mFavoriteToggle, !isFavorited);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
