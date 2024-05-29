package com.openclassrooms.go4lunch.view.main.workmate;

import android.Manifest;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.utilities.ServiceLocator;
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.utitlities.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class WorkmateFragmentTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;

    List<Place> places;

    RecyclerView workmateList;

    UiDevice uiDevice;

    @Before
    public void setUp() {
        userRepository = ServiceLocator.provideUserRepository(getApplicationContext());
        placeRepository = ServiceLocator.providePlaceRepository(getApplicationContext());
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());

        // make sure user is authenticated
        userAuthenticated = userRepository.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        places = placeRepository.findPlaces().blockingGet();
        placeRepository.savePlaces(userAuthenticated.getUid()).blockingGet();
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        userRepository.deleteUser(userAuthenticated, true).blockingGet();
        uiDevice = null;
    }

    @Test
    public void when_launch_main_activity_then_display_workmates_list() throws InterruptedException, UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched

        // WHEN : the user click on the 'Workmates' button
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_workmates);

        UiObject workmatesViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        workmatesViewButton.click();

        // THEN : the workmate fragment is displayed with his list containing ar least one workmate
        Thread.sleep(3000);
        onView(withId(R.id.workmate_recycler_view)).check(matches(isDisplayed()));

        NavHostFragment navHostFragment = (NavHostFragment) mainActivityActivityTestRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        WorkmateFragment workmateFragment = (WorkmateFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        workmateList = workmateFragment.workmateRecyclerView;

        // Check that it contains more than one element
        assertThat(Objects.requireNonNull(Objects.requireNonNull(workmateList.getAdapter()))
                .getItemCount(), greaterThanOrEqualTo(1));
    }

    @Test
    public void click_on_workmate_with_midday_restaurant_then_launch_detail_activity() throws InterruptedException, UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched with one workmate that has chosen a restaurant
        userRepository.updateUser(userAuthenticated, places.get(0)).blockingGet();

        // WHEN : the user click on the 'Workmates' button and click on the item of workmates
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_workmates);

        UiObject workmatesViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        workmatesViewButton.click();

        Thread.sleep(3000);
        onView(withId(R.id.workmate_recycler_view)).check(matches(isDisplayed()));

        // THEN : the Detail activity is launched
        Intents.init();
        onView(TestUtils.withRecyclerView(R.id.workmate_recycler_view).atPositionOnView(0, R.id.tv_workmate_joining))
                .perform(click());

        Intents.intended(IntentMatchers.hasComponent(DetailActivity.class.getName()));
        Intents.release();
    }
}