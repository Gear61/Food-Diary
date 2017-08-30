package com.randomappsinc.foodjournal.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.Adapters.CheckInsAdapter;
import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Persistence.DatabaseManager;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Views.CheckInAdder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class CheckInsFragment extends Fragment {

    @BindView(R.id.no_results) TextView mNoResults;
    @BindView(R.id.check_ins) ListView mCheckInsList;
    @BindView(R.id.add_check_in) FloatingActionButton mAddCheckIn;

    private final CheckInAdder.Listener mCheckInListener = new CheckInAdder.Listener() {
        @Override
        public void onCheckInCreated(CheckIn checkIn) {
            DatabaseManager.get().addCheckIn(mRestaurantId, checkIn);
            mCheckInsAdapter.resyncWithDB();
        }
    };

    private CheckInsAdapter mCheckInsAdapter;
    private CheckInAdder mCheckInAdder;
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

        mCheckInAdder = new CheckInAdder(getActivity(), mCheckInListener);

        return rootView;
    }

    @OnClick(R.id.add_check_in)
    public void addCheckIn() {
        mCheckInAdder.show();
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
