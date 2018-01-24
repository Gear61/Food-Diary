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

    private List<CheckIn> mCheckIns;
    private Context mContext;
    private TextView mNoResults;
    private String mRestaurantId;

    public CheckInsAdapter(Context context, TextView noResults, String restaurantId) {
        mContext = context;
        mNoResults = noResults;
        mRestaurantId = restaurantId;
        resyncWithDB();
    }

    public void resyncWithDB() {
        mCheckIns = DatabaseManager.get().getCheckInsDBManager().getCheckInsForRestaurant(mRestaurantId);
        if (mCheckIns.isEmpty()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCheckIns.size();
    }

    @Override
    public CheckIn getItem(int position) {
        return mCheckIns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CheckInViewHolder {

        @BindView(R.id.restaurant_thumbnail) ImageView mRestaurantThumbnail;
        @BindView(R.id.restaurant_name) TextView mRestaurantName;
        @BindView(R.id.restaurant_address) TextView mRestaurantAddress;
        @BindView(R.id.check_in_date) TextView mCheckInDate;
        @BindView(R.id.num_dishes) TextView numDishes;
        @BindView(R.id.check_in_message) TextView mCheckInMessage;

        public CheckInViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            CheckIn checkIn = getItem(position);

            Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(checkIn.getRestaurantId());
            Drawable defaultThumbnail = new IconDrawable(
                    mContext,
                    IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
            if (!restaurant.getImageUrl().isEmpty()) {
                Picasso.with(mContext)
                        .load(restaurant.getImageUrl())
                        .error(defaultThumbnail)
                        .fit()
                        .centerCrop()
                        .into(mRestaurantThumbnail);
            } else {
                mRestaurantThumbnail.setImageDrawable(defaultThumbnail);
            }
            mRestaurantName.setText(restaurant.getName());
            mRestaurantAddress.setText(restaurant.getAddress());
            mCheckInDate.setText(TimeUtils.getDefaultTimeText(checkIn.getTimeAdded()));
            numDishes.setText(checkIn.getTaggedDishes().size() == 1
                    ? mContext.getString(R.string.one_dish)
                    : String.format(mContext.getString(R.string.num_dishes), checkIn.getTaggedDishes().size()));

            if (checkIn.getMessage().isEmpty()) {
                mCheckInMessage.setVisibility(View.GONE);
            } else {
                String quotedMessage = "\"" + checkIn.getMessage() + "\"";
                mCheckInMessage.setText(quotedMessage);
                mCheckInMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        CheckInViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
