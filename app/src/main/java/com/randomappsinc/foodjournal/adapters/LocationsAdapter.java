package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Location;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsAdapter extends BaseAdapter {
    private Context context;
    private List<Location> content;
    private View noContent;
    private View parent;

    public LocationsAdapter(Context context, View noContent, View parent) {
        this.context = context;
        this.noContent = noContent;
        this.parent = parent;
        resyncWithDB();
    }

    public void resyncWithDB() {
        this.content = DatabaseManager.get().getLocationsDBManager().getLocations();
        setNoContent();
    }

    private void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void showOptionsDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title(getItem(position).getName())
                .items(getItem(position).getLocationOptions())
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(context.getString(R.string.set_as_current))) {
                        } else if (text.toString().equals(context.getString(R.string.edit_name))) {
                        } else if (text.toString().equals(context.getString(R.string.edit_address))) {
                        } else if (text.toString().equals(context.getString(R.string.delete_location))) {
                        }
                    }
                })
                .show();
    }

    public class LocationViewHolder {

        @BindView(R.id.location_name) TextView mName;
        @BindView(R.id.location_address) TextView mAddress;
        @BindView(R.id.check_icon) View mCheckIcon;

        public LocationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadLocation(int position) {
            Location location = getItem(position);

            mName.setText(location.getName());
            mAddress.setText(location.getAddress());
            if (location.isCurrentLocation()) {
                mCheckIcon.setAlpha(1);
            } else {
                mCheckIcon.setAlpha(0);
            }
        }
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Location getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LocationViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.location_cell, parent, false);
            holder = new LocationViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (LocationViewHolder) view.getTag();
        }
        holder.loadLocation(position);
        return view;
    }
}
