package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchDishesAdapter extends RecyclerView.Adapter<SearchDishesAdapter.DishThumbnailViewHolder> {

    public interface Listener {
        void onDishClicked(List<Dish> dishes, int position);
    }

    private @NonNull Listener listener;
    private Context context;
    private List<Dish> dishes;
    private Drawable defaultThumbnail;

    public SearchDishesAdapter(@NonNull Listener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.dishes = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(
                context,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes.clear();
        this.dishes.addAll(dishes);
        notifyDataSetChanged();
    }

    @Override
    public DishThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.local_search_cell, parent, false);
        return new DishThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DishThumbnailViewHolder holder, int position) {
        holder.loadDish(position);
    }

    @Override
    public int getItemCount() {
        return dishes.isEmpty() ? 1 : dishes.size();
    }

    class DishThumbnailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.result_container) View resultContainer;
        @BindView(R.id.search_result_picture) ImageView dishPicture;
        @BindView(R.id.dish_rating_text) TextView ratingText;
        @BindView(R.id.search_result_text) TextView dishTitle;
        @BindView(R.id.no_results_text) TextView noResults;

        DishThumbnailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            noResults.setText(R.string.no_dish_matches);
        }

        void loadDish(int position) {
            if (dishes.isEmpty()) {
                resultContainer.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                Dish dish = dishes.get(position);

                noResults.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(dish.getUriString())
                        .error(defaultThumbnail)
                        .fit()
                        .centerCrop()
                        .into(dishPicture);

                if (dish.getRating() > 0) {
                    ratingText.setText(dish.getRatingText());
                    ratingText.setVisibility(View.VISIBLE);
                } else {
                    ratingText.setVisibility(View.GONE);
                }

                dishTitle.setText(dish.getTitle());
                resultContainer.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.parent)
        void onDishClicked() {
            if (!dishes.isEmpty()) {
                listener.onDishClicked(dishes, getAdapterPosition());
            }
        }
    }
}
