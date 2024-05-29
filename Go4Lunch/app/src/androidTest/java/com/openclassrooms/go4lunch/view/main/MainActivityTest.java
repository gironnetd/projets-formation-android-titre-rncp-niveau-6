package com.openclassrooms.go4lunch.view.main;

import android.Manifest;
import android.app.Activity;

import androidx.appcompat.widget.Toolbar;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
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
import com.openclassrooms.go4lunch.view.detail.DetailActivity;
import com.openclassrooms.go4lunch.view.settings.SettingsActivity;
import com.openclassrooms.go4lunch.view.splash.SplashActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule
            = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    private Toolbar toolbar;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;

    List<Place> places;

    UiDevice uiDevice;

    MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        userRepository = ServiceLocator.provideUserRepository(getApplicationContext());
        placeRepository = ServiceLocator.providePlaceRepository(getApplicationContext());

        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());

        // make sure user is authenticated
        userAuthenticated = userRepository.signInWithEmailAndPassword(EMAIL, PASSWORD).blockingGet();

        places = placeRepository.findPlaces().blockingGet();
        placeRepository.savePlaces(userAuthenticated.getUid()).blockingGet();

        launch(MainActivity.class);

        toolbar = getActivityInstance().findViewById(R.id.toolbar);
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        if (userAuthenticated != null) {
            userRepository.deleteUser(userAuthenticated, true);
        }
        uiDevice = null;
    }

    @Test
    public void when_click_on_android_home_icon_then_display_name_and_email() throws UiObjectNotFoundException, InterruptedException {
        // GIVEN : the Main Activity is launched
        // WHEN : the user click on the android home icon
        UiObject androidHomeIcon = this.uiDevice.findObject(new UiSelector().description((String) toolbar.getNavigationContentDescription()));
        androidHomeIcon.click();

        // THEN : the nav header display the name and email of user
        UiObject2 navHeaderDisplayName = uiDevice.findObject(By.text(userAuthenticated.getDisplayName()));
        assertThat(navHeaderDisplayName).isNotNull();

        UiObject2 navHeaderEmail = uiDevice.findObject(By.text(userAuthenticated.getEmail()));
        assertThat(navHeaderEmail).isNotNull();
    }

    @Test
    public void given_when_click_on_android_home_icon_when_click_on_settings_open_settings_activity() throws UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched
        // WHEN : the user click on the android home icon and click on 'SETTINGS' item
        UiObject androidHomeIcon = this.uiDevice.findObject(new UiSelector().description((String) toolbar.getNavigationContentDescription()));
        androidHomeIcon.click();

        String settingTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.settings_title);

        UiObject2 settingIcon = uiDevice.findObject(By.text(settingTitle));

        Intents.init();
        settingIcon.click();

        // THEN : the Settings Activity is launched
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void given_when_click_on_android_home_icon_when_click_on_log_out_open_splash_activity() throws UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched
        // WHEN : the user click on the android home icon and click on 'LOGOUT' item
        UiObject androidHomeIcon = this.uiDevice.findObject(new UiSelector().description((String) toolbar.getNavigationContentDescription()));
        androidHomeIcon.click();

        String logoutTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.log_out_title);
        UiObject2 logoutIcon = uiDevice.findObject(By.text(logoutTitle));

        Intents.init();
        logoutIcon.click();

        // THEN : the Splash Activity is launched
        Intents.intended(IntentMatchers.hasComponent(SplashActivity.class.getName()));
        Intents.release();

        getActivityInstance().finish();
    }

    @Test
    public void given_when_click_on_android_home_icon_when_click_on_your_lunch_open_detail_activity() throws UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched and the user has chosen a restaurant
        userRepository.updateUser(userAuthenticated, places.get(0)).blockingGet();

        // WHEN : the user click on the android home icon and click on 'YOUR LUNCH' item
        UiObject androidHomeIcon = this.uiDevice.findObject(new UiSelector().description((String) toolbar.getNavigationContentDescription()));
        androidHomeIcon.click();

        String yourLunchTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.your_lunch_title);

        UiObject2 yourLunchIcon = uiDevice.findObject(By.text(yourLunchTitle));

        Intents.init();
        yourLunchIcon.click();

        // THEN : the Detail Activity s launched
        Intents.intended(IntentMatchers.hasComponent(DetailActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void when_click_on_your_lunch_icon_and_no_lunch_display_toast() throws UiObjectNotFoundException {
        // GIVEN : the Main Activity is launched and the user has not chosen a restaurant
        userRepository.updateUser(userAuthenticated, null).blockingGet();

        // WHEN : the user click on the android home icon and click on 'YOUR LUNCH' item
        UiObject androidHomeIcon = this.uiDevice.findObject(new UiSelector().description((String) toolbar.getNavigationContentDescription()));
        androidHomeIcon.click();

        String yourLunchTitle = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.your_lunch_title);

        UiObject2 yourLunchIcon = uiDevice.findObject(By.text(yourLunchTitle));

        yourLunchIcon.click();

        String noLunchSelected = InstrumentationRegistry.getInstrumentation().getTargetContext()
                .getResources().getString(R.string.midday_restaurant_not_chosen);

        // THEN : a toast is displayed with message
        UiObject toastNoLunchChosen = uiDevice.findObject(new UiSelector().text(noLunchSelected));
        toastNoLunchChosen.waitForExists(3000);
        assertThat(toastNoLunchChosen).isNotNull();
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
