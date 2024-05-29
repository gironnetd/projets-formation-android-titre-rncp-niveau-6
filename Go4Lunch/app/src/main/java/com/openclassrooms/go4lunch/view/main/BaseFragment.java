package com.openclassrooms.go4lunch.view.main;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.Go4LunchApplication;
import com.openclassrooms.go4lunch.ViewModelFactory;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base fragment for fragment of the Main activity
 */
public class BaseFragment extends Fragment {

    public final ViewModel obtainViewModel(@NotNull Class viewModelClass) {
        UserRepository userRepository = ((Go4LunchApplication) requireContext().getApplicationContext()).findUserRepository();
        PlaceRepository placeRepository = ((Go4LunchApplication) requireContext().getApplicationContext()).findPlaceRepository();
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
