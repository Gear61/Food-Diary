package com.randomappsinc.foodjournal.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishesFullViewGalleryActivity;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.DishUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishGalleryAdapter extends RecyclerView.Adapter<DishGalleryAdapter.DishThumbnailViewHolder> {

    private Activity activity;
    private ArrayList<Dish> dishes;
    private Drawable defaultThumbnail;

    public DishGalleryAdapter(Activity activity) {
        this.activity = activity;
        dishes = new ArrayList<>();
        defaultThumbnail = new IconDrawable(activity, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes.clear();
        this.dishes.addAll(dishes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.dish_gallery_cell, parent, false);
        return new DishThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DishThumbnailViewHolder holder, int position) {
        holder.loadDish(position);
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    class DishThumbnailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dish_picture) ImageView mDishPicture;

        DishThumbnailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadDish(int position) {
            Picasso.get()
                    .load(dishes.get(position).getUriString())
                    .error(defaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(mDishPicture);
        }

        @OnClick(R.id.dish_picture)
        void onPictureClicked() {
            Intent intent = new Intent(activity, DishesFullViewGalleryActivity.class);
            intent.putExtra(Constants.DISH_IDS_KEY, DishUtils.getDishIdList(dishes));
            intent.putExtra(DishesFullViewGalleryActivity.POSITION_KEY, getAdapterPosition());
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
    }
}
