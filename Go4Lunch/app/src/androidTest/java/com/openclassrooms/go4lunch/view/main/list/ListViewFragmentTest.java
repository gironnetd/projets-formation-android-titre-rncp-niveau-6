package com.openclassrooms.go4lunch.view.main.list;

import android.Manifest;

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

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ListViewFragmentTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class,
            false,
            true);

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;
    List<Place> places;

    final int POSITION = 1;

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
    public void when_launch_main_activity_then_display_restaurant_list() throws InterruptedException, UiObjectNotFoundException {
        // GIVEN : Main Activity is launched

        // WHEN : the user click on the 'List View' button
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_list_view);

        UiObject listViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        listViewButton.click();

        // THEN : the list view fragment is displayed with his list
        Thread.sleep(3000);
        onView(withId(R.id.restaurant_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void click_on_item_in_recycler_view_launch_detail_activity() throws InterruptedException, UiObjectNotFoundException {
        // GIVEN : Main Activity is launched

        // WHEN : the user click on the 'List View' button and he click on an item of the list
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_list_view);

        UiObject listViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        listViewButton.click();

        onView(withId(R.id.restaurant_recycler_view)).check(matches(isDisplayed()));

        Thread.sleep(3000);
        Intents.init();
        onView(TestUtils.withRecyclerView(R.id.restaurant_recycler_view)
                .atPositionOnView(POSITION, R.id.item_list_view))
                .perform(click());

        // THEN : the Detail Activity is launched
        Intents.intended(IntentMatchers.hasComponent(DetailActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void when_click_on_recycler_view_item_then_open_corresponding_place() throws InterruptedException, UiObjectNotFoundException {
        // GIVEN : Main Activity is launched

        // WHEN : the user click on the 'List View' button and he click on an item of the list
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_list_view);

        UiObject listViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        listViewButton.click();

        onView(withId(R.id.restaurant_recycler_view)).check(matches(isDisplayed()));

        Thread.sleep(3000);
        Intents.init();
        onView(TestUtils.withRecyclerView(R.id.restaurant_recycler_view)
                .atPositionOnView(POSITION, R.id.item_list_view))
                .perform(click());

        // THEN : the detail activity is launched with the right name and address
        Intents.intended(IntentMatchers.hasComponent(DetailActivity.class.getName()));
        Intents.release();

        Place place = places.get(POSITION);

        onView(withId(R.id.tv_restaurant_name)).check(matches(withText(place.getName())));
        onView(withId(R.id.tv_restaurant_address)).check(matches(withText(place.getAddress())));
    }
}