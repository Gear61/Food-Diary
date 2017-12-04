package com.randomappsinc.foodjournal.api;

import com.randomappsinc.foodjournal.api.models.RestaurantInfo;
import com.randomappsinc.foodjournal.api.models.RestaurantResults;
import com.randomappsinc.foodjournal.api.models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpService {

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> fetchToken(@Field("client_id") String clientId,
                                   @Field("client_secret") String clientSecret,
                                   @Field("grant_type") String last);

    @GET("v3/businesses/search")
    Call<RestaurantResults> fetchRestaurants(@Query("term") String term,
                                             @Query("location") String location,
                                             @Query("limit") int limit,
                                             @Query("sort_by") String sortBy);

    @GET("v3/businesses/{id}")
    Call<RestaurantInfo> fetchRestaurantInfo(@Path("id") String restaurantId);
}
