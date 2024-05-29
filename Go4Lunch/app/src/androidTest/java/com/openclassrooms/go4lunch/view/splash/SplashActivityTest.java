package com.openclassrooms.go4lunch.view.splash;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(
            SplashActivity.class);

    UiDevice uiDevice;

    @Before
    public void setUp() throws Exception {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void launch_authentication_after_allow_location_permission() throws UiObjectNotFoundException {
        // GIVEN : location is not granted , the dialog box is diplayed
        UiObject allowButton = this.uiDevice.findObject(new UiSelector().text("ALLOW"));

        // WHEN : ALLOW button is clicked
        allowButton.click();

        // THEN : the Authentication Activity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.authentication_layout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        mActivityRule.getActivity().finish();
    }

    @Test
    public void not_launch_authentication_after_allow_location_permission() throws UiObjectNotFoundException {
        // GIVEN : location is not granted , the dialog box is diplayed
        UiObject deniedButton = this.uiDevice.findObject(new UiSelector().text("DENY"));

        // WHEN : DENY button is clicked
        deniedButton.click();

        // THEN : the Authentication Activity is not displayed
        Espresso.onView(ViewMatchers.withId(R.id.authentication_layout))
                .check(ViewAssertions.doesNotExist());

        mActivityRule.getActivity().finish();
    }
}