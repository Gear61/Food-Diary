package com.randomappsinc.foodjournal.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CheckInsFragment extends Fragment {

    @BindView(R.id.add_check_in) FloatingActionButton mAddCheckIn;

    private String mRestaurantId;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.check_ins, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mAddCheckIn.setImageDrawable(new IconDrawable(getActivity(), IoniconsIcons.ion_android_add).colorRes(R.color.white));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
