package com.randomappsinc.foodjournal.api.callbacks;

import androidx.annotation.NonNull;

import com.randomappsinc.foodjournal.api.ApiConstants;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.api.models.RestaurantSearchResults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchRestaurantsCallback implements Callback<RestaurantSearchResults> {

    @Override
    public void onResponse(
            @NonNull Call<RestaurantSearchResults> call,
            @NonNull Response<RestaurantSearchResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestClient.getInstance().processResults(response.body().getRestaurants());
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantSearchResults> call, @NonNull Throwable t) {
        // TODO: Respond to API call failure here
    }
}
