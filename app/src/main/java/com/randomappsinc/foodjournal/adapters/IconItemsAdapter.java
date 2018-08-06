package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconItemsAdapter extends BaseAdapter {

    private Context context;
    private String[] itemNames;
    private String[] itemIcons;

    public IconItemsAdapter(Context context, int optionIds, int iconIds) {
        this.context = context;
        this.itemNames = context.getResources().getStringArray(optionIds);
        this.itemIcons = context.getResources().getStringArray(iconIds);
    }

    @Override
    public int getCount() {
        return itemNames.length;
    }

    @Override
    public String getItem(int position) {
        return itemNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class IconItemViewHolder {

        @BindView(R.id.item_icon) TextView itemIcon;
        @BindView(R.id.item_name) TextView itemName;

        public IconItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadItem(int position) {
            itemName.setText(itemNames[position]);
            itemIcon.setText(itemIcons[position]);
        }
    }

    public View getView(int position, View view, ViewGroup parent) {
        IconItemViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.icon_item_cell, parent, false);
            holder = new IconItemViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (IconItemViewHolder) view.getTag();
        }
        holder.loadItem(position);
        return view;
    }
}
