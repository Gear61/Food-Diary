package com.randomappsinc.foodjournal.api;

import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.foodjournal.api.callbacks.FetchRestaurantInfoCallback;
import com.randomappsinc.foodjournal.api.callbacks.FetchRestaurantsCallback;
import com.randomappsinc.foodjournal.api.models.RestaurantSearchResults;
import com.randomappsinc.foodjournal.models.Restaurant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public interface RestaurantResultsHandler {
        void processResults(List<Restaurant> results);
    }

    private static RestClient instances;
    private YelpService yelpService;
    private Set<RestaurantResultsHandler> restaurantResultsHandlers;
    private Handler handler;
    private Call<RestaurantSearchResults> currentRestaurantsCall;

    public static RestClient getInstance() {
        if (instances == null) {
            instances = new RestClient();
        }
        return instances;
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

        yelpService = retrofit.create(YelpService.class);
        restaurantResultsHandlers = new HashSet<>();
        HandlerThread backgroundThread = new HandlerThread("");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    public void registerRestaurantResultsHandler(RestaurantResultsHandler handler) {
        restaurantResultsHandlers.add(handler);
    }

    public void unregisterRestaurantResultsHandler(RestaurantResultsHandler handler) {
        restaurantResultsHandlers.remove(handler);
    }

    public void fetchRestaurants(final String searchTerm, final String location) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentRestaurantsCall != null) {
                    currentRestaurantsCall.cancel();
                }
                currentRestaurantsCall = yelpService.fetchRestaurants(
                        searchTerm,
                        location,
                        ApiConstants.DEFAULT_NUM_RESTAURANTS,
                        searchTerm.isEmpty() ? ApiConstants.DISTANCE : ApiConstants.BEST_MATCH);
                currentRestaurantsCall.enqueue(new FetchRestaurantsCallback());
            }
        });
    }

    public void processResults(List<Restaurant> restaurants) {
        for (RestaurantResultsHandler handler : restaurantResultsHandlers) {
            handler.processResults(restaurants);
        }
    }

    public void cancelRestaurantFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentRestaurantsCall != null) {
                    currentRestaurantsCall.cancel();
                }
            }
        });
    }

    public void updateRestaurantInfo(Restaurant restaurant) {
        yelpService.fetchRestaurantInfo(restaurant.getId()).enqueue(new FetchRestaurantInfoCallback());
    }
}
