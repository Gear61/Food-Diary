package com.randomappsinc.foodjournal.API;

import com.randomappsinc.foodjournal.API.Callbacks.FetchRestaurantsCallback;
import com.randomappsinc.foodjournal.API.Callbacks.FetchTokenCallback;
import com.randomappsinc.foodjournal.Models.Restaurant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alexanderchiou on 8/13/17.
 */

public class RestClient {
    public interface RestaurantResultsHandler {
        void processResults(List<Restaurant> results);
    }

    private static RestClient mInstance;
    private YelpService mYelpService;
    private Set<RestaurantResultsHandler> mRestaurantResultsHandlers;

    public static RestClient getInstance() {
        if (mInstance == null) {
            mInstance = new RestClient();
        }
        return mInstance;
    }

    private RestClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mYelpService = retrofit.create(YelpService.class);
        mRestaurantResultsHandlers = new HashSet<>();
    }

    public void refreshToken() {
        mYelpService.fetchToken(YelpToken.CLIENT_ID, YelpToken.CLIENT_SECRET, ApiConstants.GRANT_TYPE)
                .enqueue(new FetchTokenCallback());
    }

    public void registerRestaurantResultsHandler(RestaurantResultsHandler handler) {
        mRestaurantResultsHandlers.add(handler);
    }

    public void unregisterRestaurantResultsHandler(RestaurantResultsHandler handler) {
        mRestaurantResultsHandlers.remove(handler);
    }

    public void fetchRestaurants(String searchTerm, String location) {
        mYelpService.fetchRestaurants(searchTerm, location, ApiConstants.DEFAULT_NUM_RESTAURANTS)
                .enqueue(new FetchRestaurantsCallback());
    }

    public void processResults(List<Restaurant> restaurants) {
        for (RestaurantResultsHandler handler : mRestaurantResultsHandlers) {
            handler.processResults(restaurants);
        }
    }
}