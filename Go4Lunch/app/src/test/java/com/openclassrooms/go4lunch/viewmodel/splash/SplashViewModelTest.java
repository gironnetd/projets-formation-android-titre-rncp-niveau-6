package com.openclassrooms.go4lunch.viewmodel.splash;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.openclassrooms.go4lunch.data.local.FakePlaceRepository;
import com.openclassrooms.go4lunch.data.local.FakeUserRepository;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.utilities.RxImmediateSchedulerRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;

public class SplashViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private SplashViewModel splashViewModel;

    private FakeUserRepository userRepository;
    private FakePlaceRepository placeRepository;

    private final int PLACES_COUNT = 10;

    @Before
    public void setUpViewModel() throws Exception {
        userRepository = new FakeUserRepository();
        placeRepository = new FakePlaceRepository();
        splashViewModel = new SplashViewModel(userRepository, placeRepository);
    }

    @After
    public void tearDown() throws Exception {
        userRepository = null;
        placeRepository = null;
        splashViewModel = null;
    }

    @Test
    public void check_if_user_with_midday_restaurant_is_authenticated() throws InterruptedException {
        User currentUser = UserFactory.makeUser();
        currentUser.setAuthenticated(true);
        placeRepository.savePlaces(currentUser.getUid(), Collections.singletonList(currentUser.getMiddayRestaurant()))
                .blockingGet();
        userRepository.saveUser(currentUser).blockingGet();

        splashViewModel.isUserAuthenticated();
        User expectedUser = (User) getValue(splashViewModel.currentUserAuthenticated());

        assertThat(expectedUser).isEqualTo(currentUser);
    }

    @Test
    public void check_if_user_without_midday_restaurant_is_authenticated() throws InterruptedException {
        User currentUser = UserFactory.makeUser();
        currentUser.setAuthenticated(true);
        currentUser.setMiddayRestaurantId(null);
        currentUser.setMiddayRestaurant(null);
        userRepository.saveUser(currentUser).blockingGet();

        splashViewModel.isUserAuthenticated();
        User expectedUser = (User) getValue(splashViewModel.currentUserAuthenticated());

        assertThat(expectedUser).isEqualTo(currentUser);
    }

    @Test
    public void find_places() throws InterruptedException {
        User currentUser = UserFactory.makeUser();

        List<Place> places = new ArrayList<>();
        places.add(currentUser.getMiddayRestaurant());

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }

        placeRepository.savePlaces(currentUser.getUid(), places).blockingGet();
        splashViewModel.findPlaces();
        List<Place> expectedPlaces = (List<Place>) getValue(splashViewModel.restaurants());

        assertThat(expectedPlaces).hasSize(places.size());
        assertThat(expectedPlaces).isEqualTo(places);
    }

    @Test
    public void save_places() throws InterruptedException {
        User currentUser = UserFactory.makeUser();

        List<Place> places = new ArrayList<>();
        places.add(currentUser.getMiddayRestaurant());

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }

        splashViewModel.savePlaces(currentUser.getUid(), places);
        Boolean launchMainActivity = (Boolean) getValue(splashViewModel.launchMainActivity());

        assertThat(launchMainActivity).isTrue();
    }
}