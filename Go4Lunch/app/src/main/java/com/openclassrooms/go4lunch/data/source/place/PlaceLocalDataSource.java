package com.openclassrooms.go4lunch.data.source.place;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.source.PlaceDataSource;

import java.util.List;

import io.reactivex.Completable;

/**
 * Place local data source interface
 */
public interface PlaceLocalDataSource extends PlaceDataSource {

    Completable savePlaces(List<Place> places);

    Completable deleteAllPlaces();
}
