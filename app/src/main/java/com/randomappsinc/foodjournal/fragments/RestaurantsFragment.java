package com.randomappsinc.foodjournal.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.foodjournal.R;
import com.randomappsinc.foodjournal.activities.DishFormActivity;
import com.randomappsinc.foodjournal.activities.FindRestaurantActivity;
import com.randomappsinc.foodjournal.activities.RestaurantViewActivity;
import com.randomappsinc.foodjournal.activities.RestaurantsActivity;
import com.randomappsinc.foodjournal.adapters.UserRestaurantsAdapter;
import com.randomappsinc.foodjournal.models.Restaurant;
import com.randomappsinc.foodjournal.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

public class RestaurantsFragment extends Fragment {

    // Request codes
    public static final int ADD_RESTAURANT_CODE = 1;
    public static final int RESTAURANT_VIEW_CODE = 2;

    // Result codes
    public static final int RESTAURANT_DELETED_CODE = 1;

    @BindView(R.id.parent) View mParent;
    @BindView(R.id.search_input) EditText mSearchInput;
    @BindView(R.id.clear_search) View mClearSearch;
    @BindView(R.id.no_results) TextView mNoResults;
    @BindView(R.id.restaurants) ListView mRestaurantsList;
    @BindView(R.id.add_restaurant) FloatingActionButton mAddRestaurant;

    private UserRestaurantsAdapter mAdapter;
    private boolean mPickerMode;
    private Unbinder mUnbinder;

    public static RestaurantsFragment newInstance(boolean pickerMode) {
        RestaurantsFragment fragment = new RestaurantsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(RestaurantsActivity.PICKER_MODE_KEY, pickerMode);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mPickerMode = getArguments().getBoolean(RestaurantsActivity.PICKER_MODE_KEY, false);
        mAddRestaurant.setImageDrawable(new IconDrawable(getActivity(), IoniconsIcons.ion_android_add).colorRes(R.color.white));
        mAdapter = new UserRestaurantsAdapter(getActivity(), mNoResults);
        mRestaurantsList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.resyncWithDB(mSearchInput.getText().toString());
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSearchTermChanged(Editable input) {
        mAdapter.resyncWithDB(input.toString());
        if (input.length() == 0) {
            mClearSearch.setVisibility(View.GONE);
        } else {
            mClearSearch.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        mSearchInput.setText("");
    }

    @OnItemClick(R.id.restaurants)
    public void onRestaurantSelected(int position) {
        Restaurant restaurant = mAdapter.getItem(position);
        if (mPickerMode) {
            Intent returnRestaurant = new Intent();
            returnRestaurant.putExtra(DishFormActivity.RESTAURANT_KEY, restaurant);
            getActivity().setResult(Activity.RESULT_OK, returnRestaurant);
            getActivity().finish();
        } else {
            Intent intent = new Intent(getActivity(), RestaurantViewActivity.class);
            intent.putExtra(RestaurantViewActivity.RESTAURANT_KEY, restaurant);
            startActivityForResult(intent, RESTAURANT_VIEW_CODE);
        }
    }

    @OnClick(R.id.add_restaurant)
    public void addRestaurant() {
        startActivityForResult(new Intent(getActivity(), FindRestaurantActivity.class), ADD_RESTAURANT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Restaurant successfully added, refresh list to reflect that
        if (requestCode == ADD_RESTAURANT_CODE && resultCode == Activity.RESULT_OK) {
            mAdapter.resyncWithDB(mSearchInput.getText().toString());
            UIUtils.showSnackbar(mParent, getString(R.string.restaurant_added));
        } else if (requestCode == RESTAURANT_VIEW_CODE && resultCode == RESTAURANT_DELETED_CODE) {
            mAdapter.resyncWithDB(mSearchInput.getText().toString());
            UIUtils.showSnackbar(mParent, getString(R.string.restaurant_deleted));
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
