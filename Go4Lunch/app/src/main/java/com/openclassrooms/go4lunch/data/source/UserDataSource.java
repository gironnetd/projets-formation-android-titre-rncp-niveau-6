package com.openclassrooms.go4lunch.data.source;

import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Base interface for local and remote User data source
 */
public interface UserDataSource {

    Completable saveUser(User user);

    Completable updateUser(User user, Place place);

    Single<User> findCurrentUser();
}
