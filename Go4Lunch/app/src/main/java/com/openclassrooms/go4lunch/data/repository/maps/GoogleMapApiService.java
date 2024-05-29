package com.openclassrooms.go4lunch.data.repository.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;

import java.util.Objects;

import io.reactivex.Single;

import static com.openclassrooms.go4lunch.utilities.HelperClass.logErrorMessage;

/**
 * Google Map Api Service implementation
 */
public class GoogleMapApiService {

    private static GoogleMapApiService instance;
    private Context mContext;

    public GoogleMapApiService(Context mContext) {
        this.mContext = mContext;
    }

    public static GoogleMapApiService instance(Context context) {
        synchronized (GoogleMapApiService.class) {
            if (instance == null) {
                instance = new GoogleMapApiService(context);
            }
            return instance;
        }
    }

    public Single<Location> findDeviceLocation() {
        return Single.create(emitter -> {
            // Construct a FusedLocationProviderClient.
            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(mContext);

            int permissionStatus = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(20 * 1000);
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                AppPreferences appPreferences = AppPreferences.preferences(mContext);

                                double latitude = Objects.requireNonNull(location.getLatitude());
                                if (appPreferences.getPrefKeyDeviceLocationLatitude() != latitude) {
                                    appPreferences.setPrefKeyDeviceLocationLatitude(latitude);
                                }

                                double longitude = Objects.requireNonNull(location.getLongitude());
                                if (appPreferences.getPrefKeyDeviceLocationLongitude() != longitude) {
                                    appPreferences.setPrefKeyDeviceLocationLongitude(longitude);
                                }
                                emitter.onSuccess(location);
                            } else {
                                logErrorMessage("Current location is null. Using defaults.");
                                emitter.onError(new Throwable("Location is not found"));
                            }
                        }
                    }
                };
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                        Looper.getMainLooper());
            } else {
                emitter.onError(new Throwable("Location is not found"));
            }
        });
    }
}
