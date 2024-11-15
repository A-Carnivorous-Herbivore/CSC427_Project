package edu.uiuc.cs427app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherInsightActivity extends AppCompatActivity {

    private static final String TAG = "WeatherInsightActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insight);

        // Get the inputPrompt from the Intent
        String inputPrompt = getIntent().getStringExtra("inputPrompt");

        // Display the inputPrompt
        TextView insightTitle = findViewById(R.id.insightTitle);

        // Find buttons in the layout
        Button questionButton1 = findViewById(R.id.questionButton1);
        Button questionButton2 = findViewById(R.id.questionButton2);

        // Find TextViews where the responses will be displayed
        TextView responseTextView1 = findViewById(R.id.responseTextView1);
        TextView responseTextView2 = findViewById(R.id.responseTextView2);

        // Call the LLM class to generate questions
        LLM.generateQuestions(inputPrompt, new LLM.LLMResponseCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "LLM Response received: " + response);

                // Parse the questions from the LLM response
                String[] questions = response.split("\n");
                if (questions.length >= 2) {
                    // Update buttons with the questions
                    runOnUiThread(() -> {
                        questionButton1.setText(questions[0]);
                        questionButton2.setText(questions[1]);

                        // Set click listeners for the buttons
                        questionButton1.setOnClickListener(v -> {
                            Toast.makeText(WeatherInsightActivity.this, "Question 1 clicked: " + questions[0], Toast.LENGTH_SHORT).show();

                            // Call the LLM to get the response for question 1
                            LLM.queryResponse(inputPrompt, questions[0], new LLM.LLMResponseCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    // Display the response underneath the button
                                    runOnUiThread(() -> responseTextView1.setText(response));
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> responseTextView1.setText("Error: " + error));
                                }
                            });
                        });

                        questionButton2.setOnClickListener(v -> {
                            Toast.makeText(WeatherInsightActivity.this, "Question 2 clicked: " + questions[1], Toast.LENGTH_SHORT).show();

                            // Call the LLM to get the response for question 2
                            LLM.queryResponse(inputPrompt, questions[1], new LLM.LLMResponseCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    // Display the response underneath the button
                                    runOnUiThread(() -> responseTextView2.setText(response));
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> responseTextView2.setText("Error: " + error));
                                }
                            });
                        });
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(WeatherInsightActivity.this, "Failed to parse questions.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "LLM Request failed: " + error);
                runOnUiThread(() -> Toast.makeText(WeatherInsightActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
