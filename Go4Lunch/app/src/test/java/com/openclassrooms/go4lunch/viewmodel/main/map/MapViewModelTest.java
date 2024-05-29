package com.openclassrooms.go4lunch.viewmodel.main.map;

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
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;

public class MapViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private MapViewModel mapViewModel;
    private FakeUserRepository userRepository;
    private FakePlaceRepository placeRepository;

    private final int PLACES_COUNT = 10;

    @Before
    public void setUpViewModel() throws Exception {
        placeRepository = new FakePlaceRepository();
        mapViewModel = new MapViewModel(userRepository, placeRepository);
    }

    @After
    public void tearDown() throws Exception {
        placeRepository = null;
        mapViewModel = null;
    }

    @Test
    public void search_place() throws InterruptedException {
        User currentUser = UserFactory.makeUser();

        List<Place> places = new ArrayList<>();
        places.add(currentUser.getMiddayRestaurant());

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        placeRepository.savePlaces(currentUser.getUid(), places).blockingGet();

        String placeId = UUID.randomUUID().toString();
        mapViewModel.searchPlace(placeId);

        Place expectedPlace = (Place) getValue(mapViewModel.place());

        assertThat(expectedPlace).isNotNull();
        assertThat(expectedPlace.getPlaceId()).isEqualTo(placeId);
    }
}