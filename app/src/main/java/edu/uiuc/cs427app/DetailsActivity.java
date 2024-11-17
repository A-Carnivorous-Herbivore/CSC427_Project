package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DetailsActivity"; // Tag for error logging
    private String inputPrompt; // Stores the generated input prompt for use in WeatherInsightActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme that was selected previously
        String themePreference = getIntent().getStringExtra("themePreference");
//        String themePreference = "Light";
        if (themePreference != null) {
            switch (themePreference) {
                case "Default":
                    setTheme(R.style.Theme_MyFirstApp_Default);
                    break;
                case "Dark":
                    setTheme(R.style.Theme_MyFirstApp_DarkMode);
                    break;
                case "Light":
                    setTheme(R.style.Theme_MyFirstApp_LightMode);
                    break;
                case "Blue":
                    setTheme(R.style.Theme_MyFirstApp_BlueRidge);
                    break;
                case "Sunshine":
                    setTheme(R.style.Theme_MyFirstApp_SunnyDay);
                    break;
                default:
                    setTheme(R.style.Theme_MyFirstApp_Default);
                    break;
            }
        } else {
            setTheme(R.style.Theme_MyFirstApp_Default); // Fallback to default if themePreference is null
        }

        // Set layout for DetailsActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initializing the GUI elements
        TextView cityNameTextView = findViewById(R.id.cityName);
        TextView timeTextView = findViewById(R.id.time);
        TextView temperatureTextView = findViewById(R.id.temperature);
        TextView humidityTextView = findViewById(R.id.humidity);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView windConditionTextView = findViewById(R.id.windCondition);

        // Process the Intent payload that opened this Activity and show the information accordingly
        String apiKey = getString(R.string.openweather_api_key);
        String cityName = getIntent().getStringExtra("city");
        if (cityName == null) {
            cityName = "Unknown City";
        }
        String welcome = "Welcome to " + cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initialize the GUI elements
        WeatherApiService apiService = RetrofitClient.getClient().create(WeatherApiService.class);
        String units = "metric"; // "metric" for Celsius, use "imperial" for Fahrenheit
        Call<WeatherResponse> call = apiService.getWeatherDetails(cityName, apiKey, units); // Request to fetch weather details

        // Asynchronously enqueueing the network call
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        long timeStamp = weatherResponse.getDt();
                        String date = Instant.ofEpochSecond(timeStamp)
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));
                        String cityNameText = weatherResponse.getName() + " Weather Details";
                        String dateText = "Date and time: " + date;
                        String tempUnit = units.equals("metric") ? "°C" : "°F";
                        String tempText = "Temperature: " + weatherResponse.getMain().getTemp() + tempUnit;
                        String humidityText = "Humidity: " + weatherResponse.getMain().getHumidity() + "%";
                        String descriptionText = "Weather: " + weatherResponse.getWeather()[0].getDescription();
                        String windConditionUnits = units.equals("metric") ? "m/s" : "mph";
                        String windConditionText = "Wind condition: speed = " + weatherResponse.getWind().getSpeed() + windConditionUnits +
                                ", degree = " + weatherResponse.getWind().getDeg() + " (wind direction)";
                        inputPrompt = "We have such information about " + cityNameText + ": " + tempText + " " + humidityText + " " +
                                descriptionText + " " + windConditionText + ".";

                        // Update UI elements
                        cityNameTextView.setText(cityNameText);
                        timeTextView.setText(dateText);
                        temperatureTextView.setText(tempText);
                        humidityTextView.setText(humidityText);
                        descriptionTextView.setText(descriptionText);
                        windConditionTextView.setText(windConditionText);
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Failed to retrieve weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch weather data", t); // Log failures
                Toast.makeText(DetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Setting click listeners for buttons
        Button insightButton = findViewById(R.id.insightButton);
        insightButton.setOnClickListener(this);
        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.mapButton:
                double latitude = 40.1106;  // Example latitude
                double longitude = -88.2073; // Example longitude
                String cityName = getIntent().getStringExtra("city");

                if (cityName != null) {
                    String themePreference = getIntent().getStringExtra("themePreference");
                    openMapActivity(cityName, latitude, longitude,themePreference);
                } else {
                    Toast.makeText(this, "City name not available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.insightButton:
                // Navigate to WeatherInsightActivity with the inputPrompt
                Intent intent = new Intent(DetailsActivity.this, WeatherInsightActivity.class);
                intent.putExtra("inputPrompt", inputPrompt);
                startActivity(intent);
                break;
            // case R.id.weatherButton:

        }
//        // Replace with actual latitude and longitude values as per your app's data source
//        double latitude = 40.1106;  // Example latitude
//        double longitude = -88.2073; // Example longitude
//        String cityName = getIntent().getStringExtra("city");
//
//        if (cityName != null) {
//            openMapActivity(cityName, latitude, longitude);
//        } else {
//            Toast.makeText(this, "City name not available", Toast.LENGTH_SHORT).show();
//        }
    }

    // openMapActivity function opens map activity to display specified location on map
    private void openMapActivity(String cityName, double latitude, double longitude,String theme) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("themePreference", theme);
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
}
