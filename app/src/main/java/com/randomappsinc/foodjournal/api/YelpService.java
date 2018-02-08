package com.randomappsinc.foodjournal.api;

import com.randomappsinc.foodjournal.api.models.RestaurantInfo;
import com.randomappsinc.foodjournal.api.models.RestaurantSearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpService {

    @GET("v3/businesses/search")
    Call<RestaurantSearchResults> fetchRestaurants(@Query("term") String term,
                                                   @Query("location") String location,
                                                   @Query("limit") int limit,
                                                   @Query("sort_by") String sortBy);

    @GET("v3/businesses/{id}")
    Call<RestaurantInfo> fetchRestaurantInfo(@Path("id") String restaurantId);
}
