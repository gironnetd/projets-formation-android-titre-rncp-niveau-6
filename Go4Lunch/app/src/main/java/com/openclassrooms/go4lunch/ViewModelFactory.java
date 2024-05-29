package com.openclassrooms.go4lunch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.viewmodel.authentication.AuthenticationViewModel;
import com.openclassrooms.go4lunch.viewmodel.detail.DetailViewModel;
import com.openclassrooms.go4lunch.viewmodel.main.MainViewModel;
import com.openclassrooms.go4lunch.viewmodel.main.list.ListViewModel;
import com.openclassrooms.go4lunch.viewmodel.main.map.MapViewModel;
import com.openclassrooms.go4lunch.viewmodel.main.workmate.WorkmateViewModel;
import com.openclassrooms.go4lunch.viewmodel.splash.SplashViewModel;

/**
 * View Model Factory to manage ViewModel injection
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private UserRepository userRepository;
    private PlaceRepository placeRepository;

    public ViewModelFactory(UserRepository userRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (SplashViewModel.class.getName().equals(modelClass.getName()))
            return (T) new SplashViewModel(userRepository, placeRepository);
        else if (AuthenticationViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new AuthenticationViewModel(userRepository);
        } else if (MainViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new MainViewModel(userRepository, placeRepository);
        } else if (MapViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new MapViewModel(userRepository, placeRepository);
        } else if (ListViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new ListViewModel(userRepository, placeRepository);
        } else if (WorkmateViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new WorkmateViewModel(userRepository);
        } else if (DetailViewModel.class.getName().equals(modelClass.getName())) {
            return (T) new DetailViewModel(userRepository, placeRepository);
        } else {
            throw new IllegalStateException("Unexpected value: " + modelClass.getName());
        }
    }
}
