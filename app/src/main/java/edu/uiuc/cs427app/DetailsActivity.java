package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

//    private TextView cityNameTextView;
//    private TextView temperatureTextView;
//    private TextView humidityTextView;
//    private TextView descriptionTextView;
    /*
    This onCreate instantiates the details when the user hits the details button
    right now it is a skeleton while we implement the map display
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initializing the GUI elements
        TextView cityNameTextView = findViewById(R.id.cityName);
        TextView temperatureTextView = findViewById(R.id.temperature);
        TextView humidityTextView = findViewById(R.id.humidity);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView windConditionTextView = findViewById(R.id.windCondition);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city");
        String apiKey = getString(R.string.openweather_api_key);

        WeatherApiService apiService = RetrofitClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getWeatherDetails(cityName, apiKey, "metric");  // "metric" for Celsius, use "imperial" for Fahrenheit

        call.enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        cityNameTextView.setText("Welcome to " + weatherResponse.getName());
                        temperatureTextView.setText(String.valueOf(weatherResponse.getMain().getTemp()) + "°C");
                        humidityTextView.setText("Humidity: " + weatherResponse.getMain().getHumidity() + "%");
                        descriptionTextView.setText("Weather: " + weatherResponse.getWeather()[0].getDescription());
                        windConditionTextView.setText("Wind condition: speed = " + weatherResponse.getWind().getSpeed() + ", degree = " + weatherResponse.getWind().getDeg());
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Get the weather information from a Service that connects to a weather server and show the results

        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
    }
}

