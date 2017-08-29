package com.randomappsinc.foodjournal.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.Adapters.CheckInsAdapter;
import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Persistence.DatabaseManager;
import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class CheckInsFragment extends Fragment {

    @BindView(R.id.no_results) TextView mNoResults;
    @BindView(R.id.check_ins) ListView mCheckInsList;
    @BindView(R.id.add_check_in) FloatingActionButton mAddCheckIn;

    private CheckInsAdapter mCheckInsAdapter;
    private MaterialDialog mCheckInDialog;
    private String mRestaurantId;
    private Unbinder mUnbinder;

    public static CheckInsFragment newInstance(String restaurantId) {
        CheckInsFragment fragment = new CheckInsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RestaurantsActivity.ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.check_ins, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mRestaurantId = getArguments().getString(RestaurantsActivity.ID_KEY);
        mAddCheckIn.setImageDrawable(new IconDrawable(getActivity(), IoniconsIcons.ion_android_add).colorRes(R.color.white));

        mCheckInsAdapter = new CheckInsAdapter(getActivity(), mNoResults, mRestaurantId);
        mCheckInsList.setAdapter(mCheckInsAdapter);

        mCheckInDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.add_check_in)
                .input(getString(R.string.check_in_prompt), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String message = input.toString().trim();
                        DatabaseManager.get().addCheckIn(mRestaurantId, message);
                        mCheckInsAdapter.resyncWithDB();
                    }
                })
                .negativeText(android.R.string.no)
                .build();

        return rootView;
    }

    @OnClick(R.id.add_check_in)
    public void addCheckIn() {
        if (mCheckInDialog.getInputEditText() != null) {
            mCheckInDialog.getInputEditText().setText("");
        }
        mCheckInDialog.show();
    }

    @OnItemClick(R.id.check_ins)
    public void onCheckInSelected(int position) {
        CheckIn checkIn = mCheckInsAdapter.getItem(position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
