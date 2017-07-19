package com.github.randoapp.test.activity;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.randoapp.AuthActivity;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthActivityTest {

    @Rule
    public ActivityTestRule<AuthActivity> mActivityRule = new ActivityTestRule<>(
            AuthActivity.class);

    @Before
    public void setup(){
        //mActivityRule.
    }

    @Test
    public void shouldAllElementsPresent() {
        //this button is not present on emulator
        //onView(withId(R.id.google_sign_in_button)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewSkipLink)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewContactUsLink)).check(matches(isDisplayed()));
    }

    //@Test
    public void shouldLoginWithSkip() {
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).perform(typeText("test@test.com"));
        onView(withId(R.id.signupButton)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
    }
}