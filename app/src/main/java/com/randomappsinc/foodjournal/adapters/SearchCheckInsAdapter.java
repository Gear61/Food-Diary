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
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchCheckInsAdapter extends RecyclerView.Adapter<SearchCheckInsAdapter.CheckInViewHolder> {

    public interface Listener {
        void onCheckInClicked(CheckIn checkIn);
    }

    private @NonNull Listener listener;
    private Context context;
    private List<CheckIn> checkIns;
    private Drawable defaultThumbnail;

    public SearchCheckInsAdapter(@NonNull Listener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.checkIns = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(
                context,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
    }

    public void setCheckIns(List<CheckIn> checkIns) {
        this.checkIns.clear();
        this.checkIns.addAll(checkIns);
        notifyDataSetChanged();
    }

    @Override
    public CheckInViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.local_search_cell, parent, false);
        return new CheckInViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CheckInViewHolder holder, int position) {
        holder.loadCheckIn(position);
    }

    @Override
    public int getItemCount() {
        return checkIns.isEmpty() ? 1 : checkIns.size();
    }

    class CheckInViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.result_container) View resultContainer;
        @BindView(R.id.search_result_picture) ImageView picture;
        @BindView(R.id.search_result_text) TextView title;
        @BindView(R.id.no_results_text) TextView noResults;

        CheckInViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            noResults.setText(R.string.no_check_in_matches);
        }

        void loadCheckIn(int position) {
            if (checkIns.isEmpty()) {
                resultContainer.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
                CheckIn checkIn = checkIns.get(position);
                Restaurant restaurant = DatabaseManager.get().getRestaurantsDBManager().getRestaurant(checkIn.getRestaurantId());
                String imageUrl = restaurant.getImageUrl();
                if (!imageUrl.isEmpty()) {
                    Picasso.get()
                            .load(imageUrl)
                            .error(defaultThumbnail)
                            .fit().centerCrop()
                            .into(picture);
                } else {
                    picture.setImageDrawable(defaultThumbnail);
                }
                title.setText(checkIn.getSearchText());
                resultContainer.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.parent)
        void onRestaurantClicked() {
            if (!checkIns.isEmpty()) {
                listener.onCheckInClicked(checkIns.get(getAdapterPosition()));
            }
        }
    }
}
