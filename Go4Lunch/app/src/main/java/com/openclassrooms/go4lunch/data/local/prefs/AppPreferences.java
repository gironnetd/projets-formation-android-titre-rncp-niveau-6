package com.openclassrooms.go4lunch.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.openclassrooms.go4lunch.utilities.Constants;

/**
 * Preferences Class for application
 */
public class AppPreferences {

    private static final String LOCATION_PERMISSION_GRANTED = "LOCATION_PERMISSION_GRANTED";
    private static final String PREF_KEY_DEVICE_LOCATION_POSTAL_CODE = "PREF_KEY_DEVICE_LOCATION_POSTAL_CODE";
    private static final String PREF_KEY_DEVICE_LOCATION_LATITUDE = "PREF_KEY_DEVICE_LOCATION_LATITUDE";
    private static final String PREF_KEY_DEVICE_LOCATION_LONGITUDE = "PREF_KEY_DEVICE_LOCATION_LONGITUDE";
    private static final String PREF_KEY_CURRENT_USER_UUID = "PREF_KEY_CURRENT_USER_UUID";

    private static AppPreferences instance;
    private final SharedPreferences sharedPreferences;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    private AppPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_FILE,
                Context.MODE_PRIVATE);
    }

    public static AppPreferences preferences(Context context) {
        synchronized (LOCK) {
            if(instance == null) {
                instance = new AppPreferences(context);
            }
            return instance;
        }
    }

    public  Boolean getLocationPermissionGranted() {
        return sharedPreferences.getBoolean(LOCATION_PERMISSION_GRANTED, false);
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        sharedPreferences.edit().putBoolean(LOCATION_PERMISSION_GRANTED, locationPermissionGranted).apply();
    }

    public String getPrefKeyDeviceLocationPostalCode() {
        return sharedPreferences.getString(PREF_KEY_DEVICE_LOCATION_POSTAL_CODE, null);
    }

    public void setPrefKeyDeviceLocationPostalCode(String codePostal) {
        sharedPreferences.edit().putString(PREF_KEY_DEVICE_LOCATION_POSTAL_CODE, codePostal).apply();
    }

    public double getPrefKeyDeviceLocationLatitude() {
        return sharedPreferences.getFloat(PREF_KEY_DEVICE_LOCATION_LATITUDE, 0);
    }

    public void setPrefKeyDeviceLocationLatitude(double latitude) {
        sharedPreferences.edit().putFloat(PREF_KEY_DEVICE_LOCATION_LATITUDE, (float) latitude).apply();
    }

    public double getPrefKeyDeviceLocationLongitude() {
        return sharedPreferences.getFloat(PREF_KEY_DEVICE_LOCATION_LONGITUDE, 0);
    }

    public void setPrefKeyDeviceLocationLongitude(double longitude) {
        sharedPreferences.edit().putFloat(PREF_KEY_DEVICE_LOCATION_LONGITUDE, (float) longitude).apply();
    }

    public String getPrefKeyDeviceLocationLatitudeLongitude() {
        double latitude = getPrefKeyDeviceLocationLatitude();
        double longitude = getPrefKeyDeviceLocationLongitude();

        return latitude + "," + longitude;
    }

    public String getPrefKeyCurrentUserUuid() {
        return sharedPreferences.getString(PREF_KEY_CURRENT_USER_UUID, null);
    }

    public void setPrefKeyCurrentUserUuid(String uuid) {
        sharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_UUID, uuid).apply();
    }
}
