package com.example.propertyproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.propertyproject.weather.Resource;
import com.example.propertyproject.weather.WeatherViewModel;
import com.example.propertyproject.weather.models.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {
    private TextView address,updated_at,status,temp,temp_min,temp_max,sunrise,sunset,wind,pressure,humidity,feels_like;
    private ProgressBar progressBar;
    private TextView errorTxt;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        address = findViewById(R.id.address);
        updated_at = findViewById(R.id.updated_at);
        status = findViewById(R.id.status);
        temp = findViewById(R.id.temp);
        temp_min = findViewById(R.id.temp_min);
        temp_max = findViewById(R.id.temp_max);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        feels_like = findViewById(R.id.feels_like);
        progressBar = findViewById(R.id.progressBar);
        errorTxt = findViewById(R.id.error);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        WeatherViewModel weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        weatherViewModel.getWeatherInfo("dublin", "metric",Utils.WEATHER_KEY)
                .observe(this, new Observer<Resource<WeatherResponse>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(Resource<WeatherResponse> weatherResponseResource) {
                        switch (weatherResponseResource.getType()){
                            case Resource.LOADING: {
                                progressBar.setVisibility(View.VISIBLE);

                            }
                            case  Resource.SUCCESS: {
                                progressBar.setVisibility(View.GONE);
                                updateUi(weatherResponseResource.getData());
                            }

                            case  Resource.ERROR: {
                                progressBar.setVisibility(View.GONE);
                                if(weatherResponseResource.getError() != null){
                                    errorTxt.setVisibility(View.VISIBLE);
                                    errorTxt.setText("Something Wrong ...");
                                }
                            }
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void updateUi(WeatherResponse weatherResponse){
        address.setText(weatherResponse.getName() + ", " + weatherResponse.getSys().getCountry());
        updated_at.setText("Updated at: "+ new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).
                format(new Date(weatherResponse.getDt() * 1000L)));
        status.setText(weatherResponse.getWeather().get(0).getDescription());
        temp.setText(weatherResponse.getMain().getTemp()+"°C");
        temp_min.setText("Min Temp : " + weatherResponse.getMain().getTempMin()+"°C");
        temp_max.setText("Min Max : " + weatherResponse.getMain().getTempMax()+"°C");
        sunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(weatherResponse.getSys().getSunrise()* 1000L)));
        sunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(weatherResponse.getSys().getSunset()* 1000L)));
        wind.setText(String.valueOf(weatherResponse.getWind().getSpeed()));
        pressure.setText(String.valueOf(weatherResponse.getMain().getPressure()));
        humidity.setText(String.valueOf(weatherResponse.getMain().getHumidity()));
        feels_like.setText(weatherResponse.getMain().getFeelsLike() +"°C");
    }
}
//