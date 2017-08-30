package com.randomappsinc.foodjournal.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.Activities.EditCheckInActivity;
import com.randomappsinc.foodjournal.Activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.Adapters.CheckInsAdapter;
import com.randomappsinc.foodjournal.Models.CheckIn;
import com.randomappsinc.foodjournal.Persistence.DatabaseManager;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.Utils.UIUtils;
import com.randomappsinc.foodjournal.Views.CheckInAdder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class CheckInsFragment extends Fragment {

    public static final int EDIT_REQUEST_CODE = 1;

    public static final int EDIT_RESULT_CODE = 1;
    public static final int DELETED_RESULT_CODE = 2;

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.no_results) TextView mNoResults;
    @BindView(R.id.check_ins) ListView mCheckInsList;
    @BindView(R.id.add_check_in) FloatingActionButton mAddCheckIn;

    private final CheckInAdder.Listener mCheckInListener = new CheckInAdder.Listener() {
        @Override
        public void onCheckInCreated(CheckIn checkIn) {
            DatabaseManager.get().getCheckInsDBManager().addCheckIn(mRestaurantId, checkIn);
            mCheckInsAdapter.resyncWithDB();
            UIUtils.showSnackbar(mParent, getString(R.string.check_in_added));
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
        Intent intent = new Intent(getActivity(), EditCheckInActivity.class);
        intent.putExtra(EditCheckInActivity.CHECK_IN_KEY, checkIn);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE) {
            switch (resultCode) {
                case EDIT_REQUEST_CODE:
                    mCheckInsAdapter.resyncWithDB();
                    UIUtils.showSnackbar(mParent, getString(R.string.check_in_edited));
                    break;
                case DELETED_RESULT_CODE:
                    mCheckInsAdapter.resyncWithDB();
                    UIUtils.showSnackbar(mParent, getString(R.string.check_in_deleted));
                    break;
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.hideKeyboard(getActivity());
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
