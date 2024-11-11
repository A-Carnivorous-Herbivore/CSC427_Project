package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String cityName = getIntent().getStringExtra("city");
        if (cityName == null) {
            cityName = "Unknown City";
        }
        String welcome = "Welcome to " + cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + cityName;

        // Initialize the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);
        Button buttonMap = findViewById(R.id.mapButton);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);

        // Set OnClickListener for the button
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
            case R.id.weatherButton:


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

    private void openMapActivity(String cityName, double latitude, double longitude,String theme) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("themePreference", theme);
//        intent.putExtra("latitude", latitude);
//        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
}
