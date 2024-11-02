package edu.uiuc.cs427app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

public class PersonalizeLayoutActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Spinner themeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_layout);

        db = FirebaseFirestore.getInstance();
//        themeSpinner = findViewById(R.id.themeSpinner);

        Button changeThemeButton = findViewById(R.id.buttonChangeTheme);
        changeThemeButton.setOnClickListener(v -> changeThemePreference());
    }

    private void changeThemePreference() {
        final String[] themes = {"Default", "Dark", "Light", "Blue"};

        new AlertDialog.Builder(this)
                .setTitle("Select Theme")
                .setItems(themes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedTheme = themes[which];
                        saveThemePreference(selectedTheme);
                        Toast.makeText(PersonalizeLayoutActivity.this, "Theme set to " + selectedTheme, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void saveThemePreference(String themePreference) {
        String username = getIntent().getStringExtra("username");
        Toast.makeText(PersonalizeLayoutActivity.this, username, Toast.LENGTH_SHORT).show();
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        db.collection("users").document(documentId)
                                .update("themePreference", themePreference)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(PersonalizeLayoutActivity.this, "Theme preference updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(PersonalizeLayoutActivity.this, "Failed to update theme preference: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(PersonalizeLayoutActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(PersonalizeLayoutActivity.this, "Failed to find user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

