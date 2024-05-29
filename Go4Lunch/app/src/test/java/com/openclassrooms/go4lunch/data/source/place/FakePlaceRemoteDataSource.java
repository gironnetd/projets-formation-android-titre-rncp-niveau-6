package com.openclassrooms.go4lunch.data.source.place;

import android.location.Location;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakePlaceRemoteDataSource implements PlaceRemoteDataSource {

    public List<Place> places;

    public FakePlaceRemoteDataSource(List<Place> places) {
        this.places = places;
    }

    @Override
    public Single<Boolean> isNewPostalCode(Location location) {
        return Single.just(true);
    }

    @Override
    public Single<Location> findDeviceLocation() {
        return Single.just(new Location(""));
    }

    @Override
    public Single<Place> searchPlace(String placeId) {
        return Single.create(emitter -> {
            Place place = PlaceFactory.makePlace(placeId);
            emitter.onSuccess(place);
        });
    }

    @Override
    public Single<List<Place>> searchPlaces(List<String> placeIds) {
        return Single.create(emitter -> {
            List<Place> places = new ArrayList<>();
            for(String placeId : placeIds) {
                places.add(PlaceFactory.makePlace(placeId));
            }
            emitter.onSuccess(places);
        });
    }

    @Override
    public Single<Query> queryAllRestaurants(String workmateId) {
        return null;
    }

    @Override
    public Completable savePlaces(String workmateId, List<Place> places) {
        return Completable.create(emitter -> {
            this.places.addAll(places);
            emitter.onComplete();
        });
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        return Single.create(emitter -> {
            Place placeToFind = places.stream().filter(place ->
                    place.getPlaceId().equals(placeId)).findFirst().orElse(null);
            emitter.onSuccess(placeToFind);
        });
    }

    @Override
    public Single<List<Place>> findPlaces() {
        return Single.just(places);
    }

    @Override
    public Completable incrementLikes(Place place) {
        return Completable.create(emitter -> {
            int placeIndex = places.indexOf(place);
            Place placeToUpdate = places.get(placeIndex);
            placeToUpdate.setLikes(place.getLikes());
            places.set(placeIndex, placeToUpdate);
            emitter.onComplete();
        });
    }

    @Override
    public Completable removeUser(User user) {
        return Completable.create(emitter -> {
            int index = places.indexOf(user.getMiddayRestaurant());
            places.get(index).getWorkmates().remove(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable addUser(User user) {
        return Completable.create(emitter -> {
            int index = places.indexOf(user.getMiddayRestaurant());
            places.get(index).getWorkmates().add(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable changePlace(User user, Place place) {
        return Completable.create(emitter -> {
            int index = places.indexOf(user.getMiddayRestaurant());
            places.get(index).getWorkmates().remove(user);

            user.setMiddayRestaurantId(place.getPlaceId());
            user.setMiddayRestaurant(place);

            emitter.onComplete();
        });
    }
}
