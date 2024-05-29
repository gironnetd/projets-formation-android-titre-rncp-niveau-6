package com.openclassrooms.go4lunch.data.source.place;

import android.location.Location;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.PlaceDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Place remote data source interface
 */
public interface PlaceRemoteDataSource extends PlaceDataSource {

    Single<Boolean> isNewPostalCode(Location location);

    Single<Location> findDeviceLocation();

    Single<Place> searchPlace(String placeId);

    Single<List<Place>> searchPlaces(List<String> placeIds);

    Single<Query> queryAllRestaurants(String workmateId);

    Completable savePlaces(String workmateId, List<Place> places);

    Completable removeUser(User user);

    Completable addUser(User user);

    Completable changePlace(User user, Place place);
}
