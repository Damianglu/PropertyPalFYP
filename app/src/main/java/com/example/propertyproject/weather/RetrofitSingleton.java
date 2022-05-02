package com.example.propertyproject.weather;

import com.example.propertyproject.Utils;
import com.example.propertyproject.weather.auth.WebService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    public  WebService getClient()  {
       return new Retrofit.Builder()
                .baseUrl(Utils.WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(WebService.class);
    }
}
