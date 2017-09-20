package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishGalleryAdapter extends RecyclerView.Adapter<DishGalleryAdapter.DishThumbnailViewHolder> {

    private Context mContext;
    private List<Dish> mDishes;
    private Drawable mDefaultThumbnail;

    public DishGalleryAdapter(Context context) {
        mContext = context;
        mDishes = new ArrayList<>();
        mDefaultThumbnail = new IconDrawable(context, IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
    }

    public void setDishes(List<Dish> dishes) {
        mDishes = dishes;
        notifyDataSetChanged();
    }

    @Override
    public DishThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.dish_gallery_cell, parent, false);
        return new DishThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DishThumbnailViewHolder holder, int position) {
        holder.loadDish(position);
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    public class DishThumbnailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dish_picture) ImageView mDishPicture;

        public DishThumbnailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void loadDish(int position) {
            Picasso.with(mContext)
                    .load(mDishes.get(position).getUriString())
                    .error(mDefaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(mDishPicture);
        }
    }
}
