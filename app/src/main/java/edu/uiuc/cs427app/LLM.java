package edu.uiuc.cs427app;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LLM {
    private static final String TAG = "LLM";

    public static void LLMAdvice(String inputPrompt, LLMResponseCallback callback) {
        Log.d(TAG, "Starting LLMAdvice method.");

        // Replace this with a secure method to retrieve the API key
        final String apiKey = "AIzaSyAl2gTMR3f-7X1w9NFeJ2yiaUD11A7pFSw"; // Use a secure method in production

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        OkHttpClient client = new OkHttpClient();

        String json;
        try {
            // Construct JSON request body as per the API requirement
            JSONObject jsonObject = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", inputPrompt);

            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", new JSONArray().put(partsObject));
            contentsArray.put(contentObject);

            jsonObject.put("contents", contentsArray);

            json = jsonObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "JSON creation failed: ", e);
            callback.onFailure("ERROR: JSON creation failed");
            return;
        }

        Log.d(TAG, "Constructed JSON request body: " + json);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Log.d(TAG, "HTTP request built successfully.");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "HTTP request failed: " + e.getMessage(), e);
                callback.onFailure("ERROR: IOException occurred.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Received successful response: " + responseBody);

                    // Extract the text content from the JSON response
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidatesArray = jsonResponse.getJSONArray("candidates");
                        if (candidatesArray.length() > 0) {
                            JSONObject candidate = candidatesArray.getJSONObject(0);
                            JSONObject content = candidate.getJSONObject("content");
                            JSONArray partsArray = content.getJSONArray("parts");
                            if (partsArray.length() > 0) {
                                String textResponse = partsArray.getJSONObject(0).getString("text");
                                callback.onSuccess(textResponse);
                                return;
                            }
                        }
                        callback.onFailure("ERROR: No text response found.");
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse JSON response: ", e);
                        callback.onFailure("ERROR: Failed to parse JSON response.");
                    }
                } else {
                    Log.e(TAG, "Request failed with code: " + response.code());
                    callback.onFailure("ERROR: Request failed with code " + response.code());
                }
            }
        });
    }

    public interface LLMResponseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
