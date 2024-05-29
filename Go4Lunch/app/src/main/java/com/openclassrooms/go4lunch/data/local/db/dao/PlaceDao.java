package com.openclassrooms.go4lunch.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.go4lunch.data.model.db.Place;

import java.util.List;

/**
 * Room Dao for Place Entity
 */
@Dao
public interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePlaces(List<Place> places);

    @Query("SELECT * FROM place WHERE place_id = :placeId")
    Place findPlaceById(String placeId);

    @Query("DELETE FROM Place")
    void deleteAllPlaces();

    @Query("SELECT * FROM Place ORDER BY name ASC")
    List<Place> loadAllPlaces();

    @Update
    void update(Place place);
}
