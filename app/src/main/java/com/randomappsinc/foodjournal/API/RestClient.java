package com.randomappsinc.foodjournal.API;

import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.foodjournal.API.Callbacks.FetchRestaurantsCallback;
import com.randomappsinc.foodjournal.API.Callbacks.FetchTokenCallback;
import com.randomappsinc.foodjournal.API.Models.RestaurantResults;
import com.randomappsinc.foodjournal.Models.Restaurant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Call;
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
    private Handler mHandler;
    private Call<RestaurantResults> mCurrentRestaurantsCall;
    private final Runnable mCancelRestaurantsCallTask = new Runnable() {
        @Override
        public void run() {
            if (mCurrentRestaurantsCall != null) {
                mCurrentRestaurantsCall.cancel();
            }
        }
    };

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
        HandlerThread backgroundThread = new HandlerThread("");
        backgroundThread.start();
        mHandler = new Handler(backgroundThread.getLooper());
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
        cancelRestaurantFetch();
        mCurrentRestaurantsCall = mYelpService.fetchRestaurants(
                searchTerm.isEmpty() ? ApiConstants.DEFAULT_SEARCH_TERM : searchTerm,
                location,
                ApiConstants.DEFAULT_NUM_RESTAURANTS);
        mCurrentRestaurantsCall.enqueue(new FetchRestaurantsCallback());
    }

    public void processResults(List<Restaurant> restaurants) {
        for (RestaurantResultsHandler handler : mRestaurantResultsHandlers) {
            handler.processResults(restaurants);
        }
    }

    public void cancelRestaurantFetch() {
        mHandler.post(mCancelRestaurantsCallTask);
    }
}