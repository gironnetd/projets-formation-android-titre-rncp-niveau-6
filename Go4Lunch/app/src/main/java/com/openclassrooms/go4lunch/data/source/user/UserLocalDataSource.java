package com.openclassrooms.go4lunch.data.source.user;

import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.UserDataSource;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * User local data source interface
 */
public interface UserLocalDataSource extends UserDataSource {

    Single<User> findCurrentUser();

    Completable deleteUser(User user);
}
