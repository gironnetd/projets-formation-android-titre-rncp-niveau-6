package com.openclassrooms.go4lunch.view.detail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.utilities.ServiceLocator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.Constants.RESTAURANT_DETAIL_ID;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailActivityTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityTestRule<DetailActivity> mainActivityActivityTestRule
            = new ActivityTestRule<>(DetailActivity.class, false, false);

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;

    List<Place> places;
    int position = 1;
    Place place;

    @Before
    public void setUp() throws Exception {
        userRepository = ServiceLocator.provideUserRepository(getApplicationContext());
        placeRepository = ServiceLocator.providePlaceRepository(getApplicationContext());

        // make sure user is authenticated
        userAuthenticated = userRepository.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        places = placeRepository.findPlaces().blockingGet();
        placeRepository.savePlaces(userAuthenticated.getUid()).blockingGet();
        place = places.get(position);

        // GIVEN : Launch activity with the place id in extras of the Intent
        Intent intent = new Intent();
        intent.putExtra(RESTAURANT_DETAIL_ID, place.getPlaceId());
        mainActivityActivityTestRule.launchActivity(intent);

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteUser(userAuthenticated, true).blockingGet();
        places = null;
        place = null;
        placeRepository = null;
        userRepository = null;
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());

    }

    @Test
    public void when_click_on_recycler_view_item_then_open_corresponding_place() {
        // THEN : the Detail activity display the right name and address
        onView(withId(R.id.tv_restaurant_name)).check(matches(withText(place.getName())));
        onView(withId(R.id.tv_restaurant_address)).check(matches(withText(place.getAddress())));
    }

    @Test
    public void when_click_on_floating_action_button_then_workmate_is_remove() {
        // GIVEN : the user choose the place
        userRepository.updateUser(userAuthenticated, null).blockingGet();

        onView(withId(R.id.fab_restaurant_joining)).perform(click());

        RecyclerView workmateJoiningList = getActivityInstance()
                .findViewById(R.id.workmate_recycler_view);
        int itemCount = workmateJoiningList.getAdapter().getItemCount();

        // WHEN : user click on the button to remove his choice
        onView(withId(R.id.fab_restaurant_joining)).perform(click());

        // THEN : his name is remove from the list
        int newItemCount = workmateJoiningList.getAdapter().getItemCount();
        assertThat(newItemCount).isEqualTo((itemCount - 1));
    }

    @Test
    public void when_click_on_floating_action_button_then_workmate_is_joining() {
        // GIVEN : the user has not chosen place
        userRepository.updateUser(userAuthenticated, null).blockingGet();

        // WHEN : the user click on button to choose it
        onView(withId(R.id.fab_restaurant_joining)).perform(click());

        RecyclerView workmateJoiningList = getActivityInstance()
                .findViewById(R.id.workmate_recycler_view);

        // THEN : the user is added to the list of workmates
        Assert.assertThat(Objects.requireNonNull(Objects.requireNonNull(workmateJoiningList.getAdapter()))
                .getItemCount(), greaterThanOrEqualTo(1));

        placeRepository.removeUser(userAuthenticated).blockingGet();
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