package com.openclassrooms.go4lunch.viewmodel.authentication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * View model for the authentication activity
 */
public class AuthenticationViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> _userAuthenticated;
    private final MutableLiveData<String> _userAuthenticationErrorMessage;

    private final CompositeDisposable compositeDisposable;

    public AuthenticationViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        _userAuthenticated = new MutableLiveData<>();
        _userAuthenticationErrorMessage = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public void signInWithCredential(AuthCredential authCredential) {
        Disposable disposable = userRepository.firebaseSignInWithCredential(authCredential)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((user, throwable) -> {
                    if(user != null) {
                        _userAuthenticated.setValue(user);
                    }
                    if(throwable != null) {
                        _userAuthenticationErrorMessage.setValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void signInWithEmailAndPassword(String email, String password) {
        EspressoIdlingResource.increment();
        Disposable disposable = userRepository.signInWithEmailAndPassword(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doFinally(() -> {
                    EspressoIdlingResource.decrement();
                })
                .subscribe((user, throwable) -> {
                    if(user != null) {
                        _userAuthenticated.setValue(user);
                    }
                    if(throwable != null) {
                        _userAuthenticationErrorMessage.setValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void createUserWithEmailAndPassword(String email, String password) {
        Disposable disposable = userRepository.createUserWithEmailAndPassword(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((user, throwable) -> {
                    if(user != null) {
                        _userAuthenticated.setValue(user);
                    }
                    if(throwable != null) {
                        _userAuthenticationErrorMessage.setValue(throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    public LiveData<User> userAuthenticated() {
        return _userAuthenticated;
    }

    public LiveData<String> userAuthenticatedErrorMessage() {
        return _userAuthenticationErrorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}

