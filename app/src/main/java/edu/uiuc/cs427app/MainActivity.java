package edu.uiuc.cs427app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import androidx.core.content.ContextCompat;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class
MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> personalizeLayoutLauncher;
    private LinearLayout locationsContainer;

    private final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private String API_KEY;
    /*
    This on create instantiates the main activity, especially with regard to applying the theme and connecting to the
    database in order to display the users city list
     */
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
        setContentView(R.layout.activity_main);
        // initialize database
        db = FirebaseFirestore.getInstance();
        locationsContainer = findViewById(R.id.locationsContainer);

        String dynamicTitle = getIntent().getStringExtra("dynamicTitle");

        String username = getIntent().getStringExtra("username");
        // Update the ActionBar title
        if (dynamicTitle != null) {
//            getSupportActionBar().setTitle(dynamicTitle);
            ActionBar actionBar = getSupportActionBar();
            // Toast.makeText(MainActivity.this, actionBar == null ? "ActionBar is null" : "ActionBar is not null", Toast.LENGTH_SHORT).show();
            actionBar.setTitle((CharSequence) dynamicTitle);
        } else {
            // Fallback to the default app name if not found
//            getSupportActionBar().setTitle(R.string.app_name);
        }
        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
//        Button buttonChampaign = findViewById(R.id.buttonChampaign);
//        Button buttonChicago = findViewById(R.id.buttonChicago);
//        Button buttonLA = findViewById(R.id.buttonLA);
        fetchAndDisplayCities();
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogOut = findViewById(R.id.buttonLogout);
        Button buttonPersonalizeLayout = findViewById(R.id.buttonPersonalizeLayout); // layout

        buttonNew.setOnClickListener(this);
        buttonLogOut.setOnClickListener((v -> logOut()));

        buttonPersonalizeLayout.setOnClickListener(this);

        API_KEY = getString(R.string.openweather_api_key);

    }
    /*
    This onClick handles the logic for which view will be updated
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
//            case R.id.buttonChampaign:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Champaign");
//                startActivity(intent);
//                break;
//            case R.id.buttonChicago:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Chicago");
//                startActivity(intent);
//                break;
//            case R.id.buttonLA:
//                intent = new Intent(this, DetailsActivity.class);
//                intent.putExtra("city", "Los Angeles");
//                startActivity(intent);
//                break;
            case R.id.buttonAddLocation:
                // Implement this action to add a new location to the list of locations

                showAddCityDialog();
                break;
            case R.id.buttonPersonalizeLayout:
                // Navigate to PersonalizeLayoutActivity
                intent = new Intent(MainActivity.this, PersonalizeLayoutActivity.class);
                String username = getIntent().getStringExtra("username");
                String theme = getIntent().getStringExtra("themePreference");
                intent.putExtra("username",username);
                intent.putExtra("theme",theme);
                String dynamicTitle = getIntent().getStringExtra("dynamicTitle");
                intent.putExtra("dynamicTitle",dynamicTitle);
                startActivity(intent);
                finish();
                break;
        }
    }

/*
This function displays whether a city was added succesfully or not to the city list.
 */
    private void showAddCityDialog() {
        // Fetch the list of cities
        List<String> cities = readCitiesFromCsv();

        // Create an AutoCompleteTextView
        AutoCompleteTextView cityInput = new AutoCompleteTextView(this);
        cityInput.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities));
        cityInput.setHint("Enter or select a city");


        // Build the dialog
        new AlertDialog.Builder(this)
                .setTitle("Add New Location")
                .setMessage("Enter the name of the city:")
                .setView(cityInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cityName = cityInput.getText().toString().trim();
                        if (!cityName.isEmpty()) {
                            if (cities.contains(cityName)) {
                                // Add city to database if it exists in the list
                                addCityToDatabase(cityName);
                                Toast.makeText(MainActivity.this, "City name " + cityName, Toast.LENGTH_SHORT).show();
                            } else {
                                // Show error message if city is not in the list
                                Toast.makeText(MainActivity.this, "City not found in the list. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "City name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    /*
    This function adds the city to the users city list by querying the database, retunring an error message if it is
    not succesful
     */
    private void addCityToDatabase(String cityName) {
        // Query the `users` collection to find the document with the matching username field
        String username = getIntent().getStringExtra("username");
        Toast.makeText(MainActivity.this, username+ " wants to add city "+ cityName , Toast.LENGTH_SHORT).show();
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the document ID of the first matching document
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Now use the document ID to update the locations array
                        db.collection("users").document(documentId)
                                .update("locations", FieldValue.arrayUnion(cityName))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MainActivity.this, "City added successfully", Toast.LENGTH_SHORT).show();
                                    addCityToUI(cityName); // Add city to UI after successful database update
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(MainActivity.this, "Failed to add city: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to find user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    /*
    This function fetches the users city list from the database in order to be displayed, returning an error toast on failure
     */
    private void fetchAndDisplayCities() {
        String username = getIntent().getStringExtra("username");
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        List<String> locations = (List<String>) document.get("locations");
                        if (locations != null) {
                            for (String city : locations) {
                                addCityToUI(city);
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No cities found for this user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to load cities: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
/*
This function handles getting the cities and displaying their name as well as the detail, and remove button. These buttons also
allow the user to remove items from the list or enter the details of a specific city
 */
    private void addCityToUI(String cityName) {
        LinearLayout cityLayout = new LinearLayout(this);
        cityLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView cityTextView = new TextView(this);
        cityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        cityTextView.setText(cityName);

        Button showDetailsButton = new Button(this);
        showDetailsButton.setText("Show Details");
        showDetailsButton.setBackgroundResource(R.drawable.rounded_button);
        showDetailsButton.setOnClickListener(v -> openCityDetails(cityName));

        // Remove Button
        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setBackgroundResource(R.drawable.rounded_button);
        removeButton.setOnClickListener(v -> {
            removeCityFromDatabase(cityName);
            locationsContainer.removeView(cityLayout);
        });

        // Set layout parameters with margin for each button
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 8, 0, 8); // Adjust the margins as needed

        showDetailsButton.setLayoutParams(buttonParams);
        removeButton.setLayoutParams(buttonParams);

        cityLayout.addView(cityTextView);
        cityLayout.addView(showDetailsButton);
        cityLayout.addView(removeButton);

        locationsContainer.addView(cityLayout);
    }
/*
This function handles removing a specific city from the users saved city list
 */
    private void removeCityFromDatabase(String cityName) {
        String username = getIntent().getStringExtra("username");

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        db.collection("users").document(documentId)
                                .update("locations", FieldValue.arrayRemove(cityName))
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(MainActivity.this, "City removed successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(MainActivity.this, "Failed to remove city: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to find user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

/*
openCityDetails opens the details activity for a given city
 */
    private void openCityDetails(String cityName) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("city", cityName);
        startActivity(intent);
    }

    /*
    fetchWeatherData fetches the details from the api and displays them
     */
    private void fetchWeatherData(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService weatherApi = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApi.getWeatherDetails(cityName, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        String weatherDescription = weatherResponse.getWeather()[0].getDescription();
                        double temperature = weatherResponse.getMain().getTemp() - 273.15; // Convert from Kelvin to Celsius
                        String tempString = String.format("%.2f °C", temperature);
                        Toast.makeText(MainActivity.this, "Weather: " + weatherDescription + " - " + tempString, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
    The log out button clears the context and navigates back to the auth activity page for another user to signup/login
     */
    private void logOut() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved preferences
        editor.apply();

        // Create an Intent to navigate back to AuthActivity
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        // Optionally, you can also add flags to clear the activity stack if desired
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish MainActivity to remove it from the back stack
    }

/*
This function parses through the cities_list.csv and gets the list of cities
 */
    private List<String> readCitiesFromCsv() {
        List<String> cities = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("cities_list.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    cities.add(parts[0].trim()); // Add the city name, trimming any extra spaces
                }
            }
            reader.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error reading city list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return cities;
    }

}

