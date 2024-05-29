package com.openclassrooms.go4lunch.data.local.db.dao;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

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
@SmallTest
public class PlaceDaoTest {

    AppDatabase database;

    private static final int PLACES_COUNT = 10;

    @Before
    public void initDataBase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        database.clearAllTables();
        database.close();
    }

    @Test
    public void save_places() {
        List<Place> places = new ArrayList<>();

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        database.placeDao().savePlaces(places);
        List<Place> actualPlaces = database.placeDao().loadAllPlaces();

        assertThat(places.size()).isEqualTo(actualPlaces.size());
    }

    @Test
    public void find_all_places() {
        List<Place> places = new ArrayList<>();

        for (int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        database.placeDao().savePlaces(places);
        assertThat(database.placeDao().loadAllPlaces().size()).isEqualTo(PLACES_COUNT);
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
        database.placeDao().savePlaces(places);

        Place expectedPlace = database.placeDao().findPlaceById(placeId);
        assertThat(expectedPlace.getPlaceId()).isEqualTo(placeFindByPlaceId.getPlaceId());
    }

    @Test
    public void delete_all_places() {
        List<Place> places = new ArrayList<>();

        for(int index = 0; index < PLACES_COUNT; index++) {
            places.add(PlaceFactory.makePlace());
        }
        database.placeDao().savePlaces(places);
        assertThat(database.placeDao().loadAllPlaces().size()).isEqualTo(PLACES_COUNT);

        database.placeDao().deleteAllPlaces();
        assertThat(database.placeDao().loadAllPlaces()).isEmpty();
    }
}
