package edu.uiuc.cs427app;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WeatherTest {
    @Rule
    public ActivityScenarioRule<DetailsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(DetailsActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private boolean isTextViewEmpty(int viewId) {
        final String[] text = {null};
        onView(withId(viewId)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            if (view instanceof TextView) {
                text[0] = ((TextView) view).getText().toString();
            } else {
                throw new IllegalStateException("View is not a TextView");
            }
        });
        return text[0] == null || text[0].isEmpty();
    }

    @Test
    public void testWeatherDisplayForCity1() {
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                DetailsActivity.class
        );
        intent.putExtra("city", "Chicago");
        intent.putExtra("themePreference", "Light");

        ActivityScenario<DetailsActivity> scenario = launch(intent);
        assertNotNull(scenario);

        onView(withId(R.id.cityName)).check(matches(withText("Chicago Weather Details")));
        onView(withId(R.id.time)).check(matches(withSubstring("Date and time:")));
        onView(withId(R.id.temperature)).check(matches(withSubstring("Temperature: ")));
        onView(withId(R.id.humidity)).check(matches(withSubstring("Humidity: ")));
        onView(withId(R.id.description)).check(matches(withSubstring("Weather: ")));
        onView(withId(R.id.windCondition)).check(matches(withSubstring("Wind condition: speed = ")));
    }

    @Test
    public void testWeatherDisplayForCity2() {
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                DetailsActivity.class
        );
        intent.putExtra("city", "New York");
        intent.putExtra("themePreference", "Light");

        ActivityScenario<DetailsActivity> scenario = launch(intent);
        assertNotNull(scenario);

        onView(withId(R.id.cityName)).check(matches(withText("New York Weather Details")));
        onView(withId(R.id.time)).check(matches(withSubstring("Date and time:")));
        onView(withId(R.id.temperature)).check(matches(withSubstring("Temperature: ")));
        onView(withId(R.id.humidity)).check(matches(withSubstring("Humidity: ")));
        onView(withId(R.id.description)).check(matches(withSubstring("Weather: ")));
        onView(withId(R.id.windCondition)).check(matches(withSubstring("Wind condition: speed = ")));
    }

    @Test
    public void testWeatherDisplayForCity1FailureConditions() {
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                DetailsActivity.class
        );
        intent.putExtra("city", "Chicago");
        intent.putExtra("themePreference", "Light");

        ActivityScenario<DetailsActivity> scenario = launch(intent);
        assertNotNull(scenario);

        onView(withId(R.id.cityName)).check(matches(not(withText("New York Weather Details"))));

        assertFalse("Temperature details missing",
                isTextViewEmpty(R.id.temperature));

        onView(withId(R.id.humidity)).check(matches(not(withText("Humidity: 0%"))));

        assertFalse("Weather description missing",
                isTextViewEmpty(R.id.description));
    }

    @Test
    public void testWeatherDisplayForCity2FailureConditions() {
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                DetailsActivity.class
        );
        intent.putExtra("city", "New York");
        intent.putExtra("themePreference", "Light");

        ActivityScenario<DetailsActivity> scenario = launch(intent);
        assertNotNull(scenario);

        onView(withId(R.id.cityName)).check(matches(not(withText("Chicago Weather Details"))));

        assertFalse("Wind condition details missing",
                isTextViewEmpty(R.id.windCondition));
    }
}
