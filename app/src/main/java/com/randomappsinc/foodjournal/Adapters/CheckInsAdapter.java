package com.randomappsinc.foodjournal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Persistence.DatabaseManager;
import com.randomappsinc.foodjournal.R;

import java.util.List;

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
        mCheckIns = DatabaseManager.get().getCheckIns(mRestaurantId);
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


        public CheckInViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {

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