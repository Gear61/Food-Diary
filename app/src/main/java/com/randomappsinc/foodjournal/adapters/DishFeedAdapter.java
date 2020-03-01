package com.randomappsinc.foodjournal.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.randomappsinc.foodjournal.utils.UIUtils;
import com.randomappsinc.foodjournal.views.DishOptionsPresenter;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishFeedAdapter extends BaseAdapter {

    public interface Listener {
        void shareDish(Dish dish);

        void editDish(Dish dish);
    }

    private List<Dish> dishes;
    @NonNull private Listener listener;
    private Activity activity;
    private View noResults;
    private Drawable defaultThumbnail;
    private DishOptionsPresenter dishOptionsPresenter;

    private final DishOptionsPresenter.Listener dishOptionsListener = new DishOptionsPresenter.Listener() {
        @Override
        public void shareDish(Dish dish) {
            listener.shareDish(dish);
        }

        @Override
        public void editDish(Dish dish) {
            listener.editDish(dish);
        }

        @Override
        public void onDishDeleted(Dish dish) {
            updateWithDeletedDish(dish);
        }
    };

    public DishFeedAdapter(@NonNull Listener listener, Activity activity, View noResults) {
        this.listener = listener;
        this.activity = activity;
        this.noResults = noResults;
        this.defaultThumbnail = new IconDrawable(
                activity,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        this.dishOptionsPresenter = new DishOptionsPresenter(dishOptionsListener, this.activity);
        resyncWithDb();
    }

    public void resyncWithDb() {
        dishes = DatabaseManager.get().getDishesDBManager().getDishes(null);
        if (dishes.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    private void updateWithDeletedDish(Dish dish) {
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getId() == dish.getId()) {
                dishes.remove(i);
                break;
            }
        }
        if (getCount() == 0) {
            noResults.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    public void updateWithDeletedRestaurant(String restaurantId) {
        Iterator<Dish> iterator = dishes.iterator();
        while (iterator.hasNext()) {
            Dish dish = iterator.next();
            if (dish.getRestaurantId().equals(restaurantId)) {
                iterator.remove();
            }
        }
        if (getCount() == 0) {
            noResults.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dishes.size();
    }

    @Override
    public Dish getItem(int position) {
        return dishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class DishViewHolder {

        @BindView(R.id.dish_info_text) TextView dishInfoText;
        @BindView(R.id.dish_date) TextView dishDate;
        @BindView(R.id.favorite_toggle) TextView favoriteToggle;
        @BindView(R.id.dish_rating_text) TextView dishRatingText;
        @BindView(R.id.dish_picture) ImageView dishPicture;
        @BindView(R.id.dish_description) TextView dishDescription;

        @BindColor(R.color.dark_gray) int darkGray;
        @BindColor(R.color.light_red) int lightRed;

        private int position;

        DishViewHolder(View view) {
            ButterKnife.bind(this, view);
            dishInfoText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        void loadItem(int position) {
            this.position = position;

            Dish dish = getItem(position);

            dishInfoText.setText(Html.fromHtml(dish.getDishInfoText(true)));
            dishDate.setText(TimeUtils.getDefaultTimeText(dish.getTimeAdded()));

            favoriteToggle.clearAnimation();
            favoriteToggle.setText(dish.isFavorited() ? R.string.heart_filled_icon : R.string.heart_icon);
            favoriteToggle.setTextColor(dish.isFavorited() ? lightRed : darkGray);

            if (dish.getRating() > 0) {
                dishRatingText.setText(dish.getRatingText());
                dishRatingText.setVisibility(View.VISIBLE);
            } else {
                dishRatingText.setVisibility(View.GONE);
            }

            Picasso.get()
                    .load(dish.getUriString())
                    .error(defaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(dishPicture);

            if (dish.getDescription().isEmpty()) {
                dishDescription.setVisibility(View.GONE);
            } else {
                dishDescription.setText(dish.getFeedDescription());
                dishDescription.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.dish_picture)
        void dishPictureClicked() {
            Intent intent = new Intent(activity, DishesFullViewGalleryActivity.class);
            intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(dishes));
            intent.putExtra(DishesFullViewGalleryActivity.POSITION_KEY, position);
            intent.putExtra(Constants.FROM_RESTAURANT_KEY, false);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }

        @OnClick(R.id.overflow_menu)
        void overflowClicked() {
            Dish dish = getItem(position);
            dishOptionsPresenter.showOptions(dish);
        }

        @OnClick(R.id.favorite_toggle)
        void toggleFavorite() {
            Dish dish = getItem(position);
            boolean isFavorited = dish.isFavorited();
            dish.setIsFavorited(!isFavorited);
            DatabaseManager.get().getDishesDBManager().updateDish(dish);
            UIUtils.animateFavoriteToggle(favoriteToggle, !isFavorited);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
