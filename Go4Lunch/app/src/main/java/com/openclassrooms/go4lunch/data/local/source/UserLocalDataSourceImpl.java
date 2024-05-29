package com.openclassrooms.go4lunch.data.local.source;

import com.openclassrooms.go4lunch.data.local.db.dao.UserDao;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.user.UserLocalDataSource;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation for User Local Data Source Interface
 */
public class UserLocalDataSourceImpl implements UserLocalDataSource {

    private static UserLocalDataSourceImpl instance;
    private final UserDao userDao;

    public UserLocalDataSourceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public static UserLocalDataSourceImpl instance(UserDao userDao) {
        synchronized (UserLocalDataSourceImpl.class) {
            if(instance == null) {
                instance = new UserLocalDataSourceImpl(userDao);
            }
            return instance;
        }
    }

    @Override
    public Completable saveUser(User user) {
        return Completable.create(emitter -> {
            userDao.saveUser(user);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    public Single<User> findCurrentUser() {
        return Single.create(emitter -> {
            User user = userDao.loadCurrentUser();
            if (user != null) {
                emitter.onSuccess(user);
            } else {
                emitter.onError(new Throwable("Current User is not Found"));
            }
        });
    }

    @Override
    public Completable updateUser(User user, Place place) {
        return Completable.create(emitter -> {
            if (place != null) {
                user.setMiddayRestaurantId(place.getPlaceId());
            } else {
                user.setMiddayRestaurantId(null);
            }
            userDao.updateUser(user);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable deleteUser(User user) {
        return Completable.create(emitter -> {
            userDao.deleteUser(user);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
