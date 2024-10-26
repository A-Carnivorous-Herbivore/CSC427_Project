package edu.uiuc.cs427app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        checkDbConnection();
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        // Set click listeners for buttons
        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> signupUser());
    }



    private boolean dbConnected = false;
    private void checkDbConnection() {
        // Attempt to read a non-existing document
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
    private void loginUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Check if user exists in Firestore (for demonstration; ideally, use Firebase Auth for real auth)
        db.collection("users").whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(AuthActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthActivity.this, "Login failed: Incorrect credentials", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signupUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Store user data in Firestore
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(AuthActivity.this, "Account created", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AuthActivity.this, "Sign-up failed: "+ e, Toast.LENGTH_LONG).show());
    }
}
