package com.openclassrooms.go4lunch.data.repository;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.user.UserLocalDataSource;
import com.openclassrooms.go4lunch.data.source.user.UserRemoteDataSource;
import com.openclassrooms.go4lunch.data.source.user.UserRepository;
import com.openclassrooms.go4lunch.utilities.EspressoIdlingResource;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Default User Repository implementation of User Repository interface
 */
public class DefaultUserRepository implements UserRepository {

    private static DefaultUserRepository instance;

    private final UserLocalDataSource userLocalDataSource;
    private final UserRemoteDataSource userRemoteDataSource;

    private User cachedUser;

    public DefaultUserRepository(UserLocalDataSource userLocalDataSource,
                                 UserRemoteDataSource userRemoteDataSource) {
        this.userLocalDataSource = userLocalDataSource;
        this.userRemoteDataSource = userRemoteDataSource;
    }

    public static DefaultUserRepository instance(UserLocalDataSource userLocalDataSource,
                                                 UserRemoteDataSource userRemoteDataSource) {
        synchronized (DefaultUserRepository.class) {
            if (instance == null) {
                instance = new DefaultUserRepository(userLocalDataSource, userRemoteDataSource);
            }
            return instance;
        }
    }

    @Override
    public Single<User> isUserAuthenticated() {
        return userLocalDataSource.findCurrentUser().subscribeOn(Schedulers.io());
    }

    @Override
    public Single<User> firebaseSignInWithCredential(AuthCredential authCredential) {
        return userRemoteDataSource.firebaseSignInWithCredential(authCredential)
                .flatMap(user -> saveUser(user)
                        .andThen(userRemoteDataSource.findUserById(user.getUid())
                                .doOnSuccess(userToCached -> {
                                    cachedUser = userToCached;
                                })));
    }

    @Override
    public Single<User> createUserWithEmailAndPassword(String email, String password) {
        return userRemoteDataSource.createUserWithEmailAndPassword(email, password)
                .flatMap(user -> saveUser(user)
                        .andThen(userRemoteDataSource.findUserById(user.getUid())
                                .doOnSuccess(userToCached -> {
                                    cachedUser = userToCached;
                                })));
    }

    @Override
    public Single<User> signInWithEmailAndPassword(String email, String password) {
        return userRemoteDataSource.signInWithEmailAndPassword(email, password)
                .flatMap(user -> saveUser(user)
                        .andThen(userRemoteDataSource.findUserById(user.getUid())
                                .doOnSuccess(userToCached -> {
                                    cachedUser = userToCached;
                                })));

    }

    @Override
    public Single<Query> queryAllWorkmates() {
        return userRemoteDataSource.queryAllWorkmates();
    }

    @Override
    public Single<Query> queryWorkmatesByRestaurant(String restaurantId) {
        return userRemoteDataSource.queryWorkmatesByRestaurant(restaurantId);
    }

    @Override
    public Single<User> findCurrentUser() {
        EspressoIdlingResource.increment();
        return cachedUser != null ? Single.just(cachedUser)
                .doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                }) :
                userLocalDataSource.findCurrentUser().flatMap(user ->
                        userRemoteDataSource.findUserById(user.getUid())
                ).doOnSuccess(user -> {
                    cachedUser = user;
                }).doFinally(() -> {
                    if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                        EspressoIdlingResource.decrement(); // Set app as idle.
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable saveUser(User user) {
        return userLocalDataSource.saveUser(user)
                .andThen(userRemoteDataSource.saveUser(user))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable updateUser(User user, Place place) {
        return userRemoteDataSource.updateUser(user, place)
                .andThen(userLocalDataSource.updateUser(user, place))
                .doOnComplete(() -> {
                    if (cachedUser != null) {
                        if (place != null) {
                            cachedUser.setMiddayRestaurantId(place.getPlaceId());
                            cachedUser.setMiddayRestaurant(place);
                        } else {
                            cachedUser.setMiddayRestaurantId(null);
                            cachedUser.setMiddayRestaurant(null);
                        }
                    }
                });
    }

    @Override
    public Completable deleteUser(User user, boolean logout) {
        return userLocalDataSource.deleteUser(user)
                .andThen(userRemoteDataSource.deleteUser(user, logout))
                .doOnComplete(() -> {
                    cachedUser = null;
                });
    }
}
