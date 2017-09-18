package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.models.Dish;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DishTaggerAdapter extends BaseAdapter {

    private List<Dish> mDishes;
    private Context mContext;

    public DishTaggerAdapter(Context context, CheckIn mCheckIn) {
        mContext = context;
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

        @BindView(R.id.dish_picture) ImageView mDishPicture;

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
            view = vi.inflate(R.layout.dish_tagger_cell, parent, false);
            holder = new DishViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (DishViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
