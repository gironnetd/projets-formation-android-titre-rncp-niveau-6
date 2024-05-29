package com.openclassrooms.go4lunch.data.source.place;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Place repository interface
 */
public interface PlaceRepository {

    Single<List<Place>> findPlaces();

    Single<Place> searchPlace(String placeId);

    Single<List<Place>> searchPlaces(List<String> placeIds);

    Single<Query> queryAllRestaurants(String workmateId);

    Completable removeUser(User user);

    Completable addUser(User user);

    Completable changePlace(User user, Place place);

    Completable savePlaces(String workmateId);

    Single<Place> findPlaceById(String placeId);

    Completable incrementLikes(Place place);
}
