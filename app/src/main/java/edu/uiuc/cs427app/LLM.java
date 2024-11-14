package edu.uiuc.cs427app;

import android.util.Log; // Import for Logcat
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;

public class LLM {
    private static final String TAG = "LLM";

    public static String LLMAdvice(String inputPrompt) {
        Log.d(TAG, "Starting LLMAdvice method.");

        OkHttpClient client = new OkHttpClient();

        String apiKey = "YOUR_API_KEY"; // Replace with your secure API key
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + apiKey;

        // Log inputPrompt to verify input
        Log.d(TAG, "Received inputPrompt: " + inputPrompt);

        // JSON request body
        String json = "{"
                + "\"model\": \"gemini-1.5-pro\","
                + "\"prompt\": \"" + inputPrompt + "\""
                + "}";

        Log.d(TAG, "Constructed JSON request body: " + json);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Log.d(TAG, "HTTP request built successfully.");

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "HTTP request sent.");
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d(TAG, "Received successful response: " + responseBody);
                System.out.println(responseBody);
                return responseBody;
            } else {
                Log.e(TAG, "Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred while executing the request: " + e.getMessage(), e);
            e.printStackTrace();
        }

        Log.e(TAG, "Returning ERROR due to failure.");
        return "ERROR";
    }
}
