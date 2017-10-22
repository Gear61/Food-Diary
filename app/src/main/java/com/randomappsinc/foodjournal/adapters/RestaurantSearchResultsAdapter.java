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
import com.randomappsinc.foodjournal.models.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantSearchResultsAdapter extends BaseAdapter {

    private List<Restaurant> mRestaurants = new ArrayList<>();
    private Context mContext;

    public RestaurantSearchResultsAdapter(Context context) {
        mContext = context;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRestaurants.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return mRestaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RestaurantViewHolder {
        @BindView(R.id.restaurant_thumbnail) ImageView thumbnail;
        @BindView(R.id.restaurant_name) TextView name;
        @BindView(R.id.restaurant_address) TextView address;

        public RestaurantViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            Restaurant restaurant = getItem(position);

            Drawable defaultThumbnail = new IconDrawable(
                    mContext,
                    IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
            if (!restaurant.getImageUrl().isEmpty()) {
                Picasso.with(mContext)
                        .load(restaurant.getImageUrl())
                        .error(defaultThumbnail)
                        .fit().centerCrop()
                        .into(thumbnail);
            } else {
                thumbnail.setImageDrawable(defaultThumbnail);
            }
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        RestaurantViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.base_restaurant_cell, parent, false);
            holder = new RestaurantViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (RestaurantViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
