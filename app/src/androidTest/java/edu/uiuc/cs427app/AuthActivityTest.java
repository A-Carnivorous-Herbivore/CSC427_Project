package edu.uiuc.cs427app;
import java.util.Random;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.test.espresso.IdlingPolicies;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Test class for AuthActivity.
 */
@RunWith(AndroidJUnit4.class)
public class AuthActivityTest {

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
    public void testUserLoginSuccess() {
        // Enter valid credentials
        onView(withId(R.id.usernameInput))
                .perform(typeText("Rudy"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput))
                .perform(typeText("1234"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Assert that MainActivity is displayed by checking for a unique view
        onView(withId(R.id.buttonLogout)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testUserLoginFailure() {
        // Enter invalid credentials
        onView(withId(R.id.usernameInput))
                .perform(typeText("wronguser"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Assert that the error message is displayed
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.loginButton)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSignUpSuccess() {
        String username = "tempUser";
        String password = "tempPass";

        // Enter sign-up information
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText(username), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard());

        // Click the sign-up button
        onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);
        user.put("themePreference", "default");

    // Wait for asynchronous operations to complete
        try {
            Thread.sleep(5000); // Allow Firestore operations to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that MainActivity is displayed
        onView(ViewMatchers.withId(R.id.buttonLogout)).check(matches(ViewMatchers.isDisplayed()));

        // Clean up: Delete the test user from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        db.collection("users").document(documentId).delete()
                                .addOnSuccessListener(aVoid -> Log.d("Test", "Temporary test user deleted successfully."))
                                .addOnFailureListener(e -> Log.e("Test", "Failed to delete temporary test user: " + e));
                    } else {
                        Log.e("Test", "Temporary test user not found for deletion.");
                    }
                });
    }


}

