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
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRestaurantsAdapter extends BaseAdapter {
    private List<Restaurant> mRestaurants;
    private Context mContext;
    private TextView mNoResults;

    public UserRestaurantsAdapter(Context context, TextView noResults) {
        mContext = context;
        mNoResults = noResults;
        resyncWithDB("");
    }

    public void resyncWithDB(String searchTerm) {
        mRestaurants = DatabaseManager.get().getRestaurantsDBManager().getUserRestaurants(searchTerm);
        if (mRestaurants.isEmpty()) {
            mNoResults.setText(DatabaseManager.get().getRestaurantsDBManager().getNumUserRestaurants() == 0
                    ? R.string.no_restaurants_added
                    : R.string.no_restaurants_found);
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults.setVisibility(View.GONE);
        }
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
        @BindView(R.id.num_dishes) TextView numDishes;
        @BindView(R.id.num_checkins) TextView numCheckIns;

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
                        .fit()
                        .centerCrop()
                        .into(thumbnail);
            } else {
                thumbnail.setImageDrawable(defaultThumbnail);
            }
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            numDishes.setText(restaurant.getDishes().size() == 1
                    ? mContext.getString(R.string.one_dish)
                    : String.format(mContext.getString(R.string.num_dishes), restaurant.getDishes().size()));
            numCheckIns.setText(restaurant.getCheckIns().size()== 1
                    ? mContext.getString(R.string.one_check_in)
                    : String.format(mContext.getString(R.string.num_check_ins), restaurant.getCheckIns().size()));
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        RestaurantViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.user_restaurant_cell, parent, false);
            holder = new RestaurantViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (RestaurantViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
