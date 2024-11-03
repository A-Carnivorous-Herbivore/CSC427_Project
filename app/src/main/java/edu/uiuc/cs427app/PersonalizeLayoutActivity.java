package edu.uiuc.cs427app;
import android.content.Intent;
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
/*
This on create switches to the desired theme and connects to the database
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String themePreference = getIntent().getStringExtra("theme");
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
                default:
                    setTheme(R.style.Theme_MyFirstApp_Default);
                    break;
            }
        } else {
            setTheme(R.style.Theme_MyFirstApp_Default); // Fallback to default if themePreference is null
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_layout);

        db = FirebaseFirestore.getInstance();
//        themeSpinner = findViewById(R.id.themeSpinner);

        Button changeThemeButton = findViewById(R.id.buttonChangeTheme);
        changeThemeButton.setOnClickListener(v -> changeThemePreference());
    }
    /*
    This changeThemePreference manages the setting of themes and passes it to other activities to be enforced
     */
    private void changeThemePreference() {
        final String[] themes = {"Default", "Dark", "Light", "Blue"};

        new AlertDialog.Builder(this)
                .setTitle("Select Theme")
                .setItems(themes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedTheme = themes[which];
                        saveThemePreference(selectedTheme);

                        Intent intent = new Intent(PersonalizeLayoutActivity.this, MainActivity.class);
                        intent.putExtra("themePreference", selectedTheme);
                        String dynamicTitle = getIntent().getStringExtra("dynamicTitle");
                        intent.putExtra("dynamicTitle", dynamicTitle); // Example if you have a dynamic title
                        String username = getIntent().getStringExtra("username");
                        intent.putExtra("username", username); // Optional, if you want to pass the username



//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("newThemePreference", selectedTheme);
//                        setResult(RESULT_OK, resultIntent);

                        Toast.makeText(PersonalizeLayoutActivity.this, "Theme set to " + selectedTheme, Toast.LENGTH_SHORT).show();

                        // Start a new instance of MainActivity
                        startActivity(intent);

                        // Finish PersonalizeLayoutActivity to prevent returning here
                        finish();
                    }
                })
                .show();
    }
/*
This saves the theme preference to the database so that the next time a user logs in they will be greeted by the same theme
 */
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

