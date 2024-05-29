package com.openclassrooms.go4lunch.data.source.user;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.data.model.db.User;
import com.openclassrooms.go4lunch.data.source.UserDataSource;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * User remote data source interface
 */
public interface UserRemoteDataSource extends UserDataSource {

    Single<User> firebaseSignInWithCredential(AuthCredential authCredential);

    Single<User> createUserWithEmailAndPassword(String email, String password);

    Single<User> signInWithEmailAndPassword(String email, String password);

    Single<Query> queryAllWorkmates();

    Single<Query> queryWorkmatesByRestaurant(String restaurantId);

    Single<User> findUserById(String userId);

    Completable deleteUser(User user, boolean logout);
}
