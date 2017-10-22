package com.randomappsinc.foodjournal.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.foodjournal.api.ApiConstants;
import com.randomappsinc.foodjournal.api.RestClient;
import com.randomappsinc.foodjournal.api.models.RestaurantResults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchRestaurantsCallback implements Callback<RestaurantResults> {

    @Override
    public void onResponse(@NonNull Call<RestaurantResults> call, @NonNull Response<RestaurantResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestClient.getInstance().processResults(response.body().getRestaurants());
        } else if (response.code() == ApiConstants.HTTP_STATUS_UNAUTHORIZED) {
            RestClient.getInstance().refreshToken();
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantResults> call, @NonNull Throwable t) {
        // TODO: Respond to API call failure here
    }
}
