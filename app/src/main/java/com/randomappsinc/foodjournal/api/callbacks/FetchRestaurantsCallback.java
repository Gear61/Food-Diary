package com.randomappsinc.foodjournal.api.callbacks;

import com.randomappsinc.foodjournal.api.ApiConstants;
import com.randomappsinc.foodjournal.api.models.RestaurantResults;
import com.randomappsinc.foodjournal.api.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class FetchRestaurantsCallback implements Callback<RestaurantResults> {
    @Override
    public void onResponse(Call<RestaurantResults> call, Response<RestaurantResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestClient.getInstance().processResults(response.body().getRestaurants());
        } else if (response.code() == ApiConstants.HTTP_STATUS_UNAUTHORIZED) {
            RestClient.getInstance().refreshToken();
        }
    }

    @Override
    public void onFailure(Call<RestaurantResults> call, Throwable t) {}
}
