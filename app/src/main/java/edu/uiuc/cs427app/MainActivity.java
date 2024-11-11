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

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> personalizeLayoutLauncher;
    private LinearLayout locationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String themePreference = getIntent().getStringExtra("themePreference");
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
            setTheme(R.style.Theme_MyFirstApp_Default);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        locationsContainer = findViewById(R.id.locationsContainer);

        String dynamicTitle = getIntent().getStringExtra("dynamicTitle");
        String username = getIntent().getStringExtra("username");

        ActionBar actionBar = getSupportActionBar();
        if (dynamicTitle != null && actionBar != null) {
            actionBar.setTitle(dynamicTitle);
        }

        fetchAndDisplayCities();
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogOut = findViewById(R.id.buttonLogout);
        Button buttonPersonalizeLayout = findViewById(R.id.buttonPersonalizeLayout);

        buttonNew.setOnClickListener(this);
        buttonLogOut.setOnClickListener((v -> logOut()));
        buttonPersonalizeLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonAddLocation:
                showAddCityDialog();
                break;
            case R.id.buttonPersonalizeLayout:
                intent = new Intent(MainActivity.this, PersonalizeLayoutActivity.class);
                String username = getIntent().getStringExtra("username");
                String theme = getIntent().getStringExtra("themePreference");
                intent.putExtra("username", username);
                intent.putExtra("theme", theme);
                String dynamicTitle = getIntent().getStringExtra("dynamicTitle");
                intent.putExtra("dynamicTitle", dynamicTitle);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void showAddCityDialog() {
        final EditText cityInput = new EditText(this);
        cityInput.setHint("Enter city name");

        new AlertDialog.Builder(this)
                .setTitle("Add New Location")
                .setMessage("Enter the name of the city:")
                .setView(cityInput)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cityName = cityInput.getText().toString().trim();
                        if (!cityName.isEmpty()) {
                            addCityToDatabase(cityName);
                        } else {
                            Toast.makeText(MainActivity.this, "City name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addCityToDatabase(String cityName) {
        String username = getIntent().getStringExtra("username");
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document(documentId)
                                .update("locations", FieldValue.arrayUnion(cityName))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MainActivity.this, "City added successfully", Toast.LENGTH_SHORT).show();
                                    addCityToUI(cityName);
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

    private void addCityToUI(String cityName) {
        LinearLayout cityLayout = new LinearLayout(this);
        cityLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView cityTextView = new TextView(this);
        cityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        cityTextView.setText(cityName);

        Button showDetailsButton = new Button(this);
        showDetailsButton.setText("Show Details");
        showDetailsButton.setOnClickListener(v -> openCityDetails(cityName));

        Button mapButton = new Button(this);
        mapButton.setText("Map");
        mapButton.setOnClickListener(v -> openMapActivity(cityName, 40.1139, -88.2249)); // Example coordinates

        cityLayout.addView(cityTextView);
        cityLayout.addView(showDetailsButton);
        cityLayout.addView(mapButton);

        locationsContainer.addView(cityLayout);
    }

    private void openMapActivity(String cityName, double latitude, double longitude) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    private void openCityDetails(String cityName) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("city", cityName);
        startActivity(intent);
    }

    private void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
