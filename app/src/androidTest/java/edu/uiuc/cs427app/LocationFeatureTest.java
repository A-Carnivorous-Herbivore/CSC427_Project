package edu.uiuc.cs427app;

import java.util.Random;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.test.espresso.IdlingPolicies;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocationFeatureTest {
    private ActivityScenario<AuthActivity> scenario;

    @Before
    public void setUp() {
        // Increase the IdlingResource timeout to wait for asynchronous operations
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS);

        // Launch the activity before each test
        scenario = ActivityScenario.launch(AuthActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }
    //This checks if the camera latitude and longitude match the location of the city of interest
    private ViewAction checkCameraPosition(LatLng expectedLatLng) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed(); // Ensures the map view is visible
            }

            @Override
            public String getDescription() {
                return "Check if the map camera is centered on " + expectedLatLng.toString();
            }

            @Override
            public void perform(UiController uiController, View view) {
                SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity) view.getContext())
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                if (mapFragment != null) {
                    mapFragment.getMapAsync(googleMap -> {
                        LatLng actualLatLng = googleMap.getCameraPosition().target;

                        // Increase tolerance for floating-point comparisons
                        double tolerance = 0.1;
                        assertEquals(expectedLatLng.latitude, actualLatLng.latitude, tolerance);
                        assertEquals(expectedLatLng.longitude, actualLatLng.longitude, tolerance);
                    });
                } else {
                    throw new AssertionError("MapFragment is null or not initialized.");
                }
            }
        };
    }

    // Verification test for Paris feature.
    @Test
    public void testCity_Paris() {
        testCity("Paris", new LatLng(48.8566, 2.3522)); // Coordinates for Paris
    }

    // Verification test for Toronto feature.
    @Test
    public void testCity_Toronto() {
        testCity("Toronto", new LatLng(43.651070, -79.347015)); // Coordinates for Toronto
    }

    // Helper method to test cities; this tests if the city is shown expectedly in the map
    private void testCity(String cityName, LatLng expectedLatLng) {
        onView(withId(R.id.usernameInput))
                .perform(typeText("Rudy"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput))
                .perform(typeText("1234"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(500); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(
                withText("SHOW DETAILS"),
                hasSibling(withText(cityName))
        )).perform(click());

        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("SHOW MAP")).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.map)).perform(checkCameraPosition(expectedLatLng));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
