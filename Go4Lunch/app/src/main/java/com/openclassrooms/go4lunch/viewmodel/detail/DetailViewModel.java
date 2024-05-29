package com.openclassrooms.go4lunch.viewmodel.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for the detail activity
 */
public class DetailViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    private final MutableLiveData<User> _currentUser;
    private final MutableLiveData<Place> _restaurant;
    private final MutableLiveData<Boolean> _isWorkmateAdded;
    private final MutableLiveData<Boolean> _isWorkmateRemoved;
    private final MutableLiveData<Query> _queryWorkmateByRestaurant;

    private final CompositeDisposable compositeDisposable;

    public DetailViewModel(UserRepository userRepository, PlaceRepository placeRepository) {

        this.userRepository = userRepository;
        this.placeRepository = placeRepository;

        compositeDisposable = new CompositeDisposable();
        _currentUser = new MutableLiveData<>();
        _restaurant = new MutableLiveData<>();
        _isWorkmateAdded = new MutableLiveData<>();
        _isWorkmateRemoved = new MutableLiveData<>();
        _isWorkmateAdded.setValue(false);
        _isWorkmateRemoved.setValue(false);
        _queryWorkmateByRestaurant = new MutableLiveData<>();
    }

    public void addUser(User user) {
        Disposable disposable = placeRepository.addUser(user)
                .andThen(userRepository.updateUser(user, user.getMiddayRestaurant()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    _isWorkmateRemoved.setValue(false);
                    _isWorkmateAdded.setValue(true);
                });
        compositeDisposable.add(disposable);
    }

    public void removeUser(User user) {
        Disposable disposable = placeRepository.removeUser(user)
                .andThen(userRepository.updateUser(user, null))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    _isWorkmateRemoved.setValue(true);
                    _isWorkmateAdded.setValue(false);
                });
        compositeDisposable.add(disposable);
    }

    public void changeMiddayRestaurant(User user, Place place) {
        Disposable disposable = placeRepository.changePlace(user, place)
                .andThen(userRepository.updateUser(user, place))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

                .subscribe(() -> {
                    _isWorkmateRemoved.setValue(false);
                    _isWorkmateAdded.setValue(true);
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<Boolean> isWorkmateAdded() {
        return _isWorkmateAdded;
    }

    public LiveData<Boolean> isWorkmateRemoved() {
        return _isWorkmateRemoved;
    }

    public void queryWorkmateByRestaurant(String restaurantId) {
        Disposable disposable = userRepository.queryWorkmatesByRestaurant(restaurantId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(_queryWorkmateByRestaurant::setValue);
        compositeDisposable.add(disposable);
    }

    public LiveData<Query> workmateByRestaurant() {
        return _queryWorkmateByRestaurant;
    }

    public void incrementLikes(Place place) {
        Disposable disposable = placeRepository.incrementLikes(place)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public void findPlaceById(String placeId) {
        Disposable disposable = placeRepository.findPlaceById(placeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((place, throwable) -> {
                    if(place != null) {
                        _restaurant.setValue(place);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<Place> restaurant() {
        return _restaurant;
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
