package edu.uiuc.cs427app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Spinner;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Spinner themeSpinner;

    private FirebaseFirestore db;
    private boolean dbConnected = false;
    /*
        onCreate: Instantializes the activity, connecting to the firestore database, retrieving the user information
        and setting click listeners for the log in and sign up buttons.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        checkDbConnection();

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        //themeSpinner = findViewById(R.id.themeSpinner); /// themeinput
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        // Set click listeners for buttons
        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> signupUser());
    }
    /*
    This Method checks the database connection by making a dummy call and making sure that the connection is up.
    */
    private void checkDbConnection() {
        db.collection("users").document("test").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dbConnected = true;
                        Toast.makeText(AuthActivity.this, "Firestore connection established.", Toast.LENGTH_LONG).show();
                    } else {
                        dbConnected = false;
                        Toast.makeText(AuthActivity.this, "Firestore connection failed: " + task.getException(), Toast.LENGTH_LONG).show();
                        Log.d("Firebase", task.getException().toString());
                    }
                });
    }
    /*
    LoginUser() handles the log in function, checking the database for the user credentials and returning an error if it is incorrect
     */
    private void loginUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        db.collection("users").whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        saveUsernameToPreferences(username);
                        navigate();
                    } else {
                        Toast.makeText(AuthActivity.this, "Login failed: Incorrect credentials", Toast.LENGTH_LONG).show();
                    }
                });
    }
    /*
    signupUser(): this method signs up a user if they dont exist within a database
     */
    private void signupUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        //String themePreference = themeSpinner.getSelectedItem().toString(); // Retrieve theme preference

        // Store user data in Firestore
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);
        user.put("themePreference", "default");
        saveUsernameToPreferences(username);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> navigate())
                .addOnFailureListener(e -> Toast.makeText(AuthActivity.this, "Sign-up failed: "+ e, Toast.LENGTH_LONG).show());
    }
    /*
    This method sets the username within the shared preferences to be used within the app
     */
    private void saveUsernameToPreferences(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply(); // Save the username to preferences
    }
    /*
    This function handles the navigation between the auth and the main activity, setting up the the context to be passed throughout the app
     */
    private void navigate() {
        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest"); // Default to "Guest" if not found

        // Create a dynamic title
        String dynamicTitle = getString(R.string.app_name) + " - " + username;

        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.putExtra("dynamicTitle", dynamicTitle); // Pass the dynamic title to MainActivity
        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the themePreference field from the Firestore document
                        String themePreference = task.getResult().getDocuments().get(0).getString("themePreference");
                        Toast.makeText(AuthActivity.this, themePreference, Toast.LENGTH_LONG).show();

                        intent.putExtra("username", username); // Pass the theme preference to MainActivity
                        if (themePreference != null) {
                            Toast.makeText(AuthActivity.this, "Not NULL", Toast.LENGTH_LONG).show();
                            intent.putExtra("themePreference", themePreference); // Pass the theme preference to MainActivity
                        } else {
                            // Assign a default value if themePreference is null
                            Toast.makeText(AuthActivity.this, "NULL", Toast.LENGTH_LONG).show();
                            intent.putExtra("themePreference", "Default");
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Failed to retrieve theme preference.", Toast.LENGTH_LONG).show();
                        Log.d("Firebase", "Error: " + task.getException());
                    }
                });
//        intent.putExtra("themePreference", themePreference); // pass the themepreference we retreieved from firebase
//        startActivity(intent);
//        finish();
    }

}
