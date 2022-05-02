package com.example.propertyproject.weather;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.propertyproject.weather.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {

    public MutableLiveData<Resource<WeatherResponse>> weatherLiveData = new MutableLiveData<>();
    private RetrofitSingleton retrofitSingleton;
    WeatherRepository(RetrofitSingleton retrofitSingleton){
        this.retrofitSingleton = retrofitSingleton;
    }


    void getWeatherData(String query , String unit, String apiKey){
        retrofitSingleton.getClient().getWeatherResponse(query,unit,apiKey).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                weatherLiveData.postValue(Resource.loading());
                if(response.isSuccessful()){
                    weatherLiveData.postValue(Resource.success(response.body()));
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    weatherLiveData.postValue(Resource.error(t.getMessage()));
            }
        });
    }
}
