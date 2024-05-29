package com.openclassrooms.go4lunch.data.repository;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceLocalDataSource;
import com.openclassrooms.go4lunch.data.source.place.PlaceRemoteDataSource;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Default Place Repository implementation of Place Repository interface
 */
public class DefaultPlaceRepository implements PlaceRepository {

    private static DefaultPlaceRepository instance;

    private final PlaceLocalDataSource placeLocalDataSource;
    private final PlaceRemoteDataSource placeRemoteDataSource;

    private List<Place> cachedPlaces;

    public DefaultPlaceRepository(PlaceLocalDataSource placeLocalDataSource, PlaceRemoteDataSource placeRemoteDataSource) {
        this.placeLocalDataSource = placeLocalDataSource;
        this.placeRemoteDataSource = placeRemoteDataSource;
    }

    public static DefaultPlaceRepository instance(PlaceLocalDataSource placeLocalDataSource,
                                                  PlaceRemoteDataSource placeRemoteDataSource) {
        synchronized (DefaultPlaceRepository.class) {
            if (instance == null) {
                instance = new DefaultPlaceRepository(placeLocalDataSource, placeRemoteDataSource);
            }
            return instance;
        }
    }

    @Override
    public Single<List<Place>> findPlaces() {
        EspressoIdlingResource.increment();

        if (cachedPlaces != null) {
            EspressoIdlingResource.decrement();
            return Single.just(cachedPlaces);
        }

        return placeRemoteDataSource.findDeviceLocation().flatMap(location ->
                placeRemoteDataSource.isNewPostalCode(location)
                        .flatMap(isNewPostalCode -> !isNewPostalCode ?
                                placeLocalDataSource.findPlaces() :
                                placeRemoteDataSource.findPlaces()
                                        .flatMap(places ->
                                                placeLocalDataSource.deleteAllPlaces()
                                                        .andThen(placeLocalDataSource.savePlaces(places))
                                                        .andThen(placeLocalDataSource.findPlaces()))
                        ).doOnSuccess(places -> {
                    cachedPlaces = places;
                })
        ).doFinally(() -> {
            if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                EspressoIdlingResource.decrement(); // Set app as idle.
            }
        });
    }

    @Override
    public Single<Place> searchPlace(String placeId) {
        return placeRemoteDataSource.searchPlace(placeId);
    }

    @Override
    public Single<List<Place>> searchPlaces(List<String> placeIds) {
        return placeRemoteDataSource.searchPlaces(placeIds);
    }

    @Override
    public Completable savePlaces(String workmateId) {
        boolean haveToSaved = false;

        for (int index = 0; index < cachedPlaces.size(); index++) {
            if (cachedPlaces.get(index).getWorkmateId() == null
                    || (cachedPlaces.get(index).getWorkmateId() != null
                    && !cachedPlaces.get(index).getWorkmateId().equals(workmateId))) {
                haveToSaved = true;

            }
            cachedPlaces.get(index).setWorkmateId(workmateId);
        }

        if (haveToSaved) {
            EspressoIdlingResource.increment();
            return placeLocalDataSource.savePlaces(cachedPlaces)
                    .andThen(placeRemoteDataSource.savePlaces(workmateId, cachedPlaces))
                    .doOnComplete(() -> {
                        if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                            EspressoIdlingResource.decrement(); // Set app as idle.
                        }
                    });
        } else {
            return Completable.complete();
        }
    }

    @VisibleForTesting
    public Completable savePlaces(String workmateId, List<Place> places) {
        return placeLocalDataSource.savePlaces(places)
                .andThen(placeRemoteDataSource.savePlaces(workmateId, places))
                .doOnComplete(() -> {
                    cachedPlaces = places;
                });
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        if (cachedPlaces != null) {
            return Single.create(emitter -> {
                boolean isPlaceInCache = false;
                for (int index = 0; index < cachedPlaces.size(); index++) {
                    if (cachedPlaces.get(index).getPlaceId().equals(placeId)) {
                        isPlaceInCache = true;
                        if (cachedPlaces.get(index).getWorkmates() == null
                                || (cachedPlaces.get(index).getWorkmates() != null
                                && cachedPlaces.get(index).getWorkmates().isEmpty())) {
                            Place finalFound = cachedPlaces.get(index);
                            int finalIndex = index;
                            placeRemoteDataSource.findPlaceById(placeId)
                                    .doOnSuccess(remotePlace -> {
                                        finalFound.setWorkmateId(remotePlace.getWorkmateId());
                                        finalFound.setWorkmateIds(remotePlace.getWorkmateIds());
                                        finalFound.setWorkmates(remotePlace.getWorkmates());
                                    }).doFinally(() -> {
                                cachedPlaces.set(finalIndex, finalFound);
                                emitter.onSuccess(cachedPlaces.get(finalIndex));
                            }).subscribeOn(Schedulers.io()).subscribe();
                        }
                    }
                    if (!isPlaceInCache) {
                        placeRemoteDataSource.findPlaceById(placeId)
                                .doOnSuccess(place -> {
                                    emitter.onSuccess(place);
                                }).subscribeOn(Schedulers.io()).subscribe();
                    }
                }
            });
        }
        return placeRemoteDataSource.findPlaceById(placeId);
    }

    @Override
    public Completable incrementLikes(Place place) {
        return placeLocalDataSource.incrementLikes(place)
                .andThen(placeRemoteDataSource.incrementLikes(place))
                .doOnComplete(() -> {
                    Place found = null;
                    for (Place placeToUpdate : cachedPlaces) {
                        if (placeToUpdate.getPlaceId().equals(place.getPlaceId())) {
                            found = placeToUpdate;
                            found.setLikes(place.getLikes());
                            break;
                        }
                    }
                });
    }

    @Override
    public Completable removeUser(User user) {
        return placeRemoteDataSource.removeUser(user)
                .doOnComplete(() -> {
                    Place found = null;
                    for (Place place : cachedPlaces) {
                        if (place.getWorkmates() != null && place.getWorkmates().contains(user)) {
                            found = place;
                            if (found.getWorkmates() == null) {
                                found.setWorkmates(new ArrayList<>());
                            }
                            found.getWorkmates().remove(user);
                            break;
                        }
                    }
                });
    }

    @Override
    public Completable addUser(User user) {
        return placeRemoteDataSource.addUser(user)
                .doOnComplete(() -> {
                    Place found = null;
                    for (Place place : cachedPlaces) {
                        if (place.getPlaceId().equals(user.getMiddayRestaurant().getPlaceId())) {
                            found = place;
                            if (found.getWorkmates() == null) {
                                found.setWorkmates(new ArrayList<>());
                            }
                            found.getWorkmates().add(user);
                            break;
                        }
                    }
                });
    }

    @Override
    public Completable changePlace(User user, Place place) {
        return placeRemoteDataSource.changePlace(user, place)
                .andThen(placeRemoteDataSource.addUser(user));
    }

    @Override
    public Single<Query> queryAllRestaurants(String workmateId) {
        return placeRemoteDataSource.queryAllRestaurants(workmateId);
    }
}
