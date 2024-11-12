package edu.uiuc.cs427app;

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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        TextView timeTextView = findViewById(R.id.time);
        TextView temperatureTextView = findViewById(R.id.temperature);
        TextView humidityTextView = findViewById(R.id.humidity);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView windConditionTextView = findViewById(R.id.windCondition);



        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city");
        String apiKey = getString(R.string.openweather_api_key);

        WeatherApiService apiService = RetrofitClient.getClient().create(WeatherApiService.class);
        String units = "metric"; // "metric" for Celsius, use "imperial" for Fahrenheit, we can change between the 2
        Call<WeatherResponse> call = apiService.getWeatherDetails(cityName, apiKey, units);

        // Asynchronously enqueueing the network call
        call.enqueue(new Callback<WeatherResponse>() {
            /*
             * Callback method called when a network response is received successfully.
             * It parses and displays the weather data in the UI.
             */
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
                        String tempText = "Temperature: " + String.valueOf(weatherResponse.getMain().getTemp()) + tempUnit;
                        String humidityText = "Humidity: " + weatherResponse.getMain().getHumidity() + "%";
                        String descriptionText = "Weather: " + weatherResponse.getWeather()[0].getDescription();
                        String windConditionUnits = units.equals("metric") ? "m/s" : "mph";
                        String windConditionText = "Wind condition: speed = " + weatherResponse.getWind().getSpeed() + windConditionUnits +  ", degree = " + weatherResponse.getWind().getDeg() + " (wind direction)";

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

            /*
             * Callback method called when a network request fails.
             * Displays an error message to the user.
             */
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

