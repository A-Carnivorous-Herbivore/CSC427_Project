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
    private static final String TAG = "DetailsActivity";
    private String inputPrompt; // Stores the generated input prompt for use in WeatherInsightActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        String cityName = getIntent().getStringExtra("city");
        String apiKey = getString(R.string.openweather_api_key);

        WeatherApiService apiService = RetrofitClient.getClient().create(WeatherApiService.class);
        String units = "metric"; // "metric" for Celsius, use "imperial" for Fahrenheit
        Call<WeatherResponse> call = apiService.getWeatherDetails(cityName, apiKey, units);

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
                Log.e(TAG, "Failed to fetch weather data", t);
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
        int id = view.getId();
        if (id == R.id.insightButton) {
            // Navigate to WeatherInsightActivity with the inputPrompt
            Intent intent = new Intent(DetailsActivity.this, WeatherInsightActivity.class);
            intent.putExtra("inputPrompt", inputPrompt);
            startActivity(intent);
        } else if (id == R.id.mapButton) {
            // Navigate to map activity (to be implemented)
            Toast.makeText(this, "Map functionality is not yet implemented.", Toast.LENGTH_SHORT).show();
        }
    }
}
