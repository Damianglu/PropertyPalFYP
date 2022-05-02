package com.example.propertyproject.weather;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.propertyproject.weather.auth.WebService;
import com.example.propertyproject.weather.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends AndroidViewModel {

    private RetrofitSingleton retrofitSingleton;
    private WeatherRepository weatherRepository;

    public WeatherViewModel(Application application){
        super(application);
        retrofitSingleton = new RetrofitSingleton();
        weatherRepository = new WeatherRepository(retrofitSingleton);

    }


    public LiveData<Resource<WeatherResponse>> getWeatherInfo(String query, String unit, String apiKey){
        weatherRepository.getWeatherData(query,unit,apiKey);
        return weatherRepository.weatherLiveData;
    }

}
