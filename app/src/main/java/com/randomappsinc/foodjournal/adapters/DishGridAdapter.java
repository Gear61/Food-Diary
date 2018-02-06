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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Dish> dishes;
    private Drawable defaultThumbnail;
    private View noResults;

    public DishGridAdapter(Context context, View noResults) {
        this.context = context;
        this.dishes = new ArrayList<>();
        this.defaultThumbnail = new IconDrawable(
                context,
                IoniconsIcons.ion_android_restaurant).colorRes(R.color.dark_gray);
        this.noResults = noResults;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes.clear();
        this.dishes.addAll(dishes);
        noResults.setVisibility(getCount() == 0 ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dishes.size();
    }

    @Override
    public Dish getItem(int position) {
        return dishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    class DishViewHolder {

        @BindView(R.id.dish_image) ImageView dishImage;

        public DishViewHolder(View view) {
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
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.dish_grid_cell, parent, false);
            holder = new DishViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (DishViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
