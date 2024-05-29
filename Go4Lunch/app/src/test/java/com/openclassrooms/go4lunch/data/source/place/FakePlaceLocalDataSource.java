package com.openclassrooms.go4lunch.data.source.place;

import com.openclassrooms.go4lunch.data.model.db.Place;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class FakePlaceLocalDataSource implements PlaceLocalDataSource {

    public List<Place> places;

    public FakePlaceLocalDataSource(List<Place> places) {
        this.places = places;
    }

    @Override
    public Completable savePlaces(List<Place> places) {
        return Completable.create(emitter -> {
            this.places.addAll(places);
            emitter.onComplete();
        });
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        return Single.create(emitter -> {
            Place place = places.stream().filter(place1 ->
                    place1.getPlaceId().equals(placeId)).findFirst().orElse(null);
            emitter.onSuccess(place);
        });
    }

    @Override
    public Single<List<Place>> findPlaces() {
        return Single.create(emitter -> {
            emitter.onSuccess(places);
        });
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
    public Completable deleteAllPlaces() {
        return Completable.create(emitter -> {
            places.clear();
            emitter.onComplete();
        });
    }
}
