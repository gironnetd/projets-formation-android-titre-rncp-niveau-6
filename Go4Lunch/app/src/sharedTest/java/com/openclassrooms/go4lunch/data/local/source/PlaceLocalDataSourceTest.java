package com.openclassrooms.go4lunch.data.local.source;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.openclassrooms.go4lunch.data.local.db.AppDatabase;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PlaceLocalDataSourceTest {

    private AppDatabase database;
    private PlaceLocalDataSourceImpl placeLocalDataSource;

    private final int PLACES_COUNT = 10;

    @Before
    public void setUp() throws Exception {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        placeLocalDataSource = new PlaceLocalDataSourceImpl(database.placeDao());
    }

    @After
    public void tearDown() throws Exception {
        database.clearAllTables();
        database.close();
    }

    @Test
    public void save_places() {
        List<Place> places = new ArrayList<>();

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        placeLocalDataSource.savePlaces(places).blockingGet();
        List<Place> actualPlaces = placeLocalDataSource.findPlaces().blockingGet();

        assertThat(places.size()).isEqualTo(actualPlaces.size());
    }

    @Test
    public void find_place_by_id() {
        List<Place> places = new ArrayList<>();

        for(int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        String placeId = UUID.randomUUID().toString();
        Place placeFindByPlaceId = PlaceFactory.makePlace(placeId);

        places.add(placeFindByPlaceId);
        placeLocalDataSource.savePlaces(places).blockingGet();

        Place expectedPlace = placeLocalDataSource.findPlaceById(placeId).blockingGet();
        assertThat(expectedPlace.getPlaceId()).isEqualTo(placeFindByPlaceId.getPlaceId());
    }

    @Test
    public void increment_likes() {
        List<Place> places = new ArrayList<>();

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        String placeId = UUID.randomUUID().toString();
        Place placeFindByPlaceId = PlaceFactory.makePlace(placeId);

        places.add(placeFindByPlaceId);
        placeLocalDataSource.savePlaces(places).blockingGet();

        int likes = placeFindByPlaceId.getLikes();
        likes++;
        placeFindByPlaceId.setLikes(likes);

        placeLocalDataSource.incrementLikes(placeFindByPlaceId).blockingGet();
        Place expectedPlace = placeLocalDataSource.findPlaceById(placeFindByPlaceId.getPlaceId()).blockingGet();

        assertThat(expectedPlace.getLikes()).isEqualTo(likes);
    }

    @Test
    public void find_all_places() {
        List<Place> places = new ArrayList<>();

        for(int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        placeLocalDataSource.savePlaces(places).blockingGet();
        assertThat(placeLocalDataSource.findPlaces().blockingGet().size()).isEqualTo(PLACES_COUNT);
    }

    @Test
    public void delete_all_places() {
        List<Place> places = new ArrayList<>();

        for(int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        placeLocalDataSource.savePlaces(places).blockingGet();
        assertThat(placeLocalDataSource.findPlaces().blockingGet().size()).isEqualTo(PLACES_COUNT);

        placeLocalDataSource.deleteAllPlaces().blockingGet();
        assertThat(placeLocalDataSource.findPlaces().blockingGet()).isEmpty();
    }
}