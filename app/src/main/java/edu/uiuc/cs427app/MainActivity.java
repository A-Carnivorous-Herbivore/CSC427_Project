
package edu.uiuc.cs427app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private FirebaseFirestore db;
    private LinearLayout locationsContainer;
    private Switch themeToggleSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String themePreference = sharedPreferences.getString("themePreference", "Default");

        applyTheme(themePreference);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        locationsContainer = findViewById(R.id.locationsContainer);
        themeToggleSwitch = findViewById(R.id.themeToggleSwitch);

        String dynamicTitle = getIntent().getStringExtra("dynamicTitle");
        String username = getIntent().getStringExtra("username");

        if (dynamicTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(dynamicTitle);
            }
        }

        fetchAndDisplayCities();
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogOut = findViewById(R.id.buttonLogout);
        Button buttonPersonalizeLayout = findViewById(R.id.buttonPersonalizeLayout);

        buttonNew.setOnClickListener(this);
        buttonLogOut.setOnClickListener((v -> logOut()));
        buttonPersonalizeLayout.setOnClickListener(this);

        // Set the initial state of the switch based on the theme
        themeToggleSwitch.setChecked(themePreference.equals("Dark"));

        // Listen for switch changes
        themeToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newTheme = isChecked ? "Dark" : "Light";
            sharedPreferences.edit().putString("themePreference", newTheme).apply();
            recreate(); // Recreate the activity to apply the new theme
        });
    }

    private void applyTheme(String themePreference) {
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
                String theme = sharedPreferences.getString("themePreference", "Default");
                String dynamicTitle = getIntent().getStringExtra("dynamicTitle");

                intent.putExtra("username", username);
                intent.putExtra("theme", theme);
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
                .setPositiveButton("Add", (dialog, which) -> {
                    String cityName = cityInput.getText().toString().trim();
                    if (!cityName.isEmpty()) {
                        addCityToDatabase(cityName);
                        Toast.makeText(MainActivity.this, "City name " + cityName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "City name cannot be empty", Toast.LENGTH_SHORT).show();
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

        cityLayout.addView(cityTextView);
        cityLayout.addView(showDetailsButton);

        locationsContainer.addView(cityLayout);
    }

    private void openCityDetails(String cityName) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("city", cityName);
        startActivity(intent);
    }

    private void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

