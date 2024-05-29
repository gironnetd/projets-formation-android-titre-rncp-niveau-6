package com.openclassrooms.go4lunch.view.authentication;

import android.Manifest;
import android.content.Intent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;
import com.openclassrooms.go4lunch.utilities.ServiceLocator;
import com.openclassrooms.go4lunch.view.splash.SplashActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthenticationActivityTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public GrantPermissionRule mRuntimePermissionRule
            = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityTestRule<AuthenticationActivity> activityTestRule =
            new ActivityTestRule<>(AuthenticationActivity.class, false, false);

    private UserRepository userRepository;

    private final String EMAIL = "gironnetd@yahoo.se";
    private final String PASSWORD = "Gironn050580";

    User userAuthenticated;

    @Before
    public void setUp() {
        userRepository = ServiceLocator.provideUserRepository(getApplicationContext());
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());

        activityTestRule.launchActivity(new Intent());
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        userRepository.deleteUser(userAuthenticated, true).blockingGet();
    }

    @Test
    public void authenticate_with_email_and_password_then_launch_splash_activity() throws InterruptedException {
        // GIVEN : type on email and password button
        onView(withId(R.id.email_password_sign_in_button)).perform(click());

        //WHEN : type email and password on edit text and click sign in button
        onView(withId(R.id.email_text_input_edit_text)).perform(replaceText(EMAIL));
        onView(withId(R.id.password_text_input_edit_text)).perform(replaceText(PASSWORD));

        Intents.init();
        onView(withId(R.id.email_sign_in_button)).perform(click());

        // THEN : Splash Activity is launched
        Intents.intended(IntentMatchers.hasComponent(SplashActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void when_user_is_authenticated_with_email_password_then_current_user_is_saved() {
        // GIVEN : type on email and password button
        onView(withId(R.id.email_password_sign_in_button)).perform(click());

        //WHEN : type email and password on edit text and click sign in button
        onView(withId(R.id.email_text_input_edit_text)).perform(replaceText(EMAIL));
        onView(withId(R.id.password_text_input_edit_text)).perform(replaceText(PASSWORD));

        onView(withId(R.id.email_sign_in_button)).perform(click());

        // THEN : current user is authenticated
        userAuthenticated = userRepository.findCurrentUser().blockingGet();

        assertThat(userAuthenticated).isNotNull();
        assertThat(userAuthenticated.getDisplayName()).isEqualTo(EMAIL);
    }
}