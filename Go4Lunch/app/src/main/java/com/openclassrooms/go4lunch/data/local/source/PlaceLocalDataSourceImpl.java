package com.openclassrooms.go4lunch.data.local.source;

import com.openclassrooms.go4lunch.data.local.db.dao.PlaceDao;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.source.place.PlaceLocalDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation for Place Local Data Source Interface
 */
public class PlaceLocalDataSourceImpl implements PlaceLocalDataSource {

    private static PlaceLocalDataSourceImpl instance;
    private final PlaceDao placeDao;

    public PlaceLocalDataSourceImpl(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public static PlaceLocalDataSourceImpl instance(PlaceDao placeDao) {
        synchronized (PlaceLocalDataSourceImpl.class) {
            if(instance == null) {
                instance = new PlaceLocalDataSourceImpl(placeDao);
            }
            return instance;
        }
    }

    @Override
    public Completable savePlaces(List<Place> places) {
        return Completable.create(emitter -> {
            placeDao.savePlaces(places);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        return Single.create(emitter -> {
            Place place = placeDao.findPlaceById(placeId);
            emitter.onSuccess(place);
        });
    }

    @Override
    public Completable incrementLikes(Place place) {
        return Completable.create(emitter -> {
            placeDao.update(place);
            emitter.onComplete();
        });
    }

    public Single<List<Place>> findPlaces() {
        return Single.create(emitter -> {
            List<Place> places = placeDao.loadAllPlaces();
            emitter.onSuccess(places);
        });
    }

    public Completable deleteAllPlaces() {
        return Completable.create(emitter -> {
            placeDao.deleteAllPlaces();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
