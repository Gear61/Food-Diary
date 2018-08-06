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
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckInsAdapter extends BaseAdapter {

    private List<CheckIn> checkIns;
    private Context context;
    private TextView noResults;
    private String restaurantId;

    public CheckInsAdapter(Context context, TextView noResults, String restaurantId) {
        this.context = context;
        this.noResults = noResults;
        this.restaurantId = restaurantId;
        resyncWithDB();
    }

    public void resyncWithDB() {
        checkIns = DatabaseManager.get().getCheckInsDBManager().getCheckInsForRestaurant(restaurantId);
        if (checkIns.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return checkIns.size();
    }

    @Override
    public CheckIn getItem(int position) {
        return checkIns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CheckInViewHolder {

        @BindView(R.id.restaurant_thumbnail) ImageView restaurantThumbnail;
        @BindView(R.id.restaurant_name) TextView restaurantName;
        @BindView(R.id.restaurant_address) TextView restaurantAddress;
        @BindView(R.id.check_in_date) TextView checkInDate;
        @BindView(R.id.num_dishes) TextView numDishes;
        @BindView(R.id.check_in_message) TextView checkInMessage;

        public CheckInViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            CheckIn checkIn = getItem(position);

            Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(checkIn.getRestaurantId());
            Drawable defaultThumbnail = new IconDrawable(
                    context,
                    IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
            if (!restaurant.getImageUrl().isEmpty()) {
                Picasso.get()
                        .load(restaurant.getImageUrl())
                        .error(defaultThumbnail)
                        .fit()
                        .centerCrop()
                        .into(restaurantThumbnail);
            } else {
                restaurantThumbnail.setImageDrawable(defaultThumbnail);
            }
            restaurantName.setText(restaurant.getName());
            restaurantAddress.setText(restaurant.getAddress());
            checkInDate.setText(TimeUtils.getDefaultTimeText(checkIn.getTimeAdded()));
            numDishes.setText(checkIn.getTaggedDishes().size() == 1
                    ? context.getString(R.string.one_dish_attached)
                    : String.format(
                        context.getString(R.string.num_dishes_attached),
                        checkIn.getTaggedDishes().size()));

            if (checkIn.getMessage().isEmpty()) {
                checkInMessage.setVisibility(View.GONE);
            } else {
                String quotedMessage = "\"" + checkIn.getMessage() + "\"";
                checkInMessage.setText(quotedMessage);
                checkInMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        CheckInViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.check_in_cell, parent, false);
            holder = new CheckInViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (CheckInViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
