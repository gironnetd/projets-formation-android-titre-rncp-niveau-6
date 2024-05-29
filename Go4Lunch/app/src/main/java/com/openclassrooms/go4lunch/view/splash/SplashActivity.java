package com.openclassrooms.go4lunch.view.splash;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.data.local.prefs.AppPreferences;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.utilities.Constants;
import com.openclassrooms.go4lunch.view.BaseActivity;
import com.openclassrooms.go4lunch.view.authentication.AuthenticationActivity;
import com.openclassrooms.go4lunch.view.main.MainActivity;
import com.openclassrooms.go4lunch.view.settings.notification.AlarmBroadcastReceiver;
import com.openclassrooms.go4lunch.viewmodel.splash.SplashViewModel;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.openclassrooms.go4lunch.utilities.Constants.AUTHENTICATION_REQUEST_CODE;
import static com.openclassrooms.go4lunch.utilities.Constants.CURRENT_USER_AUTHENTICATED;
import static com.openclassrooms.go4lunch.utilities.HelperClass.logErrorMessage;

/**
 * Splash Activity class implementation
 */
public class SplashActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private ArrayList<Place> places = new ArrayList<>();
    private SplashViewModel splashViewModel;
    private User currentUser;

    @BindView(R.id.go4lunch_animated_logo)
    ImageView go4lunchLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AnimatedVectorDrawableCompat avd =
                    AnimatedVectorDrawableCompat.create(this, R.drawable.go4lunch_logo_anim);
            // only for gingerbread and newer versions
            go4lunchLogo.setImageDrawable(avd);

            Objects.requireNonNull(avd).registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    ((Animatable) go4lunchLogo.getDrawable()).start();
                }
            });
            if (!isRunningInstrumentedTest()) {
                avd.start();
            }
        } else {
            go4lunchLogo.setBackgroundResource(R.drawable.ic_hot_clipart_hot_food);
        }

        initSplashViewModel();
        checkLocationPermissionGranted();
        verifyIfUserIsAuthenticated();

        if (!AlarmBroadcastReceiver.isAlarmStarted) {
            AlarmBroadcastReceiver.scheduleAlarm(getBaseContext());
        }
    }

    private void checkLocationPermissionGranted() {
        mLocationPermissionGranted = AppPreferences.preferences(getApplicationContext())
                .getLocationPermissionGranted();
    }

    private void initSplashViewModel() {
        splashViewModel = (SplashViewModel) obtainViewModel(SplashViewModel.class);

        splashViewModel.launchMainActivity().observe(this, launchMainActivity -> {
            if (launchMainActivity) {
                startMainActivity();
            }
        });
    }

    private void verifyIfUserIsAuthenticated() {
        splashViewModel.isUserAuthenticated();
        splashViewModel.currentUserAuthenticated().observe(this, user -> {
            currentUser = user;

            if (currentUser == null && mLocationPermissionGranted) {
                findDeviceLocation();
                startAuthenticationActivity();
            } else if ((currentUser == null || !currentUser.isAuthenticated()) && !mLocationPermissionGranted) {
                // Prompt the user for permission.
                getLocationPermission();
            } else if (currentUser != null && currentUser.isAuthenticated() && mLocationPermissionGranted && places.isEmpty()) {
                // Get the current location of the device and set the position of the map.
                findDeviceLocation();
            }
        });
    }

    private void startAuthenticationActivity() {
        Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
        startActivityForResult(intent, AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATION_REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Animatable) go4lunchLogo.getDrawable()).start();
            }
            if (data != null) {
                currentUser = Objects.requireNonNull(data).getParcelableExtra(CURRENT_USER_AUTHENTICATED);
                if (!places.isEmpty() && currentUser != null && Objects.requireNonNull(currentUser).isAuthenticated()) {
                    savePlaces();
                }
            }
        }
    }

    private void savePlaces() {
        splashViewModel.savePlaces(currentUser.getUid(), places);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putParcelableArrayListExtra(Constants.PLACES, places);
        intent.putExtra(CURRENT_USER_AUTHENTICATED, currentUser);
        startActivity(intent);
        finish();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void findDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted && places.isEmpty()) {
                splashViewModel.findPlaces();
                splashViewModel.restaurants().observe(this, places -> {
                    this.places = (ArrayList<Place>) places;
                    if (currentUser != null && currentUser.isAuthenticated() && !this.places.isEmpty()) {
                        savePlaces();
                    }
                });
            }
        } catch (SecurityException e) {
            logErrorMessage(e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            AppPreferences.preferences(getApplicationContext()).setLocationPermissionGranted(true);
            // Get the current location of the device and set the position of the map.
            findDeviceLocation();
            startAuthenticationActivity();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        ((Animatable) go4lunchLogo.getDrawable()).start();
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                AppPreferences.preferences(getApplicationContext()).setLocationPermissionGranted(true);
                // Get the current location of the device and set the position of the map.
                findDeviceLocation();
                startAuthenticationActivity();
            }
        }
    }
}

