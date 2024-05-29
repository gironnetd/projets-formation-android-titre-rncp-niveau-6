package com.openclassrooms.go4lunch.utilities;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.data.local.db.AppDatabase;
import com.openclassrooms.go4lunch.data.local.source.PlaceLocalDataSourceImpl;
import com.openclassrooms.go4lunch.data.local.source.UserLocalDataSourceImpl;
import com.openclassrooms.go4lunch.data.remote.source.PlaceRemoteDataSourceImpl;
import com.openclassrooms.go4lunch.data.remote.source.UserRemoteDataSourceImpl;
import com.openclassrooms.go4lunch.data.repository.DefaultPlaceRepository;
import com.openclassrooms.go4lunch.data.repository.DefaultUserRepository;
import com.openclassrooms.go4lunch.data.repository.geocoding.GoogleGeocodingApiService;
import com.openclassrooms.go4lunch.data.repository.maps.GoogleMapApiService;
import com.openclassrooms.go4lunch.data.repository.places.GooglePlaceApiService;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

/**
 * Service Locator class to manage Dependency injection in application
 */
public class ServiceLocator {

    private static UserRepository userRepository;
    private static PlaceRepository placeRepository;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public static UserRepository provideUserRepository(Context context) {
        synchronized (LOCK) {
            if (userRepository == null) {
                userRepository = DefaultUserRepository.instance(getUserLocalDataSource(context), getUserRemoteDataSource());
            }
            return userRepository;
        }
    }

    static private UserLocalDataSourceImpl getUserLocalDataSource(Context context) {
        return UserLocalDataSourceImpl.instance(AppDatabase.database(context).userDao());
    }

    static private UserRemoteDataSourceImpl getUserRemoteDataSource() {
        return UserRemoteDataSourceImpl.instance(FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance());
    }

    public static PlaceRepository providePlaceRepository(Context context) {
        synchronized (LOCK) {
            if (placeRepository == null) {
                placeRepository = DefaultPlaceRepository.instance(getPlaceLocalDataSource(context),
                        getPlaceRemoteDataSource(context));
            }
            return placeRepository;
        }
    }

    static private PlaceLocalDataSourceImpl getPlaceLocalDataSource(Context context) {
        return PlaceLocalDataSourceImpl.instance(AppDatabase.database(context).placeDao());
    }

    static private PlaceRemoteDataSourceImpl getPlaceRemoteDataSource(Context context) {
        return PlaceRemoteDataSourceImpl.instance(FirebaseFirestore.getInstance(),
                GoogleGeocodingApiService.instance(context),
                GoogleMapApiService.instance(context),
                GooglePlaceApiService.instance(context));
    }
}
