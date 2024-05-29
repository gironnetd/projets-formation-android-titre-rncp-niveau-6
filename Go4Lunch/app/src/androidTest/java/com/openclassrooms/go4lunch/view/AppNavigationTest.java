package com.openclassrooms.go4lunch.view;

import android.app.Activity;

import androidx.appcompat.widget.Toolbar;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;
import com.openclassrooms.go4lunch.view.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        launch(MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void main_activity_display_map_fragment_when_launched() {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the 'Map View' button
        onView(withId(R.id.navigation_map_view)).perform(click());

        // THEN : the map fragment is displayed
        onView(withId(R.id.fragment_map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void main_activity_display_map_fragment_when_click_on_bottom_navigation_view() {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the 'List View' button
        onView(withId(R.id.navigation_list_view)).perform(click());

        // THEN : the list fragment is displayed
        onView(withId(R.id.fragment_list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void main_activity_display_workmate_fragment_when_click_on_bottom_navigation_view() {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the 'Workmates' button
        onView(withId(R.id.navigation_workmates)).perform(click());

        // THEN : the workmate fragment is displayed
        onView(withId(R.id.fragment_workmates)).check(matches(isDisplayed()));
    }

    @Test
    public void click_on_android_home_icon_open_navigation_view() {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the android home icon
        Toolbar toolbar = getActivityInstance().findViewById(R.id.toolbar);
        onView(withContentDescription((String) toolbar.getNavigationContentDescription())).perform(click());

        // THEN : the navigation drawer is displayed
        onView(withId(R.id.nav_header)).check(matches(isDisplayed()));
    }

    @Test
    public void click_on_search_icon_then_display_search_view() {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the search icon
        onView(withId(R.id.search)).perform(click());

        // THEN : the search view is displayed
        onView(withId(R.id.cv_search_view)).check(matches(isDisplayed()));

        // AND WHEN : the user click on press back
        pressBack();

        // THEN : the search view is not displayed
        onView(withId(R.id.cv_search_view)).check(matches(not(isDisplayed())));
    }

    Activity currentActivity = null;

    public Activity getActivityInstance() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
            }
        });
        return currentActivity;
    }
}