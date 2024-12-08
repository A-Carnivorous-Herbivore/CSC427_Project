package edu.uiuc.cs427app;

import java.util.Random;

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

/**
 * Test class for AuthActivity.
 */
@RunWith(AndroidJUnit4.class)

public class AddAndRemoveCity {


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
    @Test
    public void testAddCity() {
        // Find the "Remove" button that is a sibling of the TextView displaying "Tianjin"
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

        // Open the add location dialog
        onView(withId(R.id.buttonAddLocation)).perform(click());
        onView(withId(R.id.city_input))
                .perform(typeText("Tianjin"));

        onView(withText("Tianjin")) // Ensure the text matches the dropdown item
                .perform(click());
        try {
            Thread.sleep(500); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print the content of the city_input view


        // Click the add button
        onView(withId(R.id.add_button)).perform(click());
        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        onView(allOf(withText("REMOVE"), hasSibling(withText("Tianjin"))))
//                .perform(click());
        String cityName = "Tianjin";
        String buttonDescription = cityName + "Button";
        // check exists
        onView(withText(cityName))
                .check(matches(isDisplayed()));




    }
    @Test
    // Add city "Tianjin" and check whether it is displayed, then delete it check it is not displayed
    public void testRemoveCity() {
        // Find the "Remove" button that is a sibling of the TextView displaying "Tianjin"
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

        // Open the add location dialog

//        onView(allOf(withText("REMOVE"), hasSibling(withText("Tianjin"))))
//                .perform(click());
        String cityName = "Tianjin";
        String buttonDescription = cityName + "Button";
        // check exists
        onView(withText(cityName))
                .check(matches(isDisplayed()));
        // Perform a click on the dynamically generated Remove button
        onView(withContentDescription(buttonDescription))
                .perform(click());
        try {
            Thread.sleep(5000); // Wait for 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Verify that "Tianjin" is no longer displayed on the screen
        onView(withText(cityName))
                .check(doesNotExist());



    }

}
//
