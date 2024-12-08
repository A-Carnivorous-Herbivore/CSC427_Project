package edu.uiuc.cs427app;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.intent.Intents;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class WeatherTest {

    @Before
    public void setUp() {
        Intents.init();
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS);
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

    @Test
    public void testShowDetailsButtonClick() {
        ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class);
        onView(withId(R.id.usernameInput))
                .perform(typeText("Becky"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());


        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(allOf(
                withText("Show Details"),
                hasSibling(withText("Chicago")) // Ensures the button is associated with the city "Chicago"
        ))
                .perform(click());

        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Chicago Weather Details")).check(matches(isDisplayed()));

        scenario.close();
    }

    @Test
    public void weatherInsightsTest() {
        ActivityScenario<AuthActivity> scenario = ActivityScenario.launch(AuthActivity.class);
        onView(withId(R.id.usernameInput))
                .perform(typeText("Becky"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());


        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(allOf(
                withText("Show Details"),
                hasSibling(withText("Chicago")) // Ensures the button is associated with the city "Chicago"
        ))
                .perform(click());

        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.insightButton)).perform(click());

        try {
            Thread.sleep(500); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // check if the title is displayed
        onView(withId(R.id.insightTitle)).check(matches(isDisplayed()));

        // check if buttons are displayed
        onView(withId(R.id.questionButton1)).check(matches(isDisplayed()));
        onView(withId(R.id.questionButton2)).check(matches(isDisplayed()));

        // check if responsse are displayed
        onView(withId(R.id.responseTextView1)).check(matches(isDisplayed()));
        onView(withId(R.id.responseTextView2)).check(matches(isDisplayed()));
    }

}
