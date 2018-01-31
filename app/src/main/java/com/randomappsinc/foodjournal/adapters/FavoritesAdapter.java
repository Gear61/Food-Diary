package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Dish> favorites;
    private Drawable defaultThumbnail;
    private View noResults;

    public FavoritesAdapter(Context context, View noResults) {
        this.context = context;
        this.favorites = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(
                context,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        this.noResults = noResults;
    }

    public void resyncWithDb() {
        favorites.clear();
        favorites.addAll(DatabaseManager.get().getDishesDBManager().getFavoritedDishes());
        noResults.setVisibility(getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getCount() {
        return favorites.size();
    }

    @Override
    public Dish getItem(int position) {
        return favorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Dish> getFavorites() {
        return favorites;
    }

    class FavoriteViewHolder {

        @BindView(R.id.dish_image) ImageView dishImage;

        public FavoriteViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            Dish dish = getItem(position);
            Picasso.with(context)
                    .load(dish.getUriString())
                    .error(defaultThumbnail)
                    .fit()
                    .centerCrop()
                    .into(dishImage);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        FavoriteViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.favorites_cell, parent, false);
            holder = new FavoriteViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (FavoriteViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
