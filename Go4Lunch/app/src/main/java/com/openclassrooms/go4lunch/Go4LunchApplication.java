package com.openclassrooms.go4lunch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.ServiceLocator;

import timber.log.Timber;

/**
 * Application class for Go4Lunch
 */
public class Go4LunchApplication extends MultiDexApplication {

    public UserRepository findUserRepository() {
        return ServiceLocator.provideUserRepository(getApplicationContext());
    }

    public PlaceRepository findPlaceRepository() {
        return ServiceLocator.providePlaceRepository(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        FirebaseFirestore.setLoggingEnabled(true);
    }
}
