package com.randomappsinc.foodjournal.api;

public class ApiConstants {

    public static final String GRANT_TYPE = "client_credentials";

    public static final String BASE_URL = "https://api.yelp.com";

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String DEFAULT_SEARCH_TERM = "Food";
    public static final int DEFAULT_NUM_RESTAURANTS = 10;

    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_UNAUTHORIZED = 401;
}