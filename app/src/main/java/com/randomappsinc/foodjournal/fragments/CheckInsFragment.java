package com.randomappsinc.foodjournal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.CheckInFormActivity;
import com.randomappsinc.foodjournal.adapters.CheckInsAdapter;
import com.randomappsinc.foodjournal.models.CheckIn;
import com.randomappsinc.foodjournal.utils.Constants;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class CheckInsFragment extends Fragment {

    public static CheckInsFragment newInstance(String restaurantId) {
        CheckInsFragment fragment = new CheckInsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESTAURANT_ID_KEY, restaurantId);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public static final int CHECK_IN_FORM = 1;

    public static final int ADDED_RESULT = 1;
    public static final int EDITED_RESULT = 2;
    public static final int DELETED_RESULT = 3;

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.no_results) TextView mNoResults;
    @BindView(R.id.check_ins) ListView mCheckInsList;
    @BindView(R.id.add_check_in) FloatingActionButton mAddCheckIn;

    private CheckInsAdapter mCheckInsAdapter;
    private String mRestaurantId;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.check_ins, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mRestaurantId = getArguments().getString(Constants.RESTAURANT_ID_KEY);
        mAddCheckIn.setImageDrawable(new IconDrawable(
                getActivity(),
                IoniconsIcons.ion_android_add).colorRes(R.color.white));

        mCheckInsAdapter = new CheckInsAdapter(getActivity(), mNoResults, mRestaurantId);
        mCheckInsList.setAdapter(mCheckInsAdapter);

        return rootView;
    }

    @OnClick(R.id.add_check_in)
    public void addCheckIn() {
        Intent addCheckIn = new Intent(getActivity(), CheckInFormActivity.class);
        addCheckIn.putExtra(CheckInFormActivity.ADDER_MODE_KEY, true);
        addCheckIn.putExtra(Constants.RESTAURANT_ID_KEY, mRestaurantId);
        startActivityForResult(addCheckIn, CHECK_IN_FORM);
    }

    @OnItemClick(R.id.check_ins)
    public void onCheckInSelected(int position) {
        CheckIn checkIn = mCheckInsAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), CheckInFormActivity.class);
        intent.putExtra(CheckInFormActivity.ADDER_MODE_KEY, false);
        intent.putExtra(CheckInFormActivity.CHECK_IN_KEY, checkIn);
        startActivityForResult(intent, CHECK_IN_FORM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != CHECK_IN_FORM) {
            return;
        }

        switch (resultCode) {
            case ADDED_RESULT:
                mCheckInsAdapter.resyncWithDB();
                UIUtils.showSnackbar(mParent, getString(R.string.check_in_added));
                break;
            case EDITED_RESULT:
                mCheckInsAdapter.resyncWithDB();
                UIUtils.showSnackbar(mParent, getString(R.string.check_in_edited));
                break;
            case DELETED_RESULT:
                mCheckInsAdapter.resyncWithDB();
                UIUtils.showSnackbar(mParent, getString(R.string.check_in_deleted));
                break;
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
