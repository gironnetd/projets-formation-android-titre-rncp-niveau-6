package com.openclassrooms.go4lunch.view.main.map;

import android.Manifest;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
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
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;
import com.openclassrooms.go4lunch.utilities.ServiceLocator;
import com.openclassrooms.go4lunch.view.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class MapViewFragmentTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule
            = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;

    List<Place> places;

    UiDevice uiDevice;

    @Before
    public void setUp() {

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        userRepository = ServiceLocator.provideUserRepository(getApplicationContext());
        placeRepository = ServiceLocator.providePlaceRepository(getApplicationContext());

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());

        // make sure user is authenticated
        userAuthenticated = userRepository.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        places = placeRepository.findPlaces().blockingGet();
        placeRepository.savePlaces(userAuthenticated.getUid()).blockingGet();

        launch(MainActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        userRepository.deleteUser(userAuthenticated, true).blockingGet();

        uiDevice = null;
    }

    @Test
    public void when_launch_main_activity_then_display_google_maps() throws UiObjectNotFoundException, InterruptedException {
        // GIVEN : the Main Activity is launched
        Thread.sleep(6000);
        String listViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_list_view);

        UiObject listViewButton = this.uiDevice.findObject(new UiSelector().text(listViewTitle));
        listViewButton.click();

        // WHEN : the user click on the 'Map View' button
        String mapViewTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.title_map_view);

        UiObject mapViewButton = this.uiDevice.findObject(new UiSelector().text(mapViewTitle));
        mapViewButton.click();

        // THEN : the map view fragment is displayed with map and marker
        Thread.sleep(6000);
    }
}