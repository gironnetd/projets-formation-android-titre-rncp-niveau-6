package com.openclassrooms.go4lunch.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.Go4LunchApplication;
import com.openclassrooms.go4lunch.ViewModelFactory;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base Activity class implementation
 */
public class BaseActivity extends AppCompatActivity {

    public final ViewModel obtainViewModel(@NotNull Class viewModelClass) {
        UserRepository userRepository = ((Go4LunchApplication) getApplicationContext()).findUserRepository();
        PlaceRepository placeRepository = ((Go4LunchApplication) getApplicationContext()).findPlaceRepository();
        return new ViewModelProvider(this, new ViewModelFactory(userRepository, placeRepository)).get(viewModelClass);
    }

    private static AtomicBoolean isRunningInstrumentedTest;

    public static synchronized boolean isRunningInstrumentedTest() {
        if (null == isRunningInstrumentedTest) {
            boolean istest;
            try {
                Class.forName("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }
            isRunningInstrumentedTest = new AtomicBoolean(istest);
        }
        return isRunningInstrumentedTest.get();
    }
}
