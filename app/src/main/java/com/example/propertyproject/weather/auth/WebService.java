package com.example.propertyproject.weather.auth;

import com.example.propertyproject.weather.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebService {

    @GET("weather?")
    Call<WeatherResponse> getWeatherResponse(
            @Query("q") String query,
            @Query("units") String unit,
            @Query("apiKey") String apiKey
    );

}
