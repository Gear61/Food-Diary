package com.randomappsinc.foodjournal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.models.Location;
import com.randomappsinc.foodjournal.persistence.DatabaseManager;
import com.randomappsinc.foodjournal.utils.UIUtils;

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
        content = DatabaseManager.get().getLocationsDBManager().getLocations();
        setNoContent();
        notifyDataSetChanged();
    }

    private void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void showOptionsDialog(final int position) {
        final Location location = getItem(position);

        new MaterialDialog.Builder(context)
                .title(getItem(position).getName())
                .items(getItem(position).getLocationOptions())
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(context.getString(R.string.set_as_current))) {
                            location.setIsCurrentLocation(true);
                            DatabaseManager.get().getLocationsDBManager().setCurrentLocation(location);
                            resyncWithDB();
                            UIUtils.showSnackbar(parent, context.getString(R.string.current_location_set));
                        } else if (text.toString().equals(context.getString(R.string.edit_location_name))) {
                            showRenameDialog(location);
                        } else if (text.toString().equals(context.getString(R.string.edit_location_address))) {
                            showAddressChangeDialog(location);
                        } else if (text.toString().equals(context.getString(R.string.delete_location))) {
                            showDeleteDialog(location);
                        }
                    }
                })
                .show();
    }

    private void showRenameDialog(final Location location) {
        new MaterialDialog.Builder(context)
                .title(R.string.edit_location_name_title)
                .input(context.getString(R.string.name), location.getName(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean enableNext = input.length() > 0;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(enableNext);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String name = dialog.getInputEditText().getText().toString().trim();
                        location.setName(name);
                        DatabaseManager.get().getLocationsDBManager().updateLocation(location);
                        resyncWithDB();
                        UIUtils.showSnackbar(parent, context.getString(R.string.location_edited));
                    }
                })
                .negativeText(android.R.string.no)
                .show();
    }

    private void showAddressChangeDialog(final Location location) {
        new MaterialDialog.Builder(context)
                .title(R.string.edit_location_address_title)
                .input(context.getString(R.string.address), location.getAddress(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean enableNext = input.length() > 0;
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(enableNext);
                    }
                })
                .alwaysCallInputCallback()
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String address = dialog.getInputEditText().getText().toString().trim();
                        location.setAddress(address);
                        DatabaseManager.get().getLocationsDBManager().updateLocation(location);
                        resyncWithDB();
                        UIUtils.showSnackbar(parent, context.getString(R.string.location_edited));
                    }
                })
                .negativeText(android.R.string.no)
                .show();
    }

    private void showDeleteDialog(final Location location) {
        String template = context.getString(R.string.location_delete_confirmation);
        String question = template + "\"" + location.getName() + "\"?";

        new MaterialDialog.Builder(context)
                .title(R.string.delete_location_title)
                .content(question)
                .positiveText(android.R.string.yes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DatabaseManager.get().getLocationsDBManager().deleteLocation(location);
                        resyncWithDB();
                        UIUtils.showSnackbar(parent, context.getString(R.string.location_deleted));
                    }
                })
                .negativeText(android.R.string.no)
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
