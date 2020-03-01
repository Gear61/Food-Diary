package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchRestaurantsAdapter extends RecyclerView.Adapter<SearchRestaurantsAdapter.RestaurantViewHolder> {

    public interface Listener {
        void onRestaurantClicked(Restaurant restaurant);
    }

    private @NonNull Listener listener;
    private Context context;
    private List<Restaurant> restaurants;
    private Drawable defaultThumbnail;

    public SearchRestaurantsAdapter(@NonNull Listener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.restaurants = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(
                context,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.local_search_cell, parent, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.loadRestaurant(position);
    }

    @Override
    public int getItemCount() {
        return restaurants.isEmpty() ? 1 : restaurants.size();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.result_container) View resultContainer;
        @BindView(R.id.search_result_picture) ImageView picture;
        @BindView(R.id.search_result_text) TextView title;
        @BindView(R.id.no_results_text) TextView noResults;

        RestaurantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            noResults.setText(R.string.no_restaurant_matches);
        }

        void loadRestaurant(int position) {
            if (restaurants.isEmpty()) {
                resultContainer.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
                String imageUrl = restaurants.get(position).getImageUrl();
                if (!imageUrl.isEmpty()) {
                    Picasso.get()
                            .load(imageUrl)
                            .error(defaultThumbnail)
                            .fit().centerCrop()
                            .into(picture);
                } else {
                    picture.setImageDrawable(defaultThumbnail);
                }
                title.setText(restaurants.get(position).getSearchText());
                resultContainer.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.parent)
        void onRestaurantClicked() {
            if (!restaurants.isEmpty()) {
                listener.onRestaurantClicked(restaurants.get(getAdapterPosition()));
            }
        }
    }
}
