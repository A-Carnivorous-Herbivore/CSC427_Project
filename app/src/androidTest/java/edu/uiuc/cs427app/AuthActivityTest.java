package edu.uiuc.cs427app;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.espresso.action.ViewActions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;


import androidx.test.espresso.IdlingPolicies;

import java.util.concurrent.TimeUnit;

/**
 * Test class for AuthActivity.
 */
@RunWith(AndroidJUnit4.class)
public class AuthActivityTest {

    @Rule
    public ActivityScenarioRule<AuthActivity> activityRule = new ActivityScenarioRule<>(AuthActivity.class);

    @Before
    public void setUp() {
        Intents.init();

        // Increase the IdlingResource timeout to wait for asynchronous operations
        IdlingPolicies.setIdlingResourceTimeout(10, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLoginSuccess() {
        // Enter valid credentials
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText("testuser"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText("password123"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Check that MainActivity is started
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testLoginFailure() {
        // Enter invalid credentials
        onView(ViewMatchers.withId(R.id.usernameInput))
                .perform(ViewActions.typeText("wronguser"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.passwordInput))
                .perform(ViewActions.typeText("wrongpassword"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Wait for Toast message to appear
//        onView(withText("Login failed: Incorrect credentials"))
//                .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
//                .check(matches(ViewMatchers.isDisplayed()));
    }
}
