package edu.uiuc.cs427app;
import android.content.Context;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDBClient {
    private static MongoDBClient instance;
    private MongoClient mongoClient;

    // Private constructor to prevent instantiation
    private MongoDBClient(Context context) {
        String connectionString = "mongodb+srv://<username>:<password>@cluster.mongodb.net/test"; // Update with your connection string
        mongoClient = MongoClients.create(connectionString);
    }

    // Get the singleton instance
    public static synchronized MongoDBClient getInstance(Context context) {
        if (instance == null) {
            instance = new MongoDBClient(context.getApplicationContext());
        }
        return instance;
    }

    // Method to get the MongoClient
    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
