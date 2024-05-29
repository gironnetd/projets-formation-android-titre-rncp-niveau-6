package com.openclassrooms.go4lunch.viewmodel.splash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for the splash activity
 */
public class SplashViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    private final MutableLiveData<User> _currentUserAuthenticated;
    private final MutableLiveData<List<Place>> _restaurants;
    private final MutableLiveData<Boolean> _launchMainActivity;
    private final CompositeDisposable compositeDisposable;

    public SplashViewModel(UserRepository userRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
        _currentUserAuthenticated = new MutableLiveData<>();
        _restaurants = new MutableLiveData<>();
        _launchMainActivity = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void isUserAuthenticated() {
        Disposable disposable = userRepository.isUserAuthenticated()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((user, throwable) -> {
                    if (user != null) {
                        if (user.getMiddayRestaurantId() != null)
                            placeRepository.findPlaceById(user.getMiddayRestaurantId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .doOnSuccess(place -> {
                                        if (place != null) {
                                            user.setMiddayRestaurant(place);
                                            _currentUserAuthenticated.setValue(user);
                                        }
                                    }).subscribe();
                        else {
                            _currentUserAuthenticated.setValue(user);
                        }
                    }

                    if(throwable != null) {
                        _currentUserAuthenticated.setValue(null);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<User> currentUserAuthenticated() {
        return _currentUserAuthenticated;
    }

    public void findPlaces() {
        Disposable disposable = placeRepository.findPlaces()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(_restaurants::setValue)
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public LiveData<List<Place>> restaurants() {
        return _restaurants;
    }

    public void savePlaces(String currentUserId, List<Place> restaurants) {
        Disposable disposable = placeRepository.savePlaces(currentUserId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(() -> _launchMainActivity.setValue(true));
        compositeDisposable.add(disposable);
    }

    public LiveData<Boolean> launchMainActivity() {
        return _launchMainActivity;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
