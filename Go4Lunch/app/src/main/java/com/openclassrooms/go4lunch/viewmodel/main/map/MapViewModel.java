package com.openclassrooms.go4lunch.viewmodel.main.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
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
 * View model for the map fragment in main activity
 */
public class MapViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    private MutableLiveData<Query> _queryLoadAllRestaurants;
    private MutableLiveData<User> _currentUser;
    private MutableLiveData<List<Place>> _places;
    private MutableLiveData<Place> _place;
    private CompositeDisposable compositeDisposable;

    public MapViewModel(UserRepository userRepository, PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        _queryLoadAllRestaurants = new MutableLiveData<>();
        _currentUser = new MutableLiveData<>();
        _places = new MutableLiveData<>();
        _place = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void queryAllRestaurants(String currentUserId){
        Disposable disposable = placeRepository.queryAllRestaurants(currentUserId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(query -> _queryLoadAllRestaurants.setValue(query));
        compositeDisposable.add(disposable);
    }

    public LiveData<Query> queryAllRestaurants() {
        return _queryLoadAllRestaurants;
    }

    public void findPlaces() {
        Disposable disposable = placeRepository.findPlaces()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(places -> {
                    _places.setValue(places);
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<List<Place>> places() {
        return _places;
    }

    public void searchPlace(String placeId) {
        Disposable disposable = placeRepository.searchPlace(placeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((place, throwable) -> {
                    if (place != null) {
                        _place.setValue(place);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<Place> place() {
        return _place;
    }

    public void findCurrentUser() {
        Disposable disposable = userRepository.findCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((user, throwable) -> {
                    if (user != null) {
                        _currentUser.setValue(user);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<User> currentUser() {
        return _currentUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
