package com.openclassrooms.go4lunch.viewmodel.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.place.PlaceRepository;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for the main activity
 */
public class MainViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    private MutableLiveData<Boolean> _isCurrentUserLogOut;
    private MutableLiveData<User> _currentUser;
    private MutableLiveData<List<Place>> _places;

    private CompositeDisposable compositeDisposable;

    public MainViewModel(UserRepository userRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
        _isCurrentUserLogOut = new MutableLiveData<>();
        _currentUser = new MutableLiveData<>();
        _places = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void logoutCurrentUser(User currentUser) {
        Disposable disposable = placeRepository.removeUser(currentUser)
                .andThen(userRepository.deleteUser(currentUser, true))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> _isCurrentUserLogOut.setValue(true));
        compositeDisposable.add(disposable);
    }

    public LiveData<Boolean> isCurrentUserLogOut() {
        return _isCurrentUserLogOut;
    }

    public void findCurrentUser() {
        EspressoIdlingResource.increment();
        Disposable disposable = userRepository.findCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                })
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
