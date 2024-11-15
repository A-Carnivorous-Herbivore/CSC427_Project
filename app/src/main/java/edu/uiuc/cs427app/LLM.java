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

    public static void generateQuestions(String weatherPrompt, LLMResponseCallback callback) {
        Log.d(TAG, "Starting generateQuestions method.");

        // Replace this with a secure method to retrieve the API key
        final String apiKey = "AIzaSyAl2gTMR3f-7X1w9NFeJ2yiaUD11A7pFSw"; // Use a secure method in production

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        OkHttpClient client = new OkHttpClient();

        String json;
        try {
            // Construct JSON request body
            JSONObject jsonObject = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", "Today's weather is: " + weatherPrompt +
                    ". Please generate two context-specific questions users might ask to help them make decisions about their day. " +
                    "Here is an example of questions and the format you should return:\n" +
                    "1. What should I wear today?\n" +
                    "2. What should I prepare for an outdoor event?\n" +
                    "Make sure the two questions have a 1. or 2., and that they are separated by a newline. Do not include any other text.");

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

                    // Extract questions from the JSON response
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidatesArray = jsonResponse.getJSONArray("candidates");
                        if (candidatesArray.length() > 0) {
                            JSONObject candidate = candidatesArray.getJSONObject(0);
                            JSONObject content = candidate.getJSONObject("content");
                            JSONArray partsArray = content.getJSONArray("parts");
                            if (partsArray.length() > 0) {
                                String questionsText = partsArray.getJSONObject(0).getString("text");

                                // Parse the questions based on "1." and "2."
                                String[] questions = questionsText.split("\\n");
                                if (questions.length >= 2) {
                                    String question1 = questions[0].replaceFirst("^1\\.\\s*", "").trim();
                                    String question2 = questions[1].replaceFirst("^2\\.\\s*", "").trim();
                                    callback.onSuccess(question1 + "\n" + question2);
                                    return;
                                }
                            }
                        }
                        callback.onFailure("ERROR: Could not parse questions from response.");
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

    public static void queryResponse(String question, String weatherPrompt, LLMResponseCallback callback) {
        Log.d(TAG, "Starting getResponseForQuestion method.");

        // Replace this with a secure method to retrieve the API key
        final String apiKey = "AIzaSyAl2gTMR3f-7X1w9NFeJ2yiaUD11A7pFSw"; // Use a secure method in production

        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        OkHttpClient client = new OkHttpClient();

        String json;
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", "Today's weather is: " + weatherPrompt +
                    ". Based on this, here is the question: " + question +
                    ". Please generate a helpful and informative response to this question.");

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

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

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

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidatesArray = jsonResponse.getJSONArray("candidates");
                        if (candidatesArray.length() > 0) {
                            JSONObject candidate = candidatesArray.getJSONObject(0);
                            JSONObject content = candidate.getJSONObject("content");
                            JSONArray partsArray = content.getJSONArray("parts");
                            if (partsArray.length() > 0) {
                                String responseText = partsArray.getJSONObject(0).getString("text").trim();
                                callback.onSuccess(responseText);
                                return;
                            }
                        }
                        callback.onFailure("ERROR: Could not parse response.");
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
        void onSuccess(String response); // Two questions as a newline-separated string
        void onFailure(String error);
    }
}
