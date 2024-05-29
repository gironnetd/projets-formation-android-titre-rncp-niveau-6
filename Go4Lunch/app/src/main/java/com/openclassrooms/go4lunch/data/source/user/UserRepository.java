package com.openclassrooms.go4lunch.data.source.user;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.Place;
import com.openclassrooms.go4lunch.data.model.db.User;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * User repository interface
 */
public interface UserRepository {

    Single<User> isUserAuthenticated();

    Single<User> firebaseSignInWithCredential(AuthCredential authCredential);

    Single<User> createUserWithEmailAndPassword(String email, String password);

    Single<User> signInWithEmailAndPassword(String email, String password);

    Single<Query> queryAllWorkmates();

    Single<Query> queryWorkmatesByRestaurant(String restaurantId);

    Single<User> findCurrentUser();

    Completable saveUser(User user);

    Completable updateUser(User user, Place place);

    Completable deleteUser(User user, boolean logout);

}
