package edu.uiuc.cs427app;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class AuthActivity extends AppCompatActivity {
    private MongoDBClient mongoDBClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize MongoDB Client
        mongoDBClient = MongoDBClient.getInstance(this);

        // Fetch user by username
        fetchUserByUsername("exampleUser");
    }

    private void fetchUserByUsername(String username) {
        MongoDatabase database = mongoDBClient.getMongoClient().getDatabase("AndroidApp");
        MongoCollection<Document> usersCollection = database.getCollection("users");

        // Find the user with the specified username
        Document user = usersCollection.find(eq("username", username)).first();

        if (user != null) {
            // User found, show a toast or perform any action
            String email = user.getString("email"); // Example: fetch the email
            Toast.makeText(this, "User found: " + email, Toast.LENGTH_SHORT).show();
        } else {
            // User not found
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
