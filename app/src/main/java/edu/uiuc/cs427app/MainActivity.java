package edu.uiuc.cs427app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class
MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
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

        String dynamicTitle = getIntent().getStringExtra("dynamicTitle");

        String username = getIntent().getStringExtra("username");
        // Update the ActionBar title
        if (dynamicTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            // Toast.makeText(MainActivity.this, actionBar == null ? "ActionBar is null" : "ActionBar is not null", Toast.LENGTH_SHORT).show();
            actionBar.setTitle((CharSequence) dynamicTitle);
            // getSupportActionBar().setTitle(dynamicTitle);
            // Toast.makeText(MainActivity.this, dynamicTitle, Toast.LENGTH_SHORT).show();
        } else {
            // Fallback to the default app name if not found
            // getSupportActionBar().setTitle(R.string.app_name);
        }
        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonChampaign = findViewById(R.id.buttonChampaign);
        Button buttonChicago = findViewById(R.id.buttonChicago);
        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonLogOut = findViewById(R.id.buttonLogout);

        buttonChampaign.setOnClickListener(this);
        buttonChicago.setOnClickListener(this);
        buttonLA.setOnClickListener(this);
        buttonNew.setOnClickListener(this);
        buttonLogOut.setOnClickListener((v -> logOut()));

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonChampaign:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Champaign");
                startActivity(intent);
                break;
            case R.id.buttonChicago:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Chicago");
                startActivity(intent);
                break;
            case R.id.buttonLA:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Los Angeles");
                startActivity(intent);
                break;
            case R.id.buttonAddLocation:
                // Implement this action to add a new location to the list of locations

                showAddCityDialog();
                break;
        }
    }
    private void showAddCityDialog() {
        // Create an EditText to input the city name
        final EditText cityInput = new EditText(this);
        cityInput.setHint("Enter city name");

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
                            addCityToDatabase(cityName);
                            Toast.makeText(MainActivity.this, "City name "+cityName, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "City name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void addCityToDatabase(String cityName) {
        // Query the `users` collection to find the document with the matching username field
        String username = getIntent().getStringExtra("username");
        Toast.makeText(MainActivity.this, username+ "want to add city"+ cityName , Toast.LENGTH_SHORT).show();
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
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(MainActivity.this, "City added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(MainActivity.this, "Failed to add city: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to find user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

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

}

