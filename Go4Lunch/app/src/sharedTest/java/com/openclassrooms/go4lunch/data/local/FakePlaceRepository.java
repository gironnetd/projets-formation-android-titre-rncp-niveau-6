package com.openclassrooms.go4lunch.data.local;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.model.db.factory.PlaceFactory;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakePlaceRepository implements PlaceRepository {

    private List<Place> placeServiceData = new ArrayList<>();

    @Override
    public Single<List<Place>> findPlaces() {
        return Single.just(placeServiceData);
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
            for (String placeId : placeIds) {
                places.add(PlaceFactory.makePlace(placeId));
            }
            emitter.onSuccess(places);
        });
    }

    @Override
    public Single<Query> queryAllRestaurants(String workmateId) {
        return null;
    }

    public Completable savePlaces(String workmateId, List<Place> places) {
        return Completable.create(emitter -> {
            this.placeServiceData.addAll(places);
            emitter.onComplete();
        });
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        return Single.create(emitter -> {
            Place placeToFind = placeServiceData.stream().filter(place ->
                    place.getPlaceId().equals(placeId)).findFirst().orElse(null);

            if (placeToFind != null) {
                emitter.onSuccess(placeToFind);
            } else {
                emitter.onError(new Throwable());
            }
        });
    }

    @Override
    public Completable incrementLikes(Place place) {
        return Completable.create(emitter -> {
            int placeIndex = placeServiceData.indexOf(place);
            Place placeToUpdate = placeServiceData.get(placeIndex);
            placeToUpdate.setLikes(place.getLikes());
            placeServiceData.set(placeIndex, placeToUpdate);
            emitter.onComplete();
        });
    }

    @Override
    public Completable removeUser(User user) {
        return Completable.create(emitter -> {
            int index = placeServiceData.indexOf(user.getMiddayRestaurant());
            placeServiceData.get(index).getWorkmates().remove(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable addUser(User user) {
        return Completable.create(emitter -> {
            int index = placeServiceData.indexOf(user.getMiddayRestaurant());
            placeServiceData.get(index).getWorkmates().add(user);
            emitter.onComplete();
        });
    }

    @Override
    public Completable changePlace(User user, Place place) {
        return Completable.create(emitter -> {
            int index = placeServiceData.indexOf(user.getMiddayRestaurant());
            placeServiceData.get(index).getWorkmates().remove(user);

            user.setMiddayRestaurantId(place.getPlaceId());
            user.setMiddayRestaurant(place);
            emitter.onComplete();
        }).andThen(addUser(user));
    }

    @Override
    public Completable savePlaces(String workmateId) {
        return Completable.complete();
    }
}
