package com.openclassrooms.go4lunch.data.repository;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.model.db.factory.UserFactory;
import com.openclassrooms.go4lunch.data.source.place.FakePlaceLocalDataSource;
import com.openclassrooms.go4lunch.data.source.place.FakePlaceRemoteDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class DefaultPlaceRepositoryTest {

    private DefaultPlaceRepository placeRepository;

    private FakePlaceLocalDataSource placeLocalDataSource;
    private FakePlaceRemoteDataSource placeRemoteDataSource;

    private Place newPlace;

    private List<Place> remotePlaces = new ArrayList<>();
    private List<Place> localPlaces = new ArrayList<>();
    private List<Place> newPlaces = new ArrayList<>();

    private final int PLACES_COUNT = 10;

    @Before
    public void setUp() throws Exception {
        Place place1 = PlaceFactory.makePlace();
        Place place2 = PlaceFactory.makePlace();
        Place place3 = PlaceFactory.makePlace();
        newPlace = PlaceFactory.makePlace();

        remotePlaces.add(place1);
        remotePlaces.add(place2);
        remotePlaces.add(place3);

        newPlaces.add(newPlace);

        localPlaces.add(place3);

        placeRemoteDataSource = new FakePlaceRemoteDataSource(remotePlaces);
        placeLocalDataSource = new FakePlaceLocalDataSource(localPlaces);
        placeRepository = new DefaultPlaceRepository(placeLocalDataSource, placeRemoteDataSource);
    }

    @After
    public void tearDown() throws Exception {
        placeRepository = null;
        placeLocalDataSource = null;
        placeRemoteDataSource = null;
        remotePlaces = null;
        localPlaces = null;
        newPlaces = null;
    }

    @Test
    public void save_places() {
        int localPlacesCount = localPlaces.size();
        int remotePlacesCount = remotePlaces.size();

        String userId = UUID.randomUUID().toString();
        placeRepository.savePlaces(userId, newPlaces).blockingGet();

        assertThat(placeLocalDataSource.places.size()).isEqualTo(localPlacesCount + 1);
        assertThat(placeRemoteDataSource.places.size()).isEqualTo(remotePlacesCount + 1);
    }

    @Test
    public void find_place_by_id() {
        String userId = UUID.randomUUID().toString();
        placeRepository.savePlaces(userId, newPlaces).blockingGet();

        String placeId = newPlace.getPlaceId();
        Place expectedPlace = placeRepository.findPlaceById(placeId).blockingGet();

        assertThat(expectedPlace).isEqualTo(newPlace);
    }

    @Test
    public void increment_likes() {
        String userId = UUID.randomUUID().toString();
        placeRepository.savePlaces(userId, newPlaces).blockingGet();

        String placeId = newPlace.getPlaceId();
        int initialLikeCount = newPlace.getLikes();
        int likes = newPlace.getLikes();
        likes++;
        newPlace.setLikes(likes);

        placeRepository.incrementLikes(newPlace).blockingGet();
        Place expectedPlace = placeRepository.findPlaceById(placeId).blockingGet();

        assertThat(expectedPlace.getLikes()).isEqualTo(initialLikeCount + 1);
    }

    @Test
    public void change_place() {
        User user = UserFactory.makeUser();
        String placeId = UUID.randomUUID().toString();
        Place place = PlaceFactory.makePlace(placeId);

        place.getWorkmates().add(user);
        user.setMiddayRestaurantId(place.getPlaceId());
        user.setMiddayRestaurant(place);
        newPlaces.add(place);
        placeRepository.savePlaces(user.getUid(), newPlaces).blockingGet();

        placeRepository.changePlace(user, newPlace).blockingGet();
        Place initialPlaceWithUser = placeRepository.findPlaceById(placeId).blockingGet();

        assertThat(initialPlaceWithUser.getWorkmates()).isEmpty();

        Place newPlaceWithUser = placeRepository.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(newPlaceWithUser.getWorkmates()).contains(user);
    }

    @Test
    public void search_place() {
        String placeId = UUID.randomUUID().toString();
        Place place = placeRepository.searchPlace(placeId).blockingGet();

        assertThat(place).isNotNull();
        assertThat(place.getPlaceId()).isEqualTo(placeId);
    }

    @Test
    public void search_places() {
        List<String> placeIds = new ArrayList<>();

        for(int index = 0; index < PLACES_COUNT; index++) {
            placeIds.add(UUID.randomUUID().toString());
        }
        List<Place> places = placeRepository.searchPlaces(placeIds).blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).hasSize(PLACES_COUNT);
    }

    @Test
    public void find_places() {
        List<Place> places = placeRepository.findPlaces().blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).hasSize(placeLocalDataSource.places.size());

        places = placeRepository.findPlaces().blockingGet();

        assertThat(places).isNotNull();
        assertThat(places).hasSize(placeRemoteDataSource.places.size());
        assertThat(places).hasSize(placeLocalDataSource.places.size());
    }

    @Test
    public void add_user() {
        User user = UserFactory.makeUser();
        user.setMiddayRestaurantId(newPlace.getPlaceId());
        user.setMiddayRestaurant(newPlace);

        placeRepository.savePlaces(user.getUid(), newPlaces).blockingGet();

        placeRepository.addUser(user).blockingGet();

        Place expectedPlace = placeRepository.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).contains(user);
    }

    @Test
    public void remove_user() {
        User user = UserFactory.makeUser();
        user.setMiddayRestaurantId(newPlace.getPlaceId());
        user.setMiddayRestaurant(newPlace);

        placeRepository.savePlaces(user.getUid(), newPlaces).blockingGet();

        placeRepository.addUser(user).blockingGet();
        Place expectedPlace = placeRepository.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).contains(user);

        placeRepository.removeUser(user).blockingGet();
        expectedPlace = placeRepository.findPlaceById(newPlace.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getWorkmates()).doesNotContain(user);
    }
}