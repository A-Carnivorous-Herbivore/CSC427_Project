package edu.uiuc.cs427app;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.IdlingPolicies;

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
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.SECONDS);

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
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText("Rudy"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText("1234"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Assert that MainActivity is displayed by checking for a unique view
        onView(ViewMatchers.withId(R.id.buttonLogout)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testUserLoginFailure() {
        // Enter invalid credentials
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText("wronguser"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText("wrongpassword"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Assert that the error message is displayed
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(ViewMatchers.withId(R.id.loginButton)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSignUpSuccess() {

        // Enter valid sign-up information
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText("newuser"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText("newpassword"), ViewActions.closeSoftKeyboard());

        onView(ViewMatchers.withId(R.id.signupButton)).perform(ViewActions.click());
        // Wait for any asynchronous operations to complete
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that MainActivity is displayed by checking for a unique view
        onView(ViewMatchers.withId(R.id.buttonLogout)).check(matches(ViewMatchers.isDisplayed()));
    }

}

