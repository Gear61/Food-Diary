package com.randomappsinc.foodjournal.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.foodjournal.Persistence.PreferencesManager;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.LocationUtils;
import com.randomappsinc.foodjournal.Utils.UIUtils;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsAdapter extends BaseAdapter {
    private Context context;
    private List<String> content;
    private View noContent;
    private View parent;

    public LocationsAdapter(Context context, View noContent, View parent) {
        this.context = context;
        this.content = PreferencesManager.get().getUserLocations();
        this.noContent = noContent;
        this.parent = parent;
        setNoContent();
    }

    public void setNoContent() {
        int viewVisibility = content.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public void addLocation(String location) {
        PreferencesManager.get().addSavedLocation(location);
        content.add(location);
        Collections.sort(content);
        notifyDataSetChanged();
        setNoContent();
        UIUtils.showAddedSnackbar(location, parent, this);
    }

    public void removeLocation(int index) {
        PreferencesManager.get().removeSavedLocation(getItem(index));
        content.remove(index);
        notifyDataSetChanged();
        setNoContent();
        UIUtils.showSnackbar(parent, context.getString(R.string.location_deleted));
    }

    public void editLocation(int position, String newLocation) {
        String oldLocation = getItem(position);
        PreferencesManager.get().changeSavedLocation(oldLocation, newLocation);
        content.set(position, newLocation);
        Collections.sort(content);
        notifyDataSetChanged();
        UIUtils.showSnackbar(parent, context.getString(R.string.location_edited));
    }

    public void showDeleteDialog(final int position) {
        String confirmDeletionMessage = context.getString(R.string.location_delete_confirmation) +
                getItem(position) + "?";

        new MaterialDialog.Builder(context)
                .title(R.string.delete_title)
                .content(confirmDeletionMessage)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeLocation(position);
                    }
                })
                .show();
    }

    public void showEditDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title(R.string.change_location_title)
                .input(context.getString(R.string.location), getItem(position), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !(input.toString().trim().isEmpty()
                                || PreferencesManager.get().alreadyHasLocation(input.toString()));
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newName = dialog.getInputEditText().getText().toString();
                            editLocation(position, newName);
                        }
                    }
                })
                .show();
    }

    public void showOptionsDialog(final int position) {
        new MaterialDialog.Builder(context)
                .title(getItem(position))
                .items(LocationUtils.getLocationOptions(getItem(position)))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(context.getString(R.string.set_as_current))) {
                            PreferencesManager.get().setCurrentLocation(getItem(position));
                            notifyDataSetChanged();
                            UIUtils.showSnackbar(parent, context.getString(R.string.current_location_set));
                        } else if (text.toString().equals(context.getString(R.string.edit_location))) {
                            showEditDialog(position);
                        } else if (text.toString().equals(context.getString(R.string.delete_location))) {
                            showDeleteDialog(position);
                        }
                    }
                })
                .show();
    }

    public class LocationViewHolder {
        @BindView(R.id.location) TextView locationText;
        @BindView(R.id.check_icon) View checkIcon;

        public LocationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void loadLocation(int position) {
            locationText.setText(getItem(position));
            if (getItem(position).equals(PreferencesManager.get().getCurrentLocation())) {
                checkIcon.setAlpha(1);
            } else {
                checkIcon.setAlpha(0);
            }
        }
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public String getItem(int position) {
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
