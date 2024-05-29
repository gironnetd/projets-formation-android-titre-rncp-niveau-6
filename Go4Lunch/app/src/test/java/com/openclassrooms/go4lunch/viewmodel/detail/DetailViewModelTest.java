package com.openclassrooms.go4lunch.viewmodel.detail;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.openclassrooms.go4lunch.utilities.LiveDataTestUtil.getValue;

public class DetailViewModelTest {

    @Rule
    public RxImmediateSchedulerRule testSchedulerRule = new RxImmediateSchedulerRule();

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    private DetailViewModel detailViewModel;

    private FakeUserRepository userRepository;
    private FakePlaceRepository placeRepository;

    private final int PLACES_COUNT = 10;

    @Before
    public void setUpViewModel() throws Exception {
        userRepository = new FakeUserRepository();
        placeRepository = new FakePlaceRepository();
        detailViewModel = new DetailViewModel(userRepository, placeRepository);
    }

    @After
    public void tearDown() throws Exception {
        userRepository = null;
        placeRepository = null;
        detailViewModel = null;
    }

    @Test
    public void add_user() throws InterruptedException {

        Place place = PlaceFactory.makePlace();
        User user = UserFactory.makeUser();
        userRepository.saveUser(user).blockingGet();
        placeRepository.savePlaces(user.getUid(), Collections.singletonList(place)).blockingGet();

        user.setMiddayRestaurantId(place.getPlaceId());
        user.setMiddayRestaurant(place);

        detailViewModel.addUser(user);
        Boolean isWorkmateAdded = (Boolean) getValue(detailViewModel.isWorkmateAdded());

        assertThat(isWorkmateAdded).isTrue();
    }

    @Test
    public void remove_user() throws InterruptedException {
        Place place = PlaceFactory.makePlace();
        User user = UserFactory.makeUser();
        userRepository.saveUser(user).blockingGet();
        placeRepository.savePlaces(user.getUid(), Collections.singletonList(place)).blockingGet();

        user.setMiddayRestaurantId(place.getPlaceId());
        user.setMiddayRestaurant(place);

        detailViewModel.addUser(user);
        Boolean isWorkmateAdded = (Boolean) getValue(detailViewModel.isWorkmateAdded());

        assertThat(isWorkmateAdded).isTrue();

        detailViewModel.removeUser(user);
        Boolean isWorkmateRemoved = (Boolean) getValue(detailViewModel.isWorkmateRemoved());
        isWorkmateAdded = (Boolean) getValue(detailViewModel.isWorkmateAdded());

        assertThat(isWorkmateRemoved).isTrue();
        assertThat(isWorkmateAdded).isFalse();

    }

    @Test
    public void change_midday_restaurant() throws InterruptedException {
        User user = UserFactory.makeUser();
        String placeId = UUID.randomUUID().toString();
        Place place = PlaceFactory.makePlace(placeId);

        place.getWorkmates().add(user);
        user.setMiddayRestaurantId(place.getPlaceId());
        user.setMiddayRestaurant(place);

        String newPlaceId = UUID.randomUUID().toString();
        Place newPlace = PlaceFactory.makePlace(newPlaceId);

        userRepository.saveUser(user).blockingGet();
        placeRepository.savePlaces(user.getUid(), Arrays.asList(place, newPlace)).blockingGet();

        detailViewModel.changeMiddayRestaurant(user, newPlace);

        Boolean isWorkmateAdded = (Boolean) getValue(detailViewModel.isWorkmateAdded());

        assertThat(isWorkmateAdded).isTrue();

        detailViewModel.findPlaceById(newPlaceId);
        newPlace = (Place) getValue(detailViewModel.restaurant());

        assertThat(newPlace.getWorkmates()).contains(user);
    }

    @Test
    public void update_likes() throws InterruptedException {
        Place place = PlaceFactory.makePlace();
        int initiaLike = place.getLikes();
        placeRepository.savePlaces(null, Arrays.asList(place)).blockingGet();

        int newLikeNumber = place.getLikes();
        newLikeNumber++;

        place.setLikes(newLikeNumber);

        detailViewModel.incrementLikes(place);

        detailViewModel.findPlaceById(place.getPlaceId());
        Place placeAfterUpdateLike = (Place) getValue(detailViewModel.restaurant());

        assertThat(placeAfterUpdateLike.getLikes()).isEqualTo(initiaLike + 1);
    }

    @Test
    public void find_place_by_id() throws InterruptedException {
        String placeId = UUID.randomUUID().toString();

        Place place = PlaceFactory.makePlace(placeId);

        List<Place> places = new ArrayList<>();
        places.add(place);

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }

        placeRepository.savePlaces(null, places).blockingGet();

        detailViewModel.findPlaceById(placeId);

        Place placeToFound = (Place) getValue(detailViewModel.restaurant());

        assertThat(placeToFound).isEqualTo(place);
    }
}