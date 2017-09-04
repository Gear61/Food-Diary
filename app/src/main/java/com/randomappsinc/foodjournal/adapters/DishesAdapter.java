package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Dish;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

import java.util.List;

import butterknife.ButterKnife;

public class DishesAdapter extends BaseAdapter {

    private List<Dish> mDishes;
    private Context mContext;
    private TextView mNoResults;
    private String mRestaurantId;

    public DishesAdapter(Context context, TextView noResults, String restaurantId) {
        mContext = context;
        mNoResults = noResults;
        mRestaurantId = restaurantId;
        resyncWithDB();
    }

    public void resyncWithDB() {
        mDishes = DatabaseManager.get().getDishesDBManager().getDishes(mRestaurantId);
        if (mDishes.isEmpty()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDishes.size();
    }

    @Override
    public Dish getItem(int position) {
        return mDishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class DishViewHolder {

        public DishViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {

        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        DishViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.check_in_cell, parent, false);
            holder = new DishViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (DishViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
