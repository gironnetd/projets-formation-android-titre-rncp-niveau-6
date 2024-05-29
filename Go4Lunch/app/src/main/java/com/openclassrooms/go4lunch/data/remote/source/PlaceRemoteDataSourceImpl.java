package com.openclassrooms.go4lunch.data.remote.source;

import android.location.Location;

import androidx.annotation.VisibleForTesting;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.repository.geocoding.GoogleGeocodingApiService;
import com.openclassrooms.go4lunch.data.repository.maps.GoogleMapApiService;
import com.openclassrooms.go4lunch.data.repository.places.GooglePlaceApiService;
import com.openclassrooms.go4lunch.data.source.place.PlaceRemoteDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_LIKES;
import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_NAME;
import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_PLACE_ID;
import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_WORKMATES;
import static com.openclassrooms.go4lunch.data.model.db.Place.FIELD_WORKMATE_IDS;
import static com.openclassrooms.go4lunch.data.model.db.Place.RESTAURANT_COLLECTION;
import static com.openclassrooms.go4lunch.data.model.db.User.FIELD_WORKMATE_ID;
import static com.openclassrooms.go4lunch.data.model.db.User.WORKMATE_COLLECTION;

/**
 * Implementation for Place Remote Data Source Interface
 */
public class PlaceRemoteDataSourceImpl implements PlaceRemoteDataSource {

    private static PlaceRemoteDataSourceImpl instance;

    private final FirebaseFirestore mFirebaseFirestore;

    private final GoogleGeocodingApiService geocodingApiService;
    private final GoogleMapApiService mapApiService;
    private final GooglePlaceApiService placeApiService;

    public PlaceRemoteDataSourceImpl(FirebaseFirestore mFirebaseFirestore,
                                     GoogleGeocodingApiService geocodingApiService,
                                     GoogleMapApiService mapApiService,
                                     GooglePlaceApiService placeApiService) {
        this.mFirebaseFirestore = mFirebaseFirestore;
        this.geocodingApiService = geocodingApiService;
        this.mapApiService = mapApiService;
        this.placeApiService = placeApiService;
    }

    public static PlaceRemoteDataSourceImpl instance(FirebaseFirestore firestore,
                                                     GoogleGeocodingApiService geocodingApiService,
                                                     GoogleMapApiService mapApiService,
                                                     GooglePlaceApiService placeApiService) {
        synchronized (PlaceRemoteDataSourceImpl.class) {
            if (instance == null) {
                instance = new PlaceRemoteDataSourceImpl(firestore, geocodingApiService, mapApiService, placeApiService);
            }
            return instance;
        }
    }

    @Override
    public Completable savePlaces(String workmateId, List<Place> restaurants) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Place> registeredRestaurants = queryDocumentSnapshots.toObjects(Place.class);
                    WriteBatch batch = mFirebaseFirestore.batch();

                    for (int index = 0; index < restaurants.size(); index++) {
                        DocumentReference reference = mFirebaseFirestore.collection(RESTAURANT_COLLECTION).document();
                        Place restaurant = restaurants.get(index);
                        if (!isRestaurantAlreadyRegister(restaurant, (ArrayList<Place>) registeredRestaurants)) {
                            if (restaurant.getWorkmateIds() == null) {
                                restaurant.setWorkmateIds(new ArrayList<>());
                            }
                            restaurant.getWorkmateIds().add(workmateId);
                            batch.set(reference, restaurant);
                        } else {
                            mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                                    .whereEqualTo(FIELD_PLACE_ID, restaurant.getPlaceId()).get()
                                    .addOnSuccessListener(querySnapshots -> {
                                        DocumentReference documentReference = querySnapshots.getDocuments().get(0).getReference();
                                        Place restaurantToUpdate = querySnapshots.getDocuments().get(0).toObject(Place.class);

                                        if (restaurantToUpdate != null && restaurantToUpdate.getWorkmateIds() != null && !restaurantToUpdate.getWorkmateIds().contains(workmateId)) {
                                            restaurantToUpdate.getWorkmateIds().add(workmateId);
                                            WriteBatch writeBatch = mFirebaseFirestore.batch();
                                            writeBatch.update(documentReference, FIELD_WORKMATE_IDS, restaurantToUpdate.getWorkmateIds());
                                            writeBatch.commit();
                                        }
                                    });
                        }
                    }
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        }
                    });
                }));
    }

    private boolean isRestaurantAlreadyRegister(Place restaurantToRegistered, ArrayList<Place> registeredRestaurants) {
        return registeredRestaurants.contains(restaurantToRegistered);
    }

    @Override
    public Single<Place> findPlaceById(String placeId) {
        return Single.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, placeId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                        Place place = queryDocumentSnapshots.getDocuments().get(0).toObject(Place.class);
                        if (place != null) {
                            emitter.onSuccess(place);
                        } else {
                            emitter.onError(new Throwable());
                        }
                    } else {
                        emitter.onError(new Throwable("Query is empty"));
                    }
                }));
    }

    @Override
    public Completable incrementLikes(Place place) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, place.getPlaceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    DocumentReference reference = queryDocumentSnapshots.getDocuments().get(0).getReference();

                    WriteBatch batch = mFirebaseFirestore.batch();
                    batch.update(reference, FIELD_LIKES, place.getLikes());
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        }
                    });
                }));
    }

    @Override
    public Completable removeUser(User user) {

        if (user.getMiddayRestaurant() == null) {
            return Completable.complete();
        }

        return Completable.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, user.getMiddayRestaurant().getPlaceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Place restaurant = queryDocumentSnapshots.getDocuments().get(0).toObject(Place.class);

                    if (restaurant.getWorkmates() != null && restaurant.getWorkmates().contains(user)) {
                        restaurant.getWorkmates().remove(user);
                    }

                    WriteBatch batch = mFirebaseFirestore.batch();

                    DocumentReference restaurantReference = queryDocumentSnapshots
                            .getDocuments().get(0).getReference();
                    batch.set(queryDocumentSnapshots.getDocuments().get(0).getReference(), restaurant);
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            restaurantReference.collection(WORKMATE_COLLECTION)
                                    .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                                    .get()
                                    .addOnSuccessListener(workmateDocumentSnapshots -> {
                                        WriteBatch writeBatch = mFirebaseFirestore.batch();
                                        if (!workmateDocumentSnapshots
                                                .getDocuments().isEmpty()) {
                                            DocumentReference workmateReference = workmateDocumentSnapshots
                                                    .getDocuments().get(0).getReference();

                                            writeBatch.delete(workmateReference);
                                            writeBatch.commit();
                                        }
                                        emitter.onComplete();
                                    });
                        }
                    });
                }));
    }

    @Override
    public Completable addUser(User user) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, user.getMiddayRestaurant().getPlaceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Place restaurant = queryDocumentSnapshots.getDocuments().get(0).toObject(Place.class);

                    if (restaurant.getWorkmates() == null) {
                        restaurant.setWorkmates(new ArrayList<>());
                    }
                    restaurant.getWorkmates().add(user);
                    WriteBatch batch = mFirebaseFirestore.batch();

                    DocumentReference restaurantReference = queryDocumentSnapshots
                            .getDocuments().get(0).getReference();

                    batch.update(restaurantReference, FIELD_WORKMATES, restaurant.getWorkmates());
                    batch.set(restaurantReference.collection(WORKMATE_COLLECTION).document(), user);
                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onComplete();
                        }
                    });
                }));
    }

    @Override
    public Completable changePlace(User user, Place place) {
        return Completable.create(emitter -> mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                .whereEqualTo(FIELD_PLACE_ID, user.getMiddayRestaurant().getPlaceId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                        Place restaurant = queryDocumentSnapshots.getDocuments().get(0).toObject(Place.class);

                        if (restaurant.getWorkmates() != null && restaurant.getWorkmates().contains(user)) {
                            restaurant.getWorkmates().remove(user);
                        }

                        user.setMiddayRestaurantId(place.getPlaceId());
                        user.setMiddayRestaurant(place);

                        WriteBatch batch = mFirebaseFirestore.batch();

                        DocumentReference restaurantReference = queryDocumentSnapshots
                                .getDocuments().get(0).getReference();

                        batch.update(restaurantReference,
                                FIELD_WORKMATES, restaurant.getWorkmates());
                        batch.commit().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                restaurantReference.collection(WORKMATE_COLLECTION)
                                        .whereEqualTo(FIELD_WORKMATE_ID, user.getUid())
                                        .get()
                                        .addOnSuccessListener(workmateDocumentSnapshots -> {
                                            WriteBatch writeBatch = mFirebaseFirestore.batch();
                                            DocumentReference workmateReference = workmateDocumentSnapshots
                                                    .getDocuments().get(0).getReference();
                                            writeBatch.delete(workmateReference);
                                            writeBatch.commit().addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    emitter.onComplete();
                                                }
                                            });
                                        });
                            }
                        });
                    }
                }));
    }

    public Single<Query> queryAllRestaurants(String workmateId) {
        return Single.create(emitter -> {
            Query query = mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                    .whereArrayContains(FIELD_WORKMATE_IDS, workmateId).orderBy(FIELD_NAME, Query.Direction.ASCENDING);
            emitter.onSuccess(query);
        });
    }

    public Single<Boolean> isNewPostalCode(Location location) {
        return geocodingApiService.isNewPostalCode(location).subscribeOn(Schedulers.io());
    }

    public Single<Location> findDeviceLocation() {
        return mapApiService.findDeviceLocation();
    }

    @Override
    public Single<List<Place>> findPlaces() throws IOException, InterruptedException {
        return placeApiService.findPlaces();
    }

    @Override
    public Single<Place> searchPlace(String placeId) {
        return placeApiService.searchPlace(placeId);
    }

    @Override
    public Single<List<Place>> searchPlaces(List<String> placeIds) {
        return placeApiService.searchPlaces(placeIds);
    }

    @VisibleForTesting
    public Completable deletePlace(String placeId) {
        return Completable.create(emitter -> {
            mFirebaseFirestore.collection(RESTAURANT_COLLECTION)
                    .whereEqualTo(FIELD_PLACE_ID, placeId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        WriteBatch batch = mFirebaseFirestore.batch();
                        batch.delete(queryDocumentSnapshots.getDocuments().get(0).getReference());
                        batch.commit().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onComplete();
                            }
                        });
                    });
        });
    }
}
