package com.openclassrooms.go4lunch.data.source;

import com.openclassrooms.go4lunch.data.model.db.Place;

import java.io.IOException;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Base interface for local and remote Place data source
 */
public interface PlaceDataSource {

    Single<Place> findPlaceById(String placeId);

    Single<List<Place>> findPlaces() throws IOException, InterruptedException;

    Completable incrementLikes(Place place);
}
